package jboost.util;

import java.io.IOException;

public class ByteBuffer
extends AbstractByteBuilder
implements java.io.Serializable, CharSequence
{

/** use serialVersionUID from JDK 1.0.2 for interoperability */
static final long serialVersionUID = 3388685877147921107L;

/**
 * Constructs a string buffer with no characters in it and an 
 * initial capacity of 16 characters. 
 */
public ByteBuffer() {
super(16);
}

/**
 * Constructs a string buffer with no characters in it and 
 * the specified initial capacity. 
 *
 * @param      capacity  the initial capacity.
 * @exception  NegativeArraySizeException  if the <code>capacity</code>
 *               argument is less than <code>0</code>.
 */
public ByteBuffer(int capacity) {
super(capacity);
}

/**
 * Constructs a string buffer initialized to the contents of the 
 * specified string. The initial capacity of the string buffer is 
 * <code>16</code> plus the length of the string argument.
 *
 * @param   str   the initial contents of the buffer.
 * @exception NullPointerException if <code>str</code> is <code>null</code>
 */
public ByteBuffer(String str) {
super(str.length() + 16);
append(str);
}

/**
 * Constructs a string buffer that contains the same characters
 * as the specified <code>CharSequence</code>. The initial capacity of
 * the string buffer is <code>16</code> plus the length of the
 * <code>CharSequence</code> argument.
 * <p>
 * If the length of the specified <code>CharSequence</code> is
 * less than or equal to zero, then an empty buffer of capacity
 * <code>16</code> is returned.
 *
 * @param      seq   the sequence to copy.
 * @exception NullPointerException if <code>seq</code> is <code>null</code>
 * @since 1.5
 */
public ByteBuffer(CharSequence seq) {
    this(seq.length() + 16);
    append(seq);
}

public synchronized int length() {
return count;
}

public synchronized int capacity() {
return value.length;
}


public synchronized void ensureCapacity(int minimumCapacity) {
if (minimumCapacity > value.length) {
    expandCapacity(minimumCapacity);
}
}

/**
 * @since      1.5
 */
public synchronized void trimToSize() {
    super.trimToSize();
}

/**
 * @throws IndexOutOfBoundsException {@inheritDoc}
 * @see        #length()
 */
public synchronized void setLength(int newLength) {
super.setLength(newLength);
}

/**
 * @throws IndexOutOfBoundsException {@inheritDoc}
 * @see        #length()
 */
public synchronized byte ByteAt(int index) {
if ((index < 0) || (index >= count))
    throw new StringIndexOutOfBoundsException(index);
return value[index];
}

/**
 * @since      1.5
 */
//public synchronized int codePointAt(int index) {
//    return super.codePointAt(index);
//}

/**
 * @since     1.5
 */
//public synchronized int codePointBefore(int index) {
//    return super.codePointBefore(index);
//}

/**
 * @since     1.5
 */
//public synchronized int codePointCount(int beginIndex, int endIndex) {
//return super.codePointCount(beginIndex, endIndex);
//}

/**
 * @since     1.5
 */
//public synchronized int offsetByCodePoints(int index, int codePointOffset) {
//return super.offsetByCodePoints(index, codePointOffset);
//}

/**
 * @throws NullPointerException {@inheritDoc}
 * @throws IndexOutOfBoundsException {@inheritDoc}
 */
public synchronized void getBytes(int srcBegin, int srcEnd, byte dst[],
                                  int dstBegin)
{
super.getBytes(srcBegin, srcEnd, dst, dstBegin);
}

/**
 * @throws IndexOutOfBoundsException {@inheritDoc}
 * @see        #length()
 */
public synchronized void setByteAt(int index, byte ch) {
if ((index < 0) || (index >= count))
    throw new StringIndexOutOfBoundsException(index);
value[index] = ch;
}

/**
 * @see     java.lang.String#valueOf(java.lang.Object)
 * @see     #append(java.lang.String)
 */
public synchronized ByteBuffer append(Object obj) {
super.append(String.valueOf(obj));
    return this;
}

public synchronized ByteBuffer append(String str) {
super.append(str);
    return this;
}

/**
 * Appends the specified <tt>ByteBuffer</tt> to this sequence.
 * <p>
 * The characters of the <tt>ByteBuffer</tt> argument are appended, 
 * in order, to the contents of this <tt>ByteBuffer</tt>, increasing the 
 * length of this <tt>ByteBuffer</tt> by the length of the argument. 
 * If <tt>sb</tt> is <tt>null</tt>, then the four characters 
 * <tt>"null"</tt> are appended to this <tt>ByteBuffer</tt>.
 * <p>
 * Let <i>n</i> be the length of the old character sequence, the one 
 * contained in the <tt>ByteBuffer</tt> just prior to execution of the 
 * <tt>append</tt> method. Then the character at index <i>k</i> in 
 * the new character sequence is equal to the character at index <i>k</i> 
 * in the old character sequence, if <i>k</i> is less than <i>n</i>; 
 * otherwise, it is equal to the character at index <i>k-n</i> in the 
 * argument <code>sb</code>.
 * <p>
 * This method synchronizes on <code>this</code> (the destination) 
 * object but does not synchronize on the source (<code>sb</code>).
 *
 * @param   sb   the <tt>ByteBuffer</tt> to append.
 * @return  a reference to this object.
 * @since 1.4
 */
public synchronized ByteBuffer append(ByteBuffer sb) {
    super.append(sb);
    return this;
}


/**
 * Appends the specified <code>CharSequence</code> to this
 * sequence.
 * <p>
 * The characters of the <code>CharSequence</code> argument are appended, 
 * in order, increasing the length of this sequence by the length of the 
 * argument.
 *
 * <p>The result of this method is exactly the same as if it were an
 * invocation of this.append(s, 0, s.length());
 *
 * <p>This method synchronizes on this (the destination) 
 * object but does not synchronize on the source (<code>s</code>).
 *
 * <p>If <code>s</code> is <code>null</code>, then the four characters 
 * <code>"null"</code> are appended.
 *
 * @param   s the <code>CharSequence</code> to append.
 * @return  a reference to this object.
 * @since 1.5
 */
public ByteBuffer append(CharSequence s) {
    // Note, synchronization achieved via other invocations
    if (s == null)
        s = "null";
    if (s instanceof String)
        return this.append((String)s);
    if (s instanceof ByteBuffer)
        return this.append((ByteBuffer)s);
    return this.append(s, 0, s.length());
}

/**
 * @throws IndexOutOfBoundsException {@inheritDoc}
 * @since      1.5
 */
public synchronized ByteBuffer append(CharSequence s, int start, int end) 
{
    super.append(s, start, end);
    return this;
}

public synchronized ByteBuffer append(char str[]) { 
    super.append(str);
    return this;
}

public synchronized ByteBuffer append(byte str[], int offset, int len) {
    super.append(str, offset, len);
    return this;
}

/**
 * @see     java.lang.String#valueOf(boolean)
 * @see     #append(java.lang.String)
 */
public synchronized ByteBuffer append(boolean b) {
    super.append(b);
    return this;
}

public synchronized ByteBuffer append(byte c) {
    super.append(c);
    return this;
}

/**
 * @see     java.lang.String#valueOf(int)
 * @see     #append(java.lang.String)
 */
@SuppressWarnings("deprecation")
public synchronized ByteBuffer append(int i) {
super.append(i);
    return this;
}

/**
 * @since 1.5
 */
//public synchronized ByteBuffer appendCodePoint(int codePoint) {
//super.appendCodePoint(codePoint);
//return this;
//}

/**
 * @see     java.lang.String#valueOf(long)
 * @see     #append(java.lang.String)
 */
@SuppressWarnings("deprecation")
public synchronized ByteBuffer append(long lng) {
    super.append(lng);
return this;
}

/**
 * @see     java.lang.String#valueOf(float)
 * @see     #append(java.lang.String)
 */
@SuppressWarnings("deprecation")
public synchronized ByteBuffer append(float f) {
super.append(f);
return this;
}

/**
 * @see     java.lang.String#valueOf(double)
 * @see     #append(java.lang.String)
 */
public synchronized ByteBuffer append(double d) {
super.append(d);
return this;
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @since      1.2
 */
public synchronized ByteBuffer delete(int start, int end) {
    super.delete(start, end);
    return this;
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @since      1.2
 */
public synchronized ByteBuffer deleteCharAt(int index) {
    super.deleteCharAt(index);
    return this;
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @since      1.2
 */
//public synchronized ByteBuffer replace(int start, int end, String str) {
//    super.replace(start, end, str);
//    return this;
//}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @since      1.2
 */
public synchronized String substring(int start) {
    return substring(start, count);
}

/**
 * @throws IndexOutOfBoundsException {@inheritDoc}
 * @since      1.4
 */
public synchronized CharSequence subSequence(int start, int end) {
    return super.substring(start, end);
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @since      1.2
 */
public synchronized String substring(int start, int end) {
    return super.substring(start, end);
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @since      1.2
 */
public synchronized ByteBuffer insert(int index, byte str[], int offset,
                                        int len) 
{
    super.insert(index, str, offset, len);
    return this;
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @see        java.lang.String#valueOf(java.lang.Object)
 * @see        #insert(int, java.lang.String)
 * @see        #length()
 */
public synchronized ByteBuffer insert(int offset, Object obj) {
super.insert(offset, String.valueOf(obj));
    return this;
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @see        #length()
 */
public synchronized ByteBuffer insert(int offset, String str) {
    super.insert(offset, str);
    return this;
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 */
public synchronized ByteBuffer insert(int offset, char str[]) {
    super.insert(offset, str);
return this;
}

/**
 * @throws IndexOutOfBoundsException {@inheritDoc}
 * @since      1.5
 */
public ByteBuffer insert(int dstOffset, CharSequence s) {
    // Note, synchronization achieved via other invocations
    if (s == null)
        s = "null";
    if (s instanceof String)
        return this.insert(dstOffset, (String)s);
    return this.insert(dstOffset, s, 0, s.length());
}

/**
 * @throws IndexOutOfBoundsException {@inheritDoc}
 * @since      1.5
 */
public synchronized ByteBuffer insert(int dstOffset, CharSequence s, 
                                        int start, int end)
{
    super.insert(dstOffset, s, start, end);
    return this;
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @see        java.lang.String#valueOf(boolean)
 * @see        #insert(int, java.lang.String)
 * @see        #length()
 */
public ByteBuffer insert(int offset, boolean b) {
return insert(offset, String.valueOf(b));
}

/**
 * @throws IndexOutOfBoundsException {@inheritDoc}
 * @see        #length()
 */
public synchronized ByteBuffer insert(int offset, char c) {
super.insert(offset, c);
return this;
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @see        java.lang.String#valueOf(int)
 * @see        #insert(int, java.lang.String)
 * @see        #length()
 */
public ByteBuffer insert(int offset, int i) {
return insert(offset, String.valueOf(i));
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @see        java.lang.String#valueOf(long)
 * @see        #insert(int, java.lang.String)
 * @see        #length()
 */
public ByteBuffer insert(int offset, long l) {
return insert(offset, String.valueOf(l));
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @see        java.lang.String#valueOf(float)
 * @see        #insert(int, java.lang.String)
 * @see        #length()
 */
public ByteBuffer insert(int offset, float f) {
return insert(offset, String.valueOf(f));
}

/**
 * @throws StringIndexOutOfBoundsException {@inheritDoc}
 * @see        java.lang.String#valueOf(double)
 * @see        #insert(int, java.lang.String)
 * @see        #length()
 */
public ByteBuffer insert(int offset, double d) {
return insert(offset, String.valueOf(d));
}

/**
 * @throws NullPointerException {@inheritDoc}
 * @since      1.4
 */
//public int indexOf(String str) {
//return indexOf(str, 0);
//}



/**
 * @throws NullPointerException {@inheritDoc}
 * @since      1.4
 */
//public int lastIndexOf(String str) {
//    // Note, synchronization achieved via other invocations
//    return lastIndexOf(str, count);
//}


/**
 * @since   JDK1.0.2
 */
public synchronized ByteBuffer reverse() {
super.reverse();
return this;
}

public synchronized String toString() {
return new String(value, 0, count);
}

/**
 * Serializable fields for ByteBuffer.
 * 
 * @serialField value  char[]
 *              The backing character array of this ByteBuffer.
 * @serialField count int
 *              The number of characters in this ByteBuffer.
 * @serialField shared  boolean
 *              A flag indicating whether the backing array is shared. 
 *              The value is ignored upon deserialization.
 */
private static final java.io.ObjectStreamField[] serialPersistentFields = 
{ 
    new java.io.ObjectStreamField("value", char[].class), 
    new java.io.ObjectStreamField("count", Integer.TYPE),
    new java.io.ObjectStreamField("shared", Boolean.TYPE),
};

/**
 * readObject is called to restore the state of the ByteBuffer from
 * a stream.
 */
private synchronized void writeObject(java.io.ObjectOutputStream s)
    throws java.io.IOException {
    java.io.ObjectOutputStream.PutField fields = s.putFields();
    fields.put("value", value);
    fields.put("count", count);
    fields.put("shared", false);
    s.writeFields();
}

/**
 * readObject is called to restore the state of the ByteBuffer from
 * a stream.
 */
private void readObject(java.io.ObjectInputStream s)
    throws java.io.IOException, ClassNotFoundException {
    java.io.ObjectInputStream.GetField fields = s.readFields();
    value = (byte[])fields.get("value", null);
    count = (int)fields.get("count", 0);
}

@Override
public Appendable append(char c) throws IOException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public char charAt(int index) {
	// TODO Auto-generated method stub
	return 0;
}
}
