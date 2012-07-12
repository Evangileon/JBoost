package jboost.examples;

import java.util.ArrayList;

import jboost.examples.attributes.Attribute;
import jboost.examples.attributes.RealAttribute;
import jboost.util.BoostByteBuffer;


/** Holds the input features of an Example 
 *  Assume that the whole RealAttributes are range on front part 
 *  of all Attributes, otherwise the program may occurs error
 */
public class Instance {

  /** an array of the attributes, null if attribute is undefined */
  protected Attribute[] attribute;
  private BoostByteBuffer dataPool = null;
  
  private int realMax = 0;
  private int exaIndex;

  /** constructor */
  public Instance(Attribute[] attArray) {
    attribute = attArray;
  }
  
  public Instance(BoostByteBuffer dataPool, Attribute[]	attArray, int exaIndex) {
	  byte[] data = new byte[attArray.length * 8];
	  ArrayList<Attribute> attrList = new ArrayList<Attribute>();
	  int realMax = 0;
	  
	  for(int i = 0; i < attArray.length; i++) {
		  if(attArray[i] instanceof RealAttribute) {
			  RealAttribute real = (RealAttribute) attArray[i];
			  byte[] tmp = BoostByteBuffer.double2Bytes(real.getValue());
			  BoostByteBuffer.appendTo(tmp, data, i * 8);
			  realMax++;
		  } else {
			  attrList.add(attArray[i]);
		  }
	  }
	  this.exaIndex = exaIndex;
	  this.dataPool = dataPool;
	  this.dataPool.append(data);
	  this.realMax = realMax;
	  if(!attrList.isEmpty())
		  this.attribute = (Attribute[]) attrList.toArray();
  }
  
  public Instance(BoostByteBuffer dataPool, byte[] data) {
	  this.dataPool = dataPool;
	  this.dataPool.append(data);
  }

  /** returns the number of attributes */
  public int getSize() {
    return attribute.length;
  }

  /** returns true if attribute i is defined */
  public boolean isDefined(int i) {
	  if(i < 0)
		  return false;
	 
	  return attribute[i].isDefined();
  }

  /** returns the i'th attribute (null if undefined) */
  public Attribute getAttribute(int i) {
	  if(dataPool == null) {
		  if (isDefined(i)) {
			  return attribute[i];
		  } else {
			  return null;
		  }
	  } else {
		  if(i < realMax) {
			  RealAttribute real = new RealAttribute(dataPool.getDouble(exaIndex, i));
			  return real;
		  } else {
			  return attribute[i - realMax];
		  }
	  }
  }

  public String toString() {
    String string = new String();
    for (int i = 0; i < attribute.length; i++)
      string += attribute[i] + ((i < (attribute.length - 1)) ? "," : ";");

    return string;
  }

  public String toString(ExampleDescription ed) {
    String s = new String();
    for (int i = 0; i < attribute.length; i++) {
      s += ed.getAttributeDescription(i).getAttributeName() + ":";
      s += attribute[i] + "\n";
      // s += ((i == attribute.length-1) ? "; " : ", ");
    }

    return s;
  }
}
