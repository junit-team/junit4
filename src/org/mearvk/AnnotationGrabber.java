package org.mearvk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnotationGrabber
{
	public static ArrayList<Method> grabMethodsWithAnnotations(Class<?> klass, Class<? extends Annotation> anno)
	{
		ArrayList<Method> interestingMethods = new ArrayList<Method>();
	
		ArrayList<Method> methods 		= new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
		ArrayList<Method> superMethods 	= new ArrayList<Method>(Arrays.asList(klass.getSuperclass().getDeclaredMethods()));
		ArrayList<Method> allMethods 	= (ArrayList<Method>) methods.clone();
		
		//consolidate both declared and super methods
		allMethods.addAll(superMethods);
		
		for(Method method : allMethods)
		{
			System.err.println("");
			
			if(method.isAnnotationPresent(anno))
			{
				interestingMethods.add(method);
			}
		}
		
		return interestingMethods;
	}
}
