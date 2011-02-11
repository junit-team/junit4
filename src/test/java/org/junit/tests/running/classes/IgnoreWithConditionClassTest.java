package org.junit.tests.running.classes;

import org.junit.Ignore;
import org.junit.RuntimeCondition;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;

import static org.junit.Assert.*;

public class IgnoreWithConditionClassTest {
  public static class AlwaysTrue implements RuntimeCondition {
    public boolean isTrue(FrameworkMethod method) {
      return true;
    }
    public boolean isTrue(Description description) {
      return true;
    }
  }
	@Ignore(ifTrue = AlwaysTrue.class) public static class IgnoreMeAlso {
		@Test public void iFail() {
			fail();
		}
		
		@Test public void iFailToo() {
			fail();
		}
	}
	
	@Test public void ignoreClass() {
		Result result= JUnitCore.runClasses(IgnoreMeAlso.class);
		assertEquals(0, result.getFailureCount());
		assertEquals(1, result.getIgnoreCount());
	}
}
