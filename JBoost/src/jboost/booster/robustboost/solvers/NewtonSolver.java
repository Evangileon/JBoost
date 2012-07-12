package jboost.booster.robustboost.solvers;

import jboost.booster.RobustBoost;


/**
 * This class is used to solve for ds and dt
 */
public class NewtonSolver {

  final static double RHS_EPS = 1E-7;
  final static double DET_EPS = 1E-7;
  final static int MAX_ITER = 30;

  protected StringBuffer log;

  protected boolean[] mask;
  protected double[] value;
  protected double t;
  protected double ds, dt;
  protected RobustBoost rb;

  protected boolean succeeded;

  final double SQRTPI = Math.sqrt(Math.PI);

  public NewtonSolver(RobustBoost rb, boolean[] mask, double[] value) {

    this.rb = rb;
    this.mask = mask;
    this.value = value;

    this.t = rb.m_t;

    succeeded = false;

    this.ds = Double.NaN;
    this.dt = Double.NaN;

    log = new StringBuffer();
  }

  /**
   * Suppose X is the output of calculateFandJ X[0..1] = F[0..1] X[2..5] =
   * J[0..3]
   * 
   * @param ds
   * @param dt
   * @return
   */

  private double[] calculateFAndJ(double ds, double dt) {

    double[] output = new double[6];
    int l;
    double new_t, new_margin, new_weight, step, temp;
    double[] new_mu_t, new_sigma_t, new_sigma_t_sq;
    double new_A, new_A_ds, new_A_dt, exp_neg_dt;

    new_t = t + dt;
    exp_neg_dt = Math.exp(-dt);

    new_mu_t = new double[] { RobustBoost.calculateMu(rb.m_rho[0], rb.m_theta[0], new_t), RobustBoost.calculateMu(rb.m_rho[1], rb.m_theta[1], new_t) };
    new_sigma_t_sq = new double[] { RobustBoost.calculateSigmaSquare(rb.m_sigma_f[0], new_t), RobustBoost.calculateSigmaSquare(rb.m_sigma_f[1], new_t) };

    new_sigma_t = new double[] { Math.sqrt(new_sigma_t_sq[0]), Math.sqrt(new_sigma_t_sq[1]) };

    for (int i = 0; i < rb.m_numExamples; i++) {

      l = rb.m_labels[i];
      step = (mask[i] ? value[l] : 0);

      new_margin = rb.m_margins[i] * exp_neg_dt + step * ds;
      new_weight = rb.calculateWeight(i, new_margin, new_t);

      new_A = (new_margin - new_mu_t[l]) / new_sigma_t[l];
      new_A_ds = step / new_sigma_t[l];
      new_A_dt = -(rb.m_margins[i] * exp_neg_dt - new_mu_t[l] + 2 * rb.m_rho[l]) / new_sigma_t[l] + (new_A) * (new_sigma_t_sq[l] + 1) / new_sigma_t_sq[l];

      temp = step * new_weight;
      output[0] += temp;
      output[1] += rb.calculatePotential(i, rb.m_margins[i], t) - rb.calculatePotential(i, new_margin, new_t);

      temp = temp * new_A;
      output[2] += temp * new_A_ds;
      output[3] += temp * new_A_dt;
      output[4] += new_weight * new_A_ds;
      output[5] += new_weight * new_A_dt;

    }

    output[2] *= -2.0;
    output[3] *= -2.0;
    output[4] *= 2 / SQRTPI;
    output[5] *= 2 / SQRTPI;

    return output;
  }

  /**
   * output is [a b c d] where a = d f1 / ds b = d f1 / dt c = d f2 / ds d = d
   * f2 / dt
   * 
   * @return
   */
  public double[] calculateJ(double ds, double dt) {

    double[] output = new double[] { 0, 0, 0, 0 };
    double new_t, new_margin, new_weight;
    double[] new_mu_t, new_sigma_t, new_sigma_t_sq;
    double new_A, new_A_ds, new_A_dt;
    double step, exp_neg_dt, temp;
    int l;

    new_t = t + dt;
    exp_neg_dt = Math.exp(-dt);

    new_mu_t = new double[] { RobustBoost.calculateMu(rb.m_rho[0], rb.m_theta[0], new_t), RobustBoost.calculateMu(rb.m_rho[1], rb.m_theta[1], new_t) };
    new_sigma_t_sq = new double[] { RobustBoost.calculateSigmaSquare(rb.m_sigma_f[0], new_t), RobustBoost.calculateSigmaSquare(rb.m_sigma_f[1], new_t) };

    new_sigma_t = new double[] { Math.sqrt(new_sigma_t_sq[0]), Math.sqrt(new_sigma_t_sq[1]) };

    for (int i = 0; i < rb.m_numExamples; i++) {

      l = rb.m_labels[i];
      step = (mask[i] ? value[l] : 0);

      new_margin = rb.m_margins[i] * exp_neg_dt + step * ds;
      new_weight = rb.calculateWeight(i, new_margin, new_t);

      new_A = (new_margin - new_mu_t[l]) / new_sigma_t[l];
      new_A_ds = step / new_sigma_t[l];
      new_A_dt = -(rb.m_margins[i] * exp_neg_dt - new_mu_t[l] + 2 * rb.m_rho[l]) / new_sigma_t[l] + (new_A) * (new_sigma_t_sq[l] + 1) / new_sigma_t_sq[l];

      temp = step * new_weight * new_A;
      output[0] += temp * new_A_ds;
      output[1] += temp * new_A_dt;
      output[2] += new_weight * new_A_ds;
      output[3] += new_weight * new_A_dt;
    }

    output[0] *= -2.0;
    output[1] *= -2.0;
    output[2] *= 2 / SQRTPI;
    output[3] *= 2 / SQRTPI;

    return output;
  }

