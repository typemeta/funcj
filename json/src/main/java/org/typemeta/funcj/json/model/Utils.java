package org.typemeta.funcj.json.model;

public abstract class Utils {

    public static String formatAsNumber(Object obj) {
        if (obj instanceof Double) {
            final double d = (Double) obj;
            final long l = (long)d;
            if (d == l) {
                return String.format("%d", l);
            }
        } else if (obj instanceof Float) {
            final float f = (Float) obj;
            final long l = (long)f;
            if (f == l) {
                return String.format("%d", l);
            }
        }

        return obj.toString();
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

    static RuntimeException nullTypeError(Class<?> clazz) {
        return new RuntimeException(typeErrorMessage(JsNull.class, clazz));
    }

    static RuntimeException boolTypeError(Class<?> clazz) {
        return new RuntimeException(typeErrorMessage(JsBool.class, clazz));
    }

    static RuntimeException numberTypeError(Class<?> clazz) {
        return new RuntimeException(typeErrorMessage(JsNumber.class, clazz));
    }

    static RuntimeException stringTypeError(Class<?> clazz) {
        return new RuntimeException(typeErrorMessage(JsString.class, clazz));
    }

    static RuntimeException arrayTypeError(Class<?> clazz) {
        return new RuntimeException(typeErrorMessage(JsArray.class, clazz));
    }

    static RuntimeException objectTypeError(Class<?> clazz) {
        return new RuntimeException(typeErrorMessage(JsObject.class, clazz));
    }

    private static String typeErrorMessage(Class<?> expType, Class<?> actualType) {
        return "Expecting " + expType.getSimpleName() + " but encountered a " + actualType.getSimpleName();
    }
}
