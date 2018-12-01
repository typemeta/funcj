package org.typemeta.funcj.codec.json.io;

import org.typemeta.funcj.codec.json.JsonTypes;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

import static org.typemeta.funcj.codec.json.io.JsonGeneratorUtils.raiseWriteFailure;
import static org.typemeta.funcj.codec.json.io.JsonGeneratorUtils.write;

public class JsonGenerator implements JsonTypes.OutStream {

    private final Writer writer;

    private boolean pendingComma = false;

    public JsonGenerator(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void close() {
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
    public JsonGenerator writeNull() {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString("null");
    }

    @Override
    public JsonGenerator writeBoolean(boolean value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Boolean.toString(value));
    }

    @Override
    public JsonGenerator writeString(String value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeQuotedString(value);
    }

    @Override
    public JsonGenerator writeChar(char value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeQuotedString(value);
    }

    @Override
    public JsonGenerator writeByte(byte value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Byte.toString(value));
    }

    @Override
    public JsonGenerator writeShort(short value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Short.toString(value));
    }

    @Override
    public JsonGenerator writeInt(int value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Integer.toString(value));
    }

    @Override
    public JsonGenerator writeLong(long value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Long.toString(value));
    }

    @Override
    public JsonGenerator writeFloat(float value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Float.toString(value));
    }

    @Override
    public JsonGenerator writeDouble(double value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(Double.toString(value));
    }

    @Override
    public JsonGenerator writeNumber(Number value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value.toString());
    }

    @Override
    public JsonGenerator writeBigDecimal(BigDecimal value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value.toString());
    }

    @Override
    public JsonGenerator writeStringNumber(String value) {
        if (pendingComma) {
            writeComma();
        }
        pendingComma = true;
        return writeUnquotedString(value);
    }

    @Override
    public JsonGenerator startObject() {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeUnquotedString('{');
    }

    @Override
    public JsonGenerator writeField(String name) {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeQuotedString(name)
                .writeUnquotedString(':');
    }

    @Override
    public JsonGenerator endObject() {
        pendingComma = true;
        return writeUnquotedString('}');
    }

    @Override
    public JsonGenerator startArray() {
        if (pendingComma) {
            writeComma();
            pendingComma = false;
        }
        return writeUnquotedString('[');
    }

    @Override
    public JsonGenerator endArray() {
        pendingComma = true;
        return writeUnquotedString(']');
    }
}
