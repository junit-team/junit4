package org.junit.experimental.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.JUnit4;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * Support tests declared on interfaces where they are defined as {@code Method#isDefaultMethod default} methods for
 * Java 8 and above, as well as those otherwise on the {@link JUnit4 default runner}.
 *
 * On earlier JVMs it will not behave any differently to the default runner.
 *
 * @author Ollie Robertshaw
 * @see JUnit4
 * @since 4.13
 */
public class Mixin extends BlockJUnit4ClassRunner {

    public Mixin(final Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected final TestClass createTestClass(final Class<?> testClass) {
        return new MixinDetectingTestClass(testClass);
    }

    protected static final class MixinDetectingTestClass extends TestClass {

        private static final Method isDefaultInterface;

        static {
            Method method;
            try {
                method = Method.class.getDeclaredMethod("isDefault");
            } catch (final Throwable t) {
                method = null;
            }
            isDefaultInterface = method;
        }

        MixinDetectingTestClass(final Class<?> clazz) {
            super(clazz);
        }

        @Override
        protected void scanAnnotatedMembers(
                final Map<Class<? extends Annotation>, List<FrameworkMethod>> methodsForAnnotations,
                final Map<Class<? extends Annotation>, List<FrameworkField>> fieldsForAnnotations) {
            super.scanAnnotatedMembers(methodsForAnnotations, fieldsForAnnotations);
            if (isDefaultInterface == null) {
                return;
            }
            for (final Class<?> interfaceClass : getInterfaces(this.getJavaClass())) {
                for (final Method method : interfaceClass.getDeclaredMethods()) {
                    try {
                        if (Boolean.TRUE.equals(isDefaultInterface.invoke(method))) {
                            addToAnnotationLists(new FrameworkMethod(method), methodsForAnnotations);
                        }
                    } catch (final Exception ex) {
                        //Ignore
                    }
                }
            }
        }

        private static Set<Class<?>> getInterfaces(final Class<?> clazz) {
            final Set<Class<?>> interfaces = new HashSet<Class<?>>();
            getInterfaces(clazz, interfaces);
            return interfaces;
        }

        private static void getInterfaces(final Class<?> clazz, final Set<Class<?>> interfaces) {
            for (final Class<?> interfaceClass : clazz.getInterfaces()) {
                if (interfaces.add(interfaceClass)) {
                    getInterfaces(interfaceClass, interfaces);
                }
            }
        }

    }

}
