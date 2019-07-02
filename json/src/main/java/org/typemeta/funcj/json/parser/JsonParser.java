package org.typemeta.funcj.json.parser;

import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.json.parser.JsonEvent.Type;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A parser for JSON values.
 * The parser allows token look-ahead.
 */
public class JsonParser {

    public enum Consume {
        ALL, FIRST
    }

    public enum Comments {
        ALLOWED, DISALLOWED
    }

    /**
     * Parse the given JSON string into a JSON value.
     * @param json          the JSON string to be parsed
     * @return              the parsed JSON value
     * @throws JsonException if an error occurs while parsing the input
     */
    public static JsValue parse(String json) throws JsonException {
        return parse(new StringReader(json), Consume.ALL, Comments.DISALLOWED);
    }

    /**
     * Parse the JSON content in the given reader into a JSON value.
     * @param rdr           the JSON reader to be read from
     * @return              the parsed JSON value
     * @throws JsonException if an error occurs while parsing the input
     */
    public static JsValue parse(Reader rdr) throws JsonException {
        return parse(rdr, Consume.FIRST, Comments.DISALLOWED);
    }

    /**
     * Parse the JSON content in the given reader into a JSON value.
     * @param rdr           the JSON reader to be read from
     * @param consume       consume flag
     * @param comments      comments flag
     * @return              the parsed JSON value
     * @throws JsonException if an error occurs while parsing the input
     */
    public static JsValue parse(Reader rdr, Consume consume, Comments comments) throws JsonException {
        final JsonParser parser = new JsonParser(rdr, comments.equals(Comments.ALLOWED));
        final JsValue jsv = parser.readValue();
        if (consume.equals(Consume.ALL) && !parser.isEof()) {
            throw parser.tokeniser.raiseError("Input not at EOF after parsing JSON value");
        } else if (jsv.isArray() || jsv.isObject()) {
            return jsv;
        } else {
            throw new JsonException("A JSON payload should be an object or array, not a " + jsv.type());
        }
    }

    private final JsonTokeniser tokeniser;
    private JsonEvent nextEvent = null;

    public JsonParser(Reader rdr, boolean commentsAllowed) {
        this.tokeniser = new JsonTokeniser(rdr, commentsAllowed);
    }

    public JsonParser(Reader rdr) {
        this(rdr, false);
    }

    public boolean isEof() {
        return nextEvent().type() == Type.EOF;
    }

    private JsonEvent nextEvent() {
        if (nextEvent == null) {
            nextEvent = tokeniser.getNextEvent();
        }

        return nextEvent;
    }

    private void consumeEvent() {
        nextEvent = null;
    }

    private void consumeEvent(Type eventType) {
        if (nextEvent.type() != eventType) {
            throw tokeniser.raiseError("Expected " + eventType + " but got a " + nextEvent().type());
        } else {
            nextEvent = null;
        }
    }

    private void checkEventType(Type eventType) {
        if (nextEvent.type() != eventType) {
            throw tokeniser.raiseError("Expected " + eventType + " but got a " + nextEvent().type());
        }
    }

    private void checkEventType(Type eventType1, Type eventType2) {
        if (nextEvent.type() != eventType1 && nextEvent.type() != eventType2) {
            throw tokeniser.raiseError(
                    "Expected " + eventType1 + " or " + eventType2 + " but got a " + nextEvent().type()
            );
        }
    }

    private JsValue readValue() {
        final JsonEvent event = nextEvent();

        switch (event.type()) {
            case OBJECT_START:
                return readObject();
            case ARRAY_START:
                return readArray();
            case TRUE:
                consumeEvent();
                return JsBool.TRUE;
            case FALSE:
                consumeEvent();
                return JsBool.FALSE;
            case NULL:
                consumeEvent();
                return JsNull.NULL;
            case NUMBER: {
                final String value = ((JsonEvent.JNumber) event).value;
                final JsNumber jsNum = JSAPI.num(value);
                consumeEvent();
                return jsNum;
            }
            case STRING: {
                final String value = ((JsonEvent.JString) event).value;
                JsString jsStr = JSAPI.str(value);
                consumeEvent();
                return jsStr;
            }
            case EOF:
                throw tokeniser.raiseError("Unexpected EOF");
            default:
                throw tokeniser.raiseError("Unexpected event type " + event.type());
        }
    }

    private JsObject readObject() {
        consumeEvent(Type.OBJECT_START);

        if (nextEvent().type() == Type.OBJECT_END) {
            consumeEvent();
            return JSAPI.obj();
        }

        final List<JsObject.Field> fields = new ArrayList<>();

        while (true) {
            nextEvent();

            checkEventType(Type.FIELD_NAME);
            final String name = ((JsonEvent.FieldName)nextEvent()).value;
            consumeEvent();

            nextEvent();

            consumeEvent(Type.COLON);

            final JsValue value = readValue();

            fields.add(JSAPI.field(name, value));

            final JsonEvent.Type type = nextEvent().type();

            if (type == Type.OBJECT_END) {
                break;
            } else if (type == Type.COMMA) {
                consumeEvent();
            } else {
                throw tokeniser.raiseError(
                        "Expected " + Type.OBJECT_END + " or " + Type.COMMA + " but got a " + type
                );
            }
        }

        consumeEvent();

        return JSAPI.obj(fields);
    }

    private JsArray readArray() {
        consumeEvent(Type.ARRAY_START);

        if (nextEvent().type() == Type.ARRAY_END) {
            consumeEvent();
            return JSAPI.arr();
        }

        final List<JsValue> values = new ArrayList<>();

        while (true) {
            values.add(readValue());

            final JsonEvent.Type type = nextEvent().type();

            if (type == Type.ARRAY_END) {
                break;
            } else if (type == Type.COMMA) {
                consumeEvent();
            } else {
                throw tokeniser.raiseError(
                        "Expected " + Type.ARRAY_END + " or " + Type.COMMA + " but got a " + type
                );
            }
        }

        consumeEvent();

        return JSAPI.arr(values);
    }
}
