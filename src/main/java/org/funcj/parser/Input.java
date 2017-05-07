package org.funcj.parser;

import org.funcj.control.Exceptions;
import org.funcj.data.Chr;

import java.io.*;

public interface Input<I> {
    static Input<Chr> of(char[] data) {
        return new StringInput(data);
    }

    static Input<Chr> of(String s) {
        return new StringInput(s.toCharArray());
    }

    static Input<Chr> of(Reader rdr) {
        return new ReaderInput(rdr);
    }

    boolean isEof();

    I get();

    Input<I> next();

    Object position();
}

class StringInput implements Input<Chr> {

    private final char[] data;
    private int position;
    private final StringInput other;

    StringInput(char[] data) {
        this.data = data;
        this.position = 0;
        this.other = new StringInput(this, data);
    }

    StringInput(StringInput other, char[] data) {
        this.data = data;
        this.position = 0;
        this.other = other;
    }

    private StringInput setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public String toString() {
        return "StringInput{" + position + ",data=\"" + data[position] + "\"";
    }

    @Override
    public boolean isEof() {
        return position >= data.length;
    }

    @Override
    public Chr get() {
        return Chr.valueOf(data[position]);
    }

    @Override
    public Input<Chr> next() {
        return other.setPosition(position + 1);
    }

    @Override
    public Object position() {
        return position;
    }
}

class ReaderInput implements Input<Chr> {

    private int position;
    private final Reader reader;
    private Chr current;
    private boolean isEof = false;

    private final ReaderInput other;

    ReaderInput(Reader reader) {
        this.position = 0;
        this.reader = reader;
        this.current = null;
        this.other = new ReaderInput(this, reader);
    }

    ReaderInput(ReaderInput other, Reader reader) {
        this.position = -1;
        this.reader = reader;
        this.current = null;
        this.other = other;
    }

    private ReaderInput setPosition(int position) {
        this.position = position;
        return this;
    }

    @Override
    public String toString() {
        return "ReaderInput{current=\"" + current + "\",isEof=" + isEof + "}";
    }

    @Override
    public boolean isEof() {
        if (!isEof && current == null) {
            Exceptions.wrap(() -> {
                final int ni = reader.read();
                if (ni == -1) {
                    current = null;
                    isEof = true;
                } else {
                    current = Chr.valueOf(ni);
                }
            });
        }
        return isEof;
    }

    @Override
    public Chr get() {
        return current;
    }

    @Override
    public Input<Chr> next() {
        current = null;
        return other.setPosition(position + 1);
    }

    @Override
    public Object position() {
        return position;
    }
}