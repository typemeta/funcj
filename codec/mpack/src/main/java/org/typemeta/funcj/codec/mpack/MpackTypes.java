package org.typemeta.funcj.codec.mpack;

import org.msgpack.core.MessagePack;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.mpack.io.*;

import java.io.*;
import java.math.BigInteger;

public abstract class MpackTypes {

    /**
     * Interface for classes which provide configuration information
     * for {@link MpackCodecCore} implementations.
     */
    public interface Config extends CodecConfig {
    }

    /**
     * Interface for classes which implement an input stream of bytes
     */
    public interface InStream extends CodecFormat.Input<InStream> {
        int startArray();

        int startMap();

        BigInteger readBigInteger();
    }

    /**
     * Interface for classes which implement an output stream of bytes
     */
    public interface OutStream extends CodecFormat.Output<OutStream> {
        OutStream startArray(int size);

        OutStream startMap(int size);

        OutStream writeBigInteger(BigInteger value);
    }

    public static InStream inputOf(InputStream is) {
        return new InputImpl(MessagePack.newDefaultUnpacker(is));
    }


    public static OutStream outputOf(OutputStream os) {
        return new OutputImpl(MessagePack.newDefaultPacker(os));
    }
}
