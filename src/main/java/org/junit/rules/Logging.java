package org.junit.rules;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.LevelRangeFilter;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;

/**
 * The Logging Rule allows to check if any logs were created by {@link org.apache.log4j.Logger} during
 * the run of an application and to relieve them. The logs can be filtered by {@link org.apache.log4j.Level}.
 *
 * By default al logs are captured.
 *
 * <p>Example of usage:
 * <pre>
 * public static class HasLogs {
 *  &#064;Rule
 *  public Logging logging = new Logging();
 *
 *  &#064;Test
 *  public void testUsingTempFolder() throws IOException {
 *      SomeClass.methodThatWriteToLog();
 *      List<LoggingEvent> allLogs = logging.getAllLogs();
 *      List<LoggingEvent> errorAndAboveLogs = logging.getAllLogsAbove(Level.Error)
 *      // ...
 *     }
 * }
 * </pre>
 *
 */
public class Logging implements TestRule{
    private static final String MESSAGE_WITHOUT_THROWABLE = "%s: %s, %s.";
    private static final String MESSAGE_WITH_THROWABLE = MESSAGE_WITHOUT_THROWABLE + " %s";
    protected TestAppender appender;
    protected final Logger logger = Logger.getRootLogger();

    @Override
    public Statement apply(Statement base, Description description) {
        return statement(base);
    }

    private Statement statement(final Statement base) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();
                try {
                    base.evaluate();
                } finally {
                    after();
                }
            }
        };
    }

    protected void before() throws Throwable {
        appender = new TestAppender();
        logger.addAppender(appender);
    }

    protected void after() {
        for (LoggingEvent event : appender.getLog()) {
            if (event.getThrowableInformation() != null) {
                System.out.printf(MESSAGE_WITH_THROWABLE,
                        event.getLoggerName(), event.getLevel(), event.getMessage(), event.getThrowableInformation().getThrowable().getMessage());
                System.out.println();
            } else {
                System.out.printf(MESSAGE_WITHOUT_THROWABLE,
                        event.getLoggerName(), event.getLevel(), event.getMessage());
                System.out.println();
            }
        }
        logger.removeAppender(appender);
    }

    public List<LoggingEvent> getAllLogs() {
        return appender.getLog();
    }

    public List<LoggingEvent> getAllLogsAbove(Level level) {
        Filter rangeFilter = createFilter(level);
        return appender.getLogsByLevel(rangeFilter);
    }

    protected LevelRangeFilter createFilter(Level level) {
        LevelRangeFilter rangeFilter = new LevelRangeFilter();
        rangeFilter.setLevelMin(level);
        rangeFilter.setLevelMax(Level.FATAL);
        rangeFilter.setAcceptOnMatch(true);
        return rangeFilter;
    }

    private class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> log = new ArrayList<LoggingEvent>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(final LoggingEvent loggingEvent) {
            log.add(loggingEvent);
        }

        @Override
        public void close() {
        }

        public List<LoggingEvent> getLogsByLevel(Filter filter) {
            TestAppender testAppender = new TestAppender();
            testAppender.addFilter(filter);
            for (LoggingEvent event : log) {
                testAppender.doAppend(event);
            }
            return testAppender.getLog();
        }

        public List<LoggingEvent> getLog() {
            return new ArrayList<LoggingEvent>(log);
        }
    }

}
