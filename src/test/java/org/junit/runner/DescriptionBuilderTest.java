package org.junit.runner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.samples.SimpleAnnotatedTest;
import org.junit.samples.SimpleTest;

public class DescriptionBuilderTest {
    private static final List<ImmutableDescription> NO_CHILDREN = Collections.emptyList();
    private final NameBasedDescriptionBuilder namedBuilder = DescriptionBuilder.forName("test");
    private final ClassBasedDescriptionBuilder classBasedBuilder = DescriptionBuilder.forClass(SimpleTest.class);
    private final MethodBasedDescriptionBuilder methodBasedBuilder;

    public DescriptionBuilderTest() throws Exception {
        methodBasedBuilder = DescriptionBuilder.forMethod(
                SimpleTest.class, SimpleTest.class.getMethod("divideByZero"));
    }

    @Test
    public void namedDescriptionBuilder_hasCorrectNameAndUniqueIdSet() throws Exception {
        assertThat(namedBuilder.displayName, is("test"));
        assertThat(namedBuilder.uniqueId.toString(), is("test"));
    }

    @Test
    public void namedDescriptionBuilder_hasEmptyAnnotations() throws Exception {
        assertThat(namedBuilder.annotations, is(notNullValue()));
        assertThat(namedBuilder.annotations.isEmpty(), is(true));
    }

    @Test
    public void namedDescriptionBuilder_generatesDescriptions_withTestClassOf_null() throws Exception {
        ImmutableDescription testDescription = namedBuilder.createTestDescription();
        assertThat(testDescription.getTestClass(), is(nullValue()));

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(NO_CHILDREN);
        assertThat(suiteDescription.getTestClass(), is(nullValue()));
    }

    @Test
    public void namedDescriptionBuilder_generatesTestAndSuiteDescriptions() throws Exception {
        ImmutableDescription testDescription = namedBuilder.createTestDescription();
        assertThat(testDescription.isTest(), is(true));
        assertThat(testDescription.isSuite(), is(false));
        assertEquals("test", testDescription.getDisplayName());

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(NO_CHILDREN);
        assertThat(suiteDescription.isTest(), is(false));
        assertThat(suiteDescription.isSuite(), is(true));
        Description reference = Description.createSuiteDescription("test");
        assertEquals(reference.getDisplayName(), suiteDescription.getDisplayName());
        assertEquals("test", suiteDescription.getDisplayName());
    }

    @Test
    public void classBasedDescriptionBuilder_hasCorrectNameAndUniqueIdSet() throws Exception {
        assertThat(classBasedBuilder.displayName, is(SimpleTest.class.getName()));
        assertThat(classBasedBuilder.uniqueId.toString(), is(SimpleTest.class.getCanonicalName()));
    }

    @Test
    public void classBasedDescriptionBuilder_hasAnnotationsFromClass() throws Exception {
        assertThat(classBasedBuilder.annotations, is(notNullValue()));
        assertThat(classBasedBuilder.annotations.isEmpty(), is(true));

        ClassBasedDescriptionBuilder classBasedBuilder2 = DescriptionBuilder.forClass(SimpleAnnotatedTest.class);
        assertThat(classBasedBuilder2.annotations, is(notNullValue()));
        assertThat(classBasedBuilder2.annotations.isEmpty(), is(false));
        assertThat(classBasedBuilder2.annotations.size(), is(1));
        assertThat(
                classBasedBuilder2.annotations.iterator().next().annotationType(),
                CoreMatchers.<Class<? extends Annotation>>equalTo(RunWith.class));
    }

    @Test
    public void classBasedDescriptionBuilder_generatesSuiteDescription() throws Exception {
        ImmutableDescription suiteDescription = classBasedBuilder.createSuiteDescription(NO_CHILDREN);
        assertThat(suiteDescription.isTest(), is(false));
        assertThat(suiteDescription.isSuite(), is(true));
        assertThat(suiteDescription.getTestClass(), CoreMatchers.<Class<?>>sameInstance(SimpleTest.class));
        Description reference = Description.createSuiteDescription(SimpleTest.class.getName());
        assertEquals(reference.getDisplayName(), suiteDescription.getDisplayName());
        assertEquals("org.junit.samples.SimpleTest", suiteDescription.getDisplayName());
    }

    @Test
    public void methodBasedDescriptionBuilder_hasAnnotationsFromMethod() throws Exception {
        assertThat(methodBasedBuilder.annotations, is(notNullValue()));
        assertThat(methodBasedBuilder.annotations.isEmpty(), is(false));
        assertThat(methodBasedBuilder.annotations.size(), is(1));
        assertThat(
                methodBasedBuilder.annotations.iterator().next().annotationType(),
                CoreMatchers.<Class<? extends Annotation>>equalTo(Test.class));
    }

    @Test
    public void methodBasedDescriptionBuilder_generatesTestDescription() throws Exception {
        ImmutableDescription testDescription = methodBasedBuilder.createTestDescription();
        assertThat(testDescription.isTest(), is(true));
        assertThat(testDescription.isSuite(), is(false));
        assertThat(testDescription.getTestClass(), CoreMatchers.<Class<?>>sameInstance(SimpleTest.class));
        Description reference = Description.createTestDescription(SimpleTest.class, "divideByZero");
        assertEquals(reference.getDisplayName(), testDescription.getDisplayName());
        assertEquals("divideByZero(org.junit.samples.SimpleTest)", testDescription.getDisplayName());
    }

