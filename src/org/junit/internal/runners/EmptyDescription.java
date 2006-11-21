package org.junit.internal.runners;

import org.junit.runner.Description;

public class EmptyDescription extends Description {
	public EmptyDescription() {
		super("No Tests");
	}
	@Override
	public boolean equals(Object obj) {
		return getClass().equals(obj.getClass());
	}
	@Override
	public int hashCode() {
		return 0;
	}
}
