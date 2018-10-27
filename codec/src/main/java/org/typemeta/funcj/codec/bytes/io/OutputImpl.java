package org.typemeta.funcj.codec.bytes.io;

import org.typemeta.funcj.codec.CodecException;

import java.io.*;

public final class OutputImpl implements ByteIO.Output {
    private final DataOutput output;

    OutputImpl(DataOutput output) {
        this.output = output;
    }

    @Override
    public ByteIO.Output writeBoolean(boolean v) {
        try {
            output.writeBoolean(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public ByteIO.Output writeByte(int v) {
        try {
            output.writeByte(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public ByteIO.Output writeChar(int v) {
        try {
            output.writeChar(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public ByteIO.Output writeShort(int v) {
        try {
            output.writeShort(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public ByteIO.Output writeInt(int v) {
        try {
            output.writeInt(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public ByteIO.Output writeLong(long v) {
        try {
            output.writeLong(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public ByteIO.Output writeFloat(float v) {
        try {
            output.writeFloat(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public ByteIO.Output writeDouble(double v) {
        try {
            output.writeDouble(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    public ByteIO.Output writeString(String s) {
        try {
            output.writeInt(s.length());
            output.writeChars(s);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
