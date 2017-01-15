package org.javafp.parsec4j;

import org.javafp.data.Unit;

public interface Input<I> {
    static Input<Character> of(String s) {
        return new StringInput(s);
    }

    default boolean isEof(int pos) {
        return false;
    }

    I at(int pos);
}

enum EofInput implements Input<Unit> {
    EOF {
        @Override
        public boolean isEof(int pos) {
            return true;
        }

        @Override
        public Unit at(int pos) {
            return Unit.UNIT;
        }
    };

    public static <I> Input<I> of() {
        return (Input<I>)EOF;
    }
}

class StringInput implements Input<Character> {
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
    public Character at(int pos) {
        return s[pos];
    }
}
