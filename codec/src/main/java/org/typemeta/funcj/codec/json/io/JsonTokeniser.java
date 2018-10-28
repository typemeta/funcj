package org.typemeta.funcj.codec.json.io;

import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.codec.json.io.JsonIO.Input.Event;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public class JsonTokeniser {

    private static final class Buffer {
        private static final int DEFAULT_SIZE = 64;
        private static final int MAX_SIZE = 1024;

        private char[] buffer;
        private int size = 0;

        Buffer() {
            buffer = new char[DEFAULT_SIZE];
        }

        void add(char c) {
            if (size == buffer.length) {
                if (buffer.length >= MAX_SIZE) {
                    throw new IllegalStateException("Buffer too large");
                } else {
                    buffer = Arrays.copyOf(buffer, buffer.length * 2);
                }
            }

            buffer[size++] = c;
        }

        boolean isEmpty() {
            return size == 0;
        }

        String release() {
            final String res = new String(buffer, 0, size);
            size = 0;
            return res;
        }

        @Override
        public String toString() {
            return new String(buffer, 0, size);
        }
    }

    private static final char[] TRUE = "true".toCharArray();
    private static final char[] FALSE = "false".toCharArray();
    private static final char[] NULL = "null".toCharArray();

    private static final int EMPTY = Integer.MIN_VALUE;

    enum State {
        OBJECT_NAME,
        OBJECT_VALUE,
        OTHER
    }

    private Reader rdr;
    private int nextChar = EMPTY;
    private long pos = 0;
    private final Buffer buffer;
    private State state = State.OTHER;
    private final List<State> stateStack = new ArrayList<>();

    public JsonTokeniser(Reader rdr) {
        this.rdr = rdr;
        this.buffer = new Buffer();
    }

    public long position() {
        return pos;
    }

    private CodecException raiseError(Supplier<String> msg) {
        return new CodecException(msg.get() + " at position " + pos);
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

    private int nextChar() throws IOException {
        int nc;
        if (nextChar == EMPTY) {
            nc = rdr.read();
            ++pos;
        } else {
            nc = nextChar;
            nextChar = EMPTY;
        }
        return nc;
    }

    private char nextCharOrThrow(Supplier<String> msg) throws IOException {
        int nc = nextChar();

        if (nc == -1) {
            throw raiseError(msg);
        }

        return (char)nc;
    }

    private char nextCharOrThrow() throws IOException {
        return nextCharOrThrow(() -> "Unexpected end-of-input");
    }

    private void parseSymbol(char[] s) throws IOException {
        for (int i = 1; i < s.length; ++i) {
            final char c = nextCharOrThrow();
            if (c != s[i]) {
                throw raiseError(() -> ("Unexpected input '" + c + "' while parsing '" + s + "'"));
            }
        }
    }

    enum NumState {
        A, B, C, D, E, F, G, H, I, Z
    }

    public Event getNextEvent() {
        if (rdr == null) {
            return Event.Type.EOF;
        }

        try {
            int ic = nextChar();
            char nc = (char)ic;
            while (ic != -1 && Character.isWhitespace(nc)) {
                ic = nextChar();
                nc = (char)ic;
            }

            if (ic == -1) {
                rdr = null;
                return Event.Type.EOF;
            } else {
                final char c = nc;
                switch (c) {
                    case '{':
                        pushState(State.OBJECT_NAME);
                        return Event.Type.OBJECT_START;
                    case '}':
                        popState();
                        return Event.Type.OBJECT_END;
                    case '[':
                        pushState(State.OTHER);
                        return Event.Type.ARRAY_START;
                    case ']':
                        popState();
                        return Event.Type.ARRAY_END;
                    case ',':
                        if (state == State.OBJECT_VALUE) {
                            state = State.OBJECT_NAME;
                        }
                        return Event.Type.COMMA;
                    case ':':
                        state = State.OBJECT_VALUE;
                        return Event.Type.COLON;
                    case '"': {
                        while (true) {
                            final char c2 = nextStringChar();
                            switch (c2) {
                                case '"':
                                    return (state == State.OBJECT_NAME) ?
                                            new Event.FieldName(buffer.release()) :
                                            new Event.JString(buffer.release());
                                case '\\': {
                                    final char esc0 = nextStringChar();
                                    switch (esc0) {
                                        case 'u':
                                            final byte esc1 = nextStringUniChar();
                                            final byte esc2 = nextStringUniChar();
                                            final byte esc3 = nextStringUniChar();
                                            final byte esc4 = nextStringUniChar();
                                            final int hc = (esc1 << 12) | (esc2 << 8) | (esc3 << 4) | (esc4);
                                            buffer.add((char)hc);
                                            break;
                                        case '\\':
                                            buffer.add('\\');
                                            break;
                                        case '/':
                                            buffer.add('/');
                                            break;
                                        case 'b':
                                            buffer.add('\b');
                                            break;
                                        case 'f':
                                            buffer.add('\f');
                                            break;
                                        case 'n':
                                            buffer.add('\n');
                                            break;
                                        case 'r':
                                            buffer.add('\r');
                                            break;
                                        case 't':
                                            buffer.add('\t');
                                            break;
                                    }
                                    break;
                                }
                                default:
                                    buffer.add(c2);
                                    break;
                            }
                        }
                    }
                    case 't': {
                        parseSymbol(TRUE);
                        return Event.Type.TRUE;
                    }
                    case 'f': {
                        parseSymbol(FALSE);
                        return Event.Type.FALSE;
                    }
                    case 'n': {
                        parseSymbol(NULL);
                        return Event.Type.NULL;
                    }
                    case '0':
                        buffer.add(c);
                        return parseNumber(NumState.B);
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        buffer.add(c);
                        return parseNumber(NumState.C);
                    case '-':
                    case '+':
                        buffer.add(c);
                        return parseNumber(NumState.A);
                    default:
                        int i = 0;
                        throw raiseError(() -> "Unexpected input '" + c + "'");
                }
            }
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    private char nextStringChar() throws IOException {
        return nextCharOrThrow(() -> "Unexpected end-of-input while parsing a string");
    }

    private byte nextStringUniChar() throws IOException {
        final char c = nextCharOrThrow(() ->
                "Unexpected end-of-input while parsing an escape unicode char within a string"
        );

        switch (c) {
            case '0': return 0;
            case '1': return 1;
            case '2': return 2;
            case '3': return 3;
            case '4': return 4;
            case '5': return 5;
            case '6': return 6;
            case '7': return 7;
            case '8': return 8;
            case '9': return 9;
            case 'a':
            case 'A':
                return 10;
            case 'b':
            case 'B':
                return 11;
            case 'c':
            case 'C':
                return 12;
            case 'd':
            case 'D':
                return 13;
            case 'e':
            case 'E':
                return 14;
            case 'f':
            case 'F':
                return 15;
            default:
                throw raiseError(() -> "Unexpected non-digit '" + c + "' while parsing a string escape unicode char");
        }
    }

    private Event.JNumber parseNumber(NumState state) throws IOException {
        int ic = EMPTY;
        while (state != NumState.Z && (ic = nextChar()) != -1) {
            char c = (char)ic;
            switch (state) {
                case A:
                    switch (c) {
                        case '0': {
                            state = NumState.B;
                            break;
                        }
                        case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': {
                            state = NumState.C;
                            break;
                        }
                        default:
                            throw raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
                    }
                    break;
                case B:
                    switch (c) {
                        case '.': {
                            state = NumState.D;
                            break;
                        }
                        case 'e':
                        case 'E': {
                            state = NumState.F;
                            break;
                        }
                        default: {
                            state = NumState.Z;
                            break;
                        }
                    }
                    break;
                case C:
                    switch (c) {
                        case '.': {
                            state = NumState.D;
                            break;
                        }
                        case 'e':
                        case 'E': {
                            state = NumState.F;
                            break;
                        }
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': {
                            break;
                        }
                        default: {
                            state = NumState.Z;
                            break;
                        }
                    }
                    break;
                case D:
                    switch (c) {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': {
                            state = NumState.E;
                            break;
                        }
                        default:
                            throw raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
                    }
                    break;
                case E:
                    switch (c) {
                        case 'e':
                        case 'E': {
                            state = NumState.H;
                            break;
                        }
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                            break;
                        default: {
                            state = NumState.Z;
                            break;
                        }
                    }
                    break;
                case F:
                    switch (c) {
                        case '+':
                        case '-': {
                            state = NumState.G;
                            break;
                        }
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': {
                            state = NumState.I;
                            break;
                        }
                        default:
                            throw raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
                    }
                    break;
                case G:
                    switch (c) {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': {
                            state = NumState.I;
                            break;
                        }
                        default:
                            throw raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
                    }
                    break;
                case H:
                    switch (c) {
                        case 'e':
                        case 'E': {
                            state = NumState.F;
                            break;
                        }
                        default: {
                            state = NumState.Z;
                            break;
                        }
                    }
                    break;
                case I:
                    switch (c) {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': {
                            break;
                        }
                        default: {
                            state = NumState.Z;
                            break;
                        }
                    }
                    break;
                case Z:
                    break;
            }
            if (state != NumState.Z) {
                buffer.add(c);
            }
        }

        if (ic != EMPTY) {
            nextChar = ic;
        }

        switch (state) {
            case A:
            case D:
            case F:
            case G:
                throw raiseError(() -> "Unexpected end-of-input while parsing a number");
            default:
                return new Event.JNumber(buffer.release());
        }
    }
}
