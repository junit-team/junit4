package org.junit.tests.experimental.categories;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public final class CategoriesConfiguredBySystemPropertiesTest {

    private static String convertToFileNames(final Class<?>... types) {
        final StringBuilder fileNames = new StringBuilder();
        int i = 0; for (final Class<?> type : types) {
            fileNames.append(type.getName().replace('.', '/'))
            .append(".java");
            if (++i != types.length) fileNames.append(',');
        }
        return fileNames.toString();
    }

    /**
     * This test is mentioned in {@code Categories} and any changes
     * must be reflected.
     */
    @Test public void runSuite() {
        System.setProperty("org.junit.categories.included", convertToFileNames(IRelationalStoragePlatforms.class));
        System.setProperty("org.junit.categories.excluded", convertToFileNames(IRunWithMySQL.class));

        // Targeting Test:
        final Result testResult = JUnitCore.runClasses(AllPlatformsSuite.class);

        System.setProperty("org.junit.categories.included", "");
        System.setProperty("org.junit.categories.excluded", "");

        //only IRunWithOracle and IRelationalStoragePlatforms
        assertThat("unexpected run count", testResult.getRunCount(), is(equalTo(2)));
        assertThat("unexpected failure count", testResult.getFailureCount(), is(equalTo(0)));
        assertThat("unexpected failure count", testResult.getIgnoreCount(), is(equalTo(0)));
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(IAllStoragePlatforms.class)
    @Categories.ExcludeCategory(IRunWithInformix.class)//limits the suite, its tests and test cases in a particular test
    @Suite.SuiteClasses({AllPlatformsTest.class})
    public static final class AllPlatformsSuite {}

    public static final class AllPlatformsTest {

        @Test @Category(IRunWithOracle.class)
        public void oracle() {}

        @Test @Category(IRunWithInformix.class)
        public void informix() {}

        @Test @Category(IRelationalStoragePlatforms.class)
        public void allRelationalDBs() {}

        @Test @Category(IRunWithMySQL.class)
        public void mySql() { fail(); }

        @Test public void noCategory() { fail(); }

        @Test @Category(IAllStoragePlatforms.class)
        public void all() { fail(); }

        @Test @Category(INonrelationalStoragePlatforms.class)
        public void allNonRelationalDBs() { fail(); }

        @Test @Category(IRunWithKatta.class)
        public void katta() { fail(); }

        @Test @Category(IRunWithAmazonEC2.class)
        public void amazon() { fail(); }

        @Test @Category(IRunWithTerracotta.class)
        public void terracotta() { fail(); }

        @Test @Category(IRunWithSolr.class)
        public void solr() { fail(); }

        @Test @Category(IRunWithApacheCassandra.class)
        public void cassandra() { fail(); }
    }

    public interface IAllStoragePlatforms {}
    public interface INonrelationalStoragePlatforms extends IAllStoragePlatforms {}
    public interface IRelationalStoragePlatforms extends IAllStoragePlatforms {}
    public interface IRunWithAmazonEC2 extends INonrelationalStoragePlatforms {}
    public interface IRunWithApacheCassandra extends INonrelationalStoragePlatforms {}
    public interface IRunWithInformix extends IRelationalStoragePlatforms {}
    public interface IRunWithKatta extends INonrelationalStoragePlatforms {}
    public interface IRunWithMySQL extends IRelationalStoragePlatforms {}
    public interface IRunWithOracle extends IRelationalStoragePlatforms {}
    public interface IRunWithSolr extends INonrelationalStoragePlatforms {}
    public interface IRunWithTerracotta extends INonrelationalStoragePlatforms {}
}