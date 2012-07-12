package jboost.booster;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import jboost.atree.PredictorNode;
import jboost.booster.bag.Bag;
import jboost.booster.bag.RobustBinaryBag;
import jboost.booster.brownboost.solvers.HeuristicSolver;
import jboost.booster.prediction.Prediction;
import jboost.booster.prediction.RobustBinaryPrediction;
import jboost.booster.robustboost.solvers.NewtonSolver;
import jboost.controller.Configuration;
import jboost.examples.attributes.Label;

/**
 * Java implemantation of RobustBoost.
 * 
 * @author Sunsern Cheamanunkul
 */
public class RobustBoost extends AbstractAdaBoost {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6711885239670622468L;

	/** current RobustBoost time [0,1] */
	public double m_t;

	/** sigma_f for each class */
	public double[] m_sigma_f;
	/** theta (goal margin) for each class */
	public double[] m_theta;
	/** rho for each class */
	public double[] m_rho;
	/** mistake cost for each class */
	protected double[] m_cost;

	/** most recently used ds */
	protected double m_last_ds;
	/** most recently used dt */
	protected double m_last_dt;

	/** RobustBoost time at last iteration */
	protected double m_old_t;

	/** minimum epsilon that works */
	protected static final double MIN_EPSILON = 1E-6;
	/** maximum epsilon that works */
	protected static final double MAX_EPSILON = 0.999;

	/** amount of change in potential allowed near the end */
	protected double m_potentialSlack = 1e-7;

	/** grid search parameter */
	protected static final double DS_MIN = 0;
	protected static final double DS_MAX = 10;
	protected static final double DS_STEP = 0.1;
	protected static final double DT_MIN = 0.0001;
	protected static final double DT_STEP = 0.1;



	/**
	 * default constructor epsilon = 0.1 theta = { 0.0 , 0.0 }
	 * sigma_f = { 0.1 , 0.1 } cost = { 1, 1 }
	 */
	public RobustBoost() {

		this(0.1, new double[] { 0, 0 }, new double[] { 0.1, 0.1 }, new double[] { 1, 1 });
		init(new Configuration());

	}

	public RobustBoost(double epsilon, double[] theta, double[] sigma_f, double[] cost) {

		m_tmpList = new ArrayList<TmpData>();
		m_numExamples = 0;

		m_epsilon = Math.max(epsilon, MIN_EPSILON);
		m_epsilon = Math.min(m_epsilon, MAX_EPSILON);

		m_theta = theta;
		m_sigma_f = sigma_f;

		m_cost = cost;

		m_rho = new double[] { calculateRho(m_sigma_f[0], m_epsilon, m_theta[0], m_cost[0]), calculateRho(m_sigma_f[1], m_epsilon, m_theta[1], m_cost[1]) };

		m_t = 0.0;
		m_old_t = 0.0;

		m_last_ds = 0;
		m_last_dt = 0;

	}

	/**
	 * @see jboost.booster.Booster#init(jboost.controller.Configuration)
	 */
	public void init(Configuration config) {

		m_epsilon = Math.max(config.getDouble("rb_epsilon", m_epsilon), MIN_EPSILON);
		m_epsilon = Math.min(m_epsilon, MAX_EPSILON);

		double theta = config.getDouble("rb_theta", m_theta[0]);
		double sigma_f = config.getDouble("rb_sigma_f", m_sigma_f[0]);

		m_theta[0] = theta;
		m_theta[1] = theta;

		m_theta[0] = config.getDouble("rb_theta_0", m_theta[0]);
		m_theta[1] = config.getDouble("rb_theta_1", m_theta[1]);

		m_sigma_f[0] = sigma_f;
		m_sigma_f[1] = sigma_f;

		m_sigma_f[0] = config.getDouble("rb_sigma_f_0", m_sigma_f[0]);
		m_sigma_f[1] = config.getDouble("rb_sigma_f_1", m_sigma_f[1]);

		m_cost[0] = config.getDouble("rb_cost_0", m_cost[0]);
		m_cost[1] = config.getDouble("rb_cost_1", m_cost[1]);

		m_t = config.getDouble("rb_t", m_t);

		m_rho = new double[] { 
				calculateRho(m_sigma_f[0], m_epsilon, m_theta[0], m_cost[0]), 
				calculateRho(m_sigma_f[1], m_epsilon, m_theta[1], m_cost[1]) };

		m_potentialSlack = config.getDouble("rb_potentialSlack", m_potentialSlack);

	}

