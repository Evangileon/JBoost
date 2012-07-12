/**
 * 
 */
package jboost.examples;

/**
 * @author yj
 *
 */
class WindowStyle {
	int numHor;
	int numVer;
	
	private WindowStyle(int numX,int numY) {
		numHor = numX;
		numVer = numY;
	}
	
	public int getXnum() {
		return numHor;
	}
	
	public int getYnum() {
		return numVer;
	}
	
	public static final WindowStyle LeftRight = new WindowStyle(2,1);
	public static final WindowStyle TopBottle = new WindowStyle(1,2);
	public static final WindowStyle TripleVer = new WindowStyle(1,3);
	public static final WindowStyle TripleHor = new WindowStyle(3,1);
	public static final WindowStyle CrossDiff = new WindowStyle(2,2);
}
