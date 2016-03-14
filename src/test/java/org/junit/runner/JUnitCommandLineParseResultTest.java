package org.junit.runner;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.IncludeCategories;
import org.junit.rules.ExpectedException;
import org.junit.runner.manipulation.Filter;

public class JUnitCommandLineParseResultTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final JUnitCommandLineParseResult jUnitCommandLineParseResult = new JUnitCommandLineParseResult();

    @Test
    public void shouldStopParsingOptionsUponDoubleHyphenArg() throws Exception {
        String[] restOfArgs = jUnitCommandLineParseResult.parseOptions(
                "--0", "--1", "--", "--2", "--3");

        assertThat(restOfArgs, is(new String[]{"--2", "--3"}));
    }

    @Test
    public void shouldParseFilterArgWithEqualsSyntax() throws Exception {
        String value= IncludeCategories.class.getName() + "=" + DummyCategory0.class.getName();
        jUnitCommandLineParseResult.parseOptions("--filter=" + value);

        List<String> specs= jUnitCommandLineParseResult.getFilterSpecs();

        assertThat(specs, hasItems(value));
    }

    @Test
    public void shouldCreateFailureUponBaldFilterOptionNotFollowedByValue() {
        jUnitCommandLineParseResult.parseOptions("--filter");

        Runner runner = jUnitCommandLineParseResult.createRequest(new Computer()).getRunner();
        Description description = runner.getDescription().getChildren().get(0);

        assertThat(description.toString(), containsString("initializationError"));
    }

    @Test
    public void shouldParseFilterArgInWhichValueIsASeparateArg() throws Exception {
        String value= IncludeCategories.class.getName() + "=" + DummyCategory0.class.getName();
        jUnitCommandLineParseResult.parseOptions("--filter", value);

        List<String> specs= jUnitCommandLineParseResult.getFilterSpecs();

        assertThat(specs, hasItems(value));
    }

    @Test
    public void shouldStopParsingOptionsUponNonOption() throws Exception {
        String[] restOfArgs = jUnitCommandLineParseResult.parseOptions(new String[]{
                "--0", "--1", "2", "3"
        });

        assertThat(restOfArgs, is(new String[]{"2", "3"}));
    }

    @Test
    public void shouldCreateFailureUponUnknownOption() throws Exception {
        String unknownOption = "--unknown-option";
        jUnitCommandLineParseResult.parseOptions(new String[]{
                unknownOption
        });

        Runner runner = jUnitCommandLineParseResult.createRequest(new Computer()).getRunner();
        Description description = runner.getDescription().getChildren().get(0);

        assertThat(description.toString(), containsString("initializationError"));
    }

    @Test
    public void shouldCreateFailureUponUncreatedFilter() throws Exception {
        jUnitCommandLineParseResult.parseOptions(new String[]{
                "--filter=" + FilterFactoryStub.class.getName()
        });

        Runner runner = jUnitCommandLineParseResult.createRequest(new Computer()).getRunner();
        Description description = runner.getDescription().getChildren().get(0);

        assertThat(description.toString(), containsString("initializationError"));
    }

    @Test
    public void shouldCreateFailureUponUnfoundFilterFactory() throws Exception {
        String nonExistentFilterFactory = "NonExistentFilterFactory";
        jUnitCommandLineParseResult.parseOptions(new String[]{
                "--filter=" + nonExistentFilterFactory
        });

        Runner runner = jUnitCommandLineParseResult.createRequest(new Computer()).getRunner();
        Description description = runner.getDescription().getChildren().get(0);

        assertThat(description.toString(), containsString("initializationError"));
    }

    @Test
    public void shouldAddToClasses() {
        jUnitCommandLineParseResult.parseParameters(new String[]{
                DummyTest.class.getName()
        });

        List<Class<?>> classes = jUnitCommandLineParseResult.getClasses();
        Class<?> testClass = classes.get(0);

        assertThat(testClass.getName(), is(DummyTest.class.getName()));
    }

    @Test
    public void shouldCreateFailureUponUnknownTestClass() throws Exception {
        String unknownTestClass = "UnknownTestClass";
        jUnitCommandLineParseResult.parseParameters(new String[]{
                unknownTestClass
        });

        Runner runner = jUnitCommandLineParseResult.createRequest(new Computer()).getRunner();
        Description description = runner.getDescription().getChildren().get(0);

        assertThat(description.toString(), containsString("initializationError"));
    }

    public static class FilterFactoryStub implements FilterFactory {
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
