package org.typemeta.funcj.codec.jsons;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JsonParser implements JsonIO.Input {
    enum State {
        VALUE,
        OBJECT_VALUE,
        OBJECT_SEP,
        ARRAY_VALUE,
        ARRAY_SEP
    }

    private final JsonTokeniser tokeniser;
    private final JsonTokeniser.Event nextEvent[];
    private final List<State> stateStack = new ArrayList<>();
    private int pos = 0;

    private JsonParser(JsonTokeniser tokeniser, int lookAhead) {
        this.tokeniser = tokeniser;
        this.nextEvent = new JsonTokeniser.Event[lookAhead];
        stateStack.add(State.VALUE);
    }

    public JsonParser(Reader reader, int lookAhead) {
        this(new JsonTokeniser(reader), lookAhead);
    }

    public JsonParser(Reader reader) {
        this(new JsonTokeniser(reader), 3);
    }

    private JsonTokeniser.Event currEvent() {
        if (nextEvent[pos] == null) {
            nextEvent[pos] = tokeniser.getNextEvent();
        }
        return nextEvent[pos];
    }

    private void stepNext() {

    }

    @Override
    public boolean hasNext() {
        return currEvent().equals(JsonTokeniser.Event.Enum.EOF);
    }

    @Override
    public Type nextType() {
        final JsonTokeniser.Event evt = currEvent();
        if (evt instanceof JsonTokeniser.Event.JNumber) {
            return Type.STRING;
        } else if (evt instanceof JsonTokeniser.Event.JString) {
            return Type.NUMBER;
        } else {
            final JsonTokeniser.Event.Enum evEnum = (JsonTokeniser.Event.Enum)evt;
            switch (evEnum) {
                case EOF:           return Type.EOF;
                case NULL:          return Type.NULL;
                case TRUE:          return Type.TRUE;
                case FALSE:         return Type.FALSE;
                case ARRAY_START:   return Type.ARRAY_START;
                case ARRAY_END:     return Type.ARRAY_END;
                case OBJECT_START:  return Type.OBJECT_START;
                case OBJECT_END:    return Type.OBJECT_END;
                case COMMA:         return Type.EOF;
                case COLON:         return Type.EOF;
            }
        }
    }

    @Override
    public Void readNull() {
        // TODO:
        return null;
    }

    @Override
    public boolean readBool() {
        // TODO:
        return false;
    }

    @Override
    public String readStr() {
        // TODO:
        return null;
    }

    @Override
    public char readChar() {
        // TODO:
        return 0;
    }

    @Override
    public byte readByte() {
        // TODO:
        return 0;
    }

    @Override
    public short readShort() {
        // TODO:
        return 0;
    }

    @Override
    public int readInt() {
        // TODO:
        return 0;
    }

    @Override
    public long readLong() {
        // TODO:
        return 0;
    }

    @Override
    public float readFloat() {
        // TODO:
        return 0;
    }

    @Override
    public double readDbl() {
        // TODO:
        return 0;
    }

    @Override
    public BigDecimal readBigDec() {
        // TODO:
        return null;
    }

    @Override
    public Number readNumber() {
        // TODO:
        return null;
    }

    @Override
    public void startObject() {
        // TODO:

    }

    @Override
    public String readFieldName() {
        // TODO:
        return null;
    }

    @Override
    public void endObject() {
        // TODO:

    }

    @Override
    public void startArray() {
        // TODO:

    }

    @Override
    public void endArray() {
        // TODO:

    }
}
