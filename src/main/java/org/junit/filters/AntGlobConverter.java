package org.junit.filters;

class AntGlobConverter {
    public static String convert(String globExpression) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i != globExpression.length(); ++i) {
            final char c = globExpression.charAt(i);

            if (".$".contains(Character.toString(c))) {
                result
                        .append("\\")
                        .append(c);
            } else if (c == '?') {
                result.append('.');
            } else if (c == '*') {
                if (i + 1 != globExpression.length() && globExpression.charAt(i + 1) == '*') {
                    result.append(".*");
                    ++i;
                } else {
                    result.append("[^/]*");
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static String convertCommaSeparatedList(String commaSeparatedList) {
        return convert(commaSeparatedList).replace(',', '|');
    }
}
