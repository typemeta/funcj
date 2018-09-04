package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.CodecException;

import java.io.*;
import java.util.Arrays;

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
        }

        class JNumber implements Event {
            public final String value;

            public JNumber(String value) {
                this.value = value;
            }
        }
    }

    private static final class Buffer {
        private static final int DEFAULT_SIZE = 64;

        private char[] buffer;
        private int size = 0;

        Buffer() {
            buffer = new char[DEFAULT_SIZE];
        }

        void add(char c) {
            if (size == buffer.length) {
                buffer = Arrays.copyOf(buffer, buffer.length * 2);
            }

            buffer[size++] = c;
        }

        boolean isEmpty() {
            return size == 0;
        }

        void clear() {
            size = 0;
        }

        @Override
        public String toString() {
            return new String(buffer, 0, size);
        }
    }

    private Reader rdr;
    private final Buffer buffer;

    public JsonTokeniser(Reader rdr) {
        this.rdr = rdr;
        this.buffer = new Buffer();
    }

    public Event getNextEvent() {
        if (rdr == null) {
            return parseBuffer();
        }

        try {
            int ic = rdr.read();
            char c = (char)ic;
            while (ic != -1 && Character.isWhitespace(c)) {
                ic = rdr.read();
                c = (char)ic;
            }

            if (ic == -1) {
                rdr = null;
                return parseBuffer();
            } else {
                switch (c) {
                    case '{': return Event.Enum.OBJECT_START;
                    case '}': return Event.Enum.OBJECT_END;
                    case '[': return Event.Enum.ARRAY_START;
                    case ']': return Event.Enum.ARRAY_EMD;
                    case ',': return Event.Enum.COMMA;
                    case ':': return Event.Enum.COLON;

                }
            }
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    public Event parseBuffer() {
        if (buffer.isEmpty()) {
            return Event.Enum.EOF;
        } else {

        }
    }
}
