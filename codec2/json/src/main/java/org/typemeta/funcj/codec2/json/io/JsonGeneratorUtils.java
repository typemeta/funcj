package org.typemeta.funcj.codec2.json.io;

import org.typemeta.funcj.codec2.core.utils.CodecException;

import java.io.IOException;
import java.io.Writer;

abstract class JsonGeneratorUtils {

    static CodecException raiseWriteFailure(Exception cause) {
        return new CodecException("Failed to write JSON", cause);
    }

    static Writer write(String s, Writer wtr) throws IOException {
        wtr.append('"');
        escape(s, wtr);
        return wtr.append('"');
    }

    static Writer write(char c, Writer wtr) throws IOException {
        wtr.append('"');
        escape(c, wtr);
        return wtr.append('"');
    }

    static Writer escape(String s, Writer wtr) throws IOException {
        final int len = s.length();
        for (int i = 0; i < len; ++i) {
            escape(s.charAt(i), wtr);
        }

        return wtr;
    }

    static Writer escape(char c, Writer wtr) throws IOException {
        switch(c) {
            case '\"':
                wtr.append("\\\"");
                break;
            case '\\':
                wtr.append("\\\\");
                break;
            //                case '/':
            //                    wtr.append("\\/");
            //                    break;
            case '\b':
                wtr.append("\\b");
                break;
            case '\f':
                wtr.append("\\f");
                break;
            case '\n':
                wtr.append("\\n");
                break;
            case '\r':
                wtr.append("\\r");
                break;
            case '\t':
                wtr.append("\\t");
                break;
            default:
                if (c <= '\u001F' ||
                        c >= '\u007F' && c <= '\u009F' ||
                        c >= '\u00ff') {
                    wtr.append("\\u").append(Integer.toHexString(c | 0x10000).substring(1));
                } else {
                    wtr.append(c);
                }
        }

        return wtr;
    }
}
