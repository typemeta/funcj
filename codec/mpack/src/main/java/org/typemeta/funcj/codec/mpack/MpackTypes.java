package org.typemeta.funcj.codec.mpack;

import org.msgpack.core.MessagePack;
import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.mpack.io.InputImpl;
import org.typemeta.funcj.codec.mpack.io.OutputImpl;
import org.typemeta.funcj.codec.stream.StreamCodecFormat;

import java.io.InputStream;
import java.io.OutputStream;
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
    public interface InStream extends StreamCodecFormat.Input<InStream> {
        int startArray();

        int startMap();

        BigInteger readBigInteger();
    }

    /**
     * Interface for classes which implement an output stream of bytes
     */
    public interface OutStream extends StreamCodecFormat.Output<OutStream> {
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

    public static MpackConfigImpl.BuilderImpl configBuilder() {
        return new MpackConfigImpl.BuilderImpl();
    }
}
