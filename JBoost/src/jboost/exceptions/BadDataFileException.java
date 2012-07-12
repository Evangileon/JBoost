package jboost.exceptions;

public class BadDataFileException extends ParseException {

  /**
	 * 
	 */
	private static final long serialVersionUID = -6414200126713150548L;

public BadDataFileException(String errorMessage, long lineNum) {
    super(errorMessage, lineNum);
  }
}
