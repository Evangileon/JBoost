package jboost.booster.bag;

import jboost.booster.AbstractAdaBoost;
import jboost.booster.prediction.BinaryPrediction;


/**
 * This is the definition of a bag for AdaBoost. The two m_labels are
 * internally referred to as 0 or 1. The bag maintains the total weight of
 * examples labeled 0 and the total weight of examples labeled 1. This bag
 * uses the weights and labels stored in the booster.
 * 
 * @author Yoav Freund
 */

public abstract class BinaryBag extends Bag {

	protected AbstractAdaBoost booster;

	/** default constructor */
	protected BinaryBag(AbstractAdaBoost booster) {
		this.booster = booster;
		m_w = new double[2];
		reset();
	}

	/** constructor that copies an existing bag */
	protected BinaryBag(BinaryBag bag,AbstractAdaBoost booster) {
		this.booster = booster;
		m_w = new double[2];
		m_w[0] = bag.m_w[0];
		m_w[1] = bag.m_w[1];
	}

	/** a constructor that initializes a bag the given list of examples */
	protected BinaryBag(int[] list,AbstractAdaBoost booster) {
		this.booster = booster;
		m_w = new double[2];
		reset();
		this.addExampleList(list);
	}

	/**
	 * Resets the bag to empty
	 */
	public void reset() {
		m_w[0] = 0.0;
		m_w[1] = 0.0;
	}

	/**
	 * Checks if the bag has any weight.
	 */
	public boolean isWeightless() {
		double EPS = 0.0000001;
		if (m_w[0] < EPS && m_w[1] < EPS) {
			return true;
		}
		return false;
	}

	/**
	 * Adds one example index to the bag. Update the weights in this bag using
	 * the weights from the booster The example index is used to find the label
	 * and weight for this example
	 * 
	 * @param index
	 *            the example that is being added to this bag. The index refers
	 *            to the booster's internal data structures
	 */
	public void addExample(int index) {
		m_w[booster.m_labels[index]] += booster.m_weights[index] * booster.m_sampleWeights[index];
	}

	/**
	 * Subtracts one example index from the bag.
	 */
	public void subtractExample(int i) {
		if ((m_w[booster.m_labels[i]] -= booster.m_weights[i] * booster.m_sampleWeights[i]) < 0.0) m_w[booster.m_labels[i]] = 0.0;
	}

	/**
	 * Adds the given bag to this one. It is assumed that the two bags are
	 * disjoint and the same type.
	 */
	public void addBag(Bag b) {
		m_w[0] += ((BinaryBag) b).m_w[0];
		m_w[1] += ((BinaryBag) b).m_w[1];
	}

	/**
	 * Subtracts the given bag from this one. It is assumed that the bag being
	 * subtracted is a subset of the other one, and that the two bags are the
	 * same type.
	 */
	public void subtractBag(Bag b) {
		if ((m_w[0] -= ((BinaryBag) b).m_w[0]) < 0.0) m_w[0] = 0.0;
		if ((m_w[1] -= ((BinaryBag) b).m_w[1]) < 0.0) m_w[1] = 0.0;
	}

	/**
	 * Copies a given bag of the same type into this one.
	 */
	public void copyBag(Bag b) {
		m_w[0] = ((BinaryBag) b).m_w[0];
		m_w[1] = ((BinaryBag) b).m_w[1];
	}

	/**
	 * Updates the weight of a single example contained in this bag. In other
	 * words, subtracts its old weight and adds its new weight.
	 */
	public void refresh(int i) {
		short label = booster.m_labels[i];
		if ((m_w[label] += booster.m_weights[i] - booster.m_oldWeights[i]) < 0.0) m_w[label] = 0.0;
	}

	/**
	 * Computes the loss using the following formula: 2*Sqrt(w_0 * w_1) - w_0 -
	 * w_1 Where w_0 and w_1 are the weights of the 0 and 1 labeled examples,
	 * respectively If w_0 and w_1 are equal, then the loss will return 0.
	 * 
	 * @return Z the result of the computation
	 */
	public double getLoss() {
		return 2 * Math.sqrt(m_w[0] * m_w[1]) - m_w[0] - m_w[1];
	}

	public abstract BinaryPrediction calcPrediction();
	public abstract String toString();

	public double getM_w0() {
		return m_w[0];
	}

	public double getM_w1() {
		return m_w[1];
	}

	public void setM_w0(double w0) {
		m_w[0] = w0;
	}

	public void setM_w1(double w1) {
		m_w[1] = w1;
	}
}
/** end of class BinaryBag */