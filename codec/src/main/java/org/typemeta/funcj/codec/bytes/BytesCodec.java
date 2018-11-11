package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.bytes.io.*;

import java.io.*;

public abstract class BytesCodec {

    /**
     * Interface for classes which provide configuration information
     * for {@link BytesCodecCore} implementations.
     */
    public interface Config extends CodecConfig {
    }


    public static Input inputOf(InputStream is) {
        return new InputImpl(new DataInputStream(is));
    }

    public static Input inputOf(DataInput input) {
        return new InputImpl(input);
    }

    public static Output outputOf(OutputStream os) {
        return new OutputImpl(new DataOutputStream(os));
    }

    public static Output outputOf(DataOutput output) {
        return new OutputImpl(output);
    }

    public interface Input {
        boolean readBoolean();

        byte readByte();

        char readChar();

        short readShort();

        int readInt();

        long readLong();

        float readFloat();

        double readDouble();

        String readString();
    }

    public interface Output {
        Output writeBoolean(boolean v);

        Output writeByte(int v);

        Output writeChar(int v);

        Output writeShort(int v);

        Output writeInt(int v);

        Output writeLong(long v);

        Output writeFloat(float v);

        Output writeDouble(double v);

        Output writeString(String s);
    }
}
