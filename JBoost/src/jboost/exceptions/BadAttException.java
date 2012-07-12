package jboost.exceptions;

// eventually move all to examples and make this private 

/** Attribute cannot be parsed */
public class BadAttException extends ParseException {

  /**
	 * 
	 */
	private static final long serialVersionUID = -4338111046236652813L;
int firstLineNum;

  public BadAttException(String errorMessage, int lineNum, int firstLineNum) {
    super(errorMessage, lineNum);
    this.firstLineNum = firstLineNum;
  }

  public BadAttException(String errorMessage) {
    super(errorMessage);
  }
}
