package org.typemeta.funcj.codec.avro.schema;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.impl.MapCodecs;
import org.typemeta.funcj.control.Either;
import org.typemeta.funcj.data.Unit;

import java.util.Map;

import static org.typemeta.funcj.codec.avro.schema.AvroSchemaTypes.Config;

public abstract class AvroSchemaMapCodecs {
    public static class StringMapCodec<V> extends MapCodecs.AbstractStringMapCodec<V, Unit, Either<String, Schema>, Config> {

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
            final Schema schema = value.values().stream()
                    .map(t -> valueCodec.encode(core, t, out.mapLeft(s -> s + ".map")).right())
                    .reduce(SchemaMerge::merge)
                    .orElseGet(() -> Schema.create(Schema.Type.NULL));
            return Either.right(Schema.createUnion(
                    Schema.createMap(schema),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }
}
