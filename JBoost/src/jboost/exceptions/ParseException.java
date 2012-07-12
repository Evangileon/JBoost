package jboost.exceptions;

/** Indicates that an error occured while reading the data file */

// are all the following necessary?
public class ParseException extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = 2234327096261975903L;
long lineNum;

  public ParseException(String errorMessage, long lineNum) {
    super(errorMessage);
    this.lineNum = lineNum;
  }

  public ParseException(String errorMessage) {
    this(errorMessage, 0); // eliminate eventually
  }
}
