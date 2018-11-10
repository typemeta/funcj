package org.typemeta.funcj.parser;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.util.Exceptions;

import java.io.Reader;
import java.util.Objects;

/**
 * {@code Input} represents a position in a stream of input symbols,
 * that {@link Parser}s operate on.
 * @param <I>       the input stream symbol type
 */
public interface Input<I> {
    /**
     * Construct an {@code Input} from a {@code char} array.
     * @param data      the input data
     * @return          the input stream
     */
    static Input<Chr> of(char[] data) {
        return new StringInput(data);
    }

    /**
     * Construct an {@code Input} from a {@link java.lang.String}.
     * @param s         the input data
     * @return          the input stream
     */
    static Input<Chr> of(String s) {
        return new StringInput(s.toCharArray());
    }

    /**
     * Construct an {@code Input} from a {@link java.io.Reader}.
     * @param rdr       the input data
     * @return          the input stream
     */
    static Input<Chr> of(Reader rdr) {
        return new ReaderInput(rdr);
    }

    /**
     * Returns true if and only if this input is at the end of the input stream.
     * @return          true if this input is at the end of the input stream
     */
    boolean isEof();

    /**
     * Returns the symbol from the stream indicated by this input.
     * Will throw if {@code isEof} is true.
     * @return          the next symbol
     */
    I get();

    /**
     * Get the next position in the input stream.
     * Will throw if {@code isEof} is true.
     * @return          the next position in the input stream
     */
    Input<I> next();

    /**
     * Return a implementation-specific representation of the
     * current position (e.g. an Integer).
     * @return          the current position
     */
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
        final String dataStr = isEof() ? "EOF" : String.valueOf(data[position]);
        return "StringInput{" + position + ",data=\"" + dataStr + "\"";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringInput that = (StringInput) o;
        return position == that.position &&
                data == that.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, position);
    }
}

class ReaderInput implements Input<Chr> {

    protected int position;
    protected final Reader reader;
    protected Chr current;
    protected boolean isEof = false;

    protected final ReaderInput other;

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

    protected ReaderInput setPosition(int position) {
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
        if (isEof()) {
            throw new RuntimeException("End of input");
        } else {
            return current;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReaderInput that = (ReaderInput) o;
        return position == that.position &&
                reader == that.reader;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, reader);
    }
}
