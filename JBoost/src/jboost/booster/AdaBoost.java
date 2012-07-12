/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */
package jboost.booster;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import jboost.atree.PredictorNode;
import jboost.booster.bag.AdaBoostBinaryBag;
import jboost.booster.bag.Bag;
import jboost.booster.prediction.BinaryPrediction;
import jboost.booster.prediction.Prediction;
import jboost.controller.Configuration;
import jboost.examples.attributes.Label;

/**
 * The simplest possible implementation of a booster. confidence-rated adaboost
 * based on equality/inequality of m_labels
 * 
 * @author Yoav Freund
 * @version $Header:
 *          /proj/gene/cvs-repository/jboost/src/jboost/booster/AdaBoost.java,v
 *          1.2 2003/10/01 18:36:15 freund Exp $
 */
public class AdaBoost extends AbstractAdaBoost {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2001004191738423494L;

	/**
	 * The predictions from a hypothesis for an iteration. HACK: This should
	 * eventually be removed for efficiency concerns.
	 */
	protected double[] m_hypPredictions;

	/** */
	protected int[] m_posExamples;
	/** */
	protected int[] m_negExamples;
	/** */
	protected int m_numPosExamples;
	/** */
	protected int m_numNegExamples;

	/** Constant for positive label for cost sensitive stuff */
	protected final short POSITIVE_LABEL = 0;
	/** Constant for negative label for cost sensitive stuff */
	protected final short NEGATIVE_LABEL = 1;
	/** Constant for non cost sensitive stuff */
	protected final short NO_LABEL = -99;





	protected double m_smooth;


	/**
	 * default constructor
	 */
	public AdaBoost() {
		this(0.0);
	}

	/**
	 * Constructor which takes a smoothing factor
	 * 
	 * @param smooth
	 *            "smoothing" factor
	 */
	public AdaBoost(double smooth) {
		m_tmpList = new ArrayList<TmpData>();
		m_numExamples = 0;
		m_smooth = smooth;
		init(new Configuration());
	}

	/**
	 * @see jboost.booster.Booster#init(jboost.controller.Configuration)
	 */
	public void init(Configuration config) {
		m_smooth = config.getDouble(PREFIX + "smooth", 0.5);
	}


