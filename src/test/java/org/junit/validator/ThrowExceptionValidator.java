package org.junit.validator;

import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.util.Arrays;
import java.util.List;

public class ThrowExceptionValidator extends AnnotationValidator {
    public List<Exception> validateTestClass(TestClass testClass) {
        return Arrays.asList(new Exception("test Exception1"), new Exception("Exception2"));
    }
    public List<Exception> validateAnnotatedClass(TestClass testClass) {
        return validateTestClass(testClass);
    }

    public List<Exception> validateAnnotatedField(FrameworkField field) {
        return validateTestClass(null);
    }

    public List<Exception> validateAnnotatedMethod(FrameworkMethod method) {
        return validateTestClass(null);
    }
}
