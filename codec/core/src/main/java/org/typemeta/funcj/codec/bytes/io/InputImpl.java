package org.typemeta.funcj.codec.bytes.io;

import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.codec.bytes.ByteTypes;

import java.io.*;

public final class InputImpl implements ByteTypes.InStream {
    private final DataInput input;

    public InputImpl(DataInput input) {
        this.input = input;
    }

    @Override
    public boolean readBoolean() {
        try {
            return input.readBoolean();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public byte readByte() {
        try {
            return input.readByte();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public char readChar() {
        try {
            return input.readChar();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public short readShort() {
        try {
            return input.readShort();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public int readInt() {
        try {
            return input.readInt();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public long readLong() {
        try {
            return input.readLong();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public float readFloat() {
        try {
            return input.readFloat();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public double readDouble() {
        try {
            return input.readDouble();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public String readString() {
        try {
            final int len = input.readInt();
            final char[] charArray = new char[len];
            for (int i = 0; i < len; ++i) {
                charArray[i] = input.readChar();
            }
            return new String(charArray);
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