	/**
	 * Add an example to the data set of this booster
	 * 
	 * @param index
	 * @param label
	 * @param weight
	 *            Sample weight
	 * @param margin
	 */
	public void addExample(int index, Label label, double weight, double margin) {
		int l = label.getValue();
		String failed = null;

		if (index == m_numExamples) {
			m_numExamples++;
			m_tmpList.add(new TmpData(index, (short) l, weight, margin));
		}
		else {
			failed = getClass().getName() + ".addExample received index " + index + ", when it expected index " + m_numExamples;
		}



		if (failed != null) {
			throw new IllegalArgumentException(failed);
		}
	}



	/** reset the booster */
	public void clear() {
		m_labels = null;
		m_margins = null;
		m_potentials = null;
		m_weights = null;
		m_oldWeights = null;
		m_sampleWeights = null;
		m_tmpList.clear();
		m_numExamples = 0;
		m_totalWeight = 0;
		m_t = 0;
		m_last_ds = 0;
		m_last_dt = 0;
	}

	/**
	 * Computes m_margins, m_labels, m_weights, m_sampleWeights and m_totalWeights
	 */
	public void finalizeData() {
		m_margins = new double[m_numExamples];
		m_weights = new double[m_numExamples];
		m_oldWeights = new double[m_numExamples];
		m_potentials = new double[m_numExamples];
		m_labels = new short[m_numExamples];
		m_sampleWeights = new double[m_numExamples];
		m_totalWeight = 0.0;

		for (int i = 0; i < m_tmpList.size(); i++) {
			TmpData a = (TmpData) m_tmpList.get(i);
			int index = a.getIndex();
			m_margins[index] = a.getMargin();
			m_labels[index] = a.getLabel();
			m_weights[index] = m_oldWeights[index] = calculateWeight(index, m_margins[index], m_t);
			m_potentials[index] = calculatePotential(index, m_margins[index], m_t);
			m_sampleWeights[index] = a.getWeight();
			m_totalWeight += m_weights[index] * a.getWeight();
		}

		m_tmpList.clear(); // free the memory
	}

	/**
	 * Return the theoretical bound on the training error.
	 */
	public double getTheoryBound() {
		double sum_potential = 0.0;
		for (int i = 0; i < m_numExamples; i++) {
			sum_potential += m_potentials[i];
		}
		return sum_potential / m_numExamples;
	}





	/**
	 * @return current value of rho
	 */
	public double[] getRho() {
		return m_rho;
	}

	/**
	 * @return current value of sigma_f
	 */
	public double[] getSigmaF() {
		return m_sigma_f;
	}

	/**
	 * @return current value of theta
	 */
	public double[] getTheta() {
		return m_theta;
	}

	/**
	 * @return current time in RobustBoost training process
	 */
	public double getCurrentTime() {
		return m_t;
	}



	/** output contents of this booster as a human-readable string */
	public String toString() {
		String s = getClass().getName() + ". No of examples = " + m_numExamples;
		s += "\nindex\tmargin\tweight\told weight\tsample weight\tlabel\n";
		NumberFormat f = new DecimalFormat("0.00");
		for (int i = 0; i < m_numExamples; i++) {
			s +=
				"  " + i + " \t " + f.format(m_margins[i]) + " \t " + f.format(m_weights[i]) + " \t " + f.format(m_oldWeights[i]) + " \t"
				+ f.format(m_sampleWeights[i]) + "\t\t" + m_labels[i] + "\n";
		}
		return s;
	}

	public Bag newBag(int[] list) {
		return new RobustBinaryBag(list,this);
	}

	public Bag newBag() {
		return new RobustBinaryBag(this);
	}

	public Bag newBag(Bag bag) {
		return new RobustBinaryBag((RobustBinaryBag) bag,this);
	}

