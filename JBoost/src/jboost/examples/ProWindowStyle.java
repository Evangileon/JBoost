/**
 * 
 */
package jboost.examples;

import java.awt.Point;

/**
 * @author yj
 * @use train subimage in a rectangle with manully specified size and several
 *       default style
 */
public class ProWindowStyle {
	private WindowStyle style;
	private String name;
	
	private int verticalBlock;
	private int horizonBlock;
	private int width,height;
	
	private ProWindowStyle(WindowStyle style, String name) {
		// TODO Auto-generated constructor stub
		this.style = style;
		this.name = name;
		this.horizonBlock = style.numHor;
		this.verticalBlock = style.numVer;
		
//		switch(style) {
//		case TopBottle:
//			verticalBlock = 2;
//			horizonBlock = 1;
//			break;
//		case LeftRight:
//			verticalBlock = 1;
//			horizonBlock = 2;
//			break;
//		case TripleVet:
//			verticalBlock = 3;
//			horizonBlock = 1;
//			break;
//		case TripleHor:
//			verticalBlock = 1;
//			horizonBlock = 3;
//			break;
//		case CrossDiff:
//			verticalBlock = 2;
//			horizonBlock = 2;
//			break;
//		default:
//			break;
//		}
	}
	
	public int process(int[][] intergrate, Window window) {
		return process(intergrate, window.getLeftTop(), window.getRightBottom());
	}
	
	/**
	 * @param data contains entire image byte data
	 * @param width must be even,if triple must be multiple of 3
	 * @param height must be even,if triple must be multiple of 3
	 */
	public int process(int[][] intergrate,Point leftTop, Point rightBottom) {
		int tmpWidth = rightBottom.x - leftTop.x;
		int tmpHeight = rightBottom.y - leftTop.y;
		
//		if(sizeCheck(tmpWidth, tmpHeight) == false)
//			return -1;
		
		this.width = tmpWidth;
		this.height = tmpHeight;
		int positive = 0,negative = 0;
		int widthBlock = width / horizonBlock;
		int heightBlock = height / verticalBlock;
		int flag = 0;
		
		//Point rightBottle = new Point(leftTop.x + width,leftTop.y + height);
		
		for(int i=0;i < verticalBlock;i++) {
			
			for(int j=0;j < horizonBlock;j++) {
				if(flag == 0) {
					positive += singleBlock(intergrate, 
							new Point(leftTop.x + j*widthBlock, leftTop.y + i*heightBlock), 
							new Point(leftTop.x + j*widthBlock + widthBlock, leftTop.y + i*heightBlock + heightBlock));
				}
				else {
					negative += singleBlock(intergrate, 
							new Point(leftTop.x + j*widthBlock, leftTop.y + i*heightBlock), 
							new Point(leftTop.x + j*widthBlock + widthBlock, leftTop.y + i*heightBlock + heightBlock));
				}
				flag = (flag == 0)? 1 : 0;
			}
			flag = (flag == 0)? 1 : 0;
		}
		
		int tmp = positive - negative;
		//System.out.println("pos - neg " + tmp);
		return tmp;
	}
	
	private int singleBlock(int[][] intergrate,Point leftTop,Point rightBottom) {
		int sum = 0;
		
		//System.out.println("height "+intergrate[0][0]);
		sum = intergrate[rightBottom.y][rightBottom.x] 
				- intergrate[leftTop.y][rightBottom.x]
				- intergrate[rightBottom.y][leftTop.x]
				+ intergrate[leftTop.y][leftTop.x];
		
		return sum;
	}
	
	@SuppressWarnings("unused")
	private boolean sizeCheck(int width,int height) {
		if(style.numHor != 1 && width % style.numHor != 0)
			return false;
		if(style.numVer != 1 && height % style.numVer != 0)
			return false;
		
		return true;
	}
	
	public int getXnum() {
		return horizonBlock;
	}
	
	public int getYnum() {
		return verticalBlock;
	}
	
	public String toString() {
		return this.name;
	}
	
	public static final ProWindowStyle LeftRight = new ProWindowStyle(WindowStyle.LeftRight, "LeftRight");
	public static final ProWindowStyle TopBottle = new ProWindowStyle(WindowStyle.TopBottle, "TopBottle");
	public static final ProWindowStyle TripleVet = new ProWindowStyle(WindowStyle.TripleVer, "TripleVer");
	public static final ProWindowStyle TripleHor = new ProWindowStyle(WindowStyle.TripleHor, "TripleHor");
	public static final ProWindowStyle CrossDiff = new ProWindowStyle(WindowStyle.CrossDiff, "CrossDiff");
}
