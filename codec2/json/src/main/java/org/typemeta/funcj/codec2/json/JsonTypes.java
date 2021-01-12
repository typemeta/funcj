package org.typemeta.funcj.codec2.json;

import org.typemeta.funcj.codec2.core.CodecConfig;
import org.typemeta.funcj.codec2.core.StreamCodecFormat;
import org.typemeta.funcj.codec2.json.io.JsonGenerator;
import org.typemeta.funcj.codec2.json.io.JsonStreamParser;
import org.typemeta.funcj.json.parser.JsonEvent;

import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;

public abstract class JsonTypes {

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
    public interface InStream extends StreamCodecFormat.Input {

        String location();

        boolean notEOF();

        JsonEvent.Type currentEventType();

        JsonEvent event(int lookahead);

        void skipNode();

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
    public interface OutStream extends StreamCodecFormat.Output {

        OutStream writeNull();

        @Override
        OutStream writeBoolean(boolean value);
        @Override
        OutStream writeChar(char value);
        @Override
        OutStream writeByte(byte value);
        @Override
        OutStream writeShort(short value);
        @Override
        OutStream writeInt(int value);
        @Override
        OutStream writeLong(long value);
        @Override
        OutStream writeFloat(float value);
        @Override
        OutStream writeDouble(double value);
        @Override
        OutStream writeString(String value);

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

    public static JsonStreamParser inputOf(Reader reader) {
        return new JsonStreamParser(reader, MAX_PARSER_LOOKAHEAD);
    }

    public static JsonGenerator outputOf(Writer writer) {
        return new JsonGenerator(writer);
    }
}
