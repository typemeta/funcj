package org.typemeta.funcj.codec.xml.io;

public abstract class XmlUtils {
    public static String escapeTextChar(String text) {
        int s = 0;
        StringBuilder sb = null;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            final String repl = escapeTextChar(c);
            if (repl != null) {
                if (sb == null) {
                    sb = new StringBuilder(text.substring(0, i));
                } else if (s != i) {
                    sb.append(text, s, i);
                }
                sb.append(repl);
                s = i + 1;
            }
        }

        if (sb == null) {
            return text;
        } else if (s < text.length()) {
            sb.append(text, s, text.length());
        }

        return sb.toString();
    }

    public static String escapeTextChar(char c) {
        switch (c) {
            case '&': return "&amp;";
            case '<': return "&lt;";
            case '>': return "&gt;";
            case '\t': return null;
            case '\n': return null;
            case '\r': return null;
            default:
                if (c < 0x20 || c > 0xFFFD) {
                    return "\uFFFD";
                } else {
                    return null;
                }
        }
    }
}
