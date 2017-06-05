package org.junit.experimental.runners.customizable.example;

public class FruitPicker {

	public static enum Fruit {

		APPLE,
		PEAR,
		PEACH
	}

	public boolean isApple(Fruit fruit) {
		return fruit == Fruit.APPLE;
	}
}
