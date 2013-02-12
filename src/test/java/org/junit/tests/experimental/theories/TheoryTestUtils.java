package org.junit.tests.experimental.theories;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.internal.Assignments;
import org.junit.runners.model.TestClass;

public final class TheoryTestUtils {
    
    private TheoryTestUtils() { }
    
    public static List<PotentialAssignment> potentialAssignments(Method method)
            throws Exception {
        return Assignments.allUnassigned(method,
                new TestClass(method.getDeclaringClass()))
                .potentialsForNextUnassigned();
    }

}
