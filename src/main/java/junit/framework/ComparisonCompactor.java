package junit.framework;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.LTEqLengthOf;


public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";

    private @NonNegative int fContextLength;
    private String fExpected;
    private String fActual;
    private @LTEqLengthOf({"fExpected", "fActual"}) @NonNegative int fPrefix;
    private int fSuffix;

    public ComparisonCompactor(@NonNegative int contextLength, String expected, String actual) {
        fContextLength = contextLength;
        fExpected = expected;
        fActual = actual;
    }

    @SuppressWarnings("deprecation")
    public String compact(String message) {
        if (fExpected == null || fActual == null || areStringsEqual()) {
            return Assert.format(message, fExpected, fActual);
        }

        findCommonPrefix();
        findCommonSuffix();
        String expected = compactString(fExpected);
        String actual = compactString(fActual);
        return Assert.format(message, expected, actual);
    }

    private String compactString(String source) {
        int begin = Math.min(fPrefix, source.length());
        int end = source.length() - fSuffix + 1;
        if (end > source.length()) {
            end = source.length();
        }
        if (end < begin) {
            end = begin;
        }
        String result = DELTA_START + source.substring(begin, end) + DELTA_END;
        
        if (fPrefix > 0) {
            result = computeCommonPrefix() + result;
        }
        if (fSuffix > 0) {
            result = result + computeCommonSuffix();
        }
        return result;
    }

    private void findCommonPrefix() {
        fPrefix = 0;
        int end = Math.min(fExpected.length(), fActual.length());
        for (; fPrefix < end; fPrefix++) {
            if (fExpected.charAt(fPrefix) != fActual.charAt(fPrefix)) {
                break;
            }
        }
    }

    private void findCommonSuffix() {
        int expectedSuffix = fExpected.length() - 1;
        int actualSuffix = fActual.length() - 1;
        for (; actualSuffix >= fPrefix && expectedSuffix >= fPrefix; actualSuffix--, expectedSuffix--) {
            if (fExpected.charAt(expectedSuffix) != fActual.charAt(actualSuffix)) {
                break;
            }
        }
        fSuffix = fExpected.length() - expectedSuffix;
    }

    private String computeCommonPrefix() {
    int begin = Math.max(0, fPrefix - fContextLength);
    if (begin > fPrefix) {
        begin = fPrefix;
    }
    return (fPrefix > fContextLength ? ELLIPSIS : "") + fExpected.substring(begin, fPrefix);
    }

    private String computeCommonSuffix() {
        int start = fExpected.length() - fSuffix + 1;
        if (start < 0) {
            start = 0;
        }
        if (start > fExpected.length()) {
            start = fExpected.length();
        }
        int end = Math.min(start + fContextLength, fExpected.length());
        return fExpected.substring(start, end) + (start < fExpected.length() - fContextLength ? ELLIPSIS : "");
    }

    private boolean areStringsEqual() {
        return fExpected.equals(fActual);
    }
}