    @Test
    public void namedDescriptionBuilder_withDisplayNameUpdatesTheDisplayName() throws Exception {
        namedBuilder.withDisplayName("newDisplayName");
        assertEquals("newDisplayName", namedBuilder.displayName);

        ImmutableDescription testDescription = namedBuilder.createTestDescription();
        assertEquals("newDisplayName", testDescription.getDisplayName());

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(NO_CHILDREN);
        assertEquals("newDisplayName", suiteDescription.getDisplayName());
    }

    @Test
    public void methodBasedDescriptionBuilder_withDisplayNameUpdatesTheDisplayName() throws Exception {
        methodBasedBuilder.withDisplayName("newDisplayName");
        assertEquals("newDisplayName", methodBasedBuilder.displayName);

        ImmutableDescription testDescription = methodBasedBuilder.createTestDescription();
        assertEquals("newDisplayName", testDescription.getDisplayName());
    }

    @Test
    public void namedDescriptionBuilder_withUniqueIdUpdatesTheUniqueId() throws Exception {
        Serializable uniqueId = new UniqueId();
        namedBuilder.withUniqueId(uniqueId);
        assertThat(namedBuilder.uniqueId, CoreMatchers.sameInstance(uniqueId));

        ImmutableDescription testDescription = namedBuilder.createTestDescription();
        assertThat(testDescription.fUniqueId, CoreMatchers.sameInstance(uniqueId));

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(NO_CHILDREN);
        assertThat(suiteDescription.fUniqueId, CoreMatchers.sameInstance(uniqueId));
    }
 
    @Test
    public void classBasedDescriptionBuilder_withUniqueIdUpdatesTheUniqueId() throws Exception {
        Serializable uniqueId = new UniqueId();
        classBasedBuilder.withUniqueId(uniqueId);
        assertThat(classBasedBuilder.uniqueId, CoreMatchers.sameInstance(uniqueId));

        ImmutableDescription suiteDescription = classBasedBuilder.createSuiteDescription(NO_CHILDREN);
        assertThat(suiteDescription.fUniqueId, CoreMatchers.sameInstance(uniqueId));
    }

    @Test
    public void methodBasedDescriptionBuilder_withUniqueIdUpdatesTheUniqueId() throws Exception {
        Serializable uniqueId = new UniqueId();
        methodBasedBuilder.withUniqueId(uniqueId);
        assertThat(methodBasedBuilder.uniqueId, CoreMatchers.sameInstance(uniqueId));

        ImmutableDescription testDescription = methodBasedBuilder.createTestDescription();
        assertThat(testDescription.fUniqueId, CoreMatchers.sameInstance(uniqueId));
    }
 
    @Test
    public void withAdditionalAnnotations_updatesTheAnnotations() throws Exception {
        Annotation newAnnotation = new Annotation() {
            public Class<? extends Annotation> annotationType() {
                return this.getClass();
            }
        };

        namedBuilder.withAdditionalAnnotations(newAnnotation);
        assertThat(namedBuilder.annotations, is(notNullValue()));
        assertThat(namedBuilder.annotations.isEmpty(), is(false));
        assertThat(namedBuilder.annotations.size(), is(1));

        ImmutableDescription testDescription = namedBuilder.createTestDescription();
        assertThat(testDescription.getAnnotations(), is(notNullValue()));
        assertThat(testDescription.getAnnotations().isEmpty(), is(false));
        assertThat(testDescription.getAnnotations().size(), is(1));

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(NO_CHILDREN);
        assertThat(suiteDescription.getAnnotations(), is(notNullValue()));
        assertThat(suiteDescription.getAnnotations().isEmpty(), is(false));
        assertThat(suiteDescription.getAnnotations().size(), is(1));
    }

    @Test
    public void suiteDescriptionWithChildren_childrenAreSet() throws Exception {
        ImmutableDescription child = namedBuilder.createTestDescription();
        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(Arrays.asList(child));

        assertThat(suiteDescription.getChildren(), is(notNullValue()));
        assertThat(suiteDescription.getChildren().isEmpty(), is(false));
        assertThat(suiteDescription.getChildren().size(), is(1));
    }

    @Test
    public void testDescription_serializesToMutableDescription() throws Exception {
        assertSerializesToMutableDescription(methodBasedBuilder.createTestDescription());
    }

    @Test
    public void suiteDescriptionWithoutChildren_serializesToMutableDescription() throws Exception {
        assertSerializesToMutableDescription(classBasedBuilder.createSuiteDescription(NO_CHILDREN));
    }

    @Test
    public void suiteDescriptionWithChildren_serializesToMutableDescription() throws Exception {
        ImmutableDescription child = namedBuilder.createTestDescription();
        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(Arrays.asList(child));

        assertSerializesToMutableDescription(suiteDescription);
    }
 
    private void assertSerializesToMutableDescription(Description description)
            throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteArrayOutputStream).writeObject(description);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Object fromStream = objectInputStream.readObject();
        assertEquals(Description.class, fromStream.getClass());
        Description result = Description.class.cast(fromStream);

        assertThat(result, is(description));
    }
 
    private static class UniqueId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Override
        public String toString() {
            return "uniqueId@" + Integer.toHexString(hashCode());
        }
    }
}