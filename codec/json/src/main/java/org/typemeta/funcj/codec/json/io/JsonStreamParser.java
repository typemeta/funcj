package org.typemeta.funcj.codec.json.io;

import org.typemeta.funcj.codec.json.JsonTypes;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.json.parser.JsonEvent;
import org.typemeta.funcj.json.parser.JsonTokeniser;

import java.io.Reader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Pull-based parser. Parses a stream of characters.
 * The caller calls methods on the parser to extract JSON events.
 * We implement our own parser here as we need the ability to lookahead.
 */
public class JsonStreamParser implements JsonTypes.InStream {
    enum State {
        OBJECT_NAME,
        OBJECT_COLON,
        OBJECT_COMMA,
        OBJECT_VALUE,
        ARRAY_COMMA,
        ARRAY_VALUE,
        END
    }

    private final JsonTokeniser tokeniser;
    private int bufferPos = 0;
    private final JsonEvent eventBuffer[];
    private final List<State> stateStack = new ArrayList<>();
    private State state = null;

    private JsonStreamParser(JsonTokeniser tokeniser, int lookAhead) {
        this.tokeniser = tokeniser;
        this.eventBuffer = new JsonEvent[lookAhead];
    }

    public JsonStreamParser(Reader reader, int lookAhead) {
        this(new JsonTokeniser(reader), lookAhead);
    }

    public JsonStreamParser(Reader reader) {
        this(new JsonTokeniser(reader), 1);
    }

    private CodecException raiseError(String msg) {
        return new CodecException(msg + " at position " + tokeniser.position());
    }

    private CodecException raiseError(Supplier<String> msg) {
        return new CodecException(msg.get() + " at position " + tokeniser.position());
    }

    private CodecException unexpectedToken(JsonEvent event) {
        return raiseError("Unexpected token " + event.type());
    }

    private void pushState(State newState) {
        stateStack.add(state);
        state = newState;
    }

    private void popState() {
        if (stateStack.isEmpty()) {
            throw raiseError("Attempting to pop empty stack due to mis-matching closing brace/bracket");
        } else {
            state = stateStack.remove(stateStack.size() - 1);
        }
    }

    public JsonEvent currentEvent() {
        if (eventBuffer[bufferPos] == null) {
            pullEventsIntoBuffer(0);
        }

        return eventBuffer[bufferPos];
    }

    private JsonEvent pullEventsIntoBuffer(int ahead) {
        if (ahead > eventBuffer.length) {
            throw raiseError("Lookahead of " + ahead + " not supported, max is " + eventBuffer.length + ",");
        } else {
            int pos = bufferPos;
            while(true) {
                if (eventBuffer[pos] == null) {
                    eventBuffer[pos] = tokeniser.getNextEvent();
                }
                if (ahead-- == 0) {
                    return eventBuffer[pos];
                }
                if (++pos == eventBuffer.length) {
                    pos = 0;
                }
            }
        }
    }

    public JsonEvent skipToNextEvent() {
        eventBuffer[bufferPos] = null;
        ++bufferPos;
        if (bufferPos == eventBuffer.length) {
            bufferPos = 0;
        }
        return pullEventsIntoBuffer(0);
    }

    @Override
    public String location() {
        return "position: " + tokeniser.position();
    }

    @Override
    public boolean notEOF() {
        return !currentEvent().equals(JsonEvent.Type.EOF);
    }

    @Override
    public JsonEvent.Type currentEventType() {
        return currentEvent().type();
    }

    @Override
    public JsonEvent event(int ahead) {
        return pullEventsIntoBuffer(ahead);
    }

    @Override
    public void skipNode() {
        int depth = stateStack.size();
        while (true) {
            switch (currentEventType().type()) {
                case OBJECT_START:
                case ARRAY_START:
                    processCurrentEvent();
                    break;
                case ARRAY_END:
                case OBJECT_END:
                    processCurrentEvent();
                    if (stateStack.size() == depth) {
                        return;
                    }
                    break;
                case TRUE:
                case FALSE:
                case NULL:
                case NUMBER:
                case STRING:
                    processCurrentEvent();
                    if (stateStack.size() == depth) {
                        return;
                    }
                    break;
                case FIELD_NAME:
                    processCurrentEvent();
                    break;
                case EOF:
                    throw new CodecException("Unexpected EOF");
                default:
                    throw new CodecException("Unexpected event type " + currentEventType().type());
            }
        }
     }

    private void checkTokenType(JsonEvent.Type type) {
        if (!currentEvent().type().equals(type)) {
            throw raiseError("Expecting " + type + " token but found " + currentEvent().type());
        }
    }

