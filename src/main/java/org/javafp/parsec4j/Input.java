package org.javafp.parsec4j;

import org.javafp.data.Unit;

public interface Input<I> {
    static Input<Character> of(String s) {
        return new StringInput(s, 0);
    }

    static <I> Input<I> of(I[] array) {
        return new ArrayInput<I>(array, 0);
    }

    default boolean isEof() {
        return false;
    }

    I head();

    Input<I> tail();
}

enum EofInput implements Input<Unit> {
    EOF {
        @Override
        public Unit head() {
            throw new IllegalStateException();
        }

        @Override
        public Input<Unit> tail() {
            throw new IllegalStateException();
        }

        @Override
        public boolean isEof() {
            return true;
        }
    };

    public static <I> Input<I> of() {
        return (Input<I>)EOF;
    }
}

abstract class PositionBasedInput<T> implements Input<T> {
    protected final int pos;

    protected PositionBasedInput(int pos) {
        this.pos = pos;
    }
}

class StringInput extends PositionBasedInput<Character> {
    protected final char[] s;

    StringInput(String s, int pos) {
        super(pos);
        this.s = s.toCharArray();
    }

    StringInput(char[] s, int pos) {
        super(pos);
        this.s = s;
    }

    @Override
    public String toString() {
        return "<pos=" + pos + " : [" + new String(s) + "]>";
    }

    @Override
    public Character head() {
        return s[pos];
    }

    @Override
    public Input<Character> tail() {
        if (pos+1 < s.length) {
            return new StringInput(s, pos + 1);
        } else {
            return EofInput.of();
        }
    }
}

class ArrayInput<I> extends PositionBasedInput<I> {

    final I[] array;

    ArrayInput(I[] array, int pos) {
        super(pos);
        this.array = array;
    }

    @Override
    public I head() {
        return array[pos];
    }

    @Override
    public Input<I> tail() {
        if (pos+1 < array.length) {
            return new ArrayInput(array, pos + 1);
        } else {
            return EofInput.of();
        }
    }
}
