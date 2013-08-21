package org.junit;

/**
 * Piece of code which execution could result in throwing an exception.
 *
 * @author Pavel Rappo
 */
public interface ThrowingBlock {

    void execute() throws Throwable;
}