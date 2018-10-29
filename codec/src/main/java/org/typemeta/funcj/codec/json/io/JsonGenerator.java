package org.typemeta.funcj.codec.json.io;

import java.io.*;
import java.math.BigDecimal;

import static org.typemeta.funcj.codec.json.io.JsonGeneratorUtils.raiseWriteFailure;
import static org.typemeta.funcj.codec.json.io.JsonGeneratorUtils.write;

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
    public JsonIO.Output writeNull() {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString("null");
    }

    @Override
    public JsonIO.Output writeBoolean(boolean value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Boolean.toString(value));
    }

    @Override
    public JsonIO.Output writeString(String value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeQuotedString(value);
    }

    @Override
    public JsonIO.Output writeChar(char value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeQuotedString(value);
    }

    @Override
    public JsonIO.Output writeByte(byte value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Byte.toString(value));
    }

    @Override
    public JsonIO.Output writeShort(short value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Short.toString(value));
    }

    @Override
    public JsonIO.Output writeint(int value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Integer.toString(value));
    }

    @Override
    public JsonIO.Output writeLong(long value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Long.toString(value));
    }

    @Override
    public JsonIO.Output writeFloat(float value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Float.toString(value));
    }

    @Override
    public JsonIO.Output writeDouble(double value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Double.toString(value));
    }

    @Override
    public JsonIO.Output writeNumber(Number value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value.toString());
    }

    @Override
    public JsonIO.Output writeBigDecimal(BigDecimal value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value.toString());
    }

    @Override
    public JsonIO.Output writeStringNumber(String value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value);
    }

    @Override
    public JsonIO.Output startObject() {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeUnquotedString('{');
    }

    @Override
    public JsonIO.Output writeField(String name) {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeQuotedString(name)
                .writeUnquotedString(':');
    }

    @Override
    public JsonIO.Output endObject() {
        pendingComma = true;
        return writeUnquotedString('}');
    }

    @Override
    public JsonIO.Output startArray() {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeUnquotedString('[');
    }

    @Override
    public JsonIO.Output endArray() {
        pendingComma = true;
        return writeUnquotedString(']');
    }
}
