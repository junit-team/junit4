package org.junit.tests.experimental.rules;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

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
    public void newFileWithGivenFilenameThrowsIllegalArgumentExceptionIfFileExists() throws IOException {
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
    public void newFolderWithGivenFolderThrowsIllegalArgumentExceptionIfFolderExists() throws IOException {
        tempFolder.create();
        tempFolder.newFolder("level1");

        thrown.expect(IOException.class);
        thrown.expectMessage("a folder with the name 'level1' already exists");
        tempFolder.newFolder("level1");
    }
    
    @Test
    public void newFolderWithGivenFolderThrowsIOExceptionIfFolderNameConsistsOfMultiplePathComponents()
            throws IOException {
        tempFolder.create();
        thrown.expect(IOException.class);
        thrown.expectMessage("name cannot consist of multiple path components");
        tempFolder.newFolder("temp1/temp2");
    }
    
    @Test
    public void newFolderWithGivenPathThrowsIllegalArgumentExceptionIfPathExists() throws IOException {
        tempFolder.create();
        tempFolder.newFolder("level1", "level2", "level3");

        thrown.expect(IOException.class);
        thrown.expectMessage("a folder with the name 'level3' already exists");
        tempFolder.newFolder("level1", "level2", "level3");
    }

    @Test
    public void newFolderWithGivenPathThrowsIOExceptionIfFolderNamesConsistOfMultiplePathComponents()
            throws IOException {
        tempFolder.create();
        thrown.expect(IOException.class);
        thrown.expectMessage("name cannot consist of multiple path components");
        tempFolder.newFolder("temp1", "temp2", "temp3/temp4");
    }
    
    @Test
    public void createInitializesRootFolder() throws IOException {
        tempFolder.create();
        assertFileExists(tempFolder.getRoot());
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
        assertFileExists(f);
        assertFileCreatedUnderRootFolder("Random folder", f);
    }

    @Test
    public void newNestedFoldersCreatedUnderRootFolder() throws IOException {
        tempFolder.create();

        File f = tempFolder.newFolder("top", "middle", "bottom");
        assertFileExists(f);
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

    private void assertFileExists(File file) {
        checkFileExists("does not exist", file, true);
    }

    private void assertFileCreatedUnderRootFolder(String msg, File f) {
        assertParentFolderForFileIs(f, tempFolder.getRoot());
    }

    private void assertParentFolderForFileIs(File f, File parentFolder) {
        assertThat("'" + f.getAbsolutePath() + "': not under root",
                f.getParentFile(), is(parentFolder));
    }
}
