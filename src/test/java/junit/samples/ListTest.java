package junit.samples;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * A sample test case, testing {@link java.util.ArrayList}.
 */
public class ListTest extends TestCase {
    protected List<Integer> empty;
    protected List<Integer> full;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    @Override
    protected void setUp() {
        empty = new ArrayList<Integer>();
        full = new ArrayList<Integer>();
        full.add(1);
        full.add(2);
        full.add(3);
    }

    public static Test suite() {
        return new TestSuite(ListTest.class);
    }

    public void testCapacity() {
        int size = full.size();
        for (int i = 0; i < 100; i++) {
            full.add(new Integer(i));
        }
        assertTrue(full.size() == 100 + size);
    }

    public void testContains() {
        assertTrue(full.contains(1));
        assertTrue(!empty.contains(1));
    }

    public void testElementAt() {
        int i = full.get(0);
        assertTrue(i == 1);

        try {
            full.get(full.size());
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        fail("Should raise an ArrayIndexOutOfBoundsException");
    }

    public void testRemoveAll() {
        full.removeAll(full);
        empty.removeAll(empty);
        assertTrue(full.isEmpty());
        assertTrue(empty.isEmpty());
    }

    public void testRemoveElement() {
        full.remove(new Integer(3));
        assertTrue(!full.contains(3));
    }
}