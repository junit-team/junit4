package org.junit.tests.experimental.rules;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TimeoutRuleTest {
    private static final ReentrantLock run1Lock = new ReentrantLock();

    private static volatile boolean run4done = false;

    public abstract static class AbstractTimeoutTest {
        public static final StringBuffer logger = new StringBuffer();

        @Rule
        public final TemporaryFolder tmpFile = new TemporaryFolder();

        @Test
        public void run1() throws InterruptedException {
            logger.append("run1");
            TimeoutRuleTest.run1Lock.lockInterruptibly();
            TimeoutRuleTest.run1Lock.unlock();
        }

        @Test
        public void run2() throws InterruptedException {
            logger.append("run2");
            Thread.currentThread().join();
        }

        @Test
        public synchronized void run3() throws InterruptedException {
            logger.append("run3");
            wait();
        }

        @Test
        public void run4() {
            logger.append("run4");
            while (!run4done) {
            }
        }

        @Test
        public void run5() throws IOException {
            logger.append("run5");
            Random rnd = new Random();
            byte[] data = new byte[1024];
            File tmp = tmpFile.newFile();
            while (true) {
                RandomAccessFile randomAccessFile = new RandomAccessFile(tmp, "rw");
                try {
                    FileChannel channel = randomAccessFile.getChannel();
                    rnd.nextBytes(data);
                    ByteBuffer buffer = ByteBuffer.wrap(data);
                    // Interrupted thread closes channel and throws ClosedByInterruptException.
                    channel.write(buffer);
                } finally {
                    randomAccessFile.close();
                }
                tmp.delete();
            }
        }

        @Test
        public void run6() throws InterruptedIOException {
            logger.append("run6");
            // Java IO throws InterruptedIOException only on SUN machines.
            throw new InterruptedIOException();
        }
    }

    public static class HasGlobalLongTimeout extends AbstractTimeoutTest {

        @Rule
        public final TestRule globalTimeout = Timeout.millis(200);
    }

    public static class HasGlobalTimeUnitTimeout extends AbstractTimeoutTest {

        @Rule
        public final TestRule globalTimeout = new Timeout(200, TimeUnit.MILLISECONDS);
    }
    
    public static class HasNullTimeUnit {

        @Rule
        public final TestRule globalTimeout = new Timeout(200, null);
        
        @Test
        public void wouldPass() {
        }
    }

    @Before
    public void before() {
        run4done = false;
        run1Lock.lock();
    }

    @After
    public void after() {
        // set run4done to make sure that the thread won't continue at run4()
        run4done = true;
        run1Lock.unlock();
    }

    @Test
    public void timeUnitTimeout() {
        HasGlobalTimeUnitTimeout.logger.setLength(0);
        Result result = JUnitCore.runClasses(HasGlobalTimeUnitTimeout.class);
        assertEquals(6, result.getFailureCount());
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run1"));
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run2"));
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run3"));
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run4"));
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run5"));
        assertThat(HasGlobalTimeUnitTimeout.logger.toString(), containsString("run6"));
    }

    @Test
    public void longTimeout() {
        HasGlobalLongTimeout.logger.setLength(0);
        Result result = JUnitCore.runClasses(HasGlobalLongTimeout.class);
        assertEquals(6, result.getFailureCount());
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run1"));
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run2"));
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run3"));
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run4"));
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run5"));
        assertThat(HasGlobalLongTimeout.logger.toString(), containsString("run6"));
    }

    @Test
    public void nullTimeUnit() {
        Result result = JUnitCore.runClasses(HasNullTimeUnit.class);
        assertEquals(1, result.getFailureCount());
        Failure failure = result.getFailures().get(0);
        assertThat(failure.getException().getMessage(),
                containsString("Invalid parameters for Timeout"));
        Throwable cause = failure.getException().getCause();
        assertThat(cause.getMessage(), containsString("TimeUnit cannot be null"));
    }
}
