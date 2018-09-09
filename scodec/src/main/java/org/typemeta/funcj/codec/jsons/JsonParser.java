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
        OBJECT_FIRST_NAME,
        OBJECT_VALUE,
        ARRAY_FIRST_VALUE,
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

    public Event currEvent() {
        return currEvent(bufferPos);
    }

    public Event currEvent(int pos) {
        if (eventBuffer[pos] == null) {
            Event event = tokeniser.getNextEvent();
            if (state == null) {
                switch (event.type()) {
                    case ARRAY_START:
                        pushState(State.ARRAY_FIRST_VALUE);
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
                        pushState(State.OBJECT_FIRST_NAME);
                        break;
                    default:
                        throw unexpectedToken(event);
                }
            } else {
                switch (state) {
                    case OBJECT_NAME:
                        switch (event.type()) {
                            case COMMA:
                                event = tokeniser.getNextEvent();
                                break;
                            case OBJECT_END:
                                break;
                            default:
                                throw unexpectedToken(event);
                        }
                    case OBJECT_FIRST_NAME:
                        switch (event.type()) {
                            case STRING:
                                event = new Event.FieldName(((Event.JString)event).value);
                                state = State.OBJECT_VALUE;
                                break;
                            case OBJECT_END:
                                eventBuffer[pos] = event;
                                popState();
                                break;
                            default:
                                throw unexpectedToken(event);
                        }
                        break;
                    case OBJECT_VALUE:
                        if (!event.type().equals(Event.Type.COLON)) {
                            throw unexpectedToken(event);
                        } else {
                            event = tokeniser.getNextEvent();
                            switch (event.type()) {
                                case FALSE:
                                case NULL:
                                case NUMBER:
                                case STRING:
                                case TRUE:
                                    state = State.OBJECT_NAME;
                                    break;
                                case ARRAY_START:
                                    state = State.OBJECT_NAME;
                                    pushState(State.ARRAY_FIRST_VALUE);
                                    break;
                                case OBJECT_START:
                                    state = State.OBJECT_NAME;
                                    pushState(State.OBJECT_FIRST_NAME);
                                    break;
                                case OBJECT_END:
                                    popState();
                                    break;
                                default:
                                    throw unexpectedToken(event);
                            }
                        }
                        break;
                    case ARRAY_VALUE:
                        switch (event.type()) {
                            case COMMA:
                                event = tokeniser.getNextEvent();
                                break;
                            case ARRAY_END:
                                break;
                            default:
                                throw unexpectedToken(event);
                        }
                    case ARRAY_FIRST_VALUE:
                        switch (event.type()) {
                            case FALSE:
                            case NULL:
                            case NUMBER:
                            case STRING:
                            case TRUE:
                                state = State.ARRAY_VALUE;
                                break;
                            case ARRAY_START:
                                state = State.ARRAY_VALUE;
                                pushState(State.ARRAY_FIRST_VALUE);
                                break;
                            case OBJECT_START:
                                state = State.ARRAY_VALUE;
                                pushState(State.OBJECT_FIRST_NAME);
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
            eventBuffer[pos] = event;
        }
        return eventBuffer[pos];
    }


    public void stepNext() {
        eventBuffer[bufferPos] = null;
        ++bufferPos;
        if (bufferPos == eventBuffer.length) {
            bufferPos = 0;
        }
    }

    private int nextPos() {
        return nextPos(bufferPos);
    }

    private int nextPos(int pos) {
        ++pos;
        if (++pos == eventBuffer.length) {
            return 0;
        } else {
            return pos;
        }
    }

    @Override
    public boolean notEOF() {
        return !currEvent().equals(Event.Type.EOF);
    }

    @Override
    public Event.Type currentEventType() {
        return currEvent().type();
    }

    @Override
    public Event.Type eventType(int lookahead) {
        return event(lookahead).type();
    }

    @Override
    public Event event(int lookahead) {
        if (lookahead > eventBuffer.length) {
            throw new CodecException("Lookahead of " + lookahead + " not supported, max is " + eventBuffer.length);
        } else {
            for (int i = 0; i < lookahead; ++i) {
                currEvent(i);
            }
            return currEvent(lookahead);
        }
    }

    private void checkTokenType(Event.Type type) {
        if (!currEvent().type().equals(type)) {
            throw new CodecException("Expecting " + type + " token but found " + currEvent().type() + " at position " + tokeniser.position());
        }
    }

    @Override
    public Void readNull() {
        checkTokenType(Event.Type.NULL);
        stepNext();
        return null;
    }

    @Override
    public boolean readBool() {
        final Event.Type currType = currEvent().type();
        if (currType.equals(Event.Type.FALSE)) {
            stepNext();
            return false;
        } else if (currType.equals(Event.Type.TRUE)) {
            stepNext();
            return true;
        } else {
            throw new CodecException("Expecting boolean token but found " + currEvent().type() + " at position " + tokeniser.position());
        }
    }

    @Override
    public String readStr() {
        checkTokenType(Event.Type.STRING);
        final String result = ((Event.JString)currEvent()).value;
        stepNext();
        return result;
    }

    @Override
    public char readChar() {
        checkTokenType(Event.Type.STRING);
        final char result = ((Event.JString)currEvent()).value.charAt(0);
        stepNext();
        return result;
    }

    @Override
    public byte readByte() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber)currEvent()).value;
        stepNext();
        return Byte.parseByte(value);
    }

    @Override
    public short readShort() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber)currEvent()).value;
        stepNext();
        return Short.parseShort(value);
    }

    @Override
    public int readInt() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber)currEvent()).value;
        stepNext();
        return Integer.parseInt(value);
    }

    @Override
    public long readLong() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber)currEvent()).value;
        stepNext();
        return Long.parseLong(value);
    }

    @Override
    public float readFloat() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber)currEvent()).value;
        stepNext();
        return Float.parseFloat(value);
    }

    @Override
    public double readDbl() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber)currEvent()).value;
        stepNext();
        return Double.parseDouble(value);
    }

    @Override
    public BigDecimal readBigDec() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber)currEvent()).value;
        stepNext();
        return new BigDecimal(value);
    }

    @Override
    public Number readNumber() {
        checkTokenType(Event.Type.NUMBER);
        final String value = ((Event.JNumber)currEvent()).value;
        stepNext();
        try {
            return NumberFormat.getInstance().parse(value);
        } catch (ParseException ex) {
            throw new CodecException("Number token not a valid number", ex);
        }
    }

    @Override
    public void startObject() {
        checkTokenType(Event.Type.OBJECT_START);
        stepNext();
    }

    @Override
    public String readFieldName() {
        checkTokenType(Event.Type.FIELD_NAME);
        final String result = ((Event.FieldName)currEvent()).value;
        stepNext();
        return result;
    }

    @Override
    public void endObject() {
        checkTokenType(Event.Type.OBJECT_END);
        stepNext();
    }

    @Override
    public void startArray() {
        checkTokenType(Event.Type.ARRAY_START);
        stepNext();
    }

    @Override
    public void endArray() {
        checkTokenType(Event.Type.ARRAY_END);
        stepNext();
    }
}
