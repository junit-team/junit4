package org.junit.internal;

import java.util.Random;

import org.junit.runner.manipulation.Shufflable;

public class Shuffler {
	private final Random random;

	public Shuffler(Random random) {
		this.random= random;
	}

	public void apply(Object object) {
		if (object instanceof Shufflable) {
			Shufflable shufflable= (Shufflable) object;
			shufflable.shuffle(random);
		}
	}
}
