package io.typemeta.funcj.document;

abstract class Utils {
    static String trimTrailing(String s) {
        final char[] data = s.toCharArray();
        int len = data.length;

        while ((0 < len) && (data[len - 1] <= ' ')) {
            len--;
        }

        return (len < data.length) ? s.substring(0, len) : s;
    }
}
