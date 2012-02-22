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

public class OrderedSuite extends Runner
{
	public static Stack<Class> classesToBeRun = new Stack<Class>();
	private RunnerBuilder builder = null;
	
	public OrderedSuite(RunnerBuilder builder, Class<?>[] classes)
	{
		this.builder = builder;
		this.orderClasses(classes);
	}

	@Override
	public Description getDescription()
	{
		//classesToBeRun.peek().getCanonicalName()
		return Description.createSuiteDescription("To be impl'd", new Annotation[]{});
	}

	@Override
	public void run(RunNotifier notifier)
	{
		System.err.println("OrderedSuite.run() called");

        do
        {
            Class<?> klassToRun = classesToBeRun.pop();
            
            Method[] declaredMethods = klassToRun.getDeclaredMethods();
            
            ArrayList<Method> methods = orderMethods(declaredMethods);

            for(Method method : methods)
            {
                if(method.isAnnotationPresent(MethodRunOrder.class))
                {
                    System.err.println("Running "+klassToRun.getSimpleName()+"."+method.getName());
                }
                else System.err.println("Ignoring "+klassToRun.getSimpleName()+"."+method.getName());
            }
        }
        while(!classesToBeRun.empty());
	}
    
    private ArrayList<Method> orderMethods(Method[] methods)
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
	
	private void orderClasses(Class<?>[] classes)
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
		
		Collections.sort(list, c);
		
		for(Class<?> klass : list)
		{
			System.err.println("Class "+klass.getName()+" has order "+((ClassRunOrder)klass.getAnnotation(ClassRunOrder.class)).order());

            //push the last class to be run first, etc
            classesToBeRun.push(klass);
		}
	}
}
