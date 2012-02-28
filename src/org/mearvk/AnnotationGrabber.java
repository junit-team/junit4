package org.mearvk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class AnnotationGrabber
{
	public static ArrayList<Method> grabMethodsWithAnnotations(Class klass, Class<? extends Annotation> anno)
	{
		ArrayList<Method> interestingMethods = new ArrayList<Method>();
	
		Method[] methods = klass.getDeclaredMethods();
		
		for(Method method : methods)
		{
			if(method.isAnnotationPresent(anno))
			{
				interestingMethods.add(method);
			}
		}
		
		return interestingMethods;
	}
}
