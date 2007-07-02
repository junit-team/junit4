/**
 * 
 */
package org.junit.experimental.theories.matchers.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CamelCaseName {
	private static final Pattern PATTERN = Pattern.compile("([A-Za-z][a-z]*)");
	
	private final String methodName;

	public CamelCaseName(String methodName) {
		this.methodName = methodName;
	}

	public String asNaturalLanguage() {
		String description = "";
		Matcher matcher = PATTERN.matcher(methodName);
		while (matcher.find())
			description += " " + matcher.group().toLowerCase();
		return description.substring(1);
	}
}