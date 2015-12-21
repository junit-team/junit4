package org.junit.rules;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;

import java.security.InvalidParameterException;

import static org.hamcrest.MatcherAssert.assertThat;

public class LoggingTest {

    @Rule
    public Logging logging = new Logging();

    static final Logger logger = Logger.getLogger(LoggingTest.class);

    @Test
    public void testLoggerWithThrowable() throws Exception {
        //execute
        try {
            throw new InvalidParameterException("Testing invalid exception");
        } catch (InvalidParameterException e) {
            logger.error("'error', Caught exception", e);
            logger.info("'Info', Caught exception", e);
        }

        //assert
        assertThat(logging.getAllLogs().size(), CoreMatchers.is(0));
        assertThat(logging.getAllLogsAbove(Level.DEBUG).size(), CoreMatchers.is(2));
        assertThat(logging.getAllLogsAbove(Level.INFO).size(), CoreMatchers.is(2));
        assertThat(logging.getAllLogsAbove(Level.WARN).size(), CoreMatchers.is(1));
        assertThat(logging.getAllLogsAbove(Level.ERROR).size(), CoreMatchers.is(1));
        assertThat(logging.getAllLogsAbove(Level.TRACE).size(), CoreMatchers.is(2));
        assertThat(logging.getAllLogsAbove(Level.FATAL).size(), CoreMatchers.is(0));
    }

    @Test
    public void testLoggerWithoutThrowable() throws Exception {
        //execute
        try {
            throw new InvalidParameterException("Testing invalid exception");
        } catch (InvalidParameterException e) {
            logger.error("'error', Caught exception");
            logger.info("'Info', Caught exception");
        }

        //assert
        assertThat(logging.getAllLogs().size(), CoreMatchers.is(0));
        assertThat(logging.getAllLogsAbove(Level.DEBUG).size(), CoreMatchers.is(2));
        assertThat(logging.getAllLogsAbove(Level.INFO).size(), CoreMatchers.is(2));
        assertThat(logging.getAllLogsAbove(Level.WARN).size(), CoreMatchers.is(1));
        assertThat(logging.getAllLogsAbove(Level.ERROR).size(), CoreMatchers.is(1));
        assertThat(logging.getAllLogsAbove(Level.TRACE).size(), CoreMatchers.is(2));
        assertThat(logging.getAllLogsAbove(Level.FATAL).size(), CoreMatchers.is(0));
    }
}