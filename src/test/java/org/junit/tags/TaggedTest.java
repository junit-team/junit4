package org.junit.tags;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TaggedTestRunner.class)

public class TaggedTest {

    @Test
    public void testEverywhere() throws Exception {
        // actual test and assertions
    }

    @Test
    @RunTags(tags = {"integration"})
    public void testIntegration() throws Exception {
        // actual test and assertions
    }

    @Test
    @RunTags(reason = "none", tags = {"quality"})
    public void testQuality() throws Exception {
        // actual test and assertions
    }

    @Test
    @RunTags(reason = "secure environment", tags = {"production"})
    public void testProduction() throws Exception {
        // actual test and assertions
    }

    @Test 
    @Ignore("always ignored")
    public void testNowhere() throws Exception {
        // actual test and assertions
    }
}
