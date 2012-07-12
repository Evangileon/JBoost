package jboost.exceptions;

/**
 * An exception that indicates a problem in instrumenting a ComplexLearner
 * 
 * @author Nigel Duffy
 */
public class InstrumentException extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = -7073356107669035037L;

public InstrumentException(String m) {
    message = m;
  }

  public String getMessage() {
    return (message);
  }

  private String message;
}
