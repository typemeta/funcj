package org.typemeta.funcj.codec.avro.schema;

import org.typemeta.funcj.codec.Codecs;
import org.typemeta.funcj.codec.bytes.ByteCodecCore;

import java.math.BigInteger;

public abstract class AvroSchemaCodecs extends Codecs {

    /**
     * Construct and return a new instance of a {@link ByteCodecCore}.
     * @return the new {@code ByteCodecCore}
     */
    public static AvroSchemaCodecCore mpackCodec() {
        final AvroSchemaCodecCore core = registerAll(new AvroSchemaCodecCore());

        core.registerCodec(BigInteger.class, new BigIntegerCodec());

        return core;
    }
}
