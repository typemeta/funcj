package org.typemeta.funcj.codec.mpack;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.bytes.ByteCodecCore;

import java.math.BigInteger;

public abstract class MpackCodecs extends Codecs {

    /**
     * Construct and return a new instance of a {@link ByteCodecCore}.
     * @return the new {@code ByteCodecCore}
     */
    public static MpackCodecCore mpackCodec() {
        final MpackCodecCore core = registerAll(new MpackCodecCore());

        core.registerCodec(BigInteger.class, new BigIntegerCodec());

        return core;
    }

    private static class BigIntegerCodec implements Codec<BigInteger, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public Class<BigInteger> type() {
            return BigInteger.class;
        }

        @Override
        public MpackTypes.OutStream encode(
                CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core,
                BigInteger value,
                MpackTypes.OutStream out) {
            return out.writeBigInteger(value);
        }

        @Override
        public BigInteger decode(
                CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core,
                MpackTypes.InStream in) {
            return in.readBigInteger();
        }
    }
}
