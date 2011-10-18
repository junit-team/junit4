package org.junit.tags;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(TaggedTestRunner.class)

public class TaggedTest {

    @Test
    public void testEverywhere() throws Exception {
    	debug("Running Test: testEverywhere");
        // actual test and assertions
    }

    @Test
    @RunTags(tags = {"integration"})
    public void testIntegration() throws Exception {
    	debug("Running Test: testIntegration");
        // actual test and assertions
    }
    
    @Test
    @RunTags(tags = {"integration", "quality"})
    public void testQualityIntegration() throws Exception {
    	debug("Running Test: testQualityIntegration");
        // actual test and assertions
    }

    @Test
    @RunTags(reason = "none", tags = {"quality"})
    public void testQuality() throws Exception {
    	debug("Running Test: testQuality");
        // actual test and assertions
    }

    @Test
    @RunTags(reason = "secure environment", tags = {"production"})
    public void testProduction() throws Exception {
    	debug("Running Test: testProduction");
        // actual test and assertions
    }

    @Test 
    @Ignore("always ignored")
    public void testNowhere() throws Exception {
    	debug("Running Test: testNowhere");
        // actual test and assertions
    }
    
    private void debug(String msg) {
    	//System.out.println(msg);
    }
}
