package org.junit.experimental.theories.internal;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assume;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

/**
 * Supplies Theory parameters based on all public members of the target class.
 */
public class AllMembersSupplier extends ParameterSupplier {
    static class MethodParameterValue extends PotentialAssignment {
        private final FrameworkMethod fMethod;

        private MethodParameterValue(FrameworkMethod dataPointMethod) {
            fMethod = dataPointMethod;
        }

        @Override
        public Object getValue() throws CouldNotGenerateValueException {
            try {
                return fMethod.invokeExplosively(null);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(
                        "unexpected: argument length is checked");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                        "unexpected: getMethods returned an inaccessible method");
            } catch (Throwable t) {
                DataPoint annotation = fMethod.getAnnotation(DataPoint.class); 
                if (annotation != null) {
                    for (Class<? extends Throwable> ignorable : annotation.ignoredExceptions()) {
                        Assume.assumeThat(t, not(instanceOf(ignorable)));
                    }
                }
                
                throw new CouldNotGenerateValueException(t);
            }
        }

        @Override
        public String getDescription() throws CouldNotGenerateValueException {
            return fMethod.getName();
        }
    }   
    
    private final TestClass fClass;

    /**
     * Constructs a new supplier for {@code type}
     */
    public AllMembersSupplier(TestClass type) {
        fClass = type;
    }

    @Override
    public List<PotentialAssignment> getValueSources(ParameterSignature sig) throws Throwable {
        List<PotentialAssignment> list = new ArrayList<PotentialAssignment>();

        addSinglePointFields(sig, list);
        addMultiPointFields(sig, list);
        addSinglePointMethods(sig, list);
        addMultiPointMethods(sig, list);

        return list;
    }

    private void addMultiPointMethods(ParameterSignature sig, List<PotentialAssignment> list) throws Throwable {
        for (FrameworkMethod dataPointsMethod : getDataPointsMethods(sig)) {
            try {
                addMultiPointArrayValues(sig, dataPointsMethod.getName(), list, dataPointsMethod.invokeExplosively(null));
            } catch (Throwable t) {
                DataPoints annotation = dataPointsMethod.getAnnotation(DataPoints.class);
                if (annotation != null) {
                    for (Class<? extends Throwable> ignored : annotation.ignoredExceptions()) {
                        if (ignored.isAssignableFrom(t.getClass())) {
                            return;
                        }
                    }
                }
                throw t;
            }
        }
    }

    private void addSinglePointMethods(ParameterSignature sig,
            List<PotentialAssignment> list) {
        for (FrameworkMethod dataPointMethod : getSingleDataPointMethods(sig)) {
            if (sig.canAcceptType(dataPointMethod.getType())) {
                list.add(new MethodParameterValue(dataPointMethod));
            }
        }
    }
    
    private void addMultiPointFields(ParameterSignature sig,
            List<PotentialAssignment> list) {
        for (final Field field : getDataPointsFields(sig)) {
            Class<?> type = field.getType();
            if (sig.canAcceptArrayType(type)) {
                try {
                    addArrayValues(field.getName(), list, getStaticFieldValue(field));
                } catch (Throwable e) {
                    // ignore and move on
                }
            }
        }
    }    

    private void addSinglePointFields(ParameterSignature sig,
            List<PotentialAssignment> list) {
        for (final Field field : getSingleDataPointFields(sig)) {
            Class<?> type = field.getType();
            if (sig.canAcceptType(type)) {
                list.add(PotentialAssignment.forValue(field.getName(), getStaticFieldValue(field)));
            }
        }
    }

    private void addArrayValues(String name, List<PotentialAssignment> list, Object array) {
        for (int i = 0; i < Array.getLength(array); i++) {
            list.add(PotentialAssignment.forValue(name + "[" + i + "]", Array.get(array, i)));
        }
    }

    private void addMultiPointArrayValues(ParameterSignature sig, String name, List<PotentialAssignment> list,
            Object array) throws Throwable {
        for (int i = 0; i < Array.getLength(array); i++) {
            if (!sig.canAcceptValue(Array.get(array, i))) {
                return;
            }
            list.add(PotentialAssignment.forValue(name + "[" + i + "]", Array.get(array, i)));
        }
    }

    private Object getStaticFieldValue(final Field field) {
        try {
            return field.get(null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "unexpected: field from getClass doesn't exist on object");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "unexpected: getFields returned an inaccessible field");
        }
    }

    protected Collection<FrameworkMethod> getDataPointsMethods(ParameterSignature sig) {
        return fClass.getAnnotatedMethods(DataPoints.class);        
    }
    
    protected Collection<Field> getSingleDataPointFields(ParameterSignature sig) {
        List<FrameworkField> fields = fClass.getAnnotatedFields(DataPoint.class);
        Collection<Field> validFields = new ArrayList<Field>();

        for (FrameworkField frameworkField : fields) {
            validFields.add(frameworkField.getField());
        }

        return validFields;
    }
    
    protected Collection<Field> getDataPointsFields(ParameterSignature sig) {
        List<FrameworkField> fields = fClass.getAnnotatedFields(DataPoints.class);
        Collection<Field> validFields = new ArrayList<Field>();

        for (FrameworkField frameworkField : fields) {
            validFields.add(frameworkField.getField());
        }

        return validFields;
    }
    
    protected Collection<FrameworkMethod> getSingleDataPointMethods(ParameterSignature sig) {
        return fClass.getAnnotatedMethods(DataPoint.class);
    }

}