package org.junit.experimental.theories.internal;

import static java.util.Collections.emptyList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.ParametersSuppliedBy;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.experimental.theories.PotentialAssignment.CouldNotGenerateValueException;
import org.junit.runners.model.TestClass;

/**
 * A potentially incomplete list of value assignments for a method's formal
 * parameters
 */
public class Assignments {
    private final List<PotentialAssignment> assigned;

    private final List<ParameterSignature> unassigned;

    private final TestClass clazz;

    private Assignments(List<PotentialAssignment> assigned,
            List<ParameterSignature> unassigned, TestClass clazz) {
        this.unassigned = unassigned;
        this.assigned = assigned;
        this.clazz = clazz;
    }

    /**
     * Returns a new assignment list for {@code testMethod}, with no params
     * assigned.
     */
    public static Assignments allUnassigned(Method testMethod,
            TestClass testClass) {
        List<ParameterSignature> signatures;
        signatures = ParameterSignature.signatures(testClass
                .getOnlyConstructor());
        signatures.addAll(ParameterSignature.signatures(testMethod));
        return new Assignments(new ArrayList<PotentialAssignment>(),
                signatures, testClass);
    }

    public boolean isComplete() {
        return unassigned.isEmpty();
    }

    public ParameterSignature nextUnassigned() {
        return unassigned.get(0);
    }

    public Assignments assignNext(PotentialAssignment source) {
        List<PotentialAssignment> potentialAssignments = new ArrayList<PotentialAssignment>(assigned);
        potentialAssignments.add(source);

        return new Assignments(potentialAssignments, unassigned.subList(1,
                unassigned.size()), clazz);
    }

    public Object[] getActualValues(int start, int stop) 
            throws CouldNotGenerateValueException {
        Object[] values = new Object[stop - start];
        for (int i = start; i < stop; i++) {
            values[i - start] = assigned.get(i).getValue();
        }
        return values;
    }

    public List<PotentialAssignment> potentialsForNextUnassigned()
            throws Throwable {
        ParameterSignature unassigned = nextUnassigned();
        List<PotentialAssignment> assignments = getSupplier(unassigned).getValueSources(unassigned);
        
        if (assignments.isEmpty()) {
            assignments = generateAssignmentsFromTypeAlone(unassigned);
        }
        
        return assignments;
    }

    private List<PotentialAssignment> generateAssignmentsFromTypeAlone(ParameterSignature unassigned) {
        Class<?> paramType = unassigned.getType();
        
        if (paramType.isEnum()) {
            return new EnumSupplier(paramType).getValueSources(unassigned);  
        } else if (paramType.equals(Boolean.class) || paramType.equals(boolean.class)) {
            return new BooleanSupplier().getValueSources(unassigned);
        } else {
            return emptyList();
        }
    }

    private ParameterSupplier getSupplier(ParameterSignature unassigned)
            throws Exception {
        ParametersSuppliedBy annotation = unassigned
                .findDeepAnnotation(ParametersSuppliedBy.class);
        
        if (annotation != null) {
            return buildParameterSupplierFromClass(annotation.value());
        } else {
            return new AllMembersSupplier(clazz);
        }
    }

    private ParameterSupplier buildParameterSupplierFromClass(
            Class<? extends ParameterSupplier> cls) throws Exception {
        Constructor<?>[] supplierConstructors = cls.getConstructors();

        for (Constructor<?> constructor : supplierConstructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1
                    && parameterTypes[0].equals(TestClass.class)) {
                return (ParameterSupplier) constructor.newInstance(clazz);
            }
        }

        return cls.newInstance();
    }

    public Object[] getConstructorArguments()
            throws CouldNotGenerateValueException {
        return getActualValues(0, getConstructorParameterCount());
    }

    public Object[] getMethodArguments() throws CouldNotGenerateValueException {
        return getActualValues(getConstructorParameterCount(), assigned.size());
    }

    public Object[] getAllArguments() throws CouldNotGenerateValueException {
        return getActualValues(0, assigned.size());
    }

    private int getConstructorParameterCount() {
        List<ParameterSignature> signatures = ParameterSignature
                .signatures(clazz.getOnlyConstructor());
        int constructorParameterCount = signatures.size();
        return constructorParameterCount;
    }

    public Object[] getArgumentStrings(boolean nullsOk)
            throws CouldNotGenerateValueException {
        Object[] values = new Object[assigned.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = assigned.get(i).getDescription();
        }
        return values;
    }
}