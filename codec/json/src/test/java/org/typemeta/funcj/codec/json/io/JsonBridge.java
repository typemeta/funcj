package org.typemeta.funcj.codec.json.io;

import org.typemeta.funcj.codec.json.JsonTypes;

import java.io.*;

/**
 * Connect a JSON input to a JSON output.
 */
public class JsonBridge {
    private final JsonTypes.InStream input;
    private final JsonTypes.OutStream output;

    public JsonBridge(JsonTypes.InStream input, JsonTypes.OutStream output) {
        this.input = input;
        this.output = output;
    }

    public JsonBridge(Reader reader, Writer writer) {
        this(new JsonStreamParser(reader, 3), new JsonGenerator(writer));
    }

    void run() {
        while (input.notEOF()) {
            //System.out.println(input.currentEventType());
            switch (input.currentEventType()) {
                case ARRAY_END:
                    input.endArray();
                    output.endArray();
                    break;
                case ARRAY_START:
                    input.startArray();
                    output.startArray();
                    break;
                case EOF:
                    break;
                case FALSE:
                case TRUE:
                    output.writeBoolean(input.readBoolean());
                    break;
                case FIELD_NAME:
                    output.writeField(input.readFieldName());
                    break;
                case NULL:
                    input.readNull();
                    output.writeNull();
                    break;
                case NUMBER:
                    output.writeNumber(input.readNumber());
                    break;
                case OBJECT_END:
                    input.endObject();
                    output.endObject();
                    break;
                case OBJECT_START:
                    input.startObject();
                    output.startObject();
                    break;
                case STRING:
                    output.writeString(input.readString());
                    break;
            }
        }
    }
}
