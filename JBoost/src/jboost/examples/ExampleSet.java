/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */
package jboost.examples;

// import java.io.*;
import java.util.ArrayList;

import jboost.Predictor;
import jboost.booster.prediction.Prediction;
import jboost.examples.attributes.Label;
import jboost.monitor.Monitor;

/**
 * Holds a set of examples and the text that describes them
 * 
 * @author Yoav Freund
 * @version $Header:
 *          /cvsroot/jboost/jboost/src/jboost/examples/ExampleSet.java,v 1.4
 *          2009/01/03 05:13:32 aarvey Exp $
 */
public class ExampleSet {

  /**
   * The content of the ExampleSet
   */
  private Example[] exampleSet;

  /**
   * A temporary storage for the examples
   */
  private ArrayList<Example> exampleList;
  private int nextExample; // an internal counter for checking that the
  // indexes are coming in order
  /**
   * The textual description of the fields in an Example
   */
  private ExampleDescription exampleDescription;
  @SuppressWarnings("unused")
private int noOfLabels = 2; // no. of possible m_labels
  private boolean isBinary = true; // identifies the data as binary-labeled

  /** last iteration on which calc* was called */
  private int lastIter = -2;
  private Prediction[] prediction = null;

  /** default constructor */
  public ExampleSet() {
    exampleSet = null;
    exampleDescription = null;
    exampleList = new ArrayList<Example>();
    nextExample = 0;
  }

  /** constructor that gets an exampleDescription */
  public ExampleSet(ExampleDescription exampleDescription) {
    this();
    this.exampleDescription = exampleDescription;
 
    isBinary = true;

  }

  /** add an example */
  public void addExample(int index, Example example) {
    if (index != nextExample) throw new RuntimeException("ExampleSet.addExample: index=" + index + " Expected next index=" + nextExample);
    example.setDescription(exampleDescription);
    exampleList.add(example);
    nextExample++;
  }

  /** finalize the dataset */
  public void finalizeData() {
    exampleSet = (Example[]) exampleList.toArray(new Example[0]);
    prediction = new Prediction[exampleSet.length];
    exampleList.clear(); // free the space
    exampleList = null;
  }

  /** get the description of the fields in an example */
  public ExampleDescription getExampleDescription() {
    return exampleDescription;
  }

  /** get no of examples */
  public int getExampleNo() {
    if (exampleSet == null) return exampleList.size();
    else return exampleSet.length;
  }

  /** get no of examples */
  public int size() {
    return getExampleNo();
  }

  /** get example no. i */
  public Example getExample(int i) {
    if (exampleSet == null) return (Example) exampleList.get(i);
    else return exampleSet[i];
  }

  /**
   * calculate error of a Predictor on this ExampleSet. If curIter equals
   * iteration number from last call to calcError, calcMargins or calcScores,
   * and base != null then previously computed predictions are used. If curIter
   * equals last iteration number plus one and base != null, then base predictor
   * is used to update previously computed predictions. If base predictor is
   * null and in all other cases, combined predictor is used for predictions.
   * 
   * @param curIter
   *            current iteration number
   * @param combined
   *            current combined predictor
   * @param base
   *            last added base predictor
   */
  public double calcError(int curIter, Predictor combined, Predictor base) {
    updatePredictions(curIter, combined, base);
    int size = exampleSet.length;
    if (size == 0) {
      return Double.NaN;
      // throw new RuntimeException("ExampleSet.calcError: ExampleSet is
      // empty");
    }
    double errors = 0.0;
    int i = 0;
    try {
      for (i = 0; i < size; i++) {
        Example x = exampleSet[i];
        errors += x.getLabel().lossOfPrediction(prediction[i].getBestClass());
      }
    }
    catch (Exception e) {
      Monitor.log("in ExampleSet.calcError():" + " got exception on example no " + i,Monitor.LOG_LEVEL_THREE);
      Monitor.log(e.getMessage(),Monitor.LOG_LEVEL_THREE);
      e.printStackTrace();
    }

    return errors / size;
  }

