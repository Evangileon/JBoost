package jboost.booster.bag;

import jboost.booster.AdaBoost;
import jboost.booster.prediction.BinaryPrediction;

public class AdaBoostBinaryBag extends BinaryBag {


	/** default constructor */
	public AdaBoostBinaryBag(AdaBoost adaBoost) {
		super(adaBoost);
	}

	/** constructor that copies an existing bag */
	public AdaBoostBinaryBag(BinaryBag bag,AdaBoost adaBoost) {
		super(bag,adaBoost);
	}

	/** a constructor that initializes a bag the given list of axamples */
	public AdaBoostBinaryBag(int[] list,AdaBoost adaBoost) {
		super(list,adaBoost);
	}

	public String toString() {
		String s = "AdaBoostBinaryBag.\t w0=" + m_w[0] + "\t w1=" + m_w[1] + "\n";
		return s;
	}

	/**
	 * compute the optimal binary prediction associated with this bag
	 */
	public BinaryPrediction calcPrediction() {
		double smoothFactor = booster.m_epsilon * booster.m_totalWeight;
		double EPS = 1e-50;
		if (Double.isNaN(smoothFactor) || (Math.abs(booster.m_totalWeight) < EPS) || (Math.abs(smoothFactor) < EPS) || Double.isNaN(booster.m_totalWeight)) {
			return new BinaryPrediction(0.0);
		}

		BinaryPrediction p = new BinaryPrediction(m_w[1] == m_w[0] ? 0.0 : // handle
			// case
			// that
			// w0=w1=0
			0.5 * Math.log((m_w[1] + smoothFactor) / (m_w[0] + smoothFactor)));
		return p;
	}

}
