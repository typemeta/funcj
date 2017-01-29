package org.javafp.parsec4j;

import org.javafp.util.*;

public interface Input<I> {
    static Input<Chr> of(String s) {
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

class StringInput implements Input<Chr> {
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
    public Chr at(int pos) {
        return Chr.valueOf(s[pos]);
    }
}
