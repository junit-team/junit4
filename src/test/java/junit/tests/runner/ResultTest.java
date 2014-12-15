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
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.tests.running.methods.AnnotationTest;

public class ResultTest extends TestCase {

    public void testRunFailureResultCanBeSerialised() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(AnnotationTest.FailureTest.class);
        assertResultSerializable(result);
    }

    public void testRunSuccessResultCanBeSerialised() throws Exception {
        JUnitCore runner = new JUnitCore();
        Result result = runner.run(Success.class);
        assertResultSerializable(result);
    }

    private void assertResultSerializable(Result result) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(result);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Result fromStream = (Result) objectInputStream.readObject();
        assertSerializedCorrectly(result, fromStream);

        InputStream resource = getClass().getResourceAsStream(getName());
        assertNotNull("Could not read resource " + getName(), resource);
        objectInputStream = new ObjectInputStream(resource);
        fromStream = (Result) objectInputStream.readObject();
        
        assertSerializedCorrectly(new ResultWithFixedRunTime(result), fromStream);
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
    }

    private void assertSerializedCorrectly(Result result, Result fromStream) {
        assertNotNull(fromStream);

        // Exceptions don't implement equals() so we need to compare field by field
        assertEquals("failureCount", result.getFailureCount(), fromStream.getFailureCount());
        assertEquals("ignoreCount", result.getIgnoreCount(), fromStream.getIgnoreCount());
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
