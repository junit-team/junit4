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
 * A sample test case, testing {@link java.util.ArrayList}.
 */
public class ListTest {
    protected List<Integer> empty;
    protected List<Integer> full;
    protected static List<Integer> heavy;

    public static void main(String... args) {
        junit.textui.TestRunner.run(suite());
    }

    @BeforeClass
    public static void setUpOnce() {
        heavy = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++) {
            heavy.add(i);
        }
    }

    @Before
    public void setUp() {
        empty = new ArrayList<Integer>();
        full = new ArrayList<Integer>();
        full.add(1);
        full.add(2);
        full.add(3);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ListTest.class);
    }

    @Ignore("not today")
    @Test
    public void capacity() {
        int size = full.size();
        for (int i = 0; i < 100; i++) {
            full.add(i);
        }
        assertTrue(full.size() == 100 + size);
    }

    @Test
    public void testCopy() {
        List<Integer> copy = new ArrayList<Integer>(full.size());
        copy.addAll(full);
        assertTrue(copy.size() == full.size());
        assertTrue(copy.contains(1));
    }

    @Test
    public void contains() {
        assertTrue(full.contains(1));
        assertTrue(!empty.contains(1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void elementAt() {
        int i = full.get(0);
        assertTrue(i == 1);
        full.get(full.size()); // Should throw IndexOutOfBoundsException
    }

    @Test
    public void removeAll() {
        full.removeAll(full);
        empty.removeAll(empty);
        assertTrue(full.isEmpty());
        assertTrue(empty.isEmpty());
    }

    @Test
    public void removeElement() {
        full.remove(new Integer(3));
        assertTrue(!full.contains(3));
    }
}