package jboost;

import jboost.booster.prediction.Prediction;
import jboost.examples.Instance;
import jboost.exceptions.IncompAttException;

/**
 * An object that can classify <code>Instance</code>s
 */

public interface Predictor {

  /**
   * Given an <code>Instance</code>, generate a <code>Prediction<\code>
   */
  public abstract Prediction predict(Instance instance) throws IncompAttException;

  /**
   * Given an <code>Instance</code>, generate a <code>Prediction<\code>.
   */
  public abstract Prediction predict(Instance instance, int numIters) throws IncompAttException;
}
