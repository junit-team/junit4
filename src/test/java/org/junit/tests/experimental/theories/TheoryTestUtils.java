package org.junit.tests.experimental.theories;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public final class TheoryTestUtils {
    
    private TheoryTestUtils() { }
    
    public static List<PotentialAssignment> potentialAssignments(Method method)
            throws Throwable {
        return Assignments.allUnassigned(method,
                new TestClass(method.getDeclaringClass()))
                .potentialsForNextUnassigned();
    }
    
    public static Result runTheoryClass(Class<?> testClass) throws InitializationError {
        Runner theoryRunner = new Theories(testClass);
        Request request = Request.runner(theoryRunner);
        return new JUnitCore().run(request);
    }

}