	/*
	 * Returns the predictions associated with a list of bags representing a
	 * partition of the data.
	 */
	public Prediction[] getPredictions(Bag[] b) {

		throw new RuntimeException("RobustBoost.getPrediction(Bag[] b) is called. " + "This should never happen.");

	}

	/**
	 * Returns the predictions associated with a list of bags representing a
	 * partition of the data.
	 */
	public Prediction[] getPredictions(Bag[] bags, int[][] exampleIndex) {
		Prediction[] basePredictions = new Prediction[bags.length];
		for (int i = 0; i < bags.length; i++) {
			basePredictions[i] = ((RobustBinaryBag) bags[i]).calcPrediction();
		}
		return getPredictions(bags, exampleIndex, basePredictions);
	}

	/**
	 * Returns the predictions associated with a list of bags representing a
	 * partition of the data. For RobustBoost, this returns predictions from weak
	 * rules. Their values will get adjusted in update().
	 */
	public Prediction[] getPredictions(Bag[] bags, int[][] exampleIndex, Prediction[] basePredictions) {

		final double EPS = 1E-7;

		Prediction[] predictions = new RobustBinaryPrediction[bags.length];

		for (int i = 0; i < bags.length; i++) {

			// bp = prediction from base classifier where its magnitude
			// represent its confidence
			double bp = basePredictions[i].getClassScores()[1];

			// if bp (confidence of weak learner) is too small or bag is weightless,
			// don't make any prediction
			if (Math.abs(bp) < EPS || bags[i].isWeightless()) {
				predictions[i] = new RobustBinaryPrediction(0.0);
			}
			else {

				predictions[i] = new RobustBinaryPrediction(bp);

				// save init_ds for NewtonSolver
				((RobustBinaryPrediction) predictions[i]).init_ds = Math.abs(((RobustBinaryBag) bags[i]).getAdaBoostAlpha(0.5));

			}
		}

		return predictions;

	}

