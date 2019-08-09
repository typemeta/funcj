package org.typemeta.funcj.codec.avro.schema;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.impl.MapCodecs.AbstractStringMapCodec;
import org.typemeta.funcj.control.Either;
import org.typemeta.funcj.data.Unit;

import java.util.*;

public abstract class AvroSchemaMapCodecs {
    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, Unit, Either<String, Schema>, Config> {

        public StringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, Unit, Either<String, Schema>, Config> valueCodec
        ) {
            super(type, valueCodec);
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Map<String, V> value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.createMap(valueCodec.encode(core, null, out.mapLeft(s -> s + "Map")).right()),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }
}
