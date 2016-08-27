package org.junit.runners;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.RepeatingSuite.RepeatingSuiteClasses;

/**
 * Tests that verify proper behavior for tests run with
 * {@link RepeatingSuite @RepeatingSuite} and
 * {@link RepeatingSuiteClasses @RepeatingSuiteClasses} annotations.
 *
 * @author Laurent Cohen
 */
public class RepeatingSuiteTest {
    private static final int REPEAT_3 = 3;
    private static final int REPEAT_100 = 100;

    @Test
    public void testRepetition() throws Exception {
        TrackingRunListener listener = new TrackingRunListener();
        JUnitCore core = new JUnitCore();
        core.addListener(listener);
        core.run(TestCase1.class);
        int repeats = REPEAT_3;
        assertEquals(repeats * 9, listener.testStartedCount.get());
        assertEquals(repeats * 9, listener.testFinishedCount.get());
        assertEquals(repeats, listener.testFailureCount.get());
        assertEquals(repeats, listener.classNamesInRunOrder.size());
        List<String> testClassNames = Arrays.asList(TestClass1.class.getName(),
                TestClass2.class.getName(), TestClass3.class.getName());
        for (int i = 0; i < repeats; i++) {
            List<String> names = listener.classNamesInRunOrder.get(i);
            assertNotNull(names);
            assertEquals(3, names.size());
            assertTrue(names.containsAll(testClassNames));
        }
    }

    @Test
    public void testShuffleClasses() throws Exception {
        TrackingRunListener listener = new TrackingRunListener();
        JUnitCore core = new JUnitCore();
        core.addListener(listener);
        core.run(TestCase2.class);
        int repeats = REPEAT_100;
        assertEquals(repeats * 9, listener.testStartedCount.get());
        assertEquals(repeats * 9, listener.testFinishedCount.get());
        assertEquals(repeats, listener.testFailureCount.get());
        assertEquals(repeats, listener.classNamesInRunOrder.size());
        List<String> testClassNames = Arrays.asList(TestClass1.class.getName(),
                TestClass2.class.getName(), TestClass3.class.getName());
        // now test that we have at least 2 sequences of classes in a different order
        int count = 0;
        for (int i = 0; i < repeats; i++) {
            List<String> names = listener.classNamesInRunOrder.get(i);
            assertNotNull(names);
            assertEquals(3, names.size());
            assertTrue(names.containsAll(testClassNames));
            if (!testClassNames.equals(names)) {
                count++;
            }
        }
        assertTrue(count > 0);
    }

    public static class TestClass1 {
        @Test public void a() { }
        @Test public void b() { fail(); }
        @Test public void c() { }
    }

    public static class TestClass2 {
        @Test public void a() { }
        @Test public void b() { }
        @Test public void c() { }
    }

    public static class TestClass3 {
        @Test public void a() { }
        @Test public void b() { }
        @Test public void c() { }
    }

    @RunWith(RepeatingSuite.class)
    @RepeatingSuite.RepeatingSuiteClasses(repeat = REPEAT_3,
            shuffleClasses = false, shuffleMethods = false,
            classes = { TestClass1.class, TestClass2.class, TestClass3.class })
    public static class TestCase1 {
    }

    @RunWith(RepeatingSuite.class)
    @RepeatingSuite.RepeatingSuiteClasses(repeat = REPEAT_100,
            shuffleClasses = true, shuffleMethods = false,
            classes = { TestClass1.class, TestClass2.class, TestClass3.class })
    public static class TestCase2 {
    }

    /**
     * Simple {@link RunListener} that tracks the number of times that
     * certain callbacks are invoked.
     */
    private static class TrackingRunListener extends RunListener {
        final Map<Integer, List<String>> classNamesInRunOrder = new HashMap<Integer, List<String>>();
        final AtomicInteger testStartedCount = new AtomicInteger();
        final AtomicInteger testFailureCount = new AtomicInteger();
        final AtomicInteger testFinishedCount = new AtomicInteger();

        @Override
        public void testStarted(Description description) throws Exception {
            int count = testStartedCount.get();
            String className = description.getClassName();
            // 3 classes * 3 methods each
            int n = count / 9;
            if (count % 9 == 0) {
                List<String> orderedNames = new ArrayList<String>();
                classNamesInRunOrder.put(n, orderedNames);
            }
            List<String> orderedNames = classNamesInRunOrder.get(n);
            if (!orderedNames.contains(className)) {
                orderedNames.add(className);
            }
            testStartedCount.incrementAndGet();
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            testFailureCount.incrementAndGet();
        }

        @Override
        public void testFinished(Description description) throws Exception {
            testFinishedCount.incrementAndGet();
        }
    }
}
