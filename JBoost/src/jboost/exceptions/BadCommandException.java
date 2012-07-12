package jboost.exceptions;

/**
 * This exception gets thrown if a command cannot be parsed sensibly.
 */
public class BadCommandException extends RuntimeException {

  /**
	 * 
	 */
	private static final long serialVersionUID = -3532523323120212170L;

public BadCommandException(String m) {
    message = m;
  }

  public String getMessage() {
    return (message);
  }

  private String message;
}
