package org.junit.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import org.junit.ClassRunOrder;
import org.junit.MethodRunOrder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.RunnerBuilder;
import org.mearvk.MyTestClass2;

public class OrderedSuite
{
	public static Stack<Class> orderedClasses = new Stack<Class>();

    private static ArrayList<Class> registeredClasses = new ArrayList<Class>();
	
	public OrderedSuite(Class<?>[] classes)
    {
		orderClasses(classes);
	}
    
    public static void registerOrderedClass(Class<?> klass)
    {
        OrderedSuite.registeredClasses.add(klass);
    }

	//@Override
	public Description getDescription()
	{
		return Description.createSuiteDescription(orderedClasses.peek().getSimpleName());
	}

	//@Override
	public static void runNext(RunNotifier notifier)
	{
		System.err.println("OrderedSuite.runNext() called");

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
                System.err.println("Running "+classToRun.getSimpleName()+"."+method.getName());
                
                try
                {
                    //try and run the method
                    method.invoke(classToRun.newInstance(), (Object[])null);

                    //debugging help
                    System.err.println(classToRun.getSimpleName()+"."+method.getName()+" ran without error");
                }
                catch(Exception e)
                {
                    System.err.println(e);
                }
            }
            else System.err.println("Ignoring "+classToRun.getSimpleName()+"."+method.getName());
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
            public int compare(Object arg0, Object arg1)
            {
                Method m1 = (Method)arg0;
                Method m2 = (Method)arg1;
                
                MethodRunOrder m1RunOrder = m1.getAnnotation(MethodRunOrder.class);
                MethodRunOrder m2RunOrder = m2.getAnnotation(MethodRunOrder.class);
                
                if(m1RunOrder.order()<m2RunOrder.order()) return -1;
                if(m1RunOrder.order()>m2RunOrder.order()) return +1;

                return 0;
            }
        };
        
        Collections.sort(validMethods, methodRunOrderComparator);
        
        return validMethods;
    }
	
	private static void orderClasses(Class<?>[] classes)
	{
		List<Class<?>> list = Arrays.asList(classes);
		
		Comparator c = new Comparator()
		{
			@Override
			public int compare(Object arg0, Object arg1)
			{
                ClassRunOrder runOrderArg0 = (ClassRunOrder)((Class)arg0).getAnnotation(ClassRunOrder.class);
                ClassRunOrder runOrderArg1 = (ClassRunOrder)((Class)arg1).getAnnotation(ClassRunOrder.class);
				
				if(runOrderArg0.order()>runOrderArg1.order()) return -1;
				if(runOrderArg0.order()<runOrderArg1.order()) return +1;
				
				try
				{
					throw new Exception("Class "+arg0.getClass()+" has the same run order as class "+arg1.getClass());
				}
				catch (Exception e)
				{
					System.err.println(e);
                }

                return 0;
			}
		};
		
        //sort the list according to annotated run order (in reverse for the purpose of a stack)
		Collections.sort(list, c);
		
        //print out some stuff for debugging and push the list onto the stack
		for(Class<?> klass : list)
		{
			System.err.println("Class "+klass.getName()+" has order "+((ClassRunOrder)klass.getAnnotation(ClassRunOrder.class)).order());

            //push the last class to be run first, etc
            orderedClasses.push(klass);
		}
	}
}
