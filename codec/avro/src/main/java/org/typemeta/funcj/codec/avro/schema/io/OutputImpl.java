package org.typemeta.funcj.codec.avro.schema.io;

import org.typemeta.funcj.codec.CodecException;
import org.typemeta.funcj.codec.avro.AvroTypes;

import java.io.IOException;
import java.math.BigInteger;

public class OutputImpl implements AvroTypes.OutStream {

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
    public AvroTypes.OutStream writeBoolean(boolean value) {
        try {
            mp.packBoolean(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeChar(char value) {
        try {
            mp.packShort((short)value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeByte(byte value) {
        try {
            mp.packByte(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeShort(short value) {
        try {
            mp.packShort(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeInt(int value) {
        try {
            mp.packInt(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeLong(long value) {
        try {
            mp.packLong(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeFloat(float value) {
        try {
            mp.packFloat(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeDouble(double value) {
        try {
            mp.packDouble(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeString(String value) {
        try {
            mp.packString(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream startArray(int size) {
        try {
            mp.packArrayHeader(size);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream startMap(int size) {
        try {
            mp.packMapHeader(size);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }

    @Override
    public AvroTypes.OutStream writeBigInteger(BigInteger value) {
        try {
            mp.packBigInteger(value);
            return this;
        } catch (IOException ex) {
            throw new CodecException(ex);
        }
    }
}
