package jboost.exceptions;

public class ConfigurationException extends Exception {

  /**
	 * 
	 */
	private static final long serialVersionUID = -3332607527065173865L;
String message;

  public ConfigurationException(String m) {
    message = m;
  }

  public String toString() {
    return (message);
  }
}
