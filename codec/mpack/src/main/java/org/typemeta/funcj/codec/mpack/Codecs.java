package org.typemeta.funcj.codec.mpack;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.mpack.MpackTypes.*;

import java.math.BigInteger;

public abstract class Codecs extends org.typemeta.funcj.codec.Codecs {

    /**
     * Construct and return a new instance of a {@link MpackCodecCore}.
     * @return the new {@code MpackCodecCore}
     */
    public static MpackCodecCore mpackCodec(CodecConfig.Builder<MpackTypes.Config> cfgBldr) {
        final MpackCodecCore core = registerAll(cfgBldr, MpackCodecCore::new);

        core.registerCodec(BigInteger.class, new BigIntegerCodec());

        return core;
    }

    private static class BigIntegerCodec implements Codec<BigInteger, InStream, OutStream, Config> {

        @Override
        public Class<BigInteger> type() {
            return BigInteger.class;
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                BigInteger value,
                OutStream out
        ) {
            return out.writeBigInteger(value);
        }

        @Override
        public BigInteger decode(
                CodecCoreEx<InStream, OutStream, Config> core,
                InStream in
        ) {
            return in.readBigInteger();
        }
    }
}
