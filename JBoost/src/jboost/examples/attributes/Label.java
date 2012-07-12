package jboost.examples.attributes;


/** Holds the label of an example */
public class Label extends DiscreteAttribute {

  /**
   * constructor
   * 
   * @param value
   *            label's value
   */
  public Label(int v) {
    super(v);
    if (v != 0 && v != 1) {
    	throw new RuntimeException("Label is not 0 nor 1.");
    }
  }


  /**
   * Computes a loss for a predicted Label relative to this Label. Specifically,
   * the loss is 1 minus the number of classes at the intersection of the two
   * Label's, divided by the number of classes in the predicted Label. (An
   * exception occurs if no classes in the predicted Label.)
   */
  /*
  public double lossOfPrediction(Label p) {
    if (p.value >= 0) return (getMultiValue(p.value) ? 0.0 : 1.0);
    else {
      int num_plus = 0;
      int num_overlap = 0;
      for (int i = 0; i < p.values.length; i++) {
        if (p.values[i]) {
          num_plus++;
          if (getMultiValue(i)) num_overlap++;
        }
      }
      if (num_plus == 0) throw new IllegalArgumentException("label must have at " + "least one class in Label.lossOfPrediction");
      return (num_plus - num_overlap) / ((double) num_plus);
    }
  }
  */
  
  public double lossOfPrediction(Label p) {
	if (p.getValue() == getValue()) {
		return 0.0;
	} else {
		return 1.0;
	}
  }
}
