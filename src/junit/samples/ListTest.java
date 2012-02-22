package junit.samples;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A sample test case, testing {@link java.util.Vector}.
 *
 */
public class ListTest extends TestCase {
	protected List<Integer> fEmpty;
	protected List<Integer> fFull;

	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}
	@Override
	protected void setUp() {
		fEmpty= new ArrayList<Integer>();
		fFull= new ArrayList<Integer>();
		fFull.add(1);
		fFull.add(2);
		fFull.add(3);
	}
	public static Test suite() {
		return new TestSuite(ListTest.class);
	}
	public void testCapacity() {
		int size= fFull.size(); 
		for (int i= 0; i < 100; i++)
			fFull.add(new Integer(i));
		assertTrue(fFull.size() == 100+size);
	}
	public void testContains() {
		assertTrue(fFull.contains(1));  
		assertTrue(!fEmpty.contains(1));
	}
	public void testElementAt() {
		int i= fFull.get(0);
		assertTrue(i == 1);

		try { 
			fFull.get(fFull.size());
		} catch (IndexOutOfBoundsException e) {
			return;
		}
		fail("Should raise an ArrayIndexOutOfBoundsException");
	}
	public void testRemoveAll() {
		fFull.removeAll(fFull);
		fEmpty.removeAll(fEmpty);
		assertTrue(fFull.isEmpty());
		assertTrue(fEmpty.isEmpty()); 
	}
	public void testRemoveElement() {
		fFull.remove(new Integer(3));
		assertTrue(!fFull.contains(3) ); 
	}
}