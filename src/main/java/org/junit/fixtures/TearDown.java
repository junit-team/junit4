package org.junit.fixtures;

/**
 * Indicates objects that that can perform tear-down activities.
 */
public interface TearDown {

    /**
     * Performs a tear-down operation. If this method throws
     * an exception, then the containing test will fail.
     *
     * @throws Exception if the tear down fails
     */
    void tearDown() throws Exception;
}
