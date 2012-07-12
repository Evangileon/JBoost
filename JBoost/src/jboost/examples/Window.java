package jboost.examples;

import java.awt.Point;

public class Window {
	private Point leftTop;
	private Point rightBottom;
	private int right_limit = 0;
	private int bottom_limit = 0;
	
	
	public Window(Point leftTop,Point rightBottle) {
		this.leftTop = leftTop;
		this.rightBottom = rightBottle;
	}
	
	public Window(int left,int top,int right,int bottle) {
		this.leftTop = new Point(left,top);
		this.rightBottom = new Point(right, bottle);
	}
	
	public Window(int width,int height) {
		this.leftTop = new Point(0,width-1);
		this.rightBottom = new Point(0, height-1);
	}
	
	public void setLimit(int x_limit,int y_limit) {
		right_limit = x_limit - 1;
		bottom_limit = y_limit - 1;
	}
	
	public void setWindow(int width,int height) {
		leftTop.x = 0;
		leftTop.y = 0;
		rightBottom.x = width - 1;
		rightBottom.y = height - 1;
	}
	
	public void winReturn() {
		int tmp = rightBottom.x - leftTop.x;
		leftTop.x = 0;
		rightBottom.x = tmp;
	}
	
	public int getRight() {
		return rightBottom.x;
	}
	
	public int getBottom() {
		return rightBottom.y;
	}
	
	public int getLeft() {
		return leftTop.x;
	}
	
	public int getTop() {
		return leftTop.y;
	}
	
	public boolean moveToward(Direction direct,int step) {
		
		switch(direct) {
		case Left:
			leftTop.x -= step;
			rightBottom.x -= step;
			break;
		case Right:
			rightBottom.x += step;
			leftTop.x += step;
			break;
		case Down:
			rightBottom.y += step;
			leftTop.y += step;
			break;
		case Up:
			leftTop.y -= step;
			rightBottom.y -= step;
			break;
		default:
			break;
		}
		
		return true;
	}
	
	public int getXlimit() {
		return right_limit;
	}
	
	public int getYlimit() {
		return bottom_limit;
	}
	
	public Point getLeftTop() {
		return leftTop;
	}
	
	public Point getRightBottom() {
		return rightBottom;
	}
}

enum Direction {
	Left,
	Right,
	Up,
	Down
}
