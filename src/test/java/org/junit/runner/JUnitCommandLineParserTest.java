package org.junit.runner;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.IncludeCategories;
import org.junit.rules.ExpectedException;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.tests.TestSystem;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

public class JUnitCommandLineParserTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private JUnitCommandLineParser jUnitCommandLineParser = new JUnitCommandLineParser(new TestSystem());

    @Test
    public void shouldStopParsingOptionsUponDoubleHyphenArg() throws Exception {
        String[] restOfArgs = jUnitCommandLineParser.parseOptions(new String[]{
                "--0", "--1", "--", "--2", "--3"
        });

        assertThat(restOfArgs, is(new String[]{"--2", "--3"}));
    }

    @Test
    public void shouldParseFilterArgWithEqualsSyntax() throws Exception {
        jUnitCommandLineParser.parseOptions(new String[]{
                "--filter=" + IncludeCategories.class.getName() + "=" + DummyCategory0.class.getName()
        });

        Filter filter = jUnitCommandLineParser.getFilter();

        assertThat(filter.describe(), startsWith("includes "));
    }

    @Test
    public void shouldCreateFailureUponBaldFilterOptionNotFollowedByValue() {
        jUnitCommandLineParser.parseOptions(new String[]{
                "--filter"
        });

        List<Failure> failures = jUnitCommandLineParser.getFailures();
        Throwable exception = failures.get(0).getException();

        assertThat(exception, instanceOf(JUnitCommandLineParser.CommandLineParserError.class));
    }

    @Test
    public void shouldParseFilterArgInWhichValueIsASeparateArg() throws Exception {
        jUnitCommandLineParser.parseOptions(new String[]{
                "--filter",
                IncludeCategories.class.getName() + "=" + DummyCategory0.class.getName()
        });

        Filter filter = jUnitCommandLineParser.getFilter();

        assertThat(filter.describe(), startsWith("includes "));
    }

    @Test
    public void shouldStopParsingOptionsUponNonOption() throws Exception {
        String[] restOfArgs = jUnitCommandLineParser.parseOptions(new String[]{
                "--0", "--1", "2", "3"
        });

        assertThat(restOfArgs, is(new String[]{"2", "3"}));
    }

    @Test
    public void shouldCreateFailureUponUnknownOption() throws Exception {
        jUnitCommandLineParser.parseOptions(new String[]{
                "--unknown-option"
        });

        List<Failure> failures = jUnitCommandLineParser.getFailures();
        Throwable exception = failures.get(0).getException();

        assertThat(exception, instanceOf(JUnitCommandLineParser.CommandLineParserError.class));
    }

    @Test
    public void shouldCreateFailureUponUncreatedFilter() throws Exception {
        jUnitCommandLineParser.parseOptions(new String[]{
                "--filter=" + FilterFactoryStub.class.getName()
        });

        List<Failure> failures = jUnitCommandLineParser.getFailures();
        Throwable exception = failures.get(0).getException();

        assertThat(exception, instanceOf(FilterFactory.FilterNotCreatedException.class));
    }

    @Test
    public void shouldCreateFailureUponUnfoundFilterFactory() throws Exception {
        jUnitCommandLineParser.parseOptions(new String[]{
                "--filter=NonExistentFilterFactory"
        });

        List<Failure> failures = jUnitCommandLineParser.getFailures();
        Throwable exception = failures.get(0).getException();

        assertThat(exception, instanceOf(FilterFactoryFactory.FilterFactoryNotCreatedException.class));
    }

    @Test
    public void shouldAddToClasses() {
        jUnitCommandLineParser.parseParameters(new String[]{
                DummyTest.class.getName()
        });

        List<Class<?>> classes = jUnitCommandLineParser.getClasses();
        Class<?> testClass = classes.get(0);

        assertThat(testClass.getName(), is(DummyTest.class.getName()));
    }

    @Test
    public void shouldCreateFailureUponUnknownTestClass() throws Exception {
        jUnitCommandLineParser.parseParameters(new String[]{
                "UnknownTestClass"
        });

        List<Failure> failures = jUnitCommandLineParser.getFailures();
        Throwable exception = failures.get(0).getException();

        assertThat(exception, instanceOf(ClassNotFoundException.class));
    }

    public static class FilterFactoryStub implements FilterFactory {
        @Override
        public Filter createFilter(FilterFactoryParams params) throws FilterNotCreatedException {
            throw new FilterNotCreatedException(new Exception("stub"));
        }
    }

    public static interface DummyCategory0 {
    }

    public static class DummyTest {
        @Test
        public void dummyTest() {
        }
    }
}
