package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.CodecException;

import java.io.*;
import java.util.Arrays;
import java.util.function.Supplier;

public class JsonTokeniser {
    public interface Event {
        enum Enum implements Event {
            EOF,
            NULL,
            TRUE,
            FALSE,
            ARRAY_START,
            ARRAY_EMD,
            OBJECT_START,
            OBJECT_END,
            COMMA,
            COLON
        }

        class JString implements Event {
            public final String value;

            public JString(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return "JString{" + value + "}";
            }
        }

        class JNumber implements Event {
            public final String value;

            public JNumber(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return "JNumber{" + value + "}";
            }
        }
    }

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

    private Reader rdr;
    private int nextChar = EMPTY;
    private long pos = 0;
    private final Buffer buffer;

    public JsonTokeniser(Reader rdr) {
        this.rdr = rdr;
        this.buffer = new Buffer();
    }

    private void raiseError(Supplier<String> msg) {
        throw new CodecException(msg.get() + " at position " + pos);
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
            raiseError(msg);
        }

        return (char)nc;
    }

    private void pushChar(char c) throws IOException {
        if (nextChar != EMPTY) {
            throw new CodecException("Internal error -tried to push twice at position " + pos);
        } else {
            nextChar = c;
        }
    }

    private void clearNextChar() {
        nextChar = EMPTY;
    }

    private char nextCharOrThrow() throws IOException {
        return nextCharOrThrow(() -> "Unexpected end-of-input");
    }

    private void parseSymbol(char[] s) throws IOException {
        for (int i = 1; i < s.length; ++i) {
            final char c = nextCharOrThrow();
            if (c != s[i]) {
                raiseError(() -> ("Unexpected input '" + c + "' while parsing '" + s + "'"));
            }
        }
    }

    enum NumState {
        A, B, C, D, E, F, G, H, I, Z
    }

    public Event getNextEvent() {
        if (rdr == null) {
            return Event.Enum.EOF;
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
                return Event.Enum.EOF;
            } else {
                final char c = nc;
                switch (c) {
                    case '{':
                        return Event.Enum.OBJECT_START;
                    case '}':
                        return Event.Enum.OBJECT_END;
                    case '[':
                        return Event.Enum.ARRAY_START;
                    case ']':
                        return Event.Enum.ARRAY_EMD;
                    case ',':
                        return Event.Enum.COMMA;
                    case ':':
                        return Event.Enum.COLON;
                    case '"': {
                        while (true) {
                            char c2 = nextCharOrThrow(() -> "Unexpected end-of-input while parsing a string");
                            if (c2 == '"') {
                                return new Event.JString(buffer.release());
                            }
                            buffer.add(c2);
                        }
                    }
                    case 't': {
                        parseSymbol(TRUE);
                        return Event.Enum.TRUE;
                    }
                    case 'f': {
                        parseSymbol(FALSE);
                        return Event.Enum.FALSE;
                    }
                    case 'n': {
                        parseSymbol(NULL);
                        return Event.Enum.NULL;
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
                        raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
                }
            }
        } catch (IOException ex) {
            throw new CodecException(ex);
        }

        throw new CodecException("Illegal state (internal error)");
    }

    private Event.JNumber parseNumber(NumState state) throws IOException {
        int ic;
        while ((ic = nextChar()) != -1 && state != NumState.Z) {
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
                            raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
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
                            raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
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
                            raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
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
                            raiseError(() -> "Unexpected input '" + c + "' while parsing a number");
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

        switch (state) {
            case A:
            case D:
            case F:
            case G:
                raiseError(() -> "Unexpected end-of-input while parsing a number");
                break;
        }
        return new Event.JNumber(buffer.release());
    }
}
