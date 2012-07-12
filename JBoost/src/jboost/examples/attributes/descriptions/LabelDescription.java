package jboost.examples.attributes.descriptions;

import java.util.List;
import java.util.StringTokenizer;

import jboost.controller.Configuration;
import jboost.examples.attributes.Attribute;
import jboost.examples.attributes.Label;
import jboost.exceptions.BadAttException;
import jboost.exceptions.BadLabelException;
import jboost.tokenizer.StringOp;


/**
 * the description for multi-finite attributes.
 */
public class LabelDescription extends FiniteDescription {

  /**
	 * 
	 */
	private static final long serialVersionUID = 6764542222862940043L;

/**
   * Label must be binary. If it is not binary, this constructor returns an error.
   * @param name
   * @param c
   * @param okValues
   * @throws ClassNotFoundException
   */
  LabelDescription(String name, Configuration c, List<String> okValues) throws ClassNotFoundException, BadLabelException {
    super(name, c, okValues);
    attributeClass = Class.forName("jboost.examples.attributes.descriptions.LabelDescription");
	if (okValues.size() != 2) {
		throw new BadLabelException("Label is not binary");
	}
  }


  public String toString() {
    return super.toString();
  }

  /*
   * public String toString(Attribute attr) throws Exception { String retval=new
   * String(); boolean[] tok=((MultiDiscreteAttribute)attr).getValue();
   * if(tok==null) { int tok1=((MultiDiscreteAttribute)attr).getSingleValue();
   * if(tok1>attributeValues.size()) throw(new Exception("More attribute values
   * than allowed")); retval+=(String)attributeValues.get(tok1); } else {
   * if(tok.length>attributeValues.size()) throw(new Exception("More attribute
   * values than allowed")); for(int i=0;i<tok.length;i++) { if(tok[i]==true)
   * retval+=(String)attributeValues.get(i); // This should be improved when the
   * reader is integrated. } } return(retval); }
   */

  /**
   * Reads a MultiFinite attribute Note that it is currently based on String but
   * should use StringBuffer Also note that right now it can only deal with one
   * value.
   */
  public Attribute str2Att(String string) throws BadAttException {
    if (string == null) return new Label(-1);

    StringTokenizer st = new StringTokenizer(string);
    if (st.countTokens() != 1) throw new BadAttException((st.countTokens() == 0 ? "Zero" : "Multiple")
                                                                          + " labels found when expecting single label: " + string);
    String s;
    boolean v[] = new boolean[getNoOfValues()];
    int labelValue = -1;
    while (st.hasMoreElements()) {
      s = st.nextToken();
      if (!caseSignificant) s = s.toLowerCase();
      if (!punctuationSignificant) s = StringOp.removePunctuation(s);
      if (map.containsKey(s)) {
    	  labelValue = ((Integer) map.get(s)).intValue();
    	  break;
      }
      else if (map.containsKey("*")) v[((Integer) map.get("*")).intValue()] = true;
      else throw (new BadAttException("Unknown label: " + s + " when not allowed."));
    }
    return new Label(labelValue);
  }
}
