package jboost.examples;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import jboost.examples.attributes.descriptions.GloWord;



/** defines a mapping from words to tokens */
public class WordTable implements Serializable {

  /**
	 * 
	 */
	private static final long serialVersionUID = -4957272333326792666L;
public HashMap<String,GloWord> map; // mapping from string to int
  public int size; // # unique words seen so far (starts with 0)
  public boolean frozen; // true if no more tokens
  public Vector<String> words; // mapping from int to string

  public void setFrozen(boolean flag) {
    frozen = flag;
  }

  public boolean getFrozen() {
    return frozen;
  }

  WordTable() {
    map = new HashMap<String,GloWord>();
    size = 0;
    frozen = false;
    words = new Vector<String>();
  }

  public String toString() {
    return "Size: " + size + "\nMap: " + map.toString() + "\nWords: " + words.toString() + "\nfrozen: " + Boolean.toString(frozen);
  }

  public void update(WordTable newTable) {
    map = newTable.map;
    size = newTable.size;
    frozen = newTable.frozen;
    words = newTable.words;
  }

  // shared WordTable
  public static WordTable globalTable = new WordTable();

}
