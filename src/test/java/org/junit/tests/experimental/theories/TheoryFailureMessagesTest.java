package org.junit.tests.experimental.theories;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;

public class TheoryFailureMessagesTest {
	
	@Test
	public void failuresUseNameAndValueWithNullValues() throws InitializationError {
		Result result = runTheoryTest(TestWithBadNullValue.class);
		
		Assert.assertEquals(1, result.getFailureCount());
		
		String errorMessage = result.getFailures().get(0).getException().getMessage();
		Assert.assertThat(errorMessage, containsString("badDatapoint"));
		Assert.assertThat(errorMessage, containsString("null"));
	}	
	
	@Test
	public void failuresUseNameAndValueWithSingleValues() throws InitializationError {
		Result result = runTheoryTest(TestWithBadValue.class);
		
		Assert.assertEquals(1, result.getFailureCount());
		
		String errorMessage = result.getFailures().get(0).getException().getMessage();
		Assert.assertThat(errorMessage, containsString("badDatapoint"));
		Assert.assertThat(errorMessage, containsString(BAD_VALUE));
	}
	
	@Test
	public void failuresUseNameAndValueWithArrays() throws InitializationError {
		Result result = runTheoryTest(TestWithBadValueInArray.class);
		
		Assert.assertEquals(1, result.getFailureCount());
		
		String errorMessage = result.getFailures().get(0).getException().getMessage();
		Assert.assertThat(errorMessage, containsString("allValues[1]"));
		Assert.assertThat(errorMessage, containsString(BAD_VALUE));
		
		return;
	}
	
	private Result runTheoryTest(Class<?> testClass) throws InitializationError {
		JUnitCore junitRunner = new JUnitCore();
		Runner theoryRunner = new Theories(testClass);
		Request request = Request.runner(theoryRunner);
		return junitRunner.run(request);
	}

	private static final String BAD_VALUE = "bad value";
    
    private static void methodUnderTest(String param) {
        if (param.equals(BAD_VALUE)) {
            throw new IllegalArgumentException("Bad param");
        }
    }
    
    public static class TestWithBadNullValue {
		
	    @DataPoint
	    public static String oneDatapoint = "good value";
	    
	    @DataPoint 
	    public static String badDatapoint = null;
	
	    @Theory
	    public void theoryTest(String param) {
	    	methodUnderTest(param);
	    }
	    
	}	
	
	public static class TestWithBadValue {
		
	    @DataPoint
	    public static String oneDatapoint = "good value";
	    
	    @DataPoint 
	    public static String badDatapoint = BAD_VALUE;
	
	    @Theory
	    public void theoryTest(String param) {
	    	methodUnderTest(param);
	    }
	    
	}	
	
	public static class TestWithBadValueInArray {
	
	    @DataPoints
	    public static String[] allValues = new String[] { "string", BAD_VALUE, "value 3" };
	
	    @Theory
	    public void theoryTest(String param) {
	    	methodUnderTest(param);
	    }
	    
	}

}