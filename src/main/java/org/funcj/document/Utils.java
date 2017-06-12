package org.funcj.document;

public abstract class Utils {
    public static final String trimTrailing(String s) {
        final char[] data = s.toCharArray();
        int len = data.length;
        char[] val = data;

        while ((0 < len) && (val[len - 1] <= ' ')) {
            len--;
        }

        return (len < data.length) ? s.substring(0, len) : s;
    }
}
