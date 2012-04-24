package org.junit.tests.experimental.categories;

import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.experimental.results.PrintableResult;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.util.HashSet;
import java.util.Set;

import static org.junit.tests.experimental.categories.CategoriesConfiguredBySystemPropertiesTest.ConcretePlatforms.*;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertThat;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import static org.junit.matchers.JUnitMatchers.hasItems;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;

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

    static enum ConcretePlatforms {
        ALL,
        RELATIONAL_DB, ORACLE_DB, MYSQL_DB, INFORMIX_DB,
        NONRELATIONAL, SOLR, CASSANDRA, KATTA, AMAZON_EC2_CLOUD
    }

    @Test public void runSuite() { shouldRun(AllPlatformsSuite.class, ORACLE_DB, RELATIONAL_DB); }

    private static void shouldRun(final Class<?> junitTestType, final ConcretePlatforms... expectedPlatforms) {
        System.setProperty("org.junit.categories.included", convertToFileNames(IRelationalStoragePlatforms.class));
        System.setProperty("org.junit.categories.excluded", convertToFileNames(IRunWithMySQL.class));

        // Targeting Test:
        final PrintableResult testResult = testResult(junitTestType);

        System.setProperty("org.junit.categories.included", "");
        System.setProperty("org.junit.categories.excluded", "");

        final Set<ConcretePlatforms> passedTestCases = AllPlatformsTest.passedTestCases;

        assertThat("unexpected size", passedTestCases.size(), is(equalTo(expectedPlatforms.length)));
        assertThat(passedTestCases, hasItems(expectedPlatforms));

        passedTestCases.clear();

        assertThat("wrong test modifications, and broken collection of expectations", testResult, isSuccessful());
    }

    @RunWith(Categories.class)
    @Categories.IncludeCategory(IAllStoragePlatforms.class)
    @Categories.ExcludeCategory(IRunWithInformix.class)//limits the suite, its tests and test cases in a particular test
    @Suite.SuiteClasses({AllPlatformsTest.class})
    public static final class AllPlatformsSuite {}

    public static final class AllPlatformsTest {
        static final Set<ConcretePlatforms> passedTestCases = new HashSet<ConcretePlatforms>();

        @Test @Category(IRunWithOracle.class)
        public void oracle() { passedTestCases.add(ORACLE_DB); }

        @Test @Category(IRunWithInformix.class)
        public void informix() { passedTestCases.add(INFORMIX_DB); }

        @Test @Category(IRelationalStoragePlatforms.class)
        public void allRelationalDBs() { passedTestCases.add(RELATIONAL_DB); }

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
