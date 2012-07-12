package jboost.util;


public class BoostByteBuffer extends ByteBuffer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3943119124412757236L;
	
	int index = 0;
	int attr_num;
	
	public BoostByteBuffer() {
		super();
		index = 0;
	}
	
	public BoostByteBuffer(int attrNum, int exa) {
		this(attrNum * exa * 8);
		this.attr_num = attrNum;
	}
	
	public BoostByteBuffer(int capacity) {
		super(capacity);
	}
	
	public BoostByteBuffer(CharSequence seq) {
		super(seq);
	}
	
	public double getDouble(int exa, int attr) {
		int start = (exa * attr_num + attr) * 8;
		
		return arr2Double(this.value, start);
	}
	
	public static double arr2Double(byte[] arr, int start) { 
		int i = 0; 
		int len = 8; 
		int cnt = 0; 
		byte[] tmp = new byte[len]; 
		for(i = start; i < (start + len); i++) { 
			tmp[cnt] = arr[i]; 
		//System.out.println(java.lang.Byte.toString(arr[i])   +   "   "   +   i); 
			cnt++; 
		} 
		long accum = 0; 
		i = 0; 
		for(int shiftBy = 0; shiftBy < 64; shiftBy += 8) { 
			accum |= ((long)(tmp[i] & 0xff )) << shiftBy; 
			i++; 
		} 
		return   Double.longBitsToDouble(accum); 
	} 
	
	public static void appendTo(byte[] arr, byte[] dst, int start) {
		for(int i = 0; i < arr.length; i++)	{
			dst[start + i] = arr[i];
		}
	}
}
