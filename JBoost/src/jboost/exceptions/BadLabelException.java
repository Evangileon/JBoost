package jboost.exceptions;

/** an Exception that is thrown when a label has in inappropriate value */
public class BadLabelException extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = -3412590719870470222L;

public BadLabelException(String message) {
    this.message = message;
  }

  public String getMessage() {
    return (message);
  }

  private String message;
}
