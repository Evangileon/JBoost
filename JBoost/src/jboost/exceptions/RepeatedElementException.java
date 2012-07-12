package jboost.exceptions;

public class RepeatedElementException extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = -2668839884515335718L;

public RepeatedElementException(String message) {
    this.message = message;
  }

  public String getMessage() {
    return (message);
  }

  private String message;
}
