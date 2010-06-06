package org.junit.tests.experimental.theories;

import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * Test for issue 76 (http://github.com/KentBeck/junit/issues/issue/76)
 */
@RunWith(Theories.class)
public class TrackableTestCase {

	@DataPoints // 1, 2
	public static Class<?>[] collectTargets() {
		Class<?>[] targets = new Class[] {
				String.class, ArrayList.class
		};
		return targets;
	}

	@DataPoints // 3, 4
	public static Class<?>[] targets = new Class[] {Collection.class, LinkedList.class};

	@DataPoints // 5, 6
	public static Collection<Class<?>> classCollection = new ArrayList<Class<?>>();
	@BeforeClass public static void initClassCollection() {
		classCollection.add(Integer.class);
		classCollection.add(Boolean.class);
	}

	@DataPoints // 7, 8
	public static Collection<Class<?>> classCollectionMethod() {
		ArrayList<Class<?>> collection= new ArrayList<Class<?>>();
		collection.add(Character.class);
		collection.add(Short.class);
		return collection;
	}

	private static List<Class<?>> classesRunThroughTheory = new ArrayList<Class<?>>();

	@Theory
	public void theory(Class<?> klass) {
		System.out.println(klass.getName());
		classesRunThroughTheory.add(klass);
	}

	@AfterClass
	public static void checkForClasses() {
		Assert.assertThat(classesRunThroughTheory.size(), is(8));
	}
}
