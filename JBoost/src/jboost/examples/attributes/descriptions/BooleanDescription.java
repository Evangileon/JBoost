package jboost.examples.attributes.descriptions;

import jboost.controller.Configuration;
import jboost.examples.attributes.Attribute;
import jboost.examples.attributes.BooleanAttribute;
import jboost.exceptions.BadAttException;

/**
 * the description for number attributes.
 */
class BooleanDescription extends AttributeDescription {

  /**
	 * 
	 */
	private static final long serialVersionUID = 8556825450013509562L;

BooleanDescription(String name, Configuration c) throws ClassNotFoundException {
    attributeName = name;
    attributeClass = Class.forName("jboost.examples.attributes.BooleanAttribute");

    // Need to make a SAFE version of configuration, which implements
    // an error() function, which can return and error message.

    crucial = c.getBool("crucial", false);
    ignoreAttribute = c.getBool("ignoreAttribute", false);
    existence = c.getBool("existence", false);
    order = c.getBool("order", true);
  }

  /**
   * checks format of string in datafile and converts to int If the attribute is
   * missing it creates a new real attribute, that is not defined.
   */
  public Attribute str2Att(String string) throws BadAttException {
    if (string == null) return (new BooleanAttribute());
    string = string.trim();
    if (string.length() == 0) return (new BooleanAttribute());
    boolean att = true; // initialized because try complains otherwise.
    try {
      if (string.equals("0") || string.toLowerCase().equals("false")) att = false;
    }
    catch (NumberFormatException e) {
      System.err.println(string + " is not a boolean.");
      throw new BadAttException(string + " is not a boolean", 0, 0);
    }
    return new BooleanAttribute(att);
  }

  public String toString() {
    String retval = new String(attributeName);
    retval += " " + attributeClass.getName();
    retval += " crucial: " + crucial;
    retval += " ignoreAttribute: " + ignoreAttribute;
    retval += " existence: " + existence;
    retval += " order: " + order;
    return (retval);
  }

  public String toString(Attribute attr) {
    String retval = new String();
    if (attr == null) return ("undefined");
    retval += ((BooleanAttribute) attr).toString();
    return (retval);
  }
}
