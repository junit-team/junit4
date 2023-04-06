package org.junit.rules.abstracts;

public class RelativeFolderPath extends FolderPath {
    public boolean isAbsolutePath() {
        return false;
    }
}