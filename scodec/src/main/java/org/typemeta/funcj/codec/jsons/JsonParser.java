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
    private final JsonTokeniser.Event nextEvent[];
    private final List<State> stateStack = new ArrayList<>();
    private State state = null;
    private int pos = 0;

    private JsonParser(JsonTokeniser tokeniser, int lookAhead) {
        this.tokeniser = tokeniser;
        this.nextEvent = new JsonTokeniser.Event[lookAhead];
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

    public JsonTokeniser.Event currEvent() {
        if (nextEvent[pos] == null) {
            JsonTokeniser.Event event = tokeniser.getNextEvent();
            if (state == null) {
                switch (event.type()) {
                    case ARRAY_START:
                        nextEvent[pos] = event;
                        pushState(State.ARRAY_FIRST_VALUE);
                        break;
                    case EOF:
                    case FALSE:
                    case NULL:
                    case NUMBER:
                    case STRING:
                    case TRUE:
                        nextEvent[pos] = event;
                        state = State.END;
                        break;
                    case OBJECT_START:
                        nextEvent[pos] = event;
                        pushState(State.OBJECT_FIRST_NAME);
                        break;
                    default:
                        throw new CodecException("Unexpected token " + event.type() + " at position " + tokeniser.position());
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
                                throw new CodecException("Unexpected token " + event.type() + " at position " + tokeniser.position());
                        }
                    case OBJECT_FIRST_NAME:
                        switch (event.type()) {
                            case STRING:
                                nextEvent[pos] = event;
                                state = State.OBJECT_VALUE;
                                break;
                            case OBJECT_END:
                                nextEvent[pos] = event;
                                popState();
                                break;
                            default:
                                throw new CodecException("Unexpected token " + event.type() + " at position " + tokeniser.position());
                        }
                        break;
                    case OBJECT_VALUE:
                        if (!event.type().equals(JsonTokeniser.Event.Type.COLON)) {
                            throw new CodecException("Unexpected token " + event.type() + " at position " + tokeniser.position());
                        } else {
                            event = tokeniser.getNextEvent();
                            switch (event.type()) {
                                case FALSE:
                                case NULL:
                                case NUMBER:
                                case STRING:
                                case TRUE:
                                    nextEvent[pos] = event;
                                    state = State.OBJECT_NAME;
                                    break;
                                case ARRAY_START:
                                    nextEvent[pos] = event;
                                    state = State.OBJECT_NAME;
                                    pushState(State.ARRAY_FIRST_VALUE);
                                    break;
                                case OBJECT_START:
                                    nextEvent[pos] = event;
                                    state = State.OBJECT_NAME;
                                    pushState(State.OBJECT_FIRST_NAME);
                                    break;
                                case OBJECT_END:
                                    nextEvent[pos] = event;
                                    popState();
                                    break;
                                default:
                                    throw new CodecException("Unexpected token " + event.type() + " at position " + tokeniser.position());
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
                                throw new CodecException("Unexpected token " + event.type() + " at position " + tokeniser.position());
                        }
                    case ARRAY_FIRST_VALUE:
                        switch (event.type()) {
                            case FALSE:
                            case NULL:
                            case NUMBER:
                            case STRING:
                            case TRUE:
                                nextEvent[pos] = event;
                                state = State.ARRAY_VALUE;
                                break;
                            case ARRAY_START:
                                nextEvent[pos] = event;
                                state = State.ARRAY_VALUE;
                                pushState(State.ARRAY_FIRST_VALUE);
                                break;
                            case OBJECT_START:
                                nextEvent[pos] = event;
                                state = State.ARRAY_VALUE;
                                pushState(State.OBJECT_FIRST_NAME);
                                break;
                            case ARRAY_END:
                                nextEvent[pos] = event;
                                popState();
                                break;
                            default:
                                throw new CodecException("Unexpected token " + event.type() + " at position " + tokeniser.position());
                        }

                        break;
                }
            }
        }
        return nextEvent[pos];
    }

    public void next() {
        nextEvent[pos++] = null;
        if (pos == nextEvent.length) {
            pos = 0;
        }
    }

    @Override
    public boolean notEOF() {
        return !currEvent().equals(JsonTokeniser.Event.Type.EOF);
    }

    @Override
    public Type currentEventType() {
        switch (currEvent().type()) {
            case ARRAY_END:
                return Type.ARRAY_END;
            case ARRAY_START:
                return Type.ARRAY_START;
            case EOF:
                return Type.EOF;
            case FALSE:
                return Type.FALSE;
            case NULL:
                return Type.NULL;
            case NUMBER:
                return Type.NUMBER;
            case OBJECT_END:
                return Type.OBJECT_END;
            case OBJECT_START:
                return Type.OBJECT_START;
            case STRING:
                return Type.STRING;
            case TRUE:
                return Type.TRUE;
            default:
                throw new CodecException("Unexpected current token type" + currEvent().type());
        }
    }

    private void checkTokenType(JsonTokeniser.Event.Type type) {
        if (currEvent().type().equals(type)) {
            throw new CodecException("Expecting " + type + " token but found " + currEvent().type());
        }
    }

    @Override
    public Void readNull() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        return null;
    }

    @Override
    public boolean readBool() {
        final JsonTokeniser.Event.Type currType = currEvent().type();
        if (currType.equals(JsonTokeniser.Event.Type.FALSE)) {
            next();
            return false;
        } else if (currType.equals(JsonTokeniser.Event.Type.TRUE)) {
            next();
            return true;
        } else {
            throw new CodecException("Expecting boolean token but found " + currEvent().type());
        }
    }

    @Override
    public String readStr() {
        checkTokenType(JsonTokeniser.Event.Type.STRING);
        next();
        return ((JsonTokeniser.Event.JString)currEvent()).value;
    }

    @Override
    public char readChar() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        return ((JsonTokeniser.Event.JString)currEvent()).value.charAt(0);
    }

    @Override
    public byte readByte() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        final String value = ((JsonTokeniser.Event.JNumber)currEvent()).value;
        return Byte.parseByte(value);
    }

    @Override
    public short readShort() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        final String value = ((JsonTokeniser.Event.JNumber)currEvent()).value;
        return Short.parseShort(value);
    }

    @Override
    public int readInt() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        final String value = ((JsonTokeniser.Event.JNumber)currEvent()).value;
        return Integer.parseInt(value);
    }

    @Override
    public long readLong() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        final String value = ((JsonTokeniser.Event.JNumber)currEvent()).value;
        return Long.parseLong(value);
    }

    @Override
    public float readFloat() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        final String value = ((JsonTokeniser.Event.JNumber)currEvent()).value;
        return Float.parseFloat(value);
    }

    @Override
    public double readDbl() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        final String value = ((JsonTokeniser.Event.JNumber)currEvent()).value;
        return Double.parseDouble(value);
    }

    @Override
    public BigDecimal readBigDec() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        final String value = ((JsonTokeniser.Event.JNumber)currEvent()).value;
        return new BigDecimal(value);
    }

    @Override
    public Number readNumber() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        final String value = ((JsonTokeniser.Event.JNumber)currEvent()).value;
        try {
            return NumberFormat.getInstance().parse(value);
        } catch (ParseException ex) {
            throw new CodecException("Number token not a valid number", ex);
        }
    }

    @Override
    public void startObject() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
    }

    @Override
    public String readFieldName() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
        return ((JsonTokeniser.Event.JString)currEvent()).value;
    }

    @Override
    public void endObject() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
    }

    @Override
    public void startArray() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
    }

    @Override
    public void endArray() {
        checkTokenType(JsonTokeniser.Event.Type.NULL);
        next();
    }
}
