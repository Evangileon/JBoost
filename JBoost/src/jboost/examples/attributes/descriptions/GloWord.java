package jboost.examples.attributes.descriptions;

/**
 * contains the global token of a word and number of times it appeared in all
 * documents
 */
public class GloWord extends LocWord {

  /**
	 * 
	 */
	private static final long serialVersionUID = 4604236950882205756L;
private int token;

  public GloWord() {
    this.token = 0;
  }

  public GloWord(int token) {
    this.token = token;
  }

  public int getToken() {
    return token;
  }
}