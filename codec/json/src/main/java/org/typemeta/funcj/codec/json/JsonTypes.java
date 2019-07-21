package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.json.io.*;
import org.typemeta.funcj.codec.stream.StreamCodecFormat;
import org.typemeta.funcj.json.parser.JsonEvent;

import java.io.*;
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
    public interface InStream extends StreamCodecFormat.Input<InStream> {

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

    public static JsonStreamParser inputOf(Reader reader) {
        return new JsonStreamParser(reader, MAX_PARSER_LOOKAHEAD);
    }

    public static JsonGenerator outputOf(Writer writer) {
        return new JsonGenerator(writer);
    }
}
