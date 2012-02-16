package org.junit.tags;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.Before;
import org.junit.Ignore;

public class TagCheckTest {
	
  private static final HashMap<String, Integer> TAG_MAP = new HashMap<String,Integer>();
	
  @BeforeClass 
	public static void populateTagMap() {
		try {
		Class klassObj = TaggedTest.class;
		Method[] methods = klassObj.getDeclaredMethods();
		for(Method m:methods) {
			if(m.isAnnotationPresent(Ignore.class)) {
				insertTag("Ignored");
				continue;
			}
			if(!m.isAnnotationPresent(Test.class)) {
				insertTag("NotTest");
				continue;
			}
			insertTag("ValidTest");
			RunTags tags =  m.getAnnotation(RunTags.class);
			if(tags == null) {
				//System.out.println("Missing Tag for"+m.getName());
				insertTag("MissingTag");
				continue;
			}
			String[] tagValues = tags.tags();
			if(tagValues.length == 0) {
				insertTag("EmptyTag");
				continue;
			}
			for(String val: tagValues) {
				insertTag(val);
			}
			
		}
		}catch(Exception exp) {
			exp.printStackTrace();
		}
	}
	public static int getTaggedMethods(String tagValue) {
		Integer tagCount = TAG_MAP.get(tagValue);
		if(tagCount == null) {
			return 0;
		}
		return tagCount;
	}
	
	private static void insertTag(String key) {
		Integer intValue = TAG_MAP.get(key);
		if(null == intValue) {
			TAG_MAP.put(key, new Integer(1));
		} else{
			TAG_MAP.put(key, ++intValue);
		}
	}
	public void setSysTag(String tag) {
		if(null == tag) {
			System.clearProperty(TaggedTestRunner.TAG_SYS_PROPERTY);
			return;
		}
		System.setProperty(TaggedTestRunner.TAG_SYS_PROPERTY,tag);
		TaggedTestRunner.readRunTags();
	}
	
	@Test
	public void runTagNull() {
		setSysTag(null);
		Result result= JUnitCore.runClasses(TaggedTest.class);
		int expected = getTaggedMethods("ValidTest");
		assertEquals("null tag",expected,result.getRunCount());
	}
	
	@Test
	public void runTagEmpty() {
		setSysTag("");
		Result result= JUnitCore.runClasses(TaggedTest.class);
		int expected = getTaggedMethods("ValidTest");
		assertEquals("null tag",expected,result.getRunCount());
	}
	
	@Test
	public void runTagQuality() {
		setSysTag("quality");
		Result result= JUnitCore.runClasses(TaggedTest.class);
		int expected = getTaggedMethods("MissingTag") + getTaggedMethods("quality");
		assertEquals("quality tagged",expected,result.getRunCount());
	}
	
	@Test
	public void runTagMultiple() {
		setSysTag("quality,integration");
		Result result= JUnitCore.runClasses(TaggedTest.class);
		int expected = getTaggedMethods("MissingTag") + getTaggedMethods("quality") + getTaggedMethods("integration");
		assertTrue("multiple tagged",expected>result.getRunCount());
	}
}
