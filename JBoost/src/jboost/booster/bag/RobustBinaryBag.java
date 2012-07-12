package jboost.booster.bag;

import jboost.booster.RobustBoost;
import jboost.booster.prediction.BinaryPrediction;

/**
 * Bag for Robustboost. Same as BinaryBag with calcPrediction rewritten
 * 
 * @author Sunsern Cheamanunkul
 */
public class RobustBinaryBag extends BinaryBag {

	/** default constructor */
	public RobustBinaryBag(RobustBoost robustBoost) {
		super(robustBoost);
	}

	/** constructor that copies an existing bag */
	public RobustBinaryBag(BinaryBag bag,RobustBoost robustBoost) {
		super(bag,robustBoost);
	}

	/** a constructor that initializes a bag the given list of examples */
	public RobustBinaryBag(int[] list,RobustBoost robustBoost) {
		super(list,robustBoost);
	}

	public String toString() {
		String s = "RobustBinaryBag.\t w0=" + m_w[0] + "\t w1=" + m_w[1] + "\n";
		return s;
	}

	/**
	 * compute the optimal binary prediction associated with this bag this might
	 * need to be rewritten
	 */
	public BinaryPrediction calcPrediction() {
		double EPS = 1E-7;
		double totalWeight = m_w[0] + m_w[1];
		if (Double.isNaN(totalWeight) || Math.abs(totalWeight) < EPS || Math.abs(m_w[0] - m_w[1]) < EPS) {
			return new BinaryPrediction(0.0);
		}
		else {
			// {1.0,-1.0}
			double pred = (m_w[1] > 0) ? 1.0 : -1.0; 
			return new BinaryPrediction(pred);
		}
	}

	public double getAdaBoostAlpha(double smooth) {
		double EPS = 1E-7;
		double totalWeight = m_w[0] + m_w[1];
		if (Double.isNaN(totalWeight) || Math.abs(totalWeight) < EPS || Math.abs(m_w[0] - m_w[1]) < EPS) {
			return 0;
		}
		else {
			return 0.5 * Math.log((m_w[1] + smooth) / (m_w[0] + smooth));
		}
	}


}

/** end of class RobustBinaryBag */
