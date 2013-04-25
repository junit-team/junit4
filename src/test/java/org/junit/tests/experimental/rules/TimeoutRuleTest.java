package org.junit.tests.experimental.rules;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
                FileChannel channel = new RandomAccessFile(tmp, "rw").getChannel();
                rnd.nextBytes(data);
                ByteBuffer buffer = ByteBuffer.wrap(data);
                // Interrupted thread closes channel and throws ClosedByInterruptException.
                channel.write(buffer);
                channel.close();
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
    public void timeUnitTimeout() throws InterruptedException {
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
    public void longTimeout() throws InterruptedException {
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
}
