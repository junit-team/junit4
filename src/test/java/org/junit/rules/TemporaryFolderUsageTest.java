package org.junit.rules;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * <tt>TemporaryFolderUsageTest</tt> provides tests for API usage correctness
 * and ensure implementation symmetry of public methods against a root folder.
 */
public class TemporaryFolderUsageTest {

    private TemporaryFolder tempFolder;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        tempFolder = new TemporaryFolder();
    }

    @After
    public void tearDown() {
        tempFolder.delete();
    }

    @Test(expected = IllegalStateException.class)
    public void getRootShouldThrowIllegalStateExceptionIfCreateWasNotInvoked() {
        new TemporaryFolder().getRoot();
    }

    @Test(expected = IllegalStateException.class)
    public void newFileThrowsIllegalStateExceptionIfCreateWasNotInvoked()
            throws IOException {
        new TemporaryFolder().newFile();
    }

    @Test(expected = IllegalStateException.class)
    public void newFileWithGivenNameThrowsIllegalStateExceptionIfCreateWasNotInvoked()
            throws IOException {
        new TemporaryFolder().newFile("MyFile.txt");
    }

    @Test
    public void newFileWithGivenFilenameThrowsIOExceptionIfFileExists() throws IOException {
        tempFolder.create();
        tempFolder.newFile("MyFile.txt");

        thrown.expect(IOException.class);
        thrown.expectMessage("a file with the name 'MyFile.txt' already exists in the test folder");
        tempFolder.newFile("MyFile.txt");
    }

    @Test(expected = IllegalStateException.class)
    public void newFolderThrowsIllegalStateExceptionIfCreateWasNotInvoked()
            throws IOException {
        new TemporaryFolder().newFolder();
    }

    @Test(expected = IllegalStateException.class)
    public void newFolderWithGivenPathThrowsIllegalStateExceptionIfCreateWasNotInvoked() throws IOException {
        new TemporaryFolder().newFolder("level1", "level2", "level3");
    }

    @Test
    public void newFolderWithGivenFolderThrowsIOExceptionIfFolderExists() throws IOException {
        tempFolder.create();
        tempFolder.newFolder("level1");

        thrown.expect(IOException.class);
        thrown.expectMessage("a folder with the path 'level1' already exists");
        tempFolder.newFolder("level1");
    }

    @Test
    public void newFolderWithGivenFolderThrowsIOExceptionIfFileExists() throws IOException {
        tempFolder.create();
        File file = new File(tempFolder.getRoot(), "level1");
        assertTrue("Could not create" + file, file.createNewFile());

        thrown.expect(IOException.class);
        thrown.expectMessage("a file with the path 'level1' exists");
        tempFolder.newFolder("level1");
    }

    @Test
    public void newFolderWithGivenFolderThrowsIOExceptionWhenFolderCannotBeCreated() throws IOException {
        tempFolder.create();
        assumeTrue("Could not make folder " + tempFolder.getRoot() + " read only.",
                tempFolder.getRoot().setReadOnly());

        thrown.expect(IOException.class);
        thrown.expectMessage("could not create a folder with the path 'level1'");
        tempFolder.newFolder("level1");
    }
    
    @Test
    public void newFolderWithPathStartingWithFileSeparatorThrowsIOException()
            throws IOException {
        String fileAtRoot;
        File[] roots = File.listRoots();
        if (roots != null && roots.length > 0) {
            fileAtRoot = roots[0].getAbsolutePath() + "temp1";
        } else {
            fileAtRoot = File.separator + "temp1";
        }
        tempFolder.create();
        thrown.expect(IOException.class);
        thrown.expectMessage("folder path '" + fileAtRoot + "' is not a relative path");
        tempFolder.newFolder(fileAtRoot);
    }
    
    @Test
    public void newFolderWithPathContainingFileSeparatorCreatesDirectories()
            throws IOException {
        tempFolder.create();
        tempFolder.newFolder("temp1" + File.separator + "temp2");
        File temp1 = new File(tempFolder.getRoot(), "temp1");
        assertFileIsDirectory(temp1);
        assertFileIsDirectory(new File(temp1, "temp2"));
    }
 
    @Test
    public void newFolderWithPathContainingForwardSlashCreatesDirectories()
            throws IOException {
        tempFolder.create();
        tempFolder.newFolder("temp1/temp2");
        File temp1 = new File(tempFolder.getRoot(), "temp1");
        assertFileIsDirectory(temp1);
        assertFileIsDirectory(new File(temp1, "temp2"));
    }
    
    @Test
    public void newFolderWithGivenPathThrowsIOExceptionIfFolderExists() throws IOException {
        tempFolder.create();
        tempFolder.newFolder("level1", "level2", "level3");

        thrown.expect(IOException.class);
        String path = "level1" + File.separator + "level2" + File.separator + "level3";
        thrown.expectMessage("a folder with the path '" + path + "' already exists");
        tempFolder.newFolder("level1", "level2", "level3");
    }

    @Test
    public void newFolderWithGivenEmptyArrayThrowsIllegalArgumentException() throws IOException {
        tempFolder.create();

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("must pass at least one path");
        tempFolder.newFolder(new String[0]);
    }

    @Test
    public void newFolderWithPathsContainingForwardSlashCreatesFullPath()
            throws IOException {
        tempFolder.create();
        tempFolder.newFolder("temp1", "temp2", "temp3/temp4");

        File directory = new File(tempFolder.getRoot(), "temp1");
        assertFileIsDirectory(directory);
        directory = new File(directory, "temp2/temp3/temp4");
        assertFileIsDirectory(directory);
    }

    @Test
    public void newFolderWithPathsContainingFileSeparatorCreatesFullPath()
            throws IOException {
        tempFolder.create();
        tempFolder.newFolder("temp1", "temp2", "temp3" + File.separator + "temp4");

        File directory = new File(tempFolder.getRoot(), "temp1");
        assertFileIsDirectory(directory);
        directory = new File(directory, "temp2/temp3/temp4");
        assertFileIsDirectory(directory);
    }
    
    @Test
    public void createInitializesRootFolder() throws IOException {
        tempFolder.create();
        assertFileIsDirectory(tempFolder.getRoot());
    }

    @Test
    public void deleteShouldDoNothingIfRootFolderWasNotInitialized() {
        tempFolder.delete();
    }

    @Test
    public void deleteRemovesRootFolder() throws IOException {
        tempFolder.create();
        tempFolder.delete();
        assertFileDoesNotExist(tempFolder.getRoot());
    }

    @Test
    public void newRandomFileIsCreatedUnderRootFolder() throws IOException {
        tempFolder.create();

        File f = tempFolder.newFile();
        assertFileExists(f);
        assertFileCreatedUnderRootFolder("Random file", f);
    }

    @Test
    public void newNamedFileIsCreatedUnderRootFolder() throws IOException {
        final String fileName = "SampleFile.txt";
        tempFolder.create();

        File f = tempFolder.newFile(fileName);

        assertFileExists(f);
        assertFileCreatedUnderRootFolder("Named file", f);
        assertThat("file name", f.getName(), equalTo(fileName));
    }

    @Test
    public void newRandomFolderIsCreatedUnderRootFolder() throws IOException {
        tempFolder.create();

        File f = tempFolder.newFolder();
        assertFileIsDirectory(f);
        assertFileCreatedUnderRootFolder("Random folder", f);
    }

    @Test
    public void newNestedFoldersCreatedUnderRootFolder() throws IOException {
        tempFolder.create();

        File f = tempFolder.newFolder("top", "middle", "bottom");
        assertFileIsDirectory(f);
        assertParentFolderForFileIs(f, new File(tempFolder.getRoot(),
                "top/middle"));
        assertParentFolderForFileIs(f.getParentFile(),
                new File(tempFolder.getRoot(), "top"));
        assertFileCreatedUnderRootFolder("top", f.getParentFile()
                .getParentFile());
    }

    @Test
    public void canSetTheBaseFileForATemporaryFolder() throws IOException {
        File tempDir = createTemporaryFolder();

        TemporaryFolder folder = new TemporaryFolder(tempDir);
        folder.create();

        assertThat(tempDir, is(folder.getRoot().getParentFile()));
    }

    private File createTemporaryFolder() throws IOException {
        File tempDir = File.createTempFile("junit", "tempFolder");
        assertTrue("Unable to delete temporary file", tempDir.delete());
        assertTrue("Unable to create temp directory", tempDir.mkdir());
        return tempDir;
    }

    private void assertFileDoesNotExist(File file) {
        checkFileExists("exists", file, false);
    }

    private void checkFileExists(String msg, File file, boolean exists) {
        assertThat("File is null", file, is(notNullValue()));
        assertThat("File '" + file.getAbsolutePath() + "' " + msg,
                file.exists(), is(exists));
    }

    private void checkFileIsDirectory(String msg, File file, boolean isDirectory) {
        assertThat("File is null", file, is(notNullValue()));
        assertThat("File '" + file.getAbsolutePath() + "' " + msg,
                file.isDirectory(), is(isDirectory));
    }

    private void assertFileExists(File file) {
        checkFileExists("does not exist", file, true);
        checkFileIsDirectory("is a directory", file, false);
    }

    private void assertFileIsDirectory(File file) {
        checkFileExists("does not exist", file, true);
        checkFileIsDirectory("is not a directory", file, true);
    }

    private void assertFileCreatedUnderRootFolder(String msg, File f) {
        assertParentFolderForFileIs(f, tempFolder.getRoot());
    }

    private void assertParentFolderForFileIs(File f, File parentFolder) {
        assertThat("'" + f.getAbsolutePath() + "': not under root",
                f.getParentFile(), is(parentFolder));
    }
}
