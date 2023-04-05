package junit.utils;

public class StringUtils {
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";
    private static final String ELLIPSIS = "...";


    public static String compactString(int fPrefix, int fSuffix, int fContextLength, String fExpected, String fActual,boolean hasfExpected) {
        String result="";
        if(hasfExpected){
            result = DELTA_START + fExpected.substring(fPrefix, fExpected.length() - fSuffix + 1) + DELTA_END;
        }else{
            result = DELTA_START + fActual.substring(fPrefix, fActual.length() - fSuffix + 1) + DELTA_END;
        }
//        if (!fExpected.isEmpty()) {
//             result = DELTA_START + fExpected.substring(fPrefix, fExpected.length() - fSuffix + 1) + DELTA_END;
//
//        } else if (!fActual.isEmpty()) {
//             result = DELTA_START + fActual.substring(fPrefix, fActual.length() - fSuffix + 1) + DELTA_END;
//
//        }
        if (fPrefix > 0) {
            result = computeCommonPrefix(fPrefix, fContextLength, fExpected) + result;
        }
        if (fSuffix > 0) {
            result = result + computeCommonSuffix(fSuffix, fContextLength, fExpected);
        }
        return result;
    }

    public static String computeCommonPrefix(int fPrefix, int fContextLength, String fExpected) {
        return (fPrefix > fContextLength ? ELLIPSIS : "") + fExpected.substring(Math.max(0, fPrefix - fContextLength), fPrefix);
    }

    public static String computeCommonSuffix(int fSuffix, int fContextLength, String fExpected) {
        int end = Math.min(fExpected.length() - fSuffix + 1 + fContextLength, fExpected.length());
        return fExpected.substring(fExpected.length() - fSuffix + 1, end) + (fExpected.length() - fSuffix + 1 < fExpected.length() - fContextLength ? ELLIPSIS : "");
    }
}
