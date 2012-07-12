/**
 * 
 */
package jboost.controller;

import java.lang.reflect.Type;

/**
 * @author yj
 * @see memory pool to store byte-based data in order to reduce number of objects
 *      of traindata,especially when traindata contains lots of attributes
 */
public class DataPool {

	/**
	 * 
	 */
	public DataPool(int capacity,Type type) {
		// TODO Auto-generated constructor stub
		int realByte = 0;
		
		if(type == double.class || type == long.class) 
			realByte = 2 * 4 * capacity;
		else if(type == int.class)
			realByte = 4 * capacity;
		else
			throw new RuntimeException("YJ,input type unknown!");
		
		DataPool.data = new byte[realByte];
	}
	
	@SuppressWarnings("unused")
	private DataPool(byte[] data) {
		DataPool.data = data;
	}
	
	public static DataPool getInstance() {
		if(data != null)
			return null;
		return null;
	}
	
	

	private static byte[] data = null;
}


