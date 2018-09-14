package org.typemeta.funcj.codec.jsons;

import java.io.Reader;
import java.io.Writer;

public class JsonBridge {
    private final JsonIO.Input input;
    private final JsonIO.Output output;

    public JsonBridge(JsonParser input, JsonGenerator output) {
        this.input = input;
        this.output = output;
    }

    public JsonBridge(Reader reader, Writer writer) {
        this(new JsonParser(reader), new JsonGenerator(writer));
    }

    void run() {
        while (input.notEOF()) {
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
                    output.writeBool(input.readBool());
                    break;
                case FIELD_NAME:
                    output.writeField(input.readFieldName());
                    break;
                case NULL:
                    input.readNull();
                    output.writeNull();
                    break;
                case NUMBER:
                    output.writeNum(input.readNumber());
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
                    output.writeStr(input.readStr());
                    break;
            }
        }
    }
}
