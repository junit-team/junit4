package org.junit.it;

import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertTrue;
import static org.junit.it.BundlesUtil.MAIN_MVN_JAR_NAME_PATTERN;
import static org.junit.it.BundlesUtil.relativePathFiles;

/**
 * @author Tibor Digana (tibor17)
 * @since 4.12
 */
public class BundlesIT {
    @Test
    public void oneProducerBundle() {
        Collection<String> bundles = relativePathFiles(MAIN_MVN_JAR_NAME_PATTERN, "target/bundles");
        assertThat(bundles.size(), is(1));
        String consumerBundlePath = "^target/bundles/junit-(\\d+).(\\d+)?(.\\d+)(.jar|-SNAPSHOT.jar)$";
        assertTrue("Producer does not match the pattern " + consumerBundlePath,
                bundles.iterator().next().matches(consumerBundlePath));
    }

    @Test
    public void oneConsumerBundle() {
        Collection<String> targetJars = relativePathFiles(MAIN_MVN_JAR_NAME_PATTERN, "target");
        assertThat(targetJars.size(), is(1));
        String consumerBundlePath = "^target/integration-tests-(\\d+).(\\d+)?(.\\d+)(.jar|-SNAPSHOT.jar)$";
        assertTrue("Consumer does not match the pattern " + consumerBundlePath,
                targetJars.iterator().next().matches(consumerBundlePath));
    }
}
