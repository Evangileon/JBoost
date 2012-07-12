package jboost.util;

public class PoolableObjectFactory {
	static PoolableObjectFactory poolFactory;
	
	public static PoolableObjectFactory getInstance() {
		if (poolFactory == null) {
			poolFactory = new PoolableObjectFactory();
		}
		return poolFactory;
	}

	public Object createObject(Class<?> clsType) {
		// TODO Auto-generated method stub
		return null;
	}
}
