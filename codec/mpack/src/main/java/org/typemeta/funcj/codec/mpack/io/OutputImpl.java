package org.typemeta.funcj.codec.mpack.io;

import org.msgpack.core.MessagePacker;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.codec.mpack.MpackTypes;

import java.io.IOException;
import java.math.BigInteger;

public class OutputImpl implements MpackTypes.OutStream {

    private final MessagePacker mp;

    public OutputImpl(MessagePacker mp) {
        this.mp = mp;
    }

    @Override
    public void close() {
        try {
            mp.close();
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeBoolean(boolean value) {
        try {
            mp.packBoolean(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeChar(char value) {
        try {
            mp.packShort((short)value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeByte(byte value) {
        try {
            mp.packByte(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeShort(short value) {
        try {
            mp.packShort(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeInt(int value) {
        try {
            mp.packInt(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeLong(long value) {
        try {
            mp.packLong(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeFloat(float value) {
        try {
            mp.packFloat(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeDouble(double value) {
        try {
            mp.packDouble(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeString(String value) {
        try {
            mp.packString(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream startArray(int size) {
        try {
            mp.packArrayHeader(size);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream startMap(int size) {
        try {
            mp.packMapHeader(size);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public MpackTypes.OutStream writeBigInteger(BigInteger value) {
        try {
            mp.packBigInteger(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
