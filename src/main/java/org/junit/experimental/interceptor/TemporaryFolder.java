package org.junit.experimental.interceptor;

import java.io.File;
import java.io.IOException;

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
	public void create() throws IOException {
		folder= File.createTempFile("junit", "");
		folder.delete();
		folder.mkdir();
	}

	public File newFile(String fileName) throws IOException {
		File file= new File(folder, fileName);
		file.createNewFile();
		return file;
	}

	public File newFolder(String folderName) {
		File file= new File(folder, folderName);
		file.mkdir();
		return file;
	}

	public File getRoot() {
		return folder;
	}

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