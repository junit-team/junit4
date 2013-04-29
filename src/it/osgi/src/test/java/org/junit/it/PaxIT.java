package org.junit.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class PaxIT {

    @Configuration
    public static Option[] baseConfiguration() {
        return options(
                systemProperty("activatorFinishedSuccessfully").value("false"),
                workingDirectory("target/pax-cache"),
                cleanCaches(true),
                frameworkProperty("felix.bootdelegation.implicit").value("false"),

                mavenBundle("junit", "junit").versionAsInProject(),
                mavenBundle("junit", "integration-tests").versionAsInProject(),
                wrappedBundle(maven("org.hamcrest", "hamcrest-core").versionAsInProject())
                        .bundleSymbolicName("org.hamcrest.hamcrest-core")
                        .exports("org.hamcrest", "org.hamcrest.core")
        );
    }

    @Test
    public void test() throws Exception {
        assertTrue("Activator failed in a test.", Boolean.getBoolean("activatorFinishedSuccessfully"));
    }
}