  /**
   * calculate m_margins of a Predictor on this ExampleSet see parameter
   * description at calcError
   */
  public ArrayList<double[]> calcMargins(int curIter, Predictor combined, Predictor base) {
    updatePredictions(curIter, combined, base);
    int size = exampleSet.length;
    if (size == 0) return null;

    ArrayList<double[]> margins = new ArrayList<double[]>();

    int i = 0;
    try {
      for (i = 0; i < size; i++) {
        Example x = exampleSet[i];
        double[] tmp = prediction[i].getMargins(x.getLabel());
        margins.add(tmp);
      }
    }
    catch (Exception e) {
      Monitor.log("in ExampleSet.calcMargins():" + " got exception on example no " + i,Monitor.LOG_LEVEL_THREE);
      Monitor.log(e.getMessage(),Monitor.LOG_LEVEL_THREE);
      e.printStackTrace();
    }

    return margins;
  }

  /**
   * calculate scores of a Predictor on this ExampleSet see parameter
   * description at calcError
   */
  public ArrayList<double[]> calcScores(int curIter, Predictor combined, Predictor base) {
    updatePredictions(curIter, combined, base);
    int size = exampleSet.length;
    if (size == 0) return null;

    ArrayList<double[]> scores = new ArrayList<double[]>();

    int i = 0;
    double[] tmp = null;
    double tmp0 = 0;
    try {
      for (i = 0; i < size; i++) {
        tmp = prediction[i].getClassScores();
        if (isBinary) { // for binary m_labels, keep only one score
          tmp0 = tmp[0];
          tmp = new double[1];
          tmp[0] = tmp0;
        }
        scores.add(tmp);
      }
    }
    catch (Exception e) {
      Monitor.log("in ExampleSet.calcScores():" + " got exception on example no " + i,Monitor.LOG_LEVEL_THREE);
      Monitor.log(e.getMessage(),Monitor.LOG_LEVEL_THREE);
      e.printStackTrace();
    }

    return scores;
  }

  /**
   * updates the saved predictions see parameter description at calcError
   */
  private void updatePredictions(int curIter, Predictor combined, Predictor base) {

    if (base != null) {
      if (curIter == lastIter) return;
      if (curIter == lastIter + 1) {
        for (int i = 0; i < exampleSet.length; i++)
          prediction[i].add(base.predict(exampleSet[i].getInstance()));
        lastIter = curIter;
        return;
      }
    }

    for (int i = 0; i < exampleSet.length; i++)
      prediction[i] =
      // combined.predict(exampleSet[i].getInstance(),curIter);
          combined.predict(exampleSet[i].getInstance());

    lastIter = curIter;
  }

  /** get the binary-m_labels equivalent of the m_labels in this ExampleSet */
  public ArrayList<Boolean[]> getBinaryLabels() {
    int size = exampleSet.length;
    if (size == 0) return null;

    ArrayList<Boolean[]> labels = new ArrayList<Boolean[]>();

    int i = 0;
    Boolean[] tmp = null;
    try {
      for (i = 0; i < size; i++) {
        Label l = exampleSet[i].getLabel();

        tmp = new Boolean[1];
        tmp[0] = new Boolean(l.getValue() == 1);


        labels.add(tmp);
      }
    }
    catch (Exception e) {
      Monitor.log("in ExampleSet.getBinaryLabels():" + " got exception on example no " + i,Monitor.LOG_LEVEL_THREE);
      Monitor.log(e.getMessage(),Monitor.LOG_LEVEL_THREE);
      e.printStackTrace();
    }

    return labels;
  }

  /** print in human-readable format */
  public String toString() {
    String s = "ExampleSet of " + exampleSet.length + " examples\n";
    s += "exampleDescription\n" + exampleDescription;
    for (int i = 0; i < exampleSet.length; i++)
      s += i + "\t" + exampleSet[i];
    return s;
  }

  /** check if there is an attribute whose name is "INDEX" * */
  public boolean hasIndex() {
    return exampleDescription.getIndexDescription() != null;
  }

  /** generate the list of example Indexes * */
  public double[] getIndexes() {
    double[] indexes = new double[exampleSet.length];
    for (int i = 0; i < indexes.length; i++) {
      indexes[i] = (double) exampleSet[i].getIndex();
    }
    return indexes;
  }
}
