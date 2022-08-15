package junit.tests.runner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import junit.framework.TestCase;
import junit.tests.framework.Success;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.tests.running.methods.AnnotationTest;

public class ResultTest extends TestCase {

    private Result fromStream;

    public void testRunFailureResultCanBeSerialised() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AnnotationTest.FailureTest.class);
        assertResultSerializable(result);
    }

    public void testRunFailureResultCanBeReserialised_v4_12() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AnnotationTest.FailureTest.class);
        assertResultReserializable(result, SerializationFormat.V4_12);
    }

    public void testRunAssumptionFailedResultCanBeSerialised() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AssumptionFailedTest.class);
        assertResultSerializable(result);
    }

    public void testRunAssumptionFailedResultCanBeReserialised_v4_12() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AssumptionFailedTest.class);
        assertResultReserializable(result, SerializationFormat.V4_12);
    }

    public void testRunAssumptionFailedResultCanBeReserialised_v4_13() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AssumptionFailedTest.class);
        assertResultReserializable(result, SerializationFormat.V4_13);
    }

    public void testRunSuccessResultCanBeSerialised() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(Success.class);
        assertResultSerializable(result);
    }

    public void testRunSuccessResultCanBeReserialised_v4_12() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(Success.class);
        assertResultReserializable(result, SerializationFormat.V4_12);
    }

    public void testRunSuccessResultCanBeReserialised_v4_13() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(Success.class);
        assertResultReserializable(result, SerializationFormat.V4_13);
    }

    private enum SerializationFormat {
        V4_12,
        V4_13
    }

    private void assertResultSerializable(Result result) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(result);
        objectOutputStream.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Result fromStream = (Result) objectInputStream.readObject();
        assertSerializedCorrectly(result, fromStream, SerializationFormat.V4_13);
    }
 
    private void assertResultReserializable(Result result, SerializationFormat resourceSerializationFormat)
            throws IOException, ClassNotFoundException {
        String resourceName = getName();
        InputStream resource = getClass().getResourceAsStream(resourceName);
        assertNotNull("Could not read resource " + resourceName, resource);
        ObjectInputStream objectInputStream = new ObjectInputStream(resource);
        fromStream = (Result) objectInputStream.readObject();

        assertSerializedCorrectly(new ResultWithFixedRunTime(result),
                fromStream, resourceSerializationFormat);
    }

    public static class AssumptionFailedTest {
        @Test
        public void assumptionFailed() throws Exception {
            org.junit.Assume.assumeTrue(false);
        }
    }

    /**
     * A version of {@code Result} that returns a hard-coded runtime.
     * This makes values returned by the methods deterministic.
     */
    private static class ResultWithFixedRunTime extends Result {

        private static final long serialVersionUID = 1L;

        private final Result delegate;

        public ResultWithFixedRunTime(Result delegate) {
            this.delegate = delegate;
        }

        @Override
        public int getRunCount() {
            return delegate.getRunCount();
        }

        @Override
        public int getFailureCount() {
            return delegate.getFailureCount();
        }

        @Override
        public long getRunTime() {
            return 2;
        }

        @Override
        public List<Failure> getFailures() {
            return delegate.getFailures();
        }

        @Override
        public int getIgnoreCount() {
            return delegate.getIgnoreCount();
        }

        @Override
        public int getAssumptionFailureCount() {
            return delegate.getAssumptionFailureCount();
        }
    }

    private void assertSerializedCorrectly(
            Result result, Result fromStream, SerializationFormat serializationFormat) {
        assertNotNull(fromStream);

        // Exceptions don't implement equals() so we need to compare field by field
        assertEquals("failureCount", result.getFailureCount(), fromStream.getFailureCount());
        assertEquals("ignoreCount", result.getIgnoreCount(), fromStream.getIgnoreCount());

        if (serializationFormat == SerializationFormat.V4_13) {
            // assumption failures are serialized
            assertEquals("assumptionFailureCount",
                    result.getAssumptionFailureCount(),
                    fromStream.getAssumptionFailureCount());
        } else {
            // assumption failures were not serialized
            try {
                fromStream.getAssumptionFailureCount();
                fail("UnsupportedOperationException expected");
            } catch (UnsupportedOperationException expected) {
            }
        }

        assertEquals("runTime", result.getRunTime(), fromStream.getRunTime());
        assertEquals("failures", result.getFailures().size(), fromStream.getFailures().size());
        int index = 0;
        for (Failure failure : result.getFailures()) {
            Failure failureFromStream = fromStream.getFailures().get(index);
            String messagePrefix = String.format("failures[%d]", index++);
            assertEquals(messagePrefix + ".description",
                    failure.getDescription(), failureFromStream.getDescription());
            Throwable exception = failure.getException();
            Throwable exceptionFromStream = failureFromStream.getException();
            assertEquals(messagePrefix + ".exception",
                    exception.getClass(), exceptionFromStream.getClass());
            assertEquals(messagePrefix + ".exception",
                    exception.getMessage(), exceptionFromStream.getMessage());
        }
    }
}
