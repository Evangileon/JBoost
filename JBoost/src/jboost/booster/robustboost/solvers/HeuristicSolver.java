package jboost.booster.robustboost.solvers;

import jboost.booster.RobustBoost;

/**
 * This class is used to solve for ds and dt using a heuristic
 */
public class HeuristicSolver {

  final static double EPS = 1E-7;
  final static int MAX_ITER = 30;

  protected StringBuffer log;

  protected RobustBoost rb;
  protected boolean[] mask;
  protected double[] value;
  protected double t;
  protected double ds;
  protected double dt;

  protected boolean succeeded;

  final double SQRTPI = Math.sqrt(Math.PI);

  public HeuristicSolver(RobustBoost rb, boolean[] mask, double[] value) {

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
      output[1] += rb.calculatePotential(i, new_margin, new_t) -
      rb.calculatePotential(i, rb.m_margins[i], t); 

    }

    return output;
  }

  public void solve() {

    succeeded = false;

    double[] F,F2;

    

    ds = 0;
    dt = 0;
    F  = calculateF(ds,dt);

    double dds,dF;
    double ddt = 1.0;
    
    while (Math.abs(F[0]) > EPS || Math.abs(F[1]) > EPS) {

      // update ds using F[0]/F'[0]
      F2 = calculateF(ds+EPS,dt);
      dF = (F2[0]-F[0])/EPS;
      if (Math.abs(dF) < EPS) {
        dF = (dF<0)?-1:1;
      }   
      dds = F[0]/dF;
      // don't jump too far
      if (Math.abs(dds) > 1.0) {
        //System.out.println("> dds = " + dds);
        //System.out.println("> F[0] = " + F[0]);
        //System.out.println("> F2[0] = " + F2[0]);
        dds = (dds<0)?-1:1;
      }
      ds -= dds;

      // lower-bound and upper-bound for dt
      double dt_L = dt;      
      double dt_R = 1.0 - t; 
      double old_t = dt;
      
      // Binary search for dt
      while (Math.abs(dt_L-dt_R) > EPS*EPS) {
        dt = (dt_L + dt_R)/2;
        F  = calculateF(ds,dt);
        if (F[1] > 0) dt_R = dt;
        else dt_L = dt;
        // if F[1] is good enough, break
        if (Math.abs(F[1]) < EPS) break;
      }
      ddt = dt-old_t;
      
//      System.out.println(">>>   ds = " + ds);
//      System.out.println(">>>   dt = " + dt);
//      System.out.println(">>> F[0] = " + F[0]);
//      System.out.println(">>> F[1] = " + F[1]);
      
      if (Math.abs(ds) > 1e5) {
        System.out.println("ds too large. report this issue to sunsern");
      }

      F  = calculateF(ds,dt);

      // we can finish now if ...
      if (dt > 1-t-EPS && F[1] < EPS) break;
      if (Math.abs(ddt) < EPS) break;
    }

    F = calculateF(ds,dt);

    if (Math.abs(F[0]) < EPS && Math.abs(F[1]) < EPS) {
      succeeded = true;
//      System.out.println("GOOD: normal case");
//      System.out.println("> dt = " + dt);
//      System.out.println("> ds = " + ds);
//      System.out.println("> F[0] = " + F[0]);
//      System.out.println("> F[1] = " + F[1]);
    }
    else if (dt > 1-t-EPS && F[1] < EPS) {
      succeeded = true;
//	  System.out.println("too easy.try decrese epsilon or increase theta.");
//      System.out.println("GOOD: border case");
//      System.out.println("> dt = " + dt);
//      System.out.println("> ds = " + ds);
//      System.out.println("> F[0] = " + F[0]);
//      System.out.println("> F[1] = " + F[1]);
    }
    else {

	if (Math.abs(F[1]) < EPS && Math.abs(ddt) < EPS) {
        succeeded = true;
	  // System.out.println("too hard. try increse epsilon or decrease theta.");
      }
      else {
        succeeded = false;
        System.out.println("This should never happen!");
        System.out.println("> dt = " + dt);
        System.out.println("> ds = " + ds);
        System.out.println("> F[0] = " + F[0]);
        System.out.println("> F[1] = " + F[1]);
      }
    }

    if (!succeeded) {
      ds = Double.NaN;
      dt = Double.NaN;
      log.append("HeuristicSolver failed!\n");
    }
    else {
      log.append("HeuristicSolver completed successfully!\n");
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

}

