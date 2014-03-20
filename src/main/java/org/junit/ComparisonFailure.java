package org.junit;

/**
 * Thrown when an {@link org.junit.Assert#assertEquals(Object, Object) assertEquals(String, String)} fails. Create and throw
 * a <code>ComparisonFailure</code> manually if you want to show users the difference between two complex
 * strings.
 *
 * Inspired by a patch from Alex Chaffee (alex@purpletech.com)
 *
 * @since 4.0
 */
public class ComparisonFailure extends AssertionError {
    /**
     * The maximum length for expected and actual strings. If it is exceeded, the strings should be shortened.
     *
     * @see ComparisonCompactor
     */
    private static final int MAX_CONTEXT_LENGTH = 20;
    private static final long serialVersionUID = 1L;

    private String expected;
    private String actual;

    /**
     * Constructs a comparison failure.
     *
     * @param message the identifying message or null
     * @param expected the expected string value
     * @param actual the actual string value
     */
    public ComparisonFailure(String message, String expected, String actual) {
        super(message);
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * Returns "..." in place of common prefix and "..." in
     * place of common suffix between expected and actual.
     *
     * @see Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return new ComparisonCompactor(MAX_CONTEXT_LENGTH, expected, actual).compact(super.getMessage());
    }

    /**
     * Returns the actual string value
     *
     * @return the actual string value
     */
    public String getActual() {
        return actual;
    }

    /**
     * Returns the expected string value
     *
     * @return the expected string value
     */
    public String getExpected() {
        return expected;
    }

    private static class ComparisonCompactor {
        private static final String ELLIPSIS = "...";
        private static final String DELTA_END = "]";
        private static final String DELTA_START = "[";

        /**
         * The maximum length for <code>expected</code> and <code>actual</code> strings to show. When <code>contextLength</code>
         * is exceeded, the Strings are shortened.
         */
        private int contextLength;
        
        private String expected;
        private String actual;
        
        /**
         * The length of the shared prefix / suffix of the expected and actual strings.
         * Equals to zero if the strings do not share a common prefix/suffix.
         */
        private int prefix;
        private int suffix;

        /**
         * @param contextLength the maximum length for <code>expected</code> and <code>actual</code> strings. When contextLength
         * is exceeded, the Strings are shortened.
         * @param expected the expected string value
         * @param actual the actual string value
         */
        public ComparisonCompactor(int contextLength, String expected, String actual) {
            this.contextLength = contextLength;
            this.expected = expected;
            this.actual = actual;
        }

        private String compact(String message) {
            if (expected == null || actual == null || areStringsEqual()) {
                return Assert.format(message, expected, actual);
            }

            findCommonPrefix();
            findCommonSuffix();
            String expected = compactString(this.expected);
            String actual = compactString(this.actual);
            return Assert.format(message, expected, actual);
        }

        private String compactString(String source) {
            String result = DELTA_START + source.substring(prefix, source.length() - suffix) + DELTA_END;
            if (prefix > 0) {
                result = computeCommonPrefix() + result;
            }
            if (suffix > 0) {
                result = result + computeCommonSuffix();
            }
            return result;
        }

        private void findCommonPrefix() {
            prefix = 0;
            int end = Math.min(expected.length(), actual.length());
            for (; prefix < end; prefix++) {
                if (expected.charAt(prefix) != actual.charAt(prefix)) {
                    break;
                }
            }
        }

        private void findCommonSuffix() {
            int expectedSuffix = expected.length() - 1;
            int actualSuffix = actual.length() - 1;
            for (; actualSuffix >= prefix && expectedSuffix >= prefix; actualSuffix--, expectedSuffix--) {
                if (expected.charAt(expectedSuffix) != actual.charAt(actualSuffix)) {
                    break;
                }
            }
            suffix = expected.length() - expectedSuffix - 1;
        }

        private String computeCommonPrefix() {
            return (prefix > contextLength ? ELLIPSIS : "") + expected.substring(Math.max(0, prefix - contextLength), prefix);
        }

        private String computeCommonSuffix() {
            int end = Math.min(expected.length() - suffix + contextLength, expected.length());
            return expected.substring(expected.length() - suffix, end) + (expected.length() - suffix < expected.length() - contextLength ? ELLIPSIS : "");
        }

        private boolean areStringsEqual() {
            return expected.equals(actual);
        }
    }
}