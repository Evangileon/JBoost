package jboost.examples.attributes;

import jboost.monitor.Monitor;


/** Discrete-valued attribute */
public class DiscreteAttribute extends Attribute {

  private int value;

  public DiscreteAttribute(int value) {
    this.value = value;
    setDefined();
  }

  public DiscreteAttribute() {
    defined = false;
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    if (isDefined() == false) Monitor.log("UNDEFINED",Monitor.LOG_LEVEL_THREE);
    return this.isDefined() ? String.valueOf(value) : "undefind";
  }
}
