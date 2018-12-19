package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.json.io.*;
import org.typemeta.funcj.codec.stream.StreamCodecFormat;

import java.io.*;
import java.math.BigDecimal;
import java.util.Objects;

public class JsonTypes {

    /**
     * Interface for classes which provide configuration information
     * for {@link JsonCodecCore} implementations.
     */
    public interface Config extends CodecConfig {

        String typeFieldName();

        String keyFieldName();

        String valueFieldName();
    }

    /**
     * Interface for classes which implement an input stream of JSON events
     */
    public interface InStream extends StreamCodecFormat.Input<InStream> {

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

        String location();

        boolean notEOF();

        Event.Type currentEventType();

        Event event(int lookahead);

        void skipValue();

        <T> T readNull();

        Number readNumber();
        BigDecimal readBigDecimal();
        String readStringNumber();

        void startObject();
        String readFieldName();
        void readFieldName(String name);
        void endObject();

        void startArray();
        void endArray();
    }

    /**
     * Interface for classes which implement an output stream of JSON events
     */
    public interface OutStream extends StreamCodecFormat.Output<OutStream> {

        OutStream writeNull();

        OutStream writeNumber(Number value);
        OutStream writeBigDecimal(BigDecimal value);
        OutStream writeStringNumber(String value);

        OutStream startObject();
        OutStream writeField(String name);
        OutStream endObject();

        OutStream startArray();
        OutStream endArray();
    }

    private static final int MAX_PARSER_LOOKAHEAD = 3;

    public static JsonParser inputOf(Reader reader) {
        return new JsonParser(reader, MAX_PARSER_LOOKAHEAD);
    }

    public static JsonGenerator outputOf(Writer writer) {
        return new JsonGenerator(writer);
    }
}
