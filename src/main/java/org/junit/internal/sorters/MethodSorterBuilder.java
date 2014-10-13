package org.junit.internal.sorters;

import org.junit.runners.model.InitializationError;

public final class MethodSorterBuilder {
    private static final String CONSTRUCTOR_ERROR_FORMAT = "Custom method sorter class %s should have a public constructor with signature %s()";

    public static MethodSorter buildMethodSorter(Class<? extends MethodSorter> methodSorterClass) throws Exception {
        try {
            return methodSorterClass.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            String simpleName = methodSorterClass.getSimpleName();
            throw new InitializationError(String.format(CONSTRUCTOR_ERROR_FORMAT, simpleName, simpleName));
        }
    }
}