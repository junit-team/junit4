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

public class EnumDatapointNamesTest {
	
	@Test
	public void enumErrorsUseEnumNameWithSingleValues() throws InitializationError {
		Result result = runTheoryTest(EnumTestWithBadValue.class);
		
		Assert.assertEquals(1, result.getFailureCount());
		
		Throwable ex = result.getFailures().get(0).getException();
		Assert.assertThat(ex.getMessage(), containsString(EnumValue.BAD_VALUE.toString()));
		return;
	}
	
	@Test
	public void enumErrorsUseEnumNameWithArrays() throws InitializationError {
		Result result = runTheoryTest(EnumTestWithBadValueInArray.class);
		
		Assert.assertEquals(1, result.getFailureCount());
		
		Throwable ex = result.getFailures().get(0).getException();
		Assert.assertThat(ex.getMessage(), containsString(EnumValue.BAD_VALUE.toString()));
		return;
	}	
	
	private Result runTheoryTest(Class<?> testClass) throws InitializationError {
		JUnitCore junitRunner = new JUnitCore();
		Runner theoryRunner = new Theories(testClass);
		Request request = Request.runner(theoryRunner);
		return junitRunner.run(request);
	}
	
    private enum EnumValue {
        ONE_VALUE, BAD_VALUE, ANOTHER_VALUE 
    }
    
    private static void methodUnderTest(EnumValue param) {
        if (param == EnumValue.BAD_VALUE) {
            throw new IllegalArgumentException("Bad param");
        }
    }
	
	public static class EnumTestWithBadValue {
		
	    @DataPoint
	    public static EnumValue oneValue = EnumValue.ONE_VALUE;
	    
	    @DataPoint 
	    public static EnumValue badValue = EnumValue.BAD_VALUE;
	
	    @Theory
	    public void theoryTest(EnumValue param) {
	    	methodUnderTest(param);
	    }
	    
	}	
	
	public static class EnumTestWithBadValueInArray {
	
	    @DataPoints
	    public static EnumValue[] allValues = EnumValue.values();
	
	    @Theory
	    public void theoryTest(EnumValue param) {
	    	methodUnderTest(param);
	    }
	    
	}

}