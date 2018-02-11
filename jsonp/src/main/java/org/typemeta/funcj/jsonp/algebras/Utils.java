package org.typemeta.funcj.jsonp.algebras;

import java.math.BigDecimal;

public abstract class Utils {

    public static String format(int value) {
        return Integer.toString(value);
    }

    public static String format(double value) {
        final String s = Double.toString(value);
        if (s.length() > 2 && Double.toString(value).endsWith(".0")) {
            return s.substring(0, s.length() - 2);
        } else {
            return s;
        }
    }

    public static String format(BigDecimal value) {
        return value.toString();
    }

    public static String format(String s) {
        return format(s, new StringBuilder()).toString();
    }

    public static StringBuilder format(String s, StringBuilder sb) {
        sb.append('"');
        escape(s, sb);
        return sb.append('"');
    }

    public static StringBuilder escape(String s, StringBuilder sb) {
        final int len = s.length();
        for (int i = 0; i < len; ++i) {
            final char c = s.charAt(i);
            switch(c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
//                case '/':
//                    sb.append("\\/");
//                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c <= '\u001F' ||
                            c >= '\u007F' && c <= '\u009F' ||
                            c >= '\u00ff') {
                        sb.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
                    } else {
                        sb.append(c);
                    }
            }
        }

        return sb;
    }
}
