package org.junit.rules;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;

/**
 * The TemporaryFolder Rule allows creation of files and folders that are
 * guaranteed to be deleted when the test method finishes (whether it passes or
 * fails):
 * 
 * <pre>
 * public static class HasTempFolder {
 * 	&#064;Rule
 * 	public TemporaryFolder folder= new TemporaryFolder();
 * 
 * 	&#064;Test
 * 	public void testUsingTempFolder() throws IOException {
 * 		File createdFile= folder.newFile(&quot;myfile.txt&quot;);
 * 		File createdFolder= folder.newFolder(&quot;subfolder&quot;);
 * 		// ...
 * 	}
 * }
 * </pre>
 */
public class TemporaryFolder extends ExternalResource {
	private File folder;

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
	 * for testing purposes only.  Do not use.
	 */
	public void create() throws IOException {
		folder= File.createTempFile("junit", "");
		folder.delete();
		folder.mkdir();
	}

	/**
	 * Returns a new fresh file with the given name under the temporary folder.
	 */
	public File newFile(String fileName) throws IOException {
		File file= new File(folder, fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * Returns a new fresh folder with the given name under the temporary folder.
	 */
	public File newFolder(String folderName) {
		File file= new File(folder, folderName);
		file.mkdir();
		return file;
	}

	/**
	 * @return the location of this temporary folder.
	 */
	public File getRoot() {
		return folder;
	}

	/**
	 * Delete all files and folders under the temporary folder.
	 * Usually not called directly, since it is automatically applied 
	 * by the {@link Rule}
	 */
	public void delete() {
		recursiveDelete(folder);
	}

	private void recursiveDelete(File file) {
		File[] files= file.listFiles();
		if (files != null)
			for (File each : files)
				recursiveDelete(each);
		file.delete();
	}
}