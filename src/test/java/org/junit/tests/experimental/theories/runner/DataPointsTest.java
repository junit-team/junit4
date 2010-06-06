package org.junit.tests.experimental.theories.runner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;

import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * A simple test class in which Collections of Strings (not just arrays of Strings) are
 * annotated with the DataPoints annotation.
 */
@RunWith(Theories.class)
public class DataPointsTest {

  @DataPoint  // 1
  public static String singleDataPoint = "single data point";

  @DataPoints // 2, 3
  public static String[] stringArray = {"one string in an array", "another string in an array", };

  @DataPoints // 4, 5
  public static String[] stringArrayMethod() {
    return new String[] {
      "one string in an array returned from a method",
      "another string in an array returned from a method"
    };
  }

  @DataPoints // 6, 7
  public static List<String> stringList = Arrays.asList("one string in a list", "another string in a list");

  @DataPoints // 8, 9
  public static Collection<String> stringCollection = new HashSet<String>();
  @BeforeClass public static void initStringCollection() {
    stringCollection.add("one string in a set");
    stringCollection.add("another string in a set");
  };

  @DataPoints // 10, 11
  public static List<String> stringList() {
    ArrayList<String> list = new ArrayList<String>();
    list.add("one string in a list returned from a method");
    list.add("another string in a list returned from a method");
    return list;
  }

  @DataPoints // 12
  public static List<Object> mixedCollection = new ArrayList<Object>();
  @BeforeClass public static void initMixedCollection() {
	  mixedCollection.add(true);
	  mixedCollection.add("string in mixed collection");
	  mixedCollection.add(new Object());
  }

  @DataPoint
  public static JFrame someRandomObject = new JFrame();

  @DataPoints
  public static Integer[] integerArray = {1, 2, 3};

  @DataPoints public static Collection<Integer> integerCollection = Arrays.asList(4, 5, 6);

  @DataPoints
  public static Collection<Integer> integerCollectionMethod() {
    return Arrays.asList(4, 5, 6);
  }

  public static List<String> collectedStrings = new ArrayList<String>();

  @Theory
  public void theory(String s) {
    collectedStrings.add(s);
  }

  @AfterClass
  public static void allStringsFedIntoTheory() {
	// ensure that all 12 strings were passed into the Theory
    Assert.assertEquals(12, collectedStrings.size());
  }
}
