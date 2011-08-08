package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TempFolderRuleTest {
	private static File createdFile;

	public static class HasTempFolder {
		@Rule
		public TemporaryFolder folder= new TemporaryFolder();

		@Test
		public void testUsingTempFolder() throws IOException {
			createdFile= folder.newFile("myfile.txt");
			assertTrue(createdFile.exists());
		}
	}

	@Test
	public void tempFolderIsDeleted() {
		assertThat(testResult(HasTempFolder.class), isSuccessful());
		assertFalse(createdFile.exists());
	}

	public static class CreatesSubFolder {
		@Rule
		public TemporaryFolder folder= new TemporaryFolder();

		@Test
		public void testUsingTempFolder() throws IOException {
			createdFile= folder.newFolder("subfolder");
			new File(createdFile, "a.txt").createNewFile();
			assertTrue(createdFile.exists());
		}
	}

	@Test
	public void subFolderIsDeleted() {
		assertThat(testResult(CreatesSubFolder.class), isSuccessful());
		assertFalse(createdFile.exists());
	}

	@Test
	public void recursiveDeleteFolderWithOneElement() throws IOException {
		TemporaryFolder folder= new TemporaryFolder();
		folder.create();
		File file= folder.newFile("a");
		folder.delete();
		assertFalse(file.exists());
		assertFalse(folder.getRoot().exists());
	}

	@Test
	public void recursiveDeleteFolderWithZeroElements() throws IOException {
		TemporaryFolder folder= new TemporaryFolder();
		folder.create();
		folder.delete();
		assertFalse(folder.getRoot().exists());
	}

	private static final String GET_ROOT_DUMMY= "dummy-getRoot";

	private static final String NEW_FILE_DUMMY= "dummy-newFile";

	private static final String NEW_FOLDER_DUMMY= "dummy-newFolder";

	public static class IncorrectUsage {
		public TemporaryFolder folder= new TemporaryFolder();

		@Test
		public void testGetRoot() throws IOException {
			new File(folder.getRoot(), GET_ROOT_DUMMY).createNewFile();
		}

		@Test
		public void testNewFile() throws IOException {
			folder.newFile(NEW_FILE_DUMMY);
		}

		@Test
		public void testNewFolder() throws IOException {
			folder.newFolder(NEW_FOLDER_DUMMY);
		}
	}

	@Test
	public void incorrectUsageWithoutApplyingTheRuleShouldNotPolluteTheCurrentWorkingDirectory() {
		assertThat(testResult(IncorrectUsage.class), failureCountIs(3));
		assertFalse("getRoot should have failed early", new File(GET_ROOT_DUMMY).exists());
		assertFalse("newFile should have failed early", new File(NEW_FILE_DUMMY).exists());
		assertFalse("newFolder should have failed early", new File(NEW_FOLDER_DUMMY).exists());
	}

	@After
	public void cleanCurrentWorkingDirectory() {
		new File(GET_ROOT_DUMMY).delete();
		new File(NEW_FILE_DUMMY).delete();
		new File(NEW_FOLDER_DUMMY).delete();
	}
}
