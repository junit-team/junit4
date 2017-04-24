package junit.framework;

public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DIFF_END = "]";
    private static final String DIFF_START = "[";

    private final int contextLength;
    private final String expected;
    private final String actual;

    public ComparisonCompactor(int contextLength, String expected, String actual) {
        this.contextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    @SuppressWarnings("deprecation")
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
        if(prefix.length() > contextLength) {
            return ELLIPSIS + prefix.substring(prefix.length() - contextLength);
        } else {
            return prefix;
        }
    }

    private String compactSuffix(String suffix){
        if(suffix.length() > contextLength){
            return suffix.substring(0, contextLength) + ELLIPSIS;
        } else {
            return suffix;
        }
    }

    private String extractDiff(String source, String prefix, String suffix){
        return DIFF_START + source.substring(prefix.length(), source.length() - suffix.length()) + DIFF_END;
    }
}
