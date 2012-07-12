package jboost.booster;

import java.util.List;

import jboost.examples.attributes.Label;

/**
 * This class holds shared come between AdaBoost and RobustBoost.
 * @author Tassapol Athiapinya (extractor)
 *
 */
public abstract class AbstractAdaBoost extends AbstractBooster {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9051402817596817634L;
	/** permanent storage for m_labels */
	public short[] m_labels;
	/** permanent storage for m_margins */
	public double[] m_margins;
	/** permanent storage for example m_weights */
	public double[] m_weights;
	/** permanent storage for example's old m_weights */
	public double[] m_oldWeights;
	/** Records the potentials. Similar to m_margins and m_weights. */
	public double[] m_potentials;
	/** sampling weights for the examples */
	public double[] m_sampleWeights;
	/** total weight of all examples */
	public double m_totalWeight;
	/** number of examples in training set */
	public int m_numExamples = 0;
	/** temporary location for storing the examples as they are read in */
	protected List<TmpData> m_tmpList;
	/** epsilon, fraction of allowed errors */
	public double m_epsilon;

	/**
	 * Add an example to the dataset Default the weight for this example to 1 If
	 * this method is used, then this booster will assume that all the sample
	 * weights are 1
	 * 
	 * @param index
	 * @param label
	 */
	public void addExample(int index, Label label) {
		addExample(index, label, 1);
	}

	/**
	 * Add an example to the data set of this booster
	 * 
	 * @param index
	 * @param label
	 * @param weight
	 *            Sample weight
	 */
	public void addExample(int index, Label label, double weight) {
		addExample(index, label, weight, 0.0);
	}



	public abstract void clear();

	/**
	 * Returns the margin values of the training examples.
	 */
	public double[][] getMargins() {
		double[][] r = new double[m_numExamples][1];
		for (int i = 0; i < m_numExamples; i++)
			r[i][0] = m_margins[i];
		return r;
	}

	/**
	 * Returns the boosting weights of the training examples.
	 */
	public double[][] getWeights() {
		double[][] r = new double[m_numExamples][1];
		for (int i = 0; i < m_numExamples; i++)
			r[i][0] = m_weights[i];
		return r;
	}

	/**
	 * Returns the potential values of the training examples.
	 */
	public double[][] getPotentials() {
		double[][] r = new double[m_numExamples][1];
		for (int i = 0; i < m_numExamples; i++)
			r[i][0] = m_potentials[i];
		return r;
	}

	/**
	 * Returns the number of training examples.
	 */
	public int getNumExamples() {
		return m_numExamples;
	}

	/**
	 * @return total weight of examples
	 */
	public double getTotalWeight() {
		return m_totalWeight;
	}

	/**
	 * Returns a string with all the weights, margins, etc
	 */
	public String getExampleData() {
		StringBuffer ret = new StringBuffer("");
		ret.append(getParamString());
		for (int i = 0; i < m_margins.length; i++) {
			ret.append(String.format("[%d];[%.4f];[%.4f];[%.4f];\n", m_labels[i], m_margins[i], m_weights[i], m_potentials[i]));
		}
		return ret.toString();
	}

	public String getParamString() {
		String ret = String.format("None (" + getClass().getName() + ")");
		return ret;
	}


	public List<TmpData> getM_tmpList() {
		return m_tmpList;
	}



	public int getM_numExamples() {
		return m_numExamples;
	}

	public short[] getM_labels() {
		return m_labels;
	}



	public double[] getM_margins() {
		return m_margins;
	}

	public double[] getM_weights() {
		return m_weights;
	}

	public double[] getM_potentials() {
		return m_potentials;
	}

	public double[] getM_oldWeights() {
		return m_oldWeights;
	}

	public double[] getM_sampleWeights() {
		return m_sampleWeights;
	}


	public double getM_totalWeight() {
		return m_totalWeight;
	}

	public void setM_labels(short[] mLabels) {
		m_labels = mLabels;
	}

	public void setM_weights(double[] mWeights) {
		m_weights = mWeights;
	}

	public void setM_sampleWeights(double[] mSampleWeights) {
		m_sampleWeights = mSampleWeights;
	}

	public void setM_totalWeight(double mTotalWeight) {
		m_totalWeight = mTotalWeight;
	}

	public void setM_oldWeights(double[] mOldWeights) {
		m_oldWeights = mOldWeights;
	}

	public void setM_margins(double[] mMargins) {
		m_margins = mMargins;
	}

	public void setM_epsilon(double mEpsilon) {
		m_epsilon = mEpsilon;
	}

	public double getM_epsilon() {
		return m_epsilon;
	}

	public void setM_numExamples(int mNumExamples) {
		m_numExamples = mNumExamples;
	}

	public void setM_potentials(double[] mPotentials) {
		m_potentials = mPotentials;
	}
	  
	
	 
}
