package org.typemeta.funcj.codec.bytes.io;

import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.codec.bytes.BytesCodec;

import java.io.*;

public final class OutputImpl implements BytesCodec.Output {
    private final DataOutput output;

    public OutputImpl(DataOutput output) {
        this.output = output;
    }

    @Override
    public BytesCodec.Output writeBoolean(boolean v) {
        try {
            output.writeBoolean(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public BytesCodec.Output writeByte(int v) {
        try {
            output.writeByte(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public BytesCodec.Output writeChar(int v) {
        try {
            output.writeChar(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public BytesCodec.Output writeShort(int v) {
        try {
            output.writeShort(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public BytesCodec.Output writeInt(int v) {
        try {
            output.writeInt(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public BytesCodec.Output writeLong(long v) {
        try {
            output.writeLong(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public BytesCodec.Output writeFloat(float v) {
        try {
            output.writeFloat(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public BytesCodec.Output writeDouble(double v) {
        try {
            output.writeDouble(v);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    public BytesCodec.Output writeString(String s) {
        try {
            output.writeInt(s.length());
            output.writeChars(s);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
