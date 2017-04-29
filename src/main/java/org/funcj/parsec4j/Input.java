package org.funcj.parsec4j;

import org.funcj.util.*;

public interface Input<I> {
    static Input<Chr> of(char[] data) {
        return new StringInput(data);
    }

    static Input<Chr> of(String s) {
        return new StringInput(s.toCharArray());
    }

    boolean isEof();

    I get();

    Input<I> next();

    String position();
}

class StringInput implements Input<Chr> {

    private StringInput other;
    private final char[] data;
    private int pos;

    StringInput(StringInput other, char[] data) {
        this.other = other;
        this.data = data;
        this.pos = 0;
    }

    StringInput(char[] data) {
        this.other = new StringInput(this, data);
        this.data = data;
        this.pos = 0;
    }

    private StringInput setPos(int pos) {
        this.pos = pos;
        return this;
    }

    @Override
    public String toString() {
        return "{pos=" + pos + "}";
    }

    @Override
    public boolean isEof() {
        return pos >= data.length;
    }

    @Override
    public Chr get() {
        return Chr.valueOf(data[pos]);
    }

    @Override
    public Input<Chr> next() {
        return other.setPos(pos+1);
    }

    @Override
    public String position() {
        return String.valueOf(pos);
    }
}
