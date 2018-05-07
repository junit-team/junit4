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
    private List<Integer> emptyList;
    private List<Integer> fullList;


    @Override
    protected void setUp() {
        emptyList = new ArrayList<Integer>();
        fullList = new ArrayList<Integer>();
        fullList.add(1);
        fullList.add(2);
        fullList.add(3);
    }

    public static Test suite() {
        return new TestSuite(ListTest.class);
    }

    public void testCapacity() {
        int size = fullList.size();
        for (int i = 0; i < 100; i++) {
            fullList.add(i);
        }
        assertTrue(fullList.size() == 100 + size);
    }

    public void testContains() {
        assertTrue(fullList.contains(1));
        assertFalse(emptyList.contains(1));
    }

    public void testElementAt() {
        int i = fullList.get(0);
        assertEquals(1,i);

        try {
            fullList.get(fullList.size());
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        fail("Should raise an ArrayIndexOutOfBoundsException");
    }

    public void testRemoveAll() {
        fullList.removeAll(fullList);
        emptyList.removeAll(emptyList);
        assertTrue(fullList.isEmpty());
        assertTrue(emptyList.isEmpty());
    }

    public void testRemoveElement() {
        fullList.remove(Integer.valueOf(3));
        assertFalse(fullList.contains(3));
    }
}