package org.junit.internal.runners;

import java.lang.reflect.Method;
import java.util.List;


public abstract class JavaElement {
	protected abstract List<Method> getAfters();

	protected abstract List<Method> getBefores();
}
