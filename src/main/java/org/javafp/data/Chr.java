package org.javafp.data;

public final class Chr implements Comparable<Chr> {

    private static final int N = 128;

    private static final Chr[] chrs;

    static {
        chrs = new Chr[N];
        for (int i = 0; i < N; ++i) {
            chrs[i] = new Chr(i);
        }
    }

    public static Chr valueOf(char c) {
        return c < N ? chrs[c] : new Chr(c);
    }

    private final char value;

    private Chr(char value) {
        this.value = value;
    }

    public Chr(int value) {
        this.value = (char)value;
    }

    public char charValue() {
        return value;
    }

    @Override
    public int compareTo(Chr rhs) {
        return (value < rhs.value) ? -1 : ((value == rhs.value) ? 0 : 1);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object rhs) {
        if (rhs instanceof Chr) {
            return value == ((Chr)rhs).charValue();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int)value;
    }
}
