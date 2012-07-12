package jboost.learner.splitters;

import jboost.examples.Instance;
import jboost.examples.attributes.Attribute;
import jboost.examples.attributes.IntegerAttribute;
import jboost.examples.attributes.RealAttribute;
import jboost.examples.attributes.descriptions.AttributeDescription;
import jboost.exceptions.IncompAttException;
import jboost.learner.SplitterType;
import jboost.learner.Summary;

/**
 * Splits examples according to whether the value of a single
 * {@link jboost.examples.attributes.RealAttribute} is smaller than a fixed threshold.
 */
public class InequalitySplitter extends Splitter {

  /**
	 * 
	 */
	private static final long serialVersionUID = -5709229322751218388L;
private int index;
  private double threshold;

  public InequalitySplitter(int index, double threshold, AttributeDescription ad) {
    this.index = index;
    this.threshold = threshold;
    desc = ad;
    m_type = SplitterType.INEQUALITY_SPLITTER;
  }

  /** return a copy of this */
  public Object clone() {
    return new InequalitySplitter(index, threshold, desc);
  }

  public double getThreshold() {
    return threshold;
  }

  public int getIndex() {
    return index;
  }

  public String toString() {
    try {
      return "InequalitySplitter. " + desc.getAttributeName() + " < " + threshold;
    }
    catch (Exception e) {
    }
    return (null);
  }

  /** Returns a String containing Java code. */
  public String toJava(String fname) {
    return (null);
  }

  /** Returns a String containing C code. */
  public String toC(String fname) {
    return (null);
  }

  /** Get a summary. */
  public Summary getSummary() {
    Summary retval = new Summary();
    retval.index = index;
    retval.type = Summary.LESS_THAN;
    retval.val = new Double(threshold);
    return (retval);
  }

  public int eval(Instance instance) throws IncompAttException {
    Attribute t = instance.getAttribute(index);
    if (t == null) {
      return (-1);
    }
    try {

      if (t instanceof RealAttribute) {
        RealAttribute a = (RealAttribute) t; // try downcasting
        return ((a.getValue() > threshold) ? 1 : 0);
      }
      else if (t instanceof IntegerAttribute) {
        IntegerAttribute a = (IntegerAttribute) t; // try downcasting
        return ((a.getValue() > threshold) ? 1 : 0);
      }
      else {
        throw new IncompAttException("InequalitySplitter Error:", index, t.getClass());
      }
    }
    catch (ClassCastException e) {
      throw new IncompAttException("InequalitySplitter Error:", index, t.getClass());
    }
  }

  public boolean equals(Splitter sp) {
    InequalitySplitter isp;
    try {
      isp = (InequalitySplitter) sp;
    }
    catch (ClassCastException e) {
      return (false);
    }
    if (index == isp.index && threshold == isp.threshold) return (true);
    else return (false);
  }
}
