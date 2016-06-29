package org.junit.fixtures;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.experimental.results.PrintableResult.testResult;
import static org.junit.experimental.results.ResultMatchers.failureCountIs;
import static org.junit.experimental.results.ResultMatchers.isSuccessful;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runners.model.MultipleFailureException;

/**
 * Tests for {@link TemporaryDirectory}.
 */
public class TemporaryDirectoryTest {
    private static File[] createdFiles = new File[20];

    public static class HasTempDirectory {
        @Fixture
        public TemporaryDirectory tmpDir = new TemporaryDirectory();

        @Test
        public void testUsingTempDirectory() throws IOException {
            createdFiles[0] = tmpDir.newFile("myfile.txt");
            assertTrue(createdFiles[0].exists());
        }
    }

    @Test
    public void tempDirectoryIsDeleted() {
        assertThat(testResult(HasTempDirectory.class), isSuccessful());
        assertFalse(createdFiles[0].exists());
    }

    public static class CreatesSubDirectory {
        @Fixture
        public TemporaryDirectory tmpDir = new TemporaryDirectory();

        @Test
        public void testUsingTempDirectoryString() throws IOException {
            String subDir = "subdir";
            String filename = "a.txt";
            // ensure that a single String works
            createdFiles[0] = tmpDir.newDirectory(subDir);
            new File(createdFiles[0], filename).createNewFile();

            File expectedFile = new File(tmpDir.getRoot(), join(subDir, filename));

            assertTrue(expectedFile.exists());
        }

        @Test
        public void testUsingTempTreeDirectories() throws IOException {
            String subDir = "subDir";
            String anotherDir = "anotherDir";
            String filename = "a.txt";

            createdFiles[0] = tmpDir.newDirectory(subDir, anotherDir);
            new File(createdFiles[0], filename).createNewFile();

            File expectedFile = new File(tmpDir.getRoot(), join(subDir, anotherDir, filename));

            assertTrue(expectedFile.exists());
        }

        private String join(String... dirNames) {
            StringBuilder path = new StringBuilder();
            for (String dirName : dirNames) {
                path.append(File.separator).append(dirName);
            }
            return path.toString();
        }
    }

    @Test
    public void subDirectoryIsDeleted() {
        assertThat(testResult(CreatesSubDirectory.class), isSuccessful());
        assertFalse(createdFiles[0].exists());
    }

    public static class CreatesRandomSubDirectories {
        @Fixture
        public TemporaryDirectory tmpDir = new TemporaryDirectory();

        @Test
        public void testUsingRandomTempDirectories() throws IOException {
            for (int i = 0; i < 20; i++) {
                File newDir = tmpDir.newDirectory();
                assertThat(Arrays.asList(createdFiles), not(hasItem(newDir)));
                createdFiles[i] = newDir;
                new File(newDir, "a.txt").createNewFile();
                assertTrue(newDir.exists());
            }
        }
    }

    @Test
    public void randomSubDirectoriesAreDeleted() {
        assertThat(testResult(CreatesRandomSubDirectories.class), isSuccessful());
        for (File f : createdFiles) {
            assertFalse(f.exists());
        }
    }

    public static class CreatesRandomFiles {
        @Fixture
        public TemporaryDirectory tmpDir = new TemporaryDirectory();

        @Test
        public void testUsingRandomTempFiles() throws IOException {
            for (int i = 0; i < 20; i++) {
                File newFile = tmpDir.newFile();
                assertThat(Arrays.asList(createdFiles), not(hasItem(newFile)));
                createdFiles[i] = newFile;
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
    public void recursiveDeleteDirectoryWithOneElement() throws Throwable {
        TemporaryDirectory tmpDir = new TemporaryDirectory();
        FixtureManager fixtureManager = new FixtureManager();
        fixtureManager.initializeFixture(tmpDir);
        File file = tmpDir.newFile("a");
        runAllTearDowns(fixtureManager);
        assertFalse(file.exists());
        assertFalse(tmpDir.getRoot().exists());
    }

    @Test
    public void recursiveDeleteDirectoryWithOneRandomElement() throws Throwable {
        TemporaryDirectory tmpDir = new TemporaryDirectory();
        FixtureManager fixtureManager = new FixtureManager();
        fixtureManager.initializeFixture(tmpDir);
        File file = tmpDir.newFile();
        runAllTearDowns(fixtureManager);
        assertFalse(file.exists());
        assertFalse(tmpDir.getRoot().exists());
    }

    @Test
    public void recursiveDeleteDirectoryWithZeroElements() throws Throwable {
        TemporaryDirectory tmpDir = new TemporaryDirectory();
        FixtureManager fixtureManager = new FixtureManager();
        fixtureManager.initializeFixture(tmpDir);
        runAllTearDowns(fixtureManager);
        assertFalse(tmpDir.getRoot().exists());
    }

    public static class NameClashes {
        @Fixture
        public TemporaryDirectory tmpDir = new TemporaryDirectory();

        @Test
        public void fileWithFileClash() throws IOException {
            tmpDir.newFile("something.txt");
            tmpDir.newFile("something.txt");
        }

        @Test
        public void fileWithDirectoryTest() throws IOException {
            tmpDir.newDirectory("dummy");
            tmpDir.newFile("dummy");
        }
    }

    @Test
    public void nameClashesResultInTestFailures() {
        assertThat(testResult(NameClashes.class), failureCountIs(2));
    }

    private static final String GET_ROOT_DUMMY = "dummy-getRoot";

    private static final String NEW_FILE_DUMMY = "dummy-newFile";

    private static final String NEW_DIR_DUMMY = "dummy-newDir";

    public static class IncorrectUsage {
        public TemporaryDirectory tmpDir = new TemporaryDirectory();

        @Test
        public void testGetRoot() throws IOException {
            new File(tmpDir.getRoot(), GET_ROOT_DUMMY).createNewFile();
        }

        @Test
        public void testNewFile() throws IOException {
            tmpDir.newFile(NEW_FILE_DUMMY);
        }

        @Test
        public void testNewDirectory() throws IOException {
            tmpDir.newDirectory(NEW_DIR_DUMMY);
        }
    }

    @Test
    public void incorrectUsageWithoutApplyingTheRuleShouldNotPolluteTheCurrentWorkingDirectory() {
        assertThat(testResult(IncorrectUsage.class), failureCountIs(3));
        assertFalse("getRoot should have failed early", new File(GET_ROOT_DUMMY).exists());
        assertFalse("newFile should have failed early", new File(NEW_FILE_DUMMY).exists());
        assertFalse("newDirectory should have failed early", new File(NEW_DIR_DUMMY).exists());
    }

    @After
    public void cleanCurrentWorkingDirectory() {
        new File(GET_ROOT_DUMMY).delete();
        new File(NEW_FILE_DUMMY).delete();
        new File(NEW_DIR_DUMMY).delete();
    }

    private static void runAllTearDowns(FixtureManager fixtureManager) throws Exception {
        List<Throwable> errors = new ArrayList<Throwable>();
        fixtureManager.runAllTearDowns(errors);
        MultipleFailureException.assertEmpty(errors);
    }
}
