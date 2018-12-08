package org.typemeta.funcj.codec.bytes.io;

import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.codec.bytes.ByteTypes;

import java.io.*;

public final class OutputImpl implements ByteTypes.OutStream {
    private final DataOutput output;

    public OutputImpl(DataOutput output) {
        this.output = output;
    }

    @Override
    public void close() {
    }

    @Override
    public OutputImpl writeBoolean(boolean v) {
        try {
            output.writeBoolean(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeByte(byte v) {
        try {
            output.writeByte(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeChar(char v) {
        try {
            output.writeChar(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeShort(short v) {
        try {
            output.writeShort(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeInt(int v) {
        try {
            output.writeInt(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeLong(long v) {
        try {
            output.writeLong(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeFloat(float v) {
        try {
            output.writeFloat(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeDouble(double v) {
        try {
            output.writeDouble(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public OutputImpl writeString(String s) {
        try {
            output.writeInt(s.length());
            output.writeChars(s);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
