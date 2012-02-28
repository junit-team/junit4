package org.mearvk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import org.junit.internal.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;

/**
 * A group of ordered runners together with their test classes
 * 
 * @see <a href="http://code.google.com/p/junit-test-orderer">Licensing, code source, etc.</a>
 * 
 * @author Max Rupplin
 */
public class OrderedSuite
{
	/**
	 *  Stack which contains the classes which have already been ordered (class 1 runs before class 2, etc)
	 */
	protected static Stack<Class<?>> orderedClasses = new Stack<Class<?>>();

	/**
	 * List which contains the classes which will be run but not yet have been ordered
	 */
	protected static ArrayList<Class<?>> undorderedClasses = new ArrayList<Class<?>>();

	/**
	 * Registers classes which are passed in from the {@code OrderedTestRunner} class
	 * 
	 * @param klass The class we are registering to be run
	 */
	public static void registerUnorderedClass(Class<?> klass)
	{
		OrderedSuite.undorderedClasses.add(klass);
	}

	/**
	 * Runs the next test class on the Stack {@code orderedClasses} of already ordered test classes
	 * 
	 * @param notifier JUnit's notification listener
	 */
	public static void runNext(RunNotifier notifier)
	{
		// if we haven't already ordered the classes
		if (orderedClasses.empty())
			orderClasses(OrderedSuite.undorderedClasses);

		// get the next class we need to run in its proper order
		Class<?> classToRun = orderedClasses.pop();

		// request all declared methods
		Method[] declaredMethods = classToRun.getDeclaredMethods();

		// request that methods are ordered according to annotation notation
		// (MethodRunOrder(order=1) and so on...)
		ArrayList<Method> methods = orderMethods(declaredMethods);

		// try and run each annotated method in the order given
		for (Method method : methods)
		{
			// double check that this method has the right annotation
			//if (method.isAnnotationPresent(MethodRunOrder.class))
			//{
				// save this for brevity
				Description testDescription = Description.createSuiteDescription(classToRun.getClass());

				try
				{
					// notify listeners that test is about to start
					notifier.fireTestRunStarted(testDescription);

					// try and run the method
					method.invoke(classToRun.newInstance(), (Object[]) null);

					// notify listeners that test run has completed
					notifier.fireTestFinished(testDescription);
				}
				catch (AssumptionViolatedException e)
				{
					notifier.fireTestIgnored(testDescription);
				}
				catch (StoppedByUserException e)
				{
					throw e;
				}
				catch (Throwable t)
				{
					if (t.getCause() == null)
					{
						notifier.fireTestFailure(new Failure(testDescription, t));
					}
					else
					{
						notifier.fireTestFailure(new Failure(testDescription, t
								.getCause()));
					}
				}
			//}
		}
	}

	/**
	 * Orders the methods with a single class at runtime so they run in appropriate order
	 * 
	 * @param methods The methods of a class we care to order by {@code MethodRunOrder} annotation
	 * @return An array of ordered methods (least to greatest)
	 */
	private static ArrayList<Method> orderMethods(Method[] methods)
	{
		ArrayList<Method> validMethods = new ArrayList<Method>();

		for (Method method : methods)
		{
			if (!method.isAnnotationPresent(MethodRunOrder.class))
				continue;

			validMethods.add(method);
		}
		
		//if there are no annotations to describe the method ordering just return the list as-is
		if(validMethods.size()==0) 
		{
			System.err.println();
			return new ArrayList<Method>(Arrays.asList(methods));
		}

		Comparator<Method> methodRunOrderComparator = new Comparator<Method>()
		{
			public int compare(Method m1, Method m2)
			{
				MethodRunOrder m1RunOrder = m1
						.getAnnotation(MethodRunOrder.class);
				MethodRunOrder m2RunOrder = m2
						.getAnnotation(MethodRunOrder.class);

				if (m1RunOrder.order() < m2RunOrder.order())
					return -1;
				if (m1RunOrder.order() > m2RunOrder.order())
					return +1;

				throw new RuntimeException("Methods in the same class (" + m1
						+ ") cannot have the same run order.");
			}
		};

		Collections.sort(validMethods, methodRunOrderComparator);

		return validMethods;
	}

	/**
	 * Orders the classes which have been registered by the {@code OrderedTestRunner} class
	 * 
	 * @param classes The classes which will be ordered by {@code ClassRunOrder} annotation
	 */
	private static void orderClasses(ArrayList<Class<?>> classes)
	{
		Comparator<Object> c = new Comparator<Object>()
		{
			@SuppressWarnings("unchecked")
			public int compare(Object arg0, Object arg1)
			{
				@SuppressWarnings("rawtypes")
				Annotation anno1 = ((Class) arg0)
						.getAnnotation(ClassRunOrder.class);

				@SuppressWarnings("rawtypes")
				Annotation anno2 = ((Class) arg1)
						.getAnnotation(ClassRunOrder.class);

				int runOrder1 = 0;
				int runOrder2 = 0;

				if (anno1 instanceof ClassRunOrder)
					runOrder1 = ((ClassRunOrder) anno1).order();

				if (anno2 instanceof ClassRunOrder)
					runOrder2 = ((ClassRunOrder) anno2).order();

				if (runOrder1 > runOrder2)
					return -1;
				if (runOrder1 < runOrder2)
					return +1;

				throw new RuntimeException(arg0 + " has the same run order as "+ arg1);
			}
		};

		// sort the list according to annotated run order (in reverse for the
		// purpose of a stack)
		Collections.sort(classes, c);

		// print out some stuff for debugging and push the list onto the stack
		for (Class<?> klass : classes)
		{
			// push the last class to be run first, etc
			orderedClasses.push(klass);
		}
	}
}
