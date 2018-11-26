package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.json.io.*;

import java.io.*;
import java.math.BigDecimal;
import java.util.Objects;

public class JsonCodec {

    /**
     * Interface for classes which provide configuration information
     * for {@link JsonCodecCore} implementations.
     */
    public interface Config extends CodecConfig {

        String typeFieldName();

        String keyFieldName();

        String valueFieldName();
    }

    private static final int MAX_PARSER_LOOKAHEAD = 3;

    public static Input inputOf(Reader reader) {
        return new JsonParser(reader, MAX_PARSER_LOOKAHEAD);
    }

    public static Output outputOf(Writer writer) {
        return new JsonGenerator(writer);
    }

    public interface Input  {
        interface Event {
            Type type();

            enum Type implements Event {
                ARRAY_END,
                ARRAY_START,
                COMMA,          // internal use only
                COLON,          // internal use only
                EOF,
                FALSE,
                FIELD_NAME,
                NULL,
                NUMBER,
                OBJECT_END,
                OBJECT_START,
                STRING,
                TRUE;

                @Override
                public Type type() {
                    return this;
                }
            }

            final class FieldName implements Event {
                public final String value;

                public FieldName(String value) {
                    this.value = Objects.requireNonNull(value);
                }

                @Override
                public Type type() {
                    return Type.FIELD_NAME;
                }

                @Override
                public String toString() {
                    return "FieldName{" + value + "}";
                }

                @Override
                public boolean equals(Object rhs) {
                    if (this == rhs) {
                        return true;
                    } else if (rhs == null || getClass() != rhs.getClass()) {
                        return false;
                    } else {
                        FieldName rhsJS = (FieldName) rhs;
                        return value.equals(rhsJS.value);
                    }
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }
            }

            final class JString implements Event {
                public final String value;

                public JString(String value) {
                    this.value = Objects.requireNonNull(value);
                }

                @Override
                public Type type() {
                    return Type.STRING;
                }

                @Override
                public String toString() {
                    return "JString{" + value + "}";
                }

                @Override
                public boolean equals(Object rhs) {
                    if (this == rhs) {
                        return true;
                    } else if (rhs == null || getClass() != rhs.getClass()) {
                        return false;
                    } else {
                        final JString rhsJS = (JString) rhs;
                        return value.equals(rhsJS.value);
                    }
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }
            }

            final class JNumber implements Event {
                public final String value;

                public JNumber(String value) {
                    this.value = Objects.requireNonNull(value);
                }

                @Override
                public Type type() {
                    return Type.NUMBER;
                }

                @Override
                public String toString() {
                    return "JNumber{" + value + "}";
                }

                @Override
                public boolean equals(Object rhs) {
                    if (this == rhs) {
                        return true;
                    } else if (rhs == null || getClass() != rhs.getClass()) {
                        return false;
                    } else {
                        final JNumber rhsJN = (JNumber) rhs;
                        return value.equals(rhsJN.value);
                    }
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }
            }
        }

        boolean notEOF();

        Event.Type currentEventType();

        Event event(int lookahead);

        <T> T readNull();

        boolean readBoolean();

        char readChar();

        byte readByte();
        short readShort();
        int readInt();
        long readLong();
        float readFloat();
        double readDouble();
        Number readNumber();
        BigDecimal readBigDecimal();
        String readStringNumber();

        String readString();

        void startObject();
        String readFieldName();
        void readFieldName(String name);
        void endObject();

        void startArray();
        void endArray();
    }

    public interface Output {

        Output writeNull();

        Output writeBoolean(boolean value);

        Output writeChar(char value);

        Output writeByte(byte value);
        Output writeShort(short value);
        Output writeint(int value);
        Output writeLong(long value);
        Output writeFloat(float value);
        Output writeDouble(double value);
        Output writeNumber(Number value);
        Output writeBigDecimal(BigDecimal value);
        Output writeStringNumber(String value);

        Output writeString(String value);

        Output startObject();
        Output writeField(String name);
        Output endObject();

        Output startArray();
        Output endArray();

        void close();
    }
}
