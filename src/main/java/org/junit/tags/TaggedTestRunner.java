package org.junit.tags;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.Arrays;
import java.util.HashSet;

/**
 * This is a custom JUnit test runner which checks for allowable test tags and allow/ignore the test.
 * Tests needs to be tagged using the annotation {@link RunTags @RunTags}.
 * <pre>
 *      &#064;Test
 *      &#064;RunTags(reason = "in-secure environment", tags = {"integration"})
 *      public void testIntegration() throws Exception {
 *          // actual test and assertions
 *      }
 * </pre>
 * <p/>
 * A runtime System Property is passed to the test using
 * {@code -Dorg.junit.RunTags="integration,quality"}
 * This runner filters out all the tests that are NOT tagged with above system property.
 * <p/>
 * With the above system property, here are some of the cases to consider
 * <ul>
 * <li>Tests tagged with "integration" - are executed
 * <li>Tests tagged with "integration, quality" - are executed
 * <li>Tests tagged with "integration, production" - are executed
 * <li>Tests tagged with "production" - are ignored
 * <li>Tests tagged with empty "" - are ignored
 * <li>Tests missing the annotation {@link RunTags @RunTags}  - are executed
 * <li>Tests annotated with {@link org.junit.Ignore @Ignore}  - are ignored
 * </ul>
 * Whenever a test is ignored for mismatching tag, a reason is displayed. e.g.
 * <pre>{@code
 * Test 'org.junit.tags.SampleTest.testProduction' ignored. Reason: in-secure environment, Expected: [integration, quality], Actual: [production]}</pre>
 * <p/>
 * See SampleTest
 * @see RunTags annotation @RunTags
 */
public class TaggedTestRunner extends BlockJUnit4ClassRunner {

    public static final String           TAG_SYS_PROPERTY = "org.junit.runtags";
    private static HashSet<String> RUN_TAGS         = getAllowedTags();

    public TaggedTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        if (isTagged(method)) {
            super.runChild(method, notifier);
        } else {
            Description description = describeChild(method);
            notifier.fireTestIgnored(description);
        }
    }
    
    public static void readRunTags() {
    	RUN_TAGS         = getAllowedTags();
    }

    private static HashSet<String> getAllowedTags() {
        String allowedTags = System.getProperty(TAG_SYS_PROPERTY, null);
        if (null == allowedTags || allowedTags.trim().length() < 1) {
            //System.out.println("TaggedTestRunner: No tags specified, " + TAG_SYS_PROPERTY + " System Property is empty.");
            return null;
        }
        //System.out.println("TaggedTestRunner: Running tests tagged with "+allowedTags);
        return new HashSet<String>(Arrays.asList(allowedTags.split(",")));
    }

    private static boolean isTagged(FrameworkMethod method) {

        if (null == RUN_TAGS || RUN_TAGS.isEmpty()) {
            //System.out.println("TagCheck: No tags specified, " + TAG_SYS_PROPERTY + " System Property is empty.");
            return true;
        }
        RunTags runTagsAnnotation = method.getAnnotation(RunTags.class);
        if (null == method.getAnnotation(RunTags.class)) {
            //System.out.format("TagCheck: Test Allowed. No tag filters found. Method: %s\n", method.getName());
            return true;
        }
        final String[] methodTags = runTagsAnnotation.tags();
        for (String checkTag : methodTags) {
            if (RUN_TAGS.contains(checkTag)) {
                //System.out.format("TagCheck: Test Allowed. Tag Found. Method: %s, Tag: [%s] \n", method.getName(),
                //                  checkTag);
                return true;
            }
        }
        /*
        System.err.format("Test '%s.%s' ignored. Reason: %s, Expected: %s, Actual: %s \n",
                          method.getMethod().getDeclaringClass().getName(), method.getName(),
                          runTagsAnnotation.reason(), RUN_TAGS, Arrays.toString(methodTags));
                          */
        return false;
    }
}
