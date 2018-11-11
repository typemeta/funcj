package org.typemeta.funcj.codec.json.io;

import org.typemeta.funcj.codec.json.JsonCodec;

import java.io.*;
import java.math.BigDecimal;

import static org.typemeta.funcj.codec.json.io.JsonGeneratorUtils.*;

public class JsonGenerator implements JsonCodec.Output {

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
            throw raiseWriteFailure(ex);
        }
    }

    private JsonGenerator writeUnquotedString(String value) {
        try {
            writer.append(value);
            return this;
        } catch (IOException ex) {
            throw raiseWriteFailure(ex);
        }
    }

    private JsonGenerator writeUnquotedString(char value) {
        try {
            writer.append(value);
            return this;
        } catch (IOException ex) {
            throw raiseWriteFailure(ex);
        }
    }

    private JsonGenerator writeQuotedString(String value) {
        try {
            write(value, writer);
            return this;
        } catch (IOException ex) {
            throw raiseWriteFailure(ex);
        }
    }

    private JsonGenerator writeQuotedString(char value) {
        try {
            write(value, writer);
            return this;
        } catch (IOException ex) {
            throw raiseWriteFailure(ex);
        }
    }

    private void writeComma() {
        writeUnquotedString(',');
    }

    @Override
    public JsonCodec.Output writeNull() {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString("null");
    }

    @Override
    public JsonCodec.Output writeBoolean(boolean value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Boolean.toString(value));
    }

    @Override
    public JsonCodec.Output writeString(String value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeQuotedString(value);
    }

    @Override
    public JsonCodec.Output writeChar(char value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeQuotedString(value);
    }

    @Override
    public JsonCodec.Output writeByte(byte value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Byte.toString(value));
    }

    @Override
    public JsonCodec.Output writeShort(short value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Short.toString(value));
    }

    @Override
    public JsonCodec.Output writeint(int value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Integer.toString(value));
    }

    @Override
    public JsonCodec.Output writeLong(long value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Long.toString(value));
    }

    @Override
    public JsonCodec.Output writeFloat(float value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Float.toString(value));
    }

    @Override
    public JsonCodec.Output writeDouble(double value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Double.toString(value));
    }

    @Override
    public JsonCodec.Output writeNumber(Number value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value.toString());
    }

    @Override
    public JsonCodec.Output writeBigDecimal(BigDecimal value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value.toString());
    }

    @Override
    public JsonCodec.Output writeStringNumber(String value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value);
    }

    @Override
    public JsonCodec.Output startObject() {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeUnquotedString('{');
    }

    @Override
    public JsonCodec.Output writeField(String name) {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeQuotedString(name)
                .writeUnquotedString(':');
    }

    @Override
    public JsonCodec.Output endObject() {
        pendingComma = true;
        return writeUnquotedString('}');
    }

    @Override
    public JsonCodec.Output startArray() {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeUnquotedString('[');
    }

    @Override
    public JsonCodec.Output endArray() {
        pendingComma = true;
        return writeUnquotedString(']');
    }
}
