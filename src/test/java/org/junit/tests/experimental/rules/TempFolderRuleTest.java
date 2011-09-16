package org.junit.tests.experimental.rules;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TempFolderRuleTest {
	private static File[] createdFiles= new File[20];

	public static class HasTempFolder {
		@Rule
		public TemporaryFolder folder= new TemporaryFolder();

		@Test
		public void testUsingTempFolder() throws IOException {
			createdFiles[0]= folder.newFile("myfile.txt");
			assertTrue(createdFiles[0].exists());
		}
	}

	@Test
	public void tempFolderIsDeleted() {
		assertThat(testResult(HasTempFolder.class), isSuccessful());
		assertFalse(createdFiles[0].exists());
	}

	public static class CreatesSubFolder {
		@Rule
		public TemporaryFolder folder= new TemporaryFolder();

		@Test
		public void testUsingTempFolder() throws IOException {
			String subfolder = "subfolder";
			String filename = "a.txt";
			createdFiles[0]= folder.newFolder(subfolder);
			new File(createdFiles[0], filename).createNewFile();
			
			File expectedFile = new File(folder.getRoot(), join(subfolder, filename));
			
			assertTrue(expectedFile.exists());
		}

		@Test
		public void testUsingTempTreeFolders() throws IOException {
			String subfolder = "subfolder";
			String anotherfolder = "anotherfolder";
			String filename = "a.txt";

			createdFiles[0] = folder.newFolder(subfolder, anotherfolder);
			new File(createdFiles[0], filename).createNewFile();

			File expectedFile = new File(folder.getRoot(), join(subfolder, anotherfolder, filename));
			
			assertTrue(expectedFile.exists());
		}
		
		private String join(String... folderNames) {
			StringBuilder path = new StringBuilder();
			for (String folderName : folderNames) {
				path.append(File.separator).append(folderName);
			}
			return path.toString();
		}
	}

	@Test
	public void subFolderIsDeleted() {
		assertThat(testResult(CreatesSubFolder.class), isSuccessful());
		assertFalse(createdFiles[0].exists());
	}

	public static class CreatesRandomSubFolders {
		@Rule
		public TemporaryFolder folder= new TemporaryFolder();

		@Test
		public void testUsingRandomTempFolders() throws IOException {
			for (int i= 0; i < 20; i++) {
				File newFolder= folder.newFolder();
				assertThat(Arrays.asList(createdFiles), not(hasItem(newFolder)));
				createdFiles[i]= newFolder;
				new File(newFolder, "a.txt").createNewFile();
				assertTrue(newFolder.exists());
			}
		}
	}

	@Test
	public void randomSubFoldersAreDeleted() {
		assertThat(testResult(CreatesRandomSubFolders.class), isSuccessful());
		for (File f : createdFiles) {
			assertFalse(f.exists());
		}
	}

	public static class CreatesRandomFiles {
		@Rule
		public TemporaryFolder folder= new TemporaryFolder();

		@Test
		public void testUsingRandomTempFiles() throws IOException {
			for (int i= 0; i < 20; i++) {
				File newFile= folder.newFile();
				assertThat(Arrays.asList(createdFiles), not(hasItem(newFile)));
				createdFiles[i]= newFile;
				assertTrue(newFile.exists());
			}
		}
	}

	@Test
	public void randomFilesAreDeleted() {
		assertThat(testResult(CreatesRandomFiles.class), isSuccessful());
		for (File f : createdFiles) {
			assertFalse(f.exists());
		}
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
	public void recursiveDeleteFolderWithOneRandomElement() throws IOException {
		TemporaryFolder folder= new TemporaryFolder();
		folder.create();
		File file= folder.newFile();
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
		public void testNewFolder() {
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
