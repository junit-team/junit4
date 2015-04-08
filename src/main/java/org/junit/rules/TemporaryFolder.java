package org.junit.rules;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;

/**
 * The TemporaryFolder Rule allows creation of files and folders that should
 * be deleted when the test method finishes (whether it passes or
 * fails).
 * By default no exception will be thrown in case the deletion fails.
 *
 * <p>Example of usage:
 * <pre>
 * public static class HasTempFolder {
 *  &#064;Rule
 *  public TemporaryFolder folder= new TemporaryFolder();
 *
 *  &#064;Test
 *  public void testUsingTempFolder() throws IOException {
 *      File createdFile= folder.newFile(&quot;myfile.txt&quot;);
 *      File createdFolder= folder.newFolder(&quot;subfolder&quot;);
 *      // ...
 *     }
 * }
 * </pre>
 *
 * <p>TemporaryFolder rule supports assured deletion mode, which
 * will fail the test in case deletion fails with {@link AssertionError}.
 *
 * <p>Creating TemporaryFolder with assured deletion:
 * <pre>
 *  &#064;Rule
 *  public TemporaryFolder folder= TemporaryFolder.builder().assureDeletion().build();
 * </pre>
 *
 * @since 4.7
 */
public class TemporaryFolder extends ExternalResource {
    private final File parentFolder;
    private final boolean assureDeletion;
    private File folder;

    /**
     * Create a temporary folder which uses system default temporary-file 
     * directory to create temporary resources.
     */
    public TemporaryFolder() {
        this((File) null);
    }

    /**
     * Create a temporary folder which uses the specified directory to create
     * temporary resources.
     *
     * @param parentFolder folder where temporary resources will be created.
     * If {@code null} then system default temporary-file directory is used.
     */
    public TemporaryFolder(File parentFolder) {
        this.parentFolder = parentFolder;
        this.assureDeletion = false;
    }

    /**
     * Create a {@link TemporaryFolder} initialized with
     * values from a builder.
     */
    protected TemporaryFolder(Builder builder) {
        this.parentFolder = builder.parentFolder;
        this.assureDeletion = builder.assureDeletion;
    }

    /**
     * Returns a new builder for building an instance of {@link TemporaryFolder}.
     *
     * @since 4.13
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builds an instance of {@link TemporaryFolder}.
     * 
     * @since 4.13
     */
    public static class Builder {
        private File parentFolder;
        private boolean assureDeletion;

        protected Builder() {}

        /**
         * Specifies which folder to use for creating temporary resources.
         * If {@code null} then system default temporary-file directory is
         * used.
         *
         * @return this
         */
        public Builder parentFolder(File parentFolder) {
            this.parentFolder = parentFolder;
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
         * Builds a {@link TemporaryFolder} instance using the values in this builder.
         */
        public TemporaryFolder build() {
            return new TemporaryFolder(this);
        }
    }

    @Override
    protected void before() throws Throwable {
        create();
    }

    @Override
    protected void after() {
        delete();
    }

    // testing purposes only

    /**
     * for testing purposes only. Do not use.
     */
    public void create() throws IOException {
        folder = createTemporaryFolderIn(parentFolder);
    }

    /**
     * Returns a new fresh file with the given name under the temporary folder.
     */
    public File newFile(String fileName) throws IOException {
        File file = new File(getRoot(), fileName);
        if (!file.createNewFile()) {
            throw new IOException(
                    "a file with the name \'" + fileName + "\' already exists in the test folder");
        }
        return file;
    }

    /**
     * Returns a new fresh file with a random name under the temporary folder.
     */
    public File newFile() throws IOException {
        return File.createTempFile("junit", null, getRoot());
    }

    /**
     * Returns a new fresh folder with the given name under the temporary
     * folder.
     */
    public File newFolder(String folder) throws IOException {
        return newFolder(new String[]{folder});
    }

    /**
     * Returns a new fresh folder with the given name(s) under the temporary
     * folder.
     */
    public File newFolder(String... folderNames) throws IOException {
        File file = getRoot();
        for (int i = 0; i < folderNames.length; i++) {
            String folderName = folderNames[i];
            validateFolderName(folderName);
            file = new File(file, folderName);
            if (!file.mkdir() && isLastElementInArray(i, folderNames)) {
                throw new IOException(
                        "a folder with the name \'" + folderName + "\' already exists");
            }
        }
        return file;
    }
    
    /**
     * Validates if multiple path components were used while creating a folder.
     * 
     * @param folderName
     *            Name of the folder being created
     */
    private void validateFolderName(String folderName) throws IOException {
        File tempFile = new File(folderName);
        if (tempFile.getParent() != null) {
            String errorMsg = "Folder name cannot consist of multiple path components separated by a file separator."
                    + " Please use newFolder('MyParentFolder','MyFolder') to create hierarchies of folders";
            throw new IOException(errorMsg);
        }
    }

    private boolean isLastElementInArray(int index, String[] array) {
        return index == array.length - 1;
    }

    /**
     * Returns a new fresh folder with a random name under the temporary folder.
     */
    public File newFolder() throws IOException {
        return createTemporaryFolderIn(getRoot());
    }

    private File createTemporaryFolderIn(File parentFolder) throws IOException {
        File createdFolder = File.createTempFile("junit", "", parentFolder);
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder;
    }

    /**
     * @return the location of this temporary folder.
     */
    public File getRoot() {
        if (folder == null) {
            throw new IllegalStateException(
                    "the temporary folder has not yet been created");
        }
        return folder;
    }

    /**
     * Delete all files and folders under the temporary folder. Usually not
     * called directly, since it is automatically applied by the {@link Rule}.
     *
     * @throws IllegalStateException if unable to clean up resources
     * and deletion of resources is assured.
     */
    public void delete() {
        if (!tryDelete()) {
            if (assureDeletion) {
                fail("Unable to clean up temporary folder " + folder);
            }
        }
    }

    /**
     * Tries to delete all files and folders under the temporary folder and
     * returns whether deletion was successful or not.
     *
     * @return {@code true} if all resources are deleted successfully,
     *         {@code false} otherwise.
     */
    protected boolean tryDelete() {
        if (folder == null) {
            return true;
        }
        
        return recursiveDelete(folder);
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
