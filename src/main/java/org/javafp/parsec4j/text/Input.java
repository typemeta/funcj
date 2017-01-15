package org.javafp.parsec4j.text;

import org.javafp.data.Unit;

public interface Input {
    static Input of(String s) {
        return new StringInput(s);
    }

    default boolean isEof(int pos) {
        return false;
    }

    char at(int pos);
}

enum EofInput implements Input {
    EOF {
        @Override
        public boolean isEof(int pos) {
            return true;
        }

        @Override
        public char at(int pos) {
            return 0;
        }
    };

    public static  Input of() {
        return (Input)EOF;
    }
}

class StringInput implements Input {
    protected final char[] s;

    StringInput(String s) {
        this.s = s.toCharArray();
    }

    StringInput(char[] s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return new String(s);
    }

    @Override
    public boolean isEof(int pos) {
        return pos >= s.length;
    }

    @Override
    public char at(int pos) {
        return s[pos];
    }
}
