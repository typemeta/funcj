package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.CodecException;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

public class JsonGenerator implements JsonIO.Output {
    private final Writer writer;
    private boolean pendingComma = false;

    public JsonGenerator(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException ex) {
            throw new CodecException("Failed to close output stream", ex);
        }
    }

    private JsonGenerator write(String value) {
        try {
            writer.append(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException("Failed to write to output stream", ex);
        }
    }

    private JsonGenerator write(char value) {
        try {
            writer.append(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException("Failed to write to output stream", ex);
        }
    }

    private JsonGenerator writeString(String value) {
        try {
            JsonGeneratorUtils.format(value, writer);
            return this;
        } catch (IOException ex) {
            throw new CodecException("Failed to write to output stream", ex);
        }
    }

    private JsonGenerator writeString(char value) {
        try {
            JsonGeneratorUtils.format(value, writer);
            return this;
        } catch (IOException ex) {
            throw new CodecException("Failed to write to output stream", ex);
        }
    }

    private void writeComma() {
        write(',');
        pendingComma = false;
    }

    @Override
    public JsonIO.Output writeNull() {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write("null");
    }

    @Override
    public JsonIO.Output writeBool(boolean value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(Boolean.toString(value));
    }

    @Override
    public JsonIO.Output writeStr(String value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeString(value);
    }

    @Override
    public JsonIO.Output writeChar(char value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeString(value);
    }

    @Override
    public JsonIO.Output writeNum(byte value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(Byte.toString(value));
    }

    @Override
    public JsonIO.Output writeNum(short value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(Short.toString(value));
    }

    @Override
    public JsonIO.Output writeNum(int value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(Integer.toString(value));
    }

    @Override
    public JsonIO.Output writeNum(long value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(Long.toString(value));
    }

    @Override
    public JsonIO.Output writeNum(float value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(Float.toString(value));
    }

    @Override
    public JsonIO.Output writeNum(double value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(Double.toString(value));
    }

    @Override
    public JsonIO.Output writeNum(Number value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(value.toString());
    }

    @Override
    public JsonIO.Output writeNum(BigDecimal value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(value.toString());
    }

    @Override
    public JsonIO.Output writeNum(String value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return write(value);
    }

    @Override
    public JsonIO.Output startObject() {
        if (pendingComma) {
            writeComma();
        }
        return write('{');
    }

    @Override
    public JsonIO.Output writeField(String name) {
        if (pendingComma) {
            writeComma();
        }
        return writeString(name)
                .write(':');
    }

    @Override
    public JsonIO.Output endObject() {
        pendingComma = true;
        return write('}');
    }

    @Override
    public JsonIO.Output startArray() {
        if (pendingComma) {
            writeComma();
        }
        return write('[');
    }

    @Override
    public JsonIO.Output endArray() {
        pendingComma = true;
        return write(']');
    }
}

abstract class JsonGeneratorUtils {

    static Writer format(String s, Writer wtr) throws IOException {
        wtr.append('"');
        escape(s, wtr);
        return wtr.append('"');
    }

    static Writer format(char c, Writer wtr) throws IOException {
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