  /**
   * output is [a b] where a = f1(ds,dt) b = f2(ds,dt)
   * 
   * @return
   */
  public double[] calculateF(double ds, double dt) {

    double[] output = new double[] { 0, 0 };
    double new_t, new_margin, new_weight;
    double step;

    new_t = t + dt;

    for (int i = 0; i < rb.m_numExamples; i++) {

      step = (mask[i] ? value[rb.m_labels[i]] : 0);

      new_margin = rb.m_margins[i] * Math.exp(-dt) + step * ds;
      new_weight = rb.calculateWeight(i, new_margin, new_t);

      output[0] += step * new_weight;
      output[1] += rb.calculatePotential(i, rb.m_margins[i], t) - 
      rb.calculatePotential(i, new_margin, new_t);

    }

    return output;
  }

  public void solve(double init_ds, double init_dt) {

    succeeded = false;

    ds = init_ds;
    dt = init_dt;

    double[] F;
    double[] J;
    double dds, ddt, det;

    for (int i = 0; i < MAX_ITER; i++) {

      // log.append("iter: " + i + ", ds: " + ds + ", dt: " + dt + "\n");

      // calculate F and J
      double[] FJ = calculateFAndJ(ds, dt);
      F = new double[] { FJ[0], FJ[1] };
      J = new double[] { FJ[2], FJ[3], FJ[4], FJ[5] };

      // solve for dds, ddt
      F[0] = -F[0];
      F[1] = -F[1];

      // Found a solution
      if (Math.abs(F[0]) < RHS_EPS && Math.abs(F[1]) < RHS_EPS && 
	    dt > 0 && dt+t-RHS_EPS < 1.0) {

        // log.append("Found a solution in " + i + " iterations!\n");
        // log.append("> ds = " + ds + "\n");
        // log.append("> dt = " + dt + "\n");
        // log.append("> F[0] =" + F[0] + "\n");
        // log.append("> F[1] =" + F[1] + "\n");

        succeeded = true;
        break;
      }

      // check determinant
      det = J[0] * J[3] - J[1] * J[2];
      if (Math.abs(det) < DET_EPS) {
        // log.append("The Jacobian is a singular matrix!\n");
        // log.append("det(J) = " + det + "\n");
        // log.append("J[0] = " + J[0] + "\n");
        // log.append("J[1] = " + J[1] + "\n");
        // log.append("J[2] = " + J[2] + "\n");
        // log.append("J[3] = " + J[3] + "\n");
        // log.append("F[0] = " + F[0] + "\n");
        // log.append("F[1] = " + F[1] + "\n");

        // if this solution is ok
        if (Math.abs(F[0]) < RHS_EPS && Math.abs(F[1]) < RHS_EPS && 
	      dt > 0 && dt+t-RHS_EPS < 1.0) {
          // log.append("Found a solution in " + i + " iterations!\n");
          // log.append("> ds = " + ds + "\n");
          // log.append("> dt = " + dt + "\n");
          // log.append("> F[0] =" + F[0] + "\n");
          // log.append("> F[1] =" + F[1] + "\n");
          succeeded = true;
        }

        break;
      }
      else {

        dds = (J[3] * F[0] - J[1] * F[1]) / det;
        ddt = (J[0] * F[1] - J[2] * F[0]) / det;

        // log.append("det(J) = " + det + "\n");
        // log.append("J[0] = " + J[0] + "\n");
        // log.append("J[1] = " + J[1] + "\n");
        // log.append("J[2] = " + J[2] + "\n");
        // log.append("J[3] = " + J[3] + "\n");
        //
        // log.append("F[0] = " + F[0] + "\n");
        // log.append("F[1] = " + F[1] + "\n");
        //
        // log.append("dds = " + dds + "\n");
        // log.append("ddt = " + ddt + "\n");

      }

      // update ds and dt
      ds += dds;
      dt += ddt;
    }

    if (!succeeded) {
      log.append("NewtonSolver failed!\n");
      ds = Double.NaN;
      dt = Double.NaN;
    }
    else {
      log.append("NewtonSolver completed successfully!\n");
    }
  }

  public boolean isSucceeded() {
    return succeeded;
  }

  public double getDs() {
    return ds;
  }

  public double getDt() {
    return dt;
  }

  public String getLog() {
    return log.toString();
  }

//public boolean canFinishNow() {
//double[] F = calculateF(0, 1 - t);
//double avgPotChange = -F[1] / m_numExamples;
//if (avgPotChange <= Math.abs(m_potentialSlack)) {

//if (avgPotChange > 0) {

//System.out.println("WARNING: RobustBoost is terminating with some increase in the");
//System.out.println(" average potential. Use a smaller -rb_potentialSlack if you think");
//System.out.println(" this is a mistake. [Avg Potential Increased by: " + avgPotChange + "]");
//}

//return true;
//}
//else return false;
//}

}