package org.junit.internal.builders;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.RunnerSpy;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerBuilderStub;

public class AnnotatedBuilderTest {
    private AnnotatedBuilder builder = new AnnotatedBuilder(new RunnerBuilderStub());

    @Test
    public void topLevelTestClassWithoutAnnotation_isRunWithDefaultRunner() throws Exception {
        Runner runner = builder.runnerForClass(Object.class);
        assertThat(runner, is(nullValue()));
    }

    @Test
    public void topLevelTestClassWithAnnotation_isRunWithAnnotatedRunner() throws Exception {
        Runner runner = builder.runnerForClass(OuterClass.class);
        assertThat(runner, is(instanceOf(RunnerSpy.class)));

        RunnerSpy runnerSpy = (RunnerSpy) runner;
        assertThat(runnerSpy.getInvokedTestClass(), is((Object) OuterClass.class));
    }

    @Test
    public void memberClassInsideAnnotatedTopLevelClass_isRunWithTopLevelRunner() throws Exception {
        Runner runner = builder.runnerForClass(OuterClass.InnerClassWithoutOwnRunWith.class);
        assertThat(runner, is(instanceOf(RunnerSpy.class)));

        RunnerSpy runnerSpy = (RunnerSpy) runner;
        assertThat(runnerSpy.getInvokedTestClass(), is((Object) OuterClass.InnerClassWithoutOwnRunWith.class));
    }

    @Test
    public void memberClassDeepInsideAnnotatedTopLevelClass_isRunWithTopLevelRunner() throws Exception {
        Runner runner = builder.runnerForClass(OuterClass.InnerClassWithoutOwnRunWith.MostInnerClass.class);
        assertThat(runner, is(instanceOf(RunnerSpy.class)));

        RunnerSpy runnerSpy = (RunnerSpy) runner;
        assertThat(runnerSpy.getInvokedTestClass(), is((Object) OuterClass.InnerClassWithoutOwnRunWith.MostInnerClass.class));
    }

    @Test
    public void annotatedMemberClassInsideAnnotatedTopLevelClass_isRunWithOwnRunner() throws Exception {
        Runner runner = builder.runnerForClass(OuterClass.InnerClassWithOwnRunWith.class);
        assertThat(runner, is(instanceOf(InnerRunner.class)));

        RunnerSpy runnerSpy = (RunnerSpy) runner;
        assertThat(runnerSpy.getInvokedTestClass(), is((Object) OuterClass.InnerClassWithOwnRunWith.class));
    }

    @Test
    public void memberClassDeepInsideAnnotatedMemberClass_isRunWithParentMemberClassRunner() throws Exception {
        Runner runner = builder.runnerForClass(OuterClass.InnerClassWithOwnRunWith.MostInnerClass.class);
        assertThat(runner, is(instanceOf(InnerRunner.class)));

        RunnerSpy runnerSpy = (RunnerSpy) runner;
        assertThat(runnerSpy.getInvokedTestClass(), is((Object) OuterClass.InnerClassWithOwnRunWith.MostInnerClass.class));
    }

    @RunWith(RunnerSpy.class)
    public static class OuterClass {
        public class InnerClassWithoutOwnRunWith {
            @Test
            public void test() {
            }

            public class MostInnerClass {
                @Test
                public void test() {
                }
            }
        }

        @RunWith(InnerRunner.class)
        public class InnerClassWithOwnRunWith {
            @Test
            public void test() {
            }

            public class MostInnerClass {
                @Test
                public void test() {
                }
            }
        }
    }

    public static class InnerRunner extends RunnerSpy {
        public InnerRunner(Class<?> testClass) {
            super(testClass);
        }

        public InnerRunner(Class<?> testClass, RunnerBuilder runnerBuilder) {
            super(testClass, runnerBuilder);
        }
    }
}