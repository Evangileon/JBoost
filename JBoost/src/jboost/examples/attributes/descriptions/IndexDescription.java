package jboost.examples.attributes.descriptions;

import jboost.controller.Configuration;
import jboost.examples.attributes.Attribute;
import jboost.examples.attributes.IntegerAttribute;
import jboost.exceptions.BadAttException;

/**
 * The description for a private weight of each example.
 */
public class IndexDescription extends IntegerDescription {

  /**
	 * 
	 */
	private static final long serialVersionUID = -8701916385950760878L;

public IndexDescription(String name, Configuration config) throws ClassNotFoundException {
    super(name, config);
  }

  /**
   * Reads an id if anything goes wrong, return -1
   * 
   * @param id
   *            the string representation of the id
   * @return Attribute the IntegerAttribute corresponding to the id
   */
  public Attribute str2Att(String id) throws BadAttException {

    int att = -1; // initialized because try complains otherwise.

    if (id != null) {
      id = id.trim();
      if (id.length() != 0) {
        try {
          att = Integer.parseInt(id);
        }
        catch (NumberFormatException nfe) {
          throw new BadAttException(id + " is not an integer", 0, 0);
        }
      }
    }
    return new IntegerAttribute(att);
  }

}
