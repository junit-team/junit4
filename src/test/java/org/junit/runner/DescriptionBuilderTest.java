package org.junit.runner;

import org.junit.Test;
import org.junit.samples.SimpleAnnotatedTest;
import org.junit.samples.SimpleTest;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class DescriptionBuilderTest {
    private final DescriptionBuilder namedBuilder = DescriptionBuilder.forName("test");
    private final DescriptionBuilder classBasedBuilder = DescriptionBuilder.forClass(SimpleTest.class);

    @Test
    public void namedDescriptionBuilder_hasCorrectNameAndUniqueIdSet() throws Exception {
        assertThat(namedBuilder.displayName, is("test"));
        assertThat(namedBuilder.uniqueId, is("test"));
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

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(Collections.EMPTY_LIST);
        assertThat(suiteDescription.getTestClass(), is(nullValue()));
    }

    @Test
    public void namedDescriptionBuilder_generatesTestAndSuiteDescriptions() throws Exception {
        ImmutableDescription testDescription = namedBuilder.createTestDescription();
        assertThat(testDescription.isTest(), is(true));
        assertThat(testDescription.isSuite(), is(false));

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(Collections.EMPTY_LIST);
        assertThat(suiteDescription.isTest(), is(false));
        assertThat(suiteDescription.isSuite(), is(true));
    }

    @Test
    public void classBasedDescriptionBuilder_hasCorrectNameAndUniqueIdSet() throws Exception {
        assertThat(classBasedBuilder.displayName, is(SimpleTest.class.getSimpleName()));
        assertThat(classBasedBuilder.uniqueId, is(SimpleTest.class.getCanonicalName()));
    }

    @Test
    public void classBasedDescriptionBuilder_hasAnnotationsFromClass() throws Exception {
        assertThat(classBasedBuilder.annotations, is(notNullValue()));
        assertThat(classBasedBuilder.annotations.isEmpty(), is(true));

        DescriptionBuilder classBasedBuilder2 = DescriptionBuilder.forClass(SimpleAnnotatedTest.class);
        assertThat(classBasedBuilder2.annotations, is(notNullValue()));
        assertThat(classBasedBuilder2.annotations.isEmpty(), is(false));
        assertThat(classBasedBuilder2.annotations.size(), is(1));
    }

    @Test
    public void classBasedDescriptionBuilder_generatesTestAndSuiteDescriptions() throws Exception {
        ImmutableDescription testDescription = classBasedBuilder.createTestDescription();
        assertThat(testDescription.isTest(), is(true));
        assertThat(testDescription.isSuite(), is(false));

        ImmutableDescription suiteDescription = classBasedBuilder.createSuiteDescription(Collections.EMPTY_LIST);
        assertThat(suiteDescription.isTest(), is(false));
        assertThat(suiteDescription.isSuite(), is(true));
    }

    @Test
    public void withDisplayName_updatesTheDisplayName() throws Exception {
        namedBuilder.withDisplayName("newDisplayName");
        assertThat(namedBuilder.displayName, is("newDisplayName"));

        ImmutableDescription testDescription = namedBuilder.createTestDescription();
        assertThat(testDescription.getDisplayName(), is("newDisplayName(test)"));

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(Collections.EMPTY_LIST);
        assertThat(suiteDescription.getDisplayName(), is("newDisplayName"));
    }

    @Test
    public void withUniqueId_updatesTheUniqueId() throws Exception {
        namedBuilder.withUniqueId("newUniqueId");
        assertThat(namedBuilder.uniqueId, is("newUniqueId"));

        ImmutableDescription testDescription = namedBuilder.createTestDescription();
        assertThat(testDescription.getDisplayName(), is("test(newUniqueId)"));

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(Collections.EMPTY_LIST);
        assertThat(suiteDescription.getDisplayName(), is("test"));
    }

    @Test
    public void withAdditionalAnnotations_updatesTheAnnotations() throws Exception {
        Annotation newAnnotation = new Annotation() {
            @Override
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

        ImmutableDescription suiteDescription = namedBuilder.createSuiteDescription(Collections.EMPTY_LIST);
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
}