package org.mearvk;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
 * @see http://code.google.com/p/junit-test-orderer/ for licensing questions.
 * 
 * @author Max Rupplin
 */
public class OrderedSuite
{
	public static Stack<Class<?>> orderedClasses = new Stack<Class<?>>();

    private static ArrayList<Class<?>> registeredClasses = new ArrayList<Class<?>>();
    
    public static void registerOrderedClass(Class<?> klass)
    {
        OrderedSuite.registeredClasses.add(klass);
    }

	public static void runNext(RunNotifier notifier)
	{        
		//if we haven't already ordered the classes
        if(orderedClasses.empty()) 
            orderClasses(OrderedSuite.registeredClasses);

        //get the next class we need to run in its proper order
        Class<?> classToRun = orderedClasses.pop();
        
        //request all declared methods
        Method[] declaredMethods = classToRun.getDeclaredMethods();
        
        //request that methods are ordered according to annotation notation (MethodRunOrder(order=1) and so on...)
        ArrayList<Method> methods = orderMethods(declaredMethods);
         
        //try and run each annotated method in the order given
        for(Method method : methods)
        {
            //double check that this method has the right annotation
            if(method.isAnnotationPresent(MethodRunOrder.class))
            {                                
            	//save this for brevity
            	Description testDescription = Description.createSuiteDescription(classToRun.getClass());
            	
                try
                {	              	
                	//notify listeners that test is about to start
                	notifier.fireTestRunStarted(testDescription);
                	
                    //try and run the method
                    method.invoke(classToRun.newInstance(), (Object[])null);
                    
                    //notify listeners that test run has completed
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
                catch(Throwable t)
                {
                	if(t.getCause()==null)
                	{
                		notifier.fireTestFailure(new Failure(testDescription, t));
                	}
                	else
                	{
                		notifier.fireTestFailure(new Failure(testDescription, t.getCause()));
                	}
                }
            }
        }
	}
    
    private static ArrayList<Method> orderMethods(Method[] methods)
    {    	
        ArrayList<Method> validMethods = new ArrayList<Method>();
        
        for(Method method : methods)
        {
            if(!method.isAnnotationPresent(MethodRunOrder.class)) continue;

            validMethods.add(method);
        }
        
        Comparator methodRunOrderComparator = new Comparator()
        {
            @Override
			public int compare(Object arg0, Object arg1)
            {
                Method m1 = (Method)arg0;
                Method m2 = (Method)arg1;
                
                MethodRunOrder m1RunOrder = m1.getAnnotation(MethodRunOrder.class);
                MethodRunOrder m2RunOrder = m2.getAnnotation(MethodRunOrder.class);
                
                if(m1RunOrder.order()<m2RunOrder.order()) return -1;
                if(m1RunOrder.order()>m2RunOrder.order()) return +1;

                throw new RuntimeException("Methods in the same class ("+arg0+") cannot have the same run order.");
            }
        };
        
        Collections.sort(validMethods, methodRunOrderComparator);
                
        return validMethods;
    }
	
	private static void orderClasses(ArrayList<Class<?>> classes)
	{
		Comparator c = new Comparator()
		{
			@Override
			public int compare(Object arg0, Object arg1)
			{
                ClassRunOrder runOrderArg0 = (ClassRunOrder)((Class)arg0).getAnnotation(ClassRunOrder.class);
                ClassRunOrder runOrderArg1 = (ClassRunOrder)((Class)arg1).getAnnotation(ClassRunOrder.class);
				
				if(runOrderArg0.order()>runOrderArg1.order()) return -1;
				if(runOrderArg0.order()<runOrderArg1.order()) return +1;				

				throw new RuntimeException(arg0+" has the same run order as "+arg1);
			}
		};
		
        //sort the list according to annotated run order (in reverse for the purpose of a stack)
		Collections.sort(classes, c);
		
        //print out some stuff for debugging and push the list onto the stack
		for(Class<?> klass : classes)
		{
            //push the last class to be run first, etc
            orderedClasses.push(klass);
		}
	}
}
