package org.typemeta.funcj.codec.avro.io;

import org.msgpack.core.MessageUnpacker;
import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.codec.avro.AvroTypes;

import java.io.IOException;
import java.math.BigInteger;

public class InputImpl implements AvroTypes.InStream {

    private final MessageUnpacker mu;

    public InputImpl(MessageUnpacker msgUnpkr) {
        this.mu = msgUnpkr;
    }

    @Override
    public void close() {
        try {
            mu.close();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public boolean readBoolean() {
        try {
            return mu.unpackBoolean();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public char readChar() {
        try {
            return (char)mu.unpackShort();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public byte readByte() {
        try {
            return mu.unpackByte();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public short readShort() {
        try {
            return mu.unpackShort();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public int readInt() {
        try {
            return mu.unpackInt();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public long readLong() {
        try {
            return mu.unpackLong();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public float readFloat() {
        try {
            return mu.unpackFloat();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public double readDouble() {
        try {
            return mu.unpackDouble();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public String readString() {
        try {
            return mu.unpackString();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public int startArray() {
        try {
            return mu.unpackArrayHeader();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public int startMap() {
        try {
            return mu.unpackMapHeader();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public BigInteger readBigInteger() {
        try {
            return mu.unpackBigInteger();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
