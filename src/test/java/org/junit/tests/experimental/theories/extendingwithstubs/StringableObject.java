package org.junit.tests.experimental.theories.extendingwithstubs;

import java.util.Arrays;

public class StringableObject {
	public Object obj;

	public StringableObject(Object obj) {
		this.obj = obj;
	}

	public Object stringableObject() {
		if (isListableArray())
			return Arrays.asList((Object[]) obj);
		else
			return obj;
	}

	private boolean isListableArray() {
		Class<?> type = obj.getClass();
		return type.isArray() && !type.getComponentType().isPrimitive();
	}
	
	@Override public String toString() {
		return stringableObject().toString();
	}
}