package org.junit.tests.experimental.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.io.File;
import java.io.IOException;

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
}
