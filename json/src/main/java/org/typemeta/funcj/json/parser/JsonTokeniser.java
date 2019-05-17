package org.typemeta.funcj.json.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * Tokenise a stream of characters into JSON tokens.
 */
public class JsonTokeniser {

    private static final class Buffer {
        private static final int DEFAULT_SIZE = 64;

        private char[] buffer;
        private int size = 0;

        Buffer() {
            buffer = new char[DEFAULT_SIZE];
        }

        void add(char c) {
            if (size == buffer.length) {
                if (buffer.length >= Integer.MAX_VALUE / 2) {
                    throw new IllegalStateException("Buffer too large");
                } else {
                    buffer = Arrays.copyOf(buffer, buffer.length * 2);
                }
            }

            buffer[size++] = c;
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

    private final boolean allowComments;
    private Reader rdr;
    private int nextChar = EMPTY;
    private long pos = 0;
    private final Buffer buffer;
    private State state = State.OTHER;
    private final List<State> stateStack = new ArrayList<>();

    public JsonTokeniser(Reader rdr, boolean allowComments) {
        this.allowComments = allowComments;
        this.rdr = rdr;
        this.buffer = new Buffer();
    }

    public JsonTokeniser(Reader rdr) {
        this(rdr, false);
    }

    public long position() {
        return pos;
    }

    public JsonException raiseError(String msg) {
        return new JsonException(msg + ", at position " + pos);
    }

    private void pushState(State newState) {
        stateStack.add(state);
        state = newState;
    }

    private void popState() {
        if (stateStack.isEmpty()) {
            throw raiseError("Can't pop empty state stack");
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

    private char nextCharOrThrow(String msg) throws IOException {
        int nc = nextChar();

        if (nc == -1) {
            throw raiseError(msg);
        }

        return (char)nc;
    }

    private char nextCharOrThrow() throws IOException {
        return nextCharOrThrow("Unexpected end-of-input");
    }

    private void parseSymbol(char[] s) throws IOException {
        for (int i = 1; i < s.length; ++i) {
            final char c = nextCharOrThrow();
            if (c != s[i]) {
                throw raiseError("Unexpected input '" + c + "' while parsing '" + Arrays.toString(s) + "'");
            }
        }
    }

    enum NumState {
        A, B, C, D, E, F, G, H, I, J, Z
    }

    public JsonEvent getNextEvent() {
        if (rdr == null) {
            return JsonEvent.Type.EOF;
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
                return JsonEvent.Type.EOF;
            } else {
                switch (nc) {
                    case '{':
                        pushState(State.OBJECT_NAME);
                        return JsonEvent.Type.OBJECT_START;
                    case '}':
                        popState();
                        return JsonEvent.Type.OBJECT_END;
                    case '[':
                        pushState(State.OTHER);
                        return JsonEvent.Type.ARRAY_START;
                    case ']':
                        popState();
                        return JsonEvent.Type.ARRAY_END;
                    case ',':
                        if (state == State.OBJECT_VALUE) {
                            state = State.OBJECT_NAME;
                        }
                        return JsonEvent.Type.COMMA;
                    case ':':
                        state = State.OBJECT_VALUE;
                        return JsonEvent.Type.COLON;
                    case '"': {
                        while (true) {
                            final char c2 = nextStringChar();
                            switch (c2) {
                                case '"':
                                    return (state == State.OBJECT_NAME) ?
                                            new JsonEvent.FieldName(buffer.release()) :
                                            new JsonEvent.JString(buffer.release());
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
                                        case '"':
                                            buffer.add('"');
                                            break;
                                        default:
                                            throw raiseError(
                                                    "Unrecognised escape character in string - '" + esc0 + "'"
                                            );
                                    }
                                    break;
                                }
                                case '\b':
                                case '\f':
                                case '\n':
                                case '\r':
                                case '\t':
                                    throw raiseError(
                                            "Control characters not allowed in strings"
                                    );
                                default:
                                    buffer.add(c2);
                                    break;
                            }
                        }
                    }
                    case 't': {
                        parseSymbol(TRUE);
                        return JsonEvent.Type.TRUE;
                    }
                    case 'f': {
                        parseSymbol(FALSE);
                        return JsonEvent.Type.FALSE;
                    }
                    case 'n': {
                        parseSymbol(NULL);
                        return JsonEvent.Type.NULL;
                    }
                    case '0':
                        buffer.add(nc);
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
                        buffer.add(nc);
                        return parseNumber(NumState.C);
                    case '-':
                    case '+':
                        buffer.add(nc);
                        return parseNumber(NumState.A);
//                    case '/':
//                        if (!allowComments) {
//                            throw raiseError(
//                                    "Unexpected input '" + nc +
//                                         "' (support for comments is not enabled)");
//                        } else {
//                            char c2 = nextCharOrThrow();
//                            switch (c2) {
//                                case '/': {
//                                    c2 = nextCharOrThrow();
//                                    while (c2 != '\r' && c2 != '\n') {
//                                        c2 = nextCharOrThrow();
//                                    }
//
//                                    nextChar = c2;
//                                    break;
//                                }
//                                case '*': {
//                                    c2 = nextCharOrThrow();
//                                    while (c2 != '*' && nextCharOrThrow() != '/') {
//                                        c2 = nextCharOrThrow();
//                                    }
//
//                                    nextChar = c2;
//                                    break;
//                                }
//                                default:
//                                    throw raiseError("Unexpected input '" + nc + "'");
//                            }
//                        }
//                        break;
                    default:
                        throw raiseError("Unexpected input '" + nc + "'");
                }
            }
        } catch (IOException ex) {
            throw new JsonException(ex);
        }
    }

    private char nextStringChar() throws IOException {
        return nextCharOrThrow("Unexpected end-of-input while parsing a string");
    }

    private byte nextStringUniChar() throws IOException {
        final char c = nextCharOrThrow("Unexpected end-of-input while parsing an escape unicode char within a string");

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
                throw raiseError("Unexpected non-digit '" + c + "' while parsing a string escape unicode char");
        }
    }

    private JsonEvent.JNumber parseNumber(NumState state) throws IOException {
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
                            throw raiseError("Unexpected input '" + c + "' while parsing a number");
                    }
                    break;
                case B:
                    switch (c) {
                        case '.': {
                            state = NumState.D;
                            break;
                        }
                        case 'e':
                            c = 'E';
                            // Fall-through
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
                            c = 'E';
                            // Fall-through
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
                            throw raiseError("Unexpected input '" + c + "' while parsing a number");
                    }
                    break;
                case E:
                    switch (c) {
                        case 'e':
                            c = 'E';
                            // Fall-through
                        case 'E': {
                            state = NumState.F;
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
                        case '+': {
                            state = NumState.J;
                            break;
                        }
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
                            throw raiseError("Unexpected input '" + c + "' while parsing a number");
                    }
                    break;
                case G:
                case J:
                    switch (c) {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9': {
                            state = NumState.I;
                            break;
                        }
                        default:
                            throw raiseError("Unexpected input '" + c + "' while parsing a number");
                    }
                    break;
                case H:
                    switch (c) {
                        case 'e':
                            c = 'E';
                            // Fall-through
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
            }
            if (state != NumState.Z && state != NumState.J) {
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
                throw raiseError("Unexpected end-of-input while parsing a number");
            default:
                return new JsonEvent.JNumber(buffer.release());
        }
    }
}