	/**
	 * Add an example to the data set of this booster
	 * 
	 * @param index
	 * @param label
	 * @param weight
	 * @param margin
	 */
	public void addExample(int index, Label label, double weight, double margin) {
		int l = label.getValue();
		@SuppressWarnings("unused")
		String failed = null;
		if (index == m_numExamples) {
			m_numExamples++;
			m_tmpList.add(new TmpData(index, (short) l, weight, margin));
			if (l == POSITIVE_LABEL) m_numPosExamples++;
		}
		else {
			// XXX DJH: determine class name at runtime
			failed = getClass().getName() + ".addExample received index " + index + ", when it expected index " + m_numExamples;
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
	}

	protected void finalizeData(double defaultWeight) {
		m_margins = new double[m_numExamples];
		m_weights = new double[m_numExamples];
		m_oldWeights = new double[m_numExamples];
		m_potentials = new double[m_numExamples];
		m_labels = new short[m_numExamples];
		m_sampleWeights = new double[m_numExamples];
		m_epsilon = m_smooth / m_numExamples;
		m_posExamples = new int[m_numPosExamples];
		m_numNegExamples = m_numExamples - m_numPosExamples;
		m_negExamples = new int[m_numNegExamples];

		int m_posIndex = 0, m_negIndex = 0;
		for (int i = 0; i < m_tmpList.size(); i++) {
			TmpData a = (TmpData) m_tmpList.get(i);
			int index = a.getIndex();
			m_margins[index] = a.getMargin();
			m_weights[index] = m_oldWeights[index] = calculateWeight(m_margins[index]);
			m_labels[index] = a.getLabel();
			if (a.getLabel() == POSITIVE_LABEL) m_posExamples[m_posIndex++] = index;
			else if (a.getLabel() == NEGATIVE_LABEL) m_negExamples[m_negIndex++] = index;
			else {
				// XXX DJH: determine class name at runtime
				System.err.println("Label of example is unknown to " + this.getClass().getName());
				System.exit(2);
			}

			m_sampleWeights[index] = a.getWeight();
			m_totalWeight += defaultWeight * a.getWeight();

		}
		m_tmpList.clear(); // free the memory
	}

	public void finalizeData() {
		finalizeData(1.0);
	}

	/**
	 * Return the theoretical bound on the training error.
	 */
	public double getTheoryBound() {
		return m_totalWeight / m_numExamples;
	}


	/** output AdaBoost contents as a human-readable string */
	public String toString() {
		// XXX DJH: determine class name at runtime
		String s = getClass().getName() + ". No of examples = " + m_numExamples + ", m_epsilon = " + m_epsilon;
		s += "\nindex\tmargin\tweight\told weight\tlabel\n";
		NumberFormat f = new DecimalFormat("0.00");
		for (int i = 0; i < m_numExamples; i++) {
			s +=
				"  " + i + " \t " + f.format(m_margins[i]) + " \t " + f.format(m_weights[i]) + " \t " + f.format(m_oldWeights[i]) + " \t"
				+ f.format(m_sampleWeights[i]) + "\t\t" + m_labels[i] + "\n";
		}
		return s;
	}

	public Bag newBag(int[] list) {
		return new AdaBoostBinaryBag(list,this);
	}

	public Bag newBag() {
		return new AdaBoostBinaryBag(this);
	}

	public Bag newBag(Bag bag) {
		return new AdaBoostBinaryBag((AdaBoostBinaryBag) bag,this);
	}

	/**
	 * Returns the prediction associated with a bag representing a subset of the
	 * data.
	 */
	public Prediction getPrediction(Bag b) {
		return ((AdaBoostBinaryBag) b).calcPrediction();
	}

	/*
	 * Returns the predictions associated with a list of bags representing a
	 * partition of the data.
	 */
	public Prediction[] getPredictions(Bag[] b) {
		Prediction[] p = new BinaryPrediction[b.length];
		for (int i = 0; i < b.length; i++) {
			p[i] = ((AdaBoostBinaryBag) b[i]).calcPrediction();
		}
		return p;
	}

	/**
	 * @param z -
	 *            any double
	 * @return If z is negative return -1 else return 1
	 */
	public static double sign(double z) {
		if (Double.compare(z, 0.0) == 0) {
			return 1.0;
		}
		else if (Double.compare(z, -0.0) == 0) {
			return -1.0;
		}

		if (z > 0) {
			return 1.0;
		}
		else {
			return -1.0;
		}
	}

	/**
	 * Get the "step" of the hypothesis on an example. The step is defined as $y_j *
	 * h_i(x_j)$ for example j and hypothesis i.
	 * 
	 * @param simple_label -
	 *            The label of the example as given by m_labels
	 * @param hyp_pred -
	 *            The hypothesized value of the example
	 * @return +1 if label matches hyp, -1 if label doesn't match hyp, 0 if no hyp
	 */
	// XXX DJH: changed from 'public' to 'protected'
	protected double getStep(short simple_label, double hyp_pred) {
		double step = getLabel(simple_label) * hyp_pred;
		double EPS = 0.000001;
		if (Math.abs(step) < EPS) return 0.0;
		return sign(step);
	}

	// XXX DJH: changed from 'public' to 'protected'
	protected double getLabel(short simple_label) {
		return sign(-simple_label + 0.5);
	}

	protected double getHypErr(Bag[] bags, int[][] exampleIndex) {
		@SuppressWarnings("unused")
		double hyp_err = 0.0;
		double gamma = 0.0;
		double total_weight = 0.0;

		// Keep track of which examples had hypotheses associated with them.
		boolean[] examplesWithHyp = new boolean[m_margins.length];
		m_hypPredictions = new double[m_margins.length];
		for (int i = 0; i < exampleIndex.length; i++) {
			int[] index = exampleIndex[i];
			AdaBoostBinaryBag b = (AdaBoostBinaryBag) bags[i];
			for (int j = 0; j < index.length; j++) {
				int example = index[j];
				m_hypPredictions[example] = b.calcPrediction().getClassScores()[0];
			}
		}

		int numExamplesWithHyps = 0;
		// Get all examples that have a hypothesis associated with them
		for (int i = 0; i < exampleIndex.length; i++) {
			int[] indexes = exampleIndex[i];
			for (int j = 0; j < indexes.length; j++) {
				int example = indexes[j];
				examplesWithHyp[example] = true;
				numExamplesWithHyps += 1;

				double step = getStep(m_labels[example], m_hypPredictions[example]);
				gamma += m_weights[example] * step;
				if (step < 0) // We got it wrong
					hyp_err += 1;
			}
		}

		// Get all examples that have no hypothesis associated with them.
		for (int i = 0; i < m_margins.length; i++) {
			total_weight += m_weights[i];
			if (!examplesWithHyp[i]) {
				m_hypPredictions[i] = 0;
				// System.out.println("m_hypPredictions[" + i + "," + example + "]: " +
				// 0 + " (No hyp for example " + example + ")");
			}
		}

		hyp_err /= numExamplesWithHyps;
		gamma /= (double) total_weight;

		if (numExamplesWithHyps > 0) {
			System.out.println("Num Examples with predictions: " + numExamplesWithHyps + "/" + m_margins.length);
			System.out.println("Gamma: " + gamma);
		}
		return gamma;
	}

	/**
	 * Returns the predictions associated with a list of bags representing a
	 * partition of the data. AdaBoost does not use the partition in exampleIndex.
	 */
	public Prediction[] getPredictions(Bag[] bags, int[][] exampleIndex) {
		// Code to see how often the splitting partition predicts the same way on
		// both sides
		/*
		 * if (bags.length > 1) { BinaryBag [] bbags = new BinaryBag[2]; bbags[0] =
		 * (BinaryBag)bags[0]; bbags[1] = (BinaryBag)bags[1]; for (int i= 0; i <
		 * bags.length; i++) { if ( (bbags[0].getWeights()[0] >
		 * bbags[0].getWeights()[1] && bbags[1].getWeights()[0] >
		 * bbags[1].getWeights()[1]) || (bbags[0].getWeights()[0] <
		 * bbags[0].getWeights()[1] && bbags[1].getWeights()[0] <
		 * bbags[1].getWeights()[1]) ) System.out.print("Bag i: " + bags[i]); } }
		 */
		return getPredictions(bags);
	}

	/**
	 * AdaBoost uses e^(-margin) as the weight calculation
	 */
	public double calculateWeight(double margin) {
		return Math.exp(-1 * margin);
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
		// save old m_weights
		for (int i = 0; i < m_weights.length; i++)
			m_oldWeights[i] = m_weights[i];

		// update m_weights and m_margins
		for (int i = 0; i < exampleIndex.length; i++) {
			double p = predictions[i].getClassScores()[1];
			double[] value = new double[] { -p, p };
			int[] indexes = exampleIndex[i];
			for (int j = 0; j < indexes.length; j++) {
				int example = indexes[j];
				m_margins[example] += value[m_labels[example]];
				m_totalWeight -= m_weights[example] * m_sampleWeights[example];
				m_weights[example] = calculateWeight(m_margins[example]);
				m_totalWeight += m_weights[example] * m_sampleWeights[example];
			}
		}
	}






	@Override
	public void normalizePrediction(Prediction[] predictions,
			ArrayList<PredictorNode> mPredictors) {
		//do nothing

	}

	public int getM_numPosExamples() {
		return m_numPosExamples;
	}

	public int getM_numNegExamples() {
		return m_numNegExamples;
	}

	public int[] getM_posExamples() {
		return m_posExamples;
	}

	public int[] getM_negExamples() {
		return m_negExamples;
	}


}
/** end of class AdaBoost */
