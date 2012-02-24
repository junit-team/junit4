package org.junit.samples;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * A sample test case, testing {@link java.util.Vector}.
 *
 */
public class ListTest {
	protected List<Integer> fEmpty;
	protected List<Integer> fFull;
	protected static List<Integer> fgHeavy;

	public static void main (String... args) {
		junit.textui.TestRunner.run (suite());
	}
	
	@BeforeClass public static void setUpOnce() {
		fgHeavy= new ArrayList<Integer>();
		for(int i= 0; i < 1000; i++)
			fgHeavy.add(i);
	}
	
	@Before public void setUp() {
		fEmpty= new ArrayList<Integer>();
		fFull= new ArrayList<Integer>();
		fFull.add(1);
		fFull.add(2);
		fFull.add(3);
	}
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ListTest.class);
	}
	@Ignore("not today") @Test public void capacity() {
		int size= fFull.size(); 
		for (int i= 0; i < 100; i++)
			fFull.add(i);
		assertTrue(fFull.size() == 100+size);
	}
	
	@Test public void testCopy() {
		List<Integer> copy= new ArrayList<Integer>(fFull.size());
		copy.addAll(fFull);
		assertTrue(copy.size() == fFull.size());
		assertTrue(copy.contains(1));
	}
	
	@Test public void contains() {
		assertTrue(fFull.contains(1));  
		assertTrue(!fEmpty.contains(1));
	}
	@Test (expected=IndexOutOfBoundsException.class) public void elementAt() {
		int i= fFull.get(0);
		assertTrue(i == 1);
		fFull.get(fFull.size()); // Should throw IndexOutOfBoundsException
	}
	
	@Test public void removeAll() {
		fFull.removeAll(fFull);
		fEmpty.removeAll(fEmpty);
		assertTrue(fFull.isEmpty());
		assertTrue(fEmpty.isEmpty()); 
	}
	@Test public void removeElement() {
		fFull.remove(new Integer(3));
		assertTrue(!fFull.contains(3)); 
	}
}