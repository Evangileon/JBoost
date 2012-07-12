/*
 * Created on Feb 8, 2004
 */
package jboost.learner;

import java.io.Serializable;

/**
 * @author cschavis
 */
public class SplitterType implements Serializable {

  /**
	* 
	*/
  private static final long serialVersionUID = 2090832967576173048L;
  private final String m_name;
  private final DataType m_type;

  /**
   * Private constructor insures that no one can create any more Splitter types.
   * 
   * @param name of the type
   */
  private SplitterType(String name) {
    m_name = name;
    m_type = DataType.ANSIString;
  }
  
  private SplitterType(DataType type) {
	  m_name = "";
	  m_type = type;
  }

  /**
   * Return true if this SplitterType is equal to the other SplitterType
   * 
   * @param other
   * @return result of comparing the String names of this and the other
   */
  public boolean equals(SplitterType other) {
	  if(!(m_type == other.m_type)) 
		  return false;
	  if(m_type == DataType.ByteStream)
		  return true;   //byte stream always the same
	  
	  return m_name.equals(other.toString());
  }
  
  
  /**
   * @return name of this type
   */
  public String toString() {
    return m_name;
  }

  public static final SplitterType EQUALITY_SPLITTER = new SplitterType("Equality Splitter");
  public static final SplitterType INEQUALITY_SPLITTER = new SplitterType("Inequality Splitter");
  public static final SplitterType SET_SPLITTER = new SplitterType("Set Splitter");
  public static final SplitterType BYTE_SPLITTER = new SplitterType(DataType.ByteStream);

}
