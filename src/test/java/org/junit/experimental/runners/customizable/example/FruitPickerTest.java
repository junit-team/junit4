package org.junit.experimental.runners.customizable.example;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.experimental.runners.customizable.CustomizableJUnit4ClassRunner;
import org.junit.experimental.runners.customizable.ReflectionTestFactory;
import org.junit.experimental.runners.customizable.TestFactories;
import org.junit.experimental.runners.customizable.example.FruitPicker.Fruit;
import org.junit.runner.RunWith;

@RunWith(CustomizableJUnit4ClassRunner.class)
@TestFactories({ ReflectionTestFactory.class, EnumTestFactory.class })
public class FruitPickerTest {

	private final FruitPicker unit= new FruitPicker();

	@Test
	public void test() {
		assertTrue(unit.isApple(Fruit.APPLE));
	}

	/*
	 * Executed multiple times, once for each enum value
	 */
	@EnumTest(enumType= Fruit.class, nullable= true)
	public void test2(Fruit fruit) {
		boolean expectedResult= fruit == Fruit.APPLE;
		assertTrue(expectedResult == unit.isApple(fruit));
	}
}
