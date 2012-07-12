package jboost.booster;

/**
 * Defines the state of an example Inner class used to store a list of
 * Examples The list is converted into the internal data structures for the
 * Booster by finalizeData();
 */
public class TmpData {

  private int m_index;
  private short m_label;
  private double m_weight;
  private double m_margin;

  /**
   * Ctor for a TmpData object
   * 
   * @param index
   * @param label
   * @param weight
   */
  public TmpData(int index, short label, double weight, double margin) {
    m_index = index;
    m_label = label;
    m_weight = weight;
    m_margin = margin;
  }

  /**
   * Get the index for this example
   * 
   * @return m_index
   */
  public int getIndex() {
    return m_index;
  }

  /**
   * Get the label for this example
   * 
   * @return m_label
   */
  public short getLabel() {
    return m_label;
  }

  /**
   * Get the weigh for this example
   * 
   * @return m_weight
   */
  public double getWeight() {
    return m_weight;
  }

  /**
   * Get the margin for this example
   * 
   * @return m_weight
   */
  public double getMargin() {
    return m_margin;
  }

  public String toString() {
	  	  
	  String retval = null;
	  retval += "m_index=" + m_index;
	  retval += ", m_label=" + m_label;
	  retval += ", m_weight=" + m_weight;
	  retval += ", m_margin=" + m_margin;
	  
	  return retval;
  }
}