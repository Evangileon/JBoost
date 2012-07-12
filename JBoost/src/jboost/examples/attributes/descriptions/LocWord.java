package jboost.examples.attributes.descriptions;

import java.io.Serializable;

/** contains the number of times a word appeared in current document */
public class LocWord implements Serializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = 6906772716560305303L;
int numApp; // number of times word appeared

  public LocWord() {
    this.numApp = 1;
  }

  public void inc() {
    numApp++;
  }

  public int getNumApp() {
    return numApp;
  }
}