    public void processCurrentEvent() {
        final JsonEvent event = eventBuffer[bufferPos];

        if (state == null) {
            switch (event.type()) {
                case ARRAY_START:
                    pushState(State.ARRAY_VALUE);
                    break;
                case EOF:
                case FALSE:
                case NULL:
                case NUMBER:
                case STRING:
                case TRUE:
                    state = State.END;
                    break;
                case OBJECT_START:
                    pushState(State.OBJECT_NAME);
                    break;
                default:
                    throw unexpectedToken(event);
            }
        } else {
            switch (state) {
                case OBJECT_NAME:
                    switch (event.type()) {
                        case FIELD_NAME:
                            state = State.OBJECT_COLON;
                            break;
                        case OBJECT_END:
                            popState();
                            break;
                        default:
                            throw unexpectedToken(event);
                    }
                    break;
                case OBJECT_COLON:
                    switch (event.type()) {
                        case COLON:
                            state = State.OBJECT_VALUE;
                            break;
                        default:
                            throw unexpectedToken(event);
                    }
                    break;
                case OBJECT_VALUE:
                    switch (event.type()) {
                        case FALSE:
                        case NULL:
                        case NUMBER:
                        case STRING:
                        case TRUE:
                            state = State.OBJECT_COMMA;
                            break;
                        case ARRAY_START:
                            state = State.OBJECT_COMMA;
                            pushState(State.ARRAY_VALUE);
                            break;
                        case OBJECT_START:
                            state = State.OBJECT_COMMA;
                            pushState(State.OBJECT_NAME);
                            break;
                        default:
                            throw unexpectedToken(event);
                    }
                    break;
                case OBJECT_COMMA:
                    switch (event.type()) {
                        case COMMA:
                            state = State.OBJECT_NAME;
                            break;
                        case OBJECT_END:
                            popState();
                            break;
                        default:
                            throw unexpectedToken(event);
                    }
                    break;
                case ARRAY_VALUE:
                    switch (event.type()) {
                        case FALSE:
                        case NULL:
                        case NUMBER:
                        case STRING:
                        case TRUE:
                            state = State.ARRAY_COMMA;
                            break;
                        case ARRAY_START:
                            state = State.ARRAY_COMMA;
                            pushState(State.ARRAY_VALUE);
                            break;
                        case ARRAY_END:
                            popState();
                            break;
                        case OBJECT_START:
                            state = State.ARRAY_COMMA;
                            pushState(State.OBJECT_NAME);
                            break;
                        default:
                            throw unexpectedToken(event);
                    }
                    break;
                case ARRAY_COMMA:
                    switch (event.type()) {
                        case COMMA:
                            state = State.ARRAY_VALUE;
                            break;
                        case ARRAY_END:
                            popState();
                            break;
                        default:
                            throw unexpectedToken(event);
                    }
                    break;
            }
        }

        eventBuffer[bufferPos++] = null;
        if (bufferPos == eventBuffer.length) {
            bufferPos = 0;
        }
        if (eventBuffer[bufferPos] == null) {
            pullEventsIntoBuffer(0);
        }

        final JsonEvent event2 = eventBuffer[bufferPos];

        switch (event2.type()) {
            case COMMA:
                switch (state) {
                    case ARRAY_COMMA:
                        state = State.ARRAY_VALUE;
                        break;
                    case OBJECT_COMMA:
                        state = State.OBJECT_NAME;
                        break;
                    default:
                        throw unexpectedToken(event2);
                }
                skipToNextEvent();
                break;
            case COLON:
                switch (state) {
                    case OBJECT_COLON:
                        state = State.OBJECT_VALUE;
                        break;
                    default:
                        throw unexpectedToken(event2);
                }
                skipToNextEvent();
                break;
        }
    }

    @Override
    public <T> T readNull() {
        checkTokenType(JsonEvent.Type.NULL);
        processCurrentEvent();
        return null;
    }

    @Override
    public boolean readBoolean() {
        final JsonEvent.Type currType = currentEvent().type();
        if (currType.equals(JsonEvent.Type.FALSE)) {
            processCurrentEvent();
            return false;
        } else if (currType.equals(JsonEvent.Type.TRUE)) {
            processCurrentEvent();
            return true;
        } else {
            throw raiseError("Expecting boolean token but found " + currentEvent().type());
        }
    }

    @Override
    public String readString() {
        checkTokenType(JsonEvent.Type.STRING);
        final String result = ((JsonEvent.JString) currentEvent()).value;
        processCurrentEvent();
        return result;
    }

    @Override
    public char readChar() {
        checkTokenType(JsonEvent.Type.STRING);
        final char result = ((JsonEvent.JString) currentEvent()).value.charAt(0);
        processCurrentEvent();
        return result;
    }

    @Override
    public byte readByte() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Byte.parseByte(value);
    }

    @Override
    public short readShort() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Short.parseShort(value);
    }

    @Override
    public int readInt() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Integer.parseInt(value);
    }

    @Override
    public long readLong() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Long.parseLong(value);
    }

    @Override
    public float readFloat() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Float.parseFloat(value);
    }

    @Override
    public double readDouble() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Double.parseDouble(value);
    }

    @Override
    public Number readNumber() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        try {
            return NumberFormat.getInstance().parse(value);
        } catch (ParseException ex) {
            final String excerpt = value.length() > 16 ? value.substring(0, 16) + "..." : value;
            throw raiseError("Number token '" + excerpt + "' is not a valid number");
        }
    }

    @Override
    public BigDecimal readBigDecimal() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        return new BigDecimal(value);
    }

    @Override
    public String readStringNumber() {
        checkTokenType(JsonEvent.Type.NUMBER);
        final String value = ((JsonEvent.JNumber) currentEvent()).value;
        processCurrentEvent();
        return value;
    }

    @Override
    public void startObject() {
        checkTokenType(JsonEvent.Type.OBJECT_START);
        processCurrentEvent();
    }

    @Override
    public String readFieldName() {
        checkTokenType(JsonEvent.Type.FIELD_NAME);
        final String result = ((JsonEvent.FieldName) currentEvent()).value;
        processCurrentEvent();
        return result;
    }

    @Override
    public void readFieldName(String name) {
        final String actualName = readFieldName();
        if (!actualName.equals(name)) {
            throw raiseError("Expected a field named '" + name +
                    "' but got '" + actualName + "'");
        }
    }

    @Override
    public void endObject() {
        checkTokenType(JsonEvent.Type.OBJECT_END);
        processCurrentEvent();
    }

    @Override
    public void startArray() {
        checkTokenType(JsonEvent.Type.ARRAY_START);
        processCurrentEvent();
    }

    @Override
    public void endArray() {
        checkTokenType(JsonEvent.Type.ARRAY_END);
        processCurrentEvent();
    }
}
