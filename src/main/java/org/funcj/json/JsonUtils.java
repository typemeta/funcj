package org.funcj.json;

import org.funcj.document.*;

import java.util.Map;

class JsonUtils {
    static <T> T duplicateKeyError(T u, T v) {
        throw new IllegalStateException("Duplicate keys - " + u + " & " + v);
    }

    static String format(double d) {
        final long ld = (long)d;
        if (d == ld) {
            return String.format("%d", ld);
        } else {
            return String.format("%s", d);
        }
    }

    static String format(String s) {
        return format(s, new StringBuilder()).toString();
    }

    static StringBuilder format(String s, StringBuilder sb) {
        sb.append('"');
        escape(s, sb);
        return sb.append('"');
    }

    static StringBuilder escape(String s, StringBuilder sb) {
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
                            c >= '\u2000' && c <= '\u20FF') {
                        sb.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
                    } else {
                        sb.append(c);
                    }
            }
        }

        return sb;
    }

    static Document toDoc(JSObject.Field field) {
        return API.concat(
                API.text("\"" + field.name + "\""),
                API.text(" : "),
                field.value.toDocument()
        );
    }

    static RuntimeException asNull(Class<?> clazz) {
        return new RuntimeException("Expecting JSNull but encountered a " + clazz.getSimpleName());
    }

    static RuntimeException asBool(Class<?> clazz) {
        return new RuntimeException("Expecting JSBool but encountered a " + clazz.getSimpleName());
    }

    static RuntimeException asNumber(Class<?> clazz) {
        return new RuntimeException("Expecting JSNumber but encountered a " + clazz.getSimpleName());
    }

    static RuntimeException asString(Class<?> clazz) {
        return new RuntimeException("Expecting JSString but encountered a " + clazz.getSimpleName());
    }

    static RuntimeException asArray(Class<?> clazz) {
        return new RuntimeException("Expecting JSArray but encountered a " + clazz.getSimpleName());
    }

    static RuntimeException asObject(Class<?> clazz) {
        return new RuntimeException("Expecting JSObject but encountered a " + clazz.getSimpleName());
    }
}