	/**
	 * Update the examples m_margins and m_weights using the exponential update
	 * 
	 * @param predictions
	 *            values for examples
	 * @param exampleIndex
	 *            the list of examples to update
	 */
	public void update(Prediction[] predictions, int[][] exampleIndex) {

		final double EPS = 1E-7;

		// save old m_weights
		for (int i = 0; i < m_weights.length; i++)
			m_oldWeights[i] = m_weights[i];

		m_old_t = m_t;

		// for each prediction
		for (int i = 0; i < predictions.length; i++) {

			if (!(predictions[i] instanceof RobustBinaryPrediction)) {
				throw new RuntimeException("RobustBoost.update() only works with RobustBinaryPrediction");
			}

			RobustBinaryPrediction rbp = (RobustBinaryPrediction) predictions[i];

			// if already finished, set the rest of the predictions to empty.
			if (isFinished()) {
				rbp.dt = 0;
				rbp.prediction = 0;
				continue;
			}

			double[] value = rbp.getClassScores();

			// if this is a zero prediction or the set is empty, we skip
			// this weak hypothesis
			if (Math.abs(value[1]) < EPS) {
				continue;
			}

			// create a mask indicating if an example is in exampleIndex[i]
			boolean[] mask = new boolean[m_numExamples];
			for (int j = 0; j < exampleIndex[i].length; j++)
				mask[exampleIndex[i][j]] = true;

			boolean foundSolution = false;
			double ds = Double.NaN;
			double dt = Double.NaN;

			// create a solver
			NewtonSolver ns = new NewtonSolver(this, mask, value);
			HeuristicSolver hs = new HeuristicSolver(this, mask, value);

			//    if (ns.canFinishNow()) {

			//    foundSolution = true;
			//    ds = 0;
			//    dt = Math.abs(1 - m_t);

			//    }
			//    else {
			// create a set of starting points for NS
			double init_ds, init_dt;
			double[][] initial_points = new double[3][];

			// #1. go as far in the future as possible
			init_dt = 1 - m_t;
			init_ds = Math.sqrt(init_dt);
			initial_points[0] = new double[] { init_ds, init_dt };

			// #2. alpha in adaboost
			init_ds = rbp.init_ds;
			init_dt = init_ds * init_ds;
			initial_points[1] = new double[] { init_ds, init_dt };

			// #3. most recently used
			init_ds = m_last_ds;
			init_dt = m_last_dt;
			initial_points[2] = new double[] { init_ds, init_dt };

			for (int k = 0; k < initial_points.length; k++) {
				ns.solve(initial_points[k][0], initial_points[k][1]);
				if (ns.isSucceeded()) {
					ds = ns.getDs();
					dt = ns.getDt();
					foundSolution = true;
					break;
				}
			}

			// if failed, try heuristic
			if (!foundSolution) {
				hs.solve();
				ds = hs.getDs();
				dt = hs.getDt();
				foundSolution = hs.isSucceeded();
			}

			// if there is a valid solution
			if (foundSolution && 
					!Double.isNaN(ds) && 
					!Double.isNaN(dt) && 
					dt >= 0) {

				m_last_ds = ds;
				m_last_dt = dt;

				// update m_t
				m_t += dt;

				// update prediction
				rbp.prediction = rbp.prediction * ds;
				rbp.dt = dt;

				value[0] *= ds;
				value[1] *= ds;

				// update m_margins
				double exp_negative_dt = Math.exp(-dt);

				for (int j = 0; j < m_numExamples; j++) {
					if (mask[j]) m_margins[j] = m_margins[j] * exp_negative_dt + value[m_labels[j]];
					else m_margins[j] = m_margins[j] * exp_negative_dt;
					// if (j==0) System.out.println("m_margins[0]=" + m_margins[0]);
				}
			}

			// no solutions found
			else {

				System.out.println("WARNING: Solvers have failed. If time is still increasing, please ignore this warning.");

				m_last_ds = 0;
				m_last_dt = 0;

				rbp.prediction = 0;
				rbp.dt = 0;

			}

			// System.out.println("T = " + m_t);
		}

		// update m_weights and m_potentials
		m_totalWeight = 0;
		for (int j = 0; j < m_numExamples; j++) {
			m_weights[j] = calculateWeight(j, m_margins[j], m_t);
			m_potentials[j] = calculatePotential(j, m_margins[j], m_t);
			m_totalWeight += m_weights[j] * m_sampleWeights[j];
		}

	}

	/**
	 * Stop when the time is really close to 1.0
	 * 
	 * @return
	 */
	public boolean isFinished() {
		return (1 - m_t < 0.001);
	}

	public static double calculateSigmaSquare(double sigma_f, double t) {
		if (t > 1) return sigma_f * sigma_f;
		else return (sigma_f * sigma_f + 1.0) * Math.exp(2.0 * (1.0 - t)) - 1.0;
	}

	public static double calculateSigma(double sigma_f, double t) {
		if (t > 1) return sigma_f;
		else return Math.sqrt(calculateSigmaSquare(sigma_f, t));
	}

	public static double calculateMu(double rho, double theta, double t) {
		if (t > 1) return theta;
		else {
			double rho_2 = 2 * rho;
			return (theta - rho_2) * Math.exp(1.0 - t) + rho_2;
		}
	}

	/**
	 * evaluate potential function at margin m and time t based on rho, theta,
	 * sigma_f
	 * 
	 * @param rho
	 * @param theta
	 * @param sigma_f
	 * @param m
	 * @param t
	 * @return Phi(m,t)
	 */
	public static double calculatePotential(double rho, double theta, double sigma_f, double cost, double m, double t) {
		return cost * Math.min(1.0, 1.0 - erf((m - calculateMu(rho, theta, t)) / calculateSigma(sigma_f, t)));
	}

	public double calculatePotential(int exampleIndex, double m, double t) {

		// get rho, theta, sigma_f based on label
		double rho = m_rho[m_labels[exampleIndex]];
		double theta = m_theta[m_labels[exampleIndex]];
		double sigma_f = m_sigma_f[m_labels[exampleIndex]];
		double cost = m_cost[m_labels[exampleIndex]];

		return calculatePotential(rho, theta, sigma_f, cost, m, t);
	}

