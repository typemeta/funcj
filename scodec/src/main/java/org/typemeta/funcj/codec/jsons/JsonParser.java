package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.CodecException;

import java.io.Reader;
import java.math.BigDecimal;
import java.text.*;
import java.util.ArrayList;
import java.util.List;

public class JsonParser implements JsonIO.Input {
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
    private final Event eventBuffer[];
    private final List<State> stateStack = new ArrayList<>();
    private State state = null;

    private JsonParser(JsonTokeniser tokeniser, int lookAhead) {
        this.tokeniser = tokeniser;
        this.eventBuffer = new Event[lookAhead];
    }

    public JsonParser(Reader reader, int lookAhead) {
        this(new JsonTokeniser(reader), lookAhead);
    }

    public JsonParser(Reader reader) {
        this(new JsonTokeniser(reader), 3);
    }

    private void pushState(State newState) {
        stateStack.add(state);
        state = newState;
    }

    private void popState() {
        if (stateStack.isEmpty()) {
            throw new CodecException("Can't pop empty state stack");
        } else {
            state = stateStack.remove(stateStack.size() - 1);
        }
    }

    private CodecException unexpectedToken(Event event) {
        return new CodecException("Unexpected token " + event.type() + " at position " + tokeniser.position());
    }

    public Event currentEvent() {
        if (eventBuffer[bufferPos] == null) {
            pullEventsIntoBuffer(0);
        }

        return eventBuffer[bufferPos];
    }

    private Event pullEventsIntoBuffer(int ahead) {
        if (ahead > eventBuffer.length) {
            throw new CodecException("Lookahead of " + ahead + " not supported, max is " + eventBuffer.length);
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

    public Event skipToNextEvent() {
        eventBuffer[bufferPos] = null;
        ++bufferPos;
        if (bufferPos == eventBuffer.length) {
            bufferPos = 0;
        }
        return pullEventsIntoBuffer(0);
    }

    @Override
    public boolean notEOF() {
        return !currentEvent().equals(Event.Type.EOF);
    }

    @Override
    public Event.Type currentEventType() {
        return currentEvent().type();
    }

    @Override
    public Event event(int ahead) {
        return pullEventsIntoBuffer(ahead);
    }

    private void checkTokenType(Event.Type type) {
        if (!currentEvent().type().equals(type)) {
            throw new CodecException(
                    "Expecting " + type + " token but found " + currentEvent().type() +
                            " at position " + tokeniser.position()
            );
        }
    }

    public void processCurrentEvent() {
        final Event event = eventBuffer[bufferPos];

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

        final Event event2 = eventBuffer[bufferPos];

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
    public Void readNull() {
        checkTokenType(Event.Type.NULL);
        processCurrentEvent();
        return null;
    }

    @Override
    public boolean readBool() {
        final Event.Type currType = currentEvent().type();
        if (currType.equals(Event.Type.FALSE)) {
            processCurrentEvent();
            return false;
        } else if (currType.equals(Event.Type.TRUE)) {
            processCurrentEvent();
            return true;
        } else {
            throw new CodecException("Expecting boolean token but found " + currentEvent().type() + " at position " + tokeniser.position());
        }
    }

    @Override
    public String readStr() {
        checkTokenType(Event.Type.STRING);
        final String result = ((Event.JString) currentEvent()).value;
        processCurrentEvent();
        return result;
    }

    @Override
    public char readChar() {
        checkTokenType(Event.Type.STRING);
        final char result = ((Event.JString) currentEvent()).value.charAt(0);
        processCurrentEvent();
        return result;
    }

    @Override
    public byte readByte() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Byte.parseByte(value);
    }

    @Override
    public short readShort() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Short.parseShort(value);
    }

    @Override
    public int readInt() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Integer.parseInt(value);
    }

    @Override
    public long readLong() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Long.parseLong(value);
    }

    @Override
    public float readFloat() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Float.parseFloat(value);
    }

    @Override
    public double readDbl() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber) currentEvent()).value;
        processCurrentEvent();
        return Double.parseDouble(value);
    }

    @Override
    public BigDecimal readBigDec() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber) currentEvent()).value;
        processCurrentEvent();
        return new BigDecimal(value);
    }

    @Override
    public Number readNumber() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber) currentEvent()).value;
        processCurrentEvent();
        try {
            return NumberFormat.getInstance().parse(value);
        } catch (ParseException ex) {
            throw new CodecException("Number token not a valid number", ex);
        }
    }

    @Override
    public void startObject() {
        checkTokenType(Event.Type.OBJECT_START);
        processCurrentEvent();
    }

    @Override
    public String readFieldName() {
        checkTokenType(Event.Type.FIELD_NAME);
        final String result = ((Event.FieldName) currentEvent()).value;
        processCurrentEvent();
        return result;
    }

    @Override
    public void endObject() {
        checkTokenType(Event.Type.OBJECT_END);
        processCurrentEvent();
    }

    @Override
    public void startArray() {
        checkTokenType(Event.Type.ARRAY_START);
        processCurrentEvent();
    }

    @Override
    public void endArray() {
        checkTokenType(Event.Type.ARRAY_END);
        processCurrentEvent();
    }
}
