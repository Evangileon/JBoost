package jboost.exceptions;

public class BadExaException extends ParseException {

  /**
	 * 
	 */
	private static final long serialVersionUID = -8533558763639721325L;

// is following constructor necessary?
  public BadExaException(String errorMessage, long lineNum) {
    super(errorMessage, lineNum);
  }
}
