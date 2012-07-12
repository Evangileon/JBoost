package jboost.atree;

import jboost.CandidateSplit;



/** A description of a candidate splitter */
public class AtreeCandidateSplit extends CandidateSplit {

  public boolean updateRoot;
  private int pNode;

  /** the predictor node in the atree which owns the builder */
  public int getPredictorNode() {
    return (pNode);
  }

  /** Constructor to convert from a {@link CandidateSplit} */
  public AtreeCandidateSplit(int pn, CandidateSplit b) {
    pNode = pn;
    builder = b.getBuilder();
    splitter = b.getSplitter();
    partition = b.getPartition();
    loss = b.getLoss();
    updateRoot = false;
  }

  /** Constructor to specify that only the root prediction should be updated. */
  public AtreeCandidateSplit(double loss) {
    updateRoot = true;
    this.loss = loss;
    pNode = 0;
    builder = null;
    splitter = null;
    partition = null;
  }
}