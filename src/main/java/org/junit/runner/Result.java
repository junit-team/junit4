package org.junit.runner;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * A <code>Result</code> collects and summarizes information from running multiple tests.
 * All tests are counted -- additional information is collected from tests that fail.
 *
 * @since 4.0
 */
public class Result implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final ObjectStreamField[] serialPersistentFields =
            ObjectStreamClass.lookup(SerializedForm.class).getFields();
    private final AtomicInteger count;
    private final AtomicInteger ignoreCount;
    private final AtomicInteger assumptionFailureCount;
    private final CopyOnWriteArrayList<Failure> failures;
    private final AtomicLong runTime;
    private final AtomicLong startTime;

    /** Only set during deserialization process. */
    private SerializedForm serializedForm;

    public Result() {
        count = new AtomicInteger();
        ignoreCount = new AtomicInteger();
        assumptionFailureCount = new AtomicInteger();
        failures = new CopyOnWriteArrayList<Failure>();
        runTime = new AtomicLong();
        startTime = new AtomicLong();
    }

    private Result(SerializedForm serializedForm) {
        count = serializedForm.fCount;
        ignoreCount = serializedForm.fIgnoreCount;
        assumptionFailureCount = serializedForm.assumptionFailureCount;
        failures = new CopyOnWriteArrayList<Failure>(serializedForm.fFailures);
        runTime = new AtomicLong(serializedForm.fRunTime);
        startTime = new AtomicLong(serializedForm.fStartTime);
    }

    /**
     * Returns the number of tests run
     */
    public int getRunCount() {
        return count.get();
    }

    /**
     * Returns the number of tests that failed during the run
     */
    public int getFailureCount() {
        return failures.size();
    }

    /**
     * Returns the number of milliseconds it took to run the entire suite to run
     */
    public long getRunTime() {
        return runTime.get();
    }

    /**
     * Returns the {@link Failure}s describing tests that failed and the problems they encountered
     */
    public List<Failure> getFailures() {
        return failures;
    }

    /**
     * @return the number of tests ignored during the run
     */
    public int getIgnoreCount() {
        return ignoreCount.get();
    }

    /**
     * Returns the number of tests skipped because of an assumption failure
     *
     * @throws UnsupportedOperationException if the result was serialized in a version before JUnit 4.13
     * @since 4.13
     */
    public int getAssumptionFailureCount() {
        if (assumptionFailureCount == null) {
            throw new UnsupportedOperationException(
                    "Result was serialized from a version of JUnit that doesn't support this method");
        }
        return assumptionFailureCount.get();
    }

    /**
     * @return <code>true</code> if all tests succeeded
     */
    public boolean wasSuccessful() {
        return getFailureCount() == 0;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        SerializedForm serializedForm = new SerializedForm(this);
        serializedForm.serialize(s);
    }

    private void readObject(ObjectInputStream s)
            throws ClassNotFoundException, IOException {
        serializedForm = SerializedForm.deserialize(s);
    }

    private Object readResolve()  {
        return new Result(serializedForm);
    }

    @RunListener.ThreadSafe
    private class Listener extends RunListener {
        @Override
        public void testRunStarted(Description description) throws Exception {
            startTime.set(System.currentTimeMillis());
        }

        @Override
        public void testRunFinished(Result result) throws Exception {
            long endTime = System.currentTimeMillis();
            runTime.addAndGet(endTime - startTime.get());
        }

        @Override
        public void testFinished(Description description) throws Exception {
            count.getAndIncrement();
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            failures.add(failure);
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            ignoreCount.getAndIncrement();
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            assumptionFailureCount.getAndIncrement();
        }
    }

    /**
     * Internal use only.
     */
    public RunListener createListener() {
        return new Listener();
    }

    /**
     * Represents the serialized output of {@code Result}. The fields on this
     * class match the files that {@code Result} had in JUnit 4.11.
     */
    private static class SerializedForm implements Serializable {
        private static final long serialVersionUID = 1L;
        private final AtomicInteger fCount;
        private final AtomicInteger fIgnoreCount;
        private final AtomicInteger assumptionFailureCount;
        private final List<Failure> fFailures;
        private final long fRunTime;
        private final long fStartTime;

        public SerializedForm(Result result) {
            fCount = result.count;
            fIgnoreCount = result.ignoreCount;
            assumptionFailureCount = result.assumptionFailureCount;
            fFailures = Collections.synchronizedList(new ArrayList<Failure>(result.failures));
            fRunTime = result.runTime.longValue();
            fStartTime = result.startTime.longValue();
        }

        @SuppressWarnings("unchecked")
        private SerializedForm(ObjectInputStream.GetField fields) throws IOException {
            fCount = (AtomicInteger) fields.get("fCount", null);
            fIgnoreCount = (AtomicInteger) fields.get("fIgnoreCount", null);
            assumptionFailureCount = (AtomicInteger) fields.get("assumptionFailureCount", null);
            fFailures = (List<Failure>) fields.get("fFailures", null);
            fRunTime = fields.get("fRunTime", 0L);
            fStartTime = fields.get("fStartTime", 0L);
        }

        public void serialize(ObjectOutputStream s) throws IOException {
            ObjectOutputStream.PutField fields = s.putFields();
            fields.put("fCount", fCount);
            fields.put("fIgnoreCount", fIgnoreCount);
            fields.put("fFailures", fFailures);
            fields.put("fRunTime", fRunTime);
            fields.put("fStartTime", fStartTime);
            fields.put("assumptionFailureCount", assumptionFailureCount);
            s.writeFields();
        }

        public static SerializedForm deserialize(ObjectInputStream s)
                throws ClassNotFoundException, IOException {
            ObjectInputStream.GetField fields = s.readFields();
            return new SerializedForm(fields);
        }
    }
}
