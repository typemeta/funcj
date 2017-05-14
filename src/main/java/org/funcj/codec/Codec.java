package org.funcj.codec;

public interface Codec<T, E> {
    interface BooleanCodec<E> {
        E encode(boolean val, E out);
        boolean decode(E in);
    }

    interface ByteCodec<E> {
        E encode(byte val, E out);
        byte decode(E in);
    }

    interface CharCodec<E> {
        E encode(char val, E out);
        char decode(E in);
    }

    interface ShortCodec<E> {
        E encode(short val, E out);
        short decode(E in);
    }

    interface IntCodec<E> {
        E encode(int val, E out);
        int decode(E in);
    }

    interface LongCodec<E> {
        E encode(long val, E out);
        long decode(E in);
    }

    interface FloatCodec<E> {
        E encode(float val, E out);
        float decode(E in);
    }

    interface DoubleCodec<E> {
        E encode(double val, E out);
        double decode(E in);
    }

    E encode(T val, E out);
    T decode(E in);
}
