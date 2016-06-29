package org.junit.fixtures;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows creation of files and directories that should be deleted when the test method
 * finishes (whether it passes or fails).
 * By default no exception will be thrown in case the deletion fails.
 *
 * <p>Example of usage:
 * <pre>
 * public static class HasTempDirectory {
 *  &#064;Rule
 *  public TemporaryDirectory tmpDir = new TemporaryDirectory();
 *
 *  &#064;Test
 *  public void testUsingTempDirectory() throws IOException {
 *      File createdFile = tmpDir.newFile(&quot;myfile.txt&quot;);
 *      File createdDirectory = tmpDir.newDirectory(&quot;subdirectory&quot;);
 *      // ...
 *     }
 * }
 * </pre>
 *
 * <p>The TemporaryDirectory fixture supports assured deletion mode, which
 * will fail the test in case deletion fails with {@link AssertionError}.
 *
 * <p>Creating TemporaryDirectory with assured deletion:
 * <pre>
 *  &#064;Rule
 *  public TemporaryDirectory tmpDir = TemporaryDirectory.builder().assureDeletion().build();
 * </pre>
 *
 * @since 4.13
 */
public class TemporaryDirectory implements TestFixture {
    private final File parentDirectory;
    private final boolean assureDeletion;
    private File directory;

    /**
     * Create a temporary directory which uses system default temporary-file 
     * directory to create temporary resources.
     */
    public TemporaryDirectory() {
        this.parentDirectory = null;
        this.assureDeletion = false;
    }

    /**
     * Create a temporary directory which uses the specified directory to create
     * temporary resources.
     *
     * @param parentDirectory directory where temporary resources will be created.
     */
    public TemporaryDirectory(File parentDirectory) {
        if (parentDirectory == null) {
            throw new NullPointerException("parentDirectory cannot be null");
        }
        this.parentDirectory = parentDirectory;
        this.assureDeletion = false;
    }

    /**
     * Create a {@link TemporaryDirectory} initialized with
     * values from a builder.
     */
    protected TemporaryDirectory(Builder builder) {
        this.parentDirectory = builder.parentDirectory;
        this.assureDeletion = builder.assureDeletion;
    }

    /**
     * Returns a new builder for building an instance of {@link TemporaryDirectory}.
     *
     * @since 4.13
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builds an instance of {@link TemporaryDirectory}.
     * 
     * @since 4.13
     */
    public static class Builder {
        private File parentDirectory;
        private boolean assureDeletion;

        protected Builder() {}

        /**
         * Specifies which directory to use for creating temporary resources.
         * If this is not called then system default temporary-file directory is
         * used.
         *
         * @return this
         */
        public Builder parentDirectory(File parentDirectory) {
            if (parentDirectory == null) {
                throw new NullPointerException("parentDirectory cannot be null");
            }
            this.parentDirectory = parentDirectory;
            return this;
        }

        /**
         * Setting this flag assures that no resources are left undeleted. Failure
         * to fulfill the assurance results in failure of tests with an
         * {@link IllegalStateException}.
         *
         * @return this
         */
        public Builder assureDeletion() {
            this.assureDeletion = true;
            return this;
        }

        /**
         * Builds a {@link TemporaryDirectory} instance using the values in this builder.
         */
        public TemporaryDirectory build() {
            return new TemporaryDirectory(this);
        }
    }

    public void initialize(FixtureContext context) throws Exception {
        create();
        context.addTearDown(new TearDown() {
            public void tearDown() throws Exception {
                delete();
            }
        });
        
    }

    /**
     * for testing purposes only. Do not use.
     */
    private void create() throws IOException {
       directory = createTemporaryDirectoryIn(parentDirectory);
    }


    /**
     * Returns a new fresh file with the given name under the temporary directory.
     */
    public File newFile(String fileName) throws IOException {
        File file = new File(getRoot(), fileName);
        if (!file.createNewFile()) {
            throw new IOException(
                    "a file with the name \'" + fileName + "\' already exists in the test directory");
        }
        return file;
    }

    /**
     * Returns a new fresh file with a random name under the temporary directory.
     */
    public File newFile() throws IOException {
        return File.createTempFile("junit", null, getRoot());
    }

    /**
     * Returns a new fresh d with the given name(s) under the temporary
     * directory.
     */
    public File newDirectory(String topDirectoryName, String... subDirectoryNames) throws IOException {
        List<String> directoryNames = new ArrayList<String>(subDirectoryNames.length + 1);
        directoryNames.add(topDirectoryName);
        directoryNames.addAll(Arrays.asList(subDirectoryNames));
        File file = getRoot();
        for (int i = 0; i < directoryNames.size(); i++) {
            String directoryName = directoryNames.get(i);
            validateDirectoryName(directoryName);
            file = new File(file, directoryName);
            if (!file.mkdir() && isLastElementInList(i, directoryNames)) {
                throw new IOException(
                        "a directory with the name \'" + directoryName + "\' already exists");
            }
        }
        return file;
    }
    
    /**
     * Validates if multiple path components were used while creating a directory.
     * 
     * @param directoryName
     *            Name of the directory being created
     */
    private void validateDirectoryName(String directoryName) throws IOException {
        File tempFile = new File(directoryName);
        if (tempFile.getParent() != null) {
            String errorMsg = "Directory name cannot consist of multiple path components separated by a file separator."
                    + " Please use newDirectory('MyParentDir','MySubDir') to create hierarchies of directories";
            throw new IOException(errorMsg);
        }
    }

    private boolean isLastElementInList(int index, List<String> list) {
        return index == list.size() - 1;
    }

    /**
     * Returns a new fresh directory with a random name under the temporary directory.
     */
    public File newDirectory() throws IOException {
        return createTemporaryDirectoryIn(getRoot());
    }

    private File createTemporaryDirectoryIn(File parentDirectory) throws IOException {
        File createdDirectory = File.createTempFile("junit", "", parentDirectory);
        createdDirectory.delete();
        createdDirectory.mkdir();
        return createdDirectory;
    }

    /**
     * @return the location of this temporary directory.
     */
    public File getRoot() {
        if (directory == null) {
            throw new IllegalStateException(
                    "the temporary directory has not yet been created");
        }
        return directory;
    }

    /**
     * Delete all files and directories under the temporary directory.
     *
     * @throws IllegalStateException if unable to clean up resources
     * and deletion of resources is assured.
     */
    private void delete() {
        if (!tryDelete()) {
            if (assureDeletion) {
                fail("Unable to clean up temporary directory " + directory);
            }
        }
    }

    /**
     * Tries to delete all files and directories under the temporary directory and
     * returns whether deletion was successful or not.
     *
     * @return {@code true} if all resources are deleted successfully,
     *         {@code false} otherwise.
     */
    protected boolean tryDelete() {
        if (directory == null) {
            return true;
        }
        
        return recursiveDelete(directory);
    }
    
    private boolean recursiveDelete(File file) {
        boolean result = true;
        File[] files = file.listFiles();
        if (files != null) {
            for (File each : files) {
                result = result && recursiveDelete(each);
            }
        }
        return result && file.delete();
    }
}
