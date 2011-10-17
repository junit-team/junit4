package org.junit.tests.experimental.rules;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * <tt>TemporaryFolderUsageTest</tt> provides tests for API usage correctness
 * and ensure implementation symmetry of public methods against a root folder.
 */
public class TemporaryFolderUsageTest {

	private TemporaryFolder tempFolder;

	@Before
	public void setUp() {
		tempFolder= new TemporaryFolder();
	}

	@After
	public void tearDown() {
		tempFolder.delete();
	}

	@Test(expected= IllegalStateException.class)
	public void getRootShouldThrowIllegalStateExceptionIfCreateWasNotInvoked() {
		new TemporaryFolder().getRoot();
	}

	@Test(expected= IllegalStateException.class)
	public void newFileThrowsIllegalStateExceptionIfCreateWasNotInvoked()
			throws IOException {
		new TemporaryFolder().newFile();
	}

	@Test(expected= IllegalStateException.class)
	public void newFileWithGivenNameThrowsIllegalStateExceptionIfCreateWasNotInvoked()
			throws IOException {
		new TemporaryFolder().newFile("MyFile.txt");
	}

	@Test(expected= IllegalStateException.class)
	public void newFolderThrowsIllegalStateExceptionIfCreateWasNotInvoked()
			throws IOException {
		new TemporaryFolder().newFolder();
	}

	@Test(expected= IllegalStateException.class)
	public void newFolderWithGivenPathThrowsIllegalStateExceptionIfCreateWasNotInvoked() {
		new TemporaryFolder().newFolder("level1", "leve2", "leve3");
	}

	@Test
	public void createInitializesRootFolder() throws IOException {
		tempFolder.create();
		assertFileExists("Root folder", tempFolder.getRoot());
	}

	@Test
	public void deleteShouldDoNothingIfRootFolderWasNotInitialized() {
		tempFolder.delete();
	}

	@Test
	public void deleteRemovesRootFolder() throws IOException {
		tempFolder.create();
		tempFolder.delete();
		assertFileDoesNotExists("Root folder", tempFolder.getRoot());
	}

	private void assertFileDoesNotExists(String msg, File file) {
		assertThat(msg + ": is null", file, is(notNullValue()));
		assertThat(msg + ": still exists", file.exists(), is(false));
	}

	private void assertFileExists(String msg, File file) {
		assertThat(msg + ": is null", file, is(notNullValue()));
		assertThat(msg + ": does not exist", file.exists(), is(true));
	}

	@Test
	public void newRandomFileIsCreatedUnderRootFolder() throws IOException {
		tempFolder.create();

		File f= tempFolder.newFile();
		assertFileExists("Random file", f);
		assertFileCreatedUnderRootFolder("Random file", f);
	}

	@Test
	public void newNamedFileIsCreatedUnderRootFolder() throws IOException {
		final String fileName= "SampleFile.txt";
		tempFolder.create();

		File f= tempFolder.newFile(fileName);

		assertFileExists("Named file", f);
		assertFileCreatedUnderRootFolder("Named file", f);
		assertThat("file name", f.getName(), equalTo(fileName));
	}

	private void assertFileCreatedUnderRootFolder(String msg, File f) {
		assertParentFolderForFileIs(msg, f, tempFolder.getRoot());
	}

	private void assertParentFolderForFileIs(String msg, File f,
			File parentFolder) {
		assertThat(msg + ": not under root", f.getParentFile(),
				is(parentFolder));
	}

	@Test
	public void newRandomFolderIsCreatedUnderRootFolder() throws IOException {
		tempFolder.create();

		File f= tempFolder.newFolder();
		assertFileExists("Random folder", f);
		assertFileCreatedUnderRootFolder("Random folder", f);
	}

	@Test
	public void newNestedFoldersCreatedUnderRootFolder() throws IOException {
		tempFolder.create();

		File f= tempFolder.newFolder("top", "middle", "bottom");
		assertFileExists("Nested folder", f);
		assertParentFolderForFileIs("bottom", f, new File(tempFolder.getRoot(),
				"top/middle"));
		assertParentFolderForFileIs("middle", f.getParentFile(), new File(
				tempFolder.getRoot(), "top"));
		assertFileCreatedUnderRootFolder("top", f.getParentFile()
				.getParentFile());
	}

}
