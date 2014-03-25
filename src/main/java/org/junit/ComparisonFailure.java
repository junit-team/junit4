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
        private static final String DIFF_END = "]";
        private static final String DIFF_START = "[";

        /**
         * The maximum length for <code>expected</code> and <code>actual</code> strings to show. When <code>contextLength</code>
         * is exceeded, the Strings are shortened.
         */
        private final int contextLength;
        private final String expected;
        private final String actual;

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

        public String compact(String message) {
            if (expected == null || actual == null || expected.equals(actual)) {
                return Assert.format(message, expected, actual);
            } else {
                String prefix = sharedPrefix(expected, actual);
                String suffix = sharedSuffix(expected, actual, prefix.length());
                String expectedDiff = extractDiff(expected, prefix, suffix);
                String actualDiff = extractDiff(actual, prefix, suffix);
                String compactedPrefix = compactPrefix(prefix);
                String compactedSuffix = compactSuffix(suffix);
                return Assert.format(message,
                        compactedPrefix + expectedDiff + compactedSuffix,
                        compactedPrefix + actualDiff + compactedSuffix);
            }

        }

        private String sharedPrefix(String a, String b){
            int end = Math.min(a.length(), b.length());
            for (int i = 0; i < end; i++){
                if (a.charAt(i) != b.charAt(i)) {
                    return a.substring(0, i);
                }
            }
            return a.substring(0, end);
        }

        private String sharedSuffix(String a, String b, int prefixLength){
            int end = Math.min(a.length(), b.length());
            for(int i = 0; i < end - prefixLength; i++){
                if (a.charAt(a.length() - (i+1)) != b.charAt(b.length() - (i+1))) {
                    return a.substring(a.length() - i);
                }
            }
            return a.substring(a.length() - end + prefixLength);
        }

        private String compactPrefix(String prefix){
            return prefix.length() > contextLength ?
                    ELLIPSIS + prefix.substring(prefix.length() - contextLength) : prefix;
        }

        private String compactSuffix(String suffix){
            return suffix.length() > contextLength ?
                    suffix.substring(0, contextLength) + ELLIPSIS : suffix;
        }

        private String extractDiff(String source, String prefix, String suffix){
            return DIFF_START + source.substring(prefix.length(), source.length() - suffix.length()) + DIFF_END;
        }
    }
}