	/**
	 * evaluate weight function at margin m and time t based on rho, theta,
	 * sigma_f
	 * 
	 * @param rho
	 * @param theta
	 * @param sigma_f
	 * @param m
	 * @param t
	 * @return w(m,t)
	 */
	public static double calculateWeight(double rho, double theta, double sigma_f, double cost, double m, double t) {

		double mu_t = calculateMu(rho, theta, t);
		double sigma_t_sq = calculateSigmaSquare(sigma_f, t);

		if (m > mu_t) {
			double m_minus_mu_t = m - mu_t;
			return cost * Math.exp(-((m_minus_mu_t) * (m_minus_mu_t)) / (sigma_t_sq));
		}
		else return 0.0;
	}

	public double calculateWeight(int exampleIndex, double m, double t) {

		// get rho, theta, sigma_f based on label
		double rho = m_rho[m_labels[exampleIndex]];
		double theta = m_theta[m_labels[exampleIndex]];
		double sigma_f = m_sigma_f[m_labels[exampleIndex]];
		double cost = m_cost[m_labels[exampleIndex]];

		return calculateWeight(rho, theta, sigma_f, cost, m, t);

	}

	public double calculateWeight(Label label, double m, double t) {

		// get rho, theta, sigma_f based on label
		double rho = m_rho[label.getValue()];
		double theta = m_theta[label.getValue()];
		double sigma_f = m_sigma_f[label.getValue()];
		double cost = m_cost[label.getValue()];

		return calculateWeight(rho, theta, sigma_f, cost, m, t);

	}

	public double calculateWeight(double margin) {
		throw new RuntimeException("calculateWeight(double margin) should never be called.");
	}

	public double getEffectiveNumExamples() {
		double wi = 0, wiSq = 0;
		for (int i = 0; i < m_numExamples; i++) {
			wi += m_weights[i];
			wiSq += m_weights[i] * m_weights[i];
		}
		if (Math.abs(wiSq) < 1e-12) return 0;
		else return wi * wi / wiSq;
	}

	public int getNumExamplesHigherThan(double threshold) {
		int num = 0;
		for (int i = 0; i < m_numExamples; i++) {
			if (m_weights[i] > threshold) num++;
		}
		return num;
	}

	/**
	 * compute rho
	 * 
	 * @return rho
	 */
	private double calculateRho(double sigma_f, double epsilon, double theta, double cost) {

		epsilon /= cost;

		double f1 = Math.sqrt(Math.exp(2.0) * ((sigma_f * sigma_f) + 1.0) - 1.0);
		double f2 = erfinv(1.0 - epsilon);
		double numer = (f1 * f2) + Math.E * theta;
		double denom = 2.0 * (Math.E - 1.0);
		return numer / denom;

	}

	public String getParameters() {
		String ret = "rb_t = " + m_t + "\n";
		ret += "rb_epsilon = " + m_epsilon + "\n";
		ret += "rb_theta_0 = " + m_theta[0] + "\n";
		ret += "rb_theta_1 = " + m_theta[1] + "\n";
		ret += "rb_sigma_f_0 = " + m_sigma_f[0] + "\n";
		ret += "rb_sigma_f_1 = " + m_sigma_f[1] + "\n";
		ret += "rb_cost_0 = " + m_cost[0] + "\n";
		ret += "rb_cost_1 = " + m_cost[1] + "\n";
		ret += "rb_potentialSlack = " + m_potentialSlack;
		return ret;
	}

	/**
	 * Already checked against Matlab. When |z| > 1.0, we approximate using the
	 * Chebyshev fitting formula from
	 * http://www.cs.princeton.edu/introcs/21function/ErrorFunction.java.html When
	 * |z| <= 1.0, we use Taylor expansion at z=0 to approximate. ref:
	 * http://en.wikipedia.org/wiki/Error_function
	 * 
	 * @param z
	 * @return erf(z)
	 */
	public static double erf(double z) {

		if (Math.abs(z) > 1.0) {

			// fractional error in math formula less than 1.2 * 10 ^ -7.
			// although subject to catastrophic cancellation when z in very close to 0
			// from Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2

			double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

			// use Horner's method
			double ans =
				1.0
				- t
				* Math
				.exp(-z
						* z
						- 1.26551223
						+ t
						* (1.00002368 + t
								* (0.37409196 + t
										* (0.09678418 + t
												* (-0.18628806 + t
														* (0.27886807 + t
																* (-1.13520398 + t
																		* (1.48851587 + t
																				* (-0.82215223 + t * (0.17087277))))))))));
			if (z >= 0) return ans;
			else return -ans;

		}
		else {

			// taylor expansion
			double EPS = 1E-7;
			double[] k = new double[] { 1, -3, 10, -42, 216, -1320, 9360, -75600, 685440, -6894720, 76204800, -918086400 };

			double t = 1.0, z_sq = z * z, ans = 0.0;

			for (int i = 0; i < k.length; i++) {
				ans += t / k[i];
				t *= z_sq;
			}

			ans = ans * 2.0 * z / Math.sqrt(Math.PI);

			if (Math.abs(ans) < EPS) return 0.0;
			return ans;

		}
	}

	/**
	 * Already checked against Matlab. This is good enough.
	 * 
	 * @param z
	 * @return erfinv(z)
	 */
	public static double erfinv(double z) {

		double EPS = 1E-7;

		if (z >= 1.0 - EPS) return Double.POSITIVE_INFINITY;
		if (z <= -1.0 + EPS) return Double.NEGATIVE_INFINITY;

		double t = Math.abs(z);

		double a = 0.147;
		double pi = Math.PI;
		double t1 = (2 / (pi * a));
		double t2 = Math.log(1 - t * t);
		double t3 = Math.sqrt((t1 + t2 / 2) * (t1 + t2 / 2) - t2 / a);

		if (z < 0) return -1 * Math.sqrt(-t1 - t2 / 2 + t3);
		else return Math.sqrt(-t1 - t2 / 2 + t3);
	}


	@Override
	public void normalizePrediction(Prediction[] predictions,
			ArrayList<PredictorNode> m_predictors) {
		// --------- RobustBoost ----------//

		for (int i = 0; i < predictions.length; i++) {

			// RobustBoost needs to scale all of the previous
			// hyphothesis by exp(-dt)
			if (predictions[i] instanceof RobustBinaryPrediction) {
				// for every RobustBinaryPrediction added before this one
				// we scale all of them by exp(-dt)
				double dt = ((RobustBinaryPrediction) predictions[i]).getDt();
				double exp_negative_dt = Math.exp(-dt);

				for (int j = 0; j < i; j++) {
					predictions[j].scale(exp_negative_dt);
				}

				// for each prediction before this one
				for (int nodeidx = 0; nodeidx < m_predictors.size(); nodeidx++) {
					PredictorNode cpn = (PredictorNode) m_predictors.get(nodeidx);
					cpn.getPrediction().scale(exp_negative_dt);
				}

			}
			else {
				throw new RuntimeException("RobustBinaryPrediction is expected. This should never happen!");
			}

		}

	}

	public double[] getM_theta() {
		return m_theta;
	}

	public void setM_theta(double[] mTheta) {
		m_theta = mTheta;
	}

	public double getM_t() {
		return m_t;
	}

	public void setM_t(double mT) {
		m_t = mT;
	}

	public double[] getM_sigma_f() {
		return m_sigma_f;
	}

	public void setM_sigma_f(double[] mSigmaF) {
		m_sigma_f = mSigmaF;
	}

	public double[] getM_rho() {
		return m_rho;
	}

	public void setM_rho(double[] mRho) {
		m_rho = mRho;
	}

	public double getM_last_ds() {
		return m_last_ds;
	}

	public void setM_last_ds(double mLastDs) {
		m_last_ds = mLastDs;
	}

	public double getM_last_dt() {
		return m_last_dt;
	}

	public void setM_last_dt(double mLastDt) {
		m_last_dt = mLastDt;
	}

	public double getM_old_t() {
		return m_old_t;
	}

	public void setM_old_t(double mOldT) {
		m_old_t = mOldT;
	}

	public double[] getM_cost() {
		return m_cost;
	}

	public void setM_cost(double[] mCost) {
		m_cost = mCost;
	}

	

}
