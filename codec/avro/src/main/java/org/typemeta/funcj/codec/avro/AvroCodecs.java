package org.typemeta.funcj.codec.avro;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.bytes.ByteCodecCore;

import java.math.BigInteger;

public abstract class AvroCodecs extends Codecs {

    /**
     * Construct and return a new instance of a {@link ByteCodecCore}.
     * @return the new {@code ByteCodecCore}
     */
    public static AvroCodecCore mpackCodec() {
        final AvroCodecCore core = registerAll(new AvroCodecCore());

        core.registerCodec(BigInteger.class, new BigIntegerCodec());

        return core;
    }

    private static class BigIntegerCodec implements Codec<BigInteger, AvroTypes.InStream, AvroTypes.OutStream, AvroTypes.Config> {

        @Override
        public Class<BigInteger> type() {
            return BigInteger.class;
        }

        @Override
        public AvroTypes.OutStream encode(
                CodecCoreEx<AvroTypes.InStream, AvroTypes.OutStream, AvroTypes.Config> core,
                BigInteger value,
                AvroTypes.OutStream out) {
            return out.writeBigInteger(value);
        }

        @Override
        public BigInteger decode(
                CodecCoreEx<AvroTypes.InStream, AvroTypes.OutStream, AvroTypes.Config> core,
                AvroTypes.InStream in) {
            return in.readBigInteger();
        }
    }
}
