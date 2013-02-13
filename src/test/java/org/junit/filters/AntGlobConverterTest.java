package org.junit.filters;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AntGlobConverterTest {
    @Test
    public void convertShouldNotConvertNonSpecialCharacters() {
        final String expected = "com/netflix/package/Class";

        final String actual = AntGlobConverter.convert("com/netflix/package/Class");

        assertEquals(actual, expected);
    }

    @Test
    public void convertShouldConvertQuestionMark() {
        final String expected = ".";

        final String actual = AntGlobConverter.convert("?");

        assertEquals(actual, expected);
    }

    @Test
    public void convertShouldConvertSplat() {
        final String expected = "[^/]*";

        final String actual = AntGlobConverter.convert("*");

        assertEquals(actual, expected);
    }

    @Test
    public void convertShouldConvertDoubleSplat() {
        final String expected = ".*";

        final String actual = AntGlobConverter.convert("**");

        assertEquals(actual, expected);
    }

    @Test
    public void convertShouldConvertDot() {
        final String expected = "\\.";

        final String actual = AntGlobConverter.convert(".");

        assertEquals(actual, expected);
    }

    @Test
    public void convertShouldConvertDollar() {
        final String expected = "\\$";

        final String actual = AntGlobConverter.convert("$");

        assertEquals(actual, expected);
    }

    @Test
    public void convertCommaSeparatedListShouldConvertCommaToPipe() {
        final String expected = "0|1";

        final String actual = AntGlobConverter.convertCommaSeparatedList("0,1");

        assertEquals(actual, expected);
    }
}
