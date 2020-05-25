package org.typemeta.funcj.codec.avro.schema;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.avro.AvroTypes.WithSchema;
import org.typemeta.funcj.codec.impl.MapCodecs;

import java.util.Map;

import static org.typemeta.funcj.codec.avro.AvroTypes.Config;

public abstract class AvroSchemaMapCodecs {
    public static class StringMapCodec<V> extends MapCodecs.AbstractStringMapCodec<V, WithSchema, Object, Config> {

        public StringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, WithSchema, Object, Config> valueCodec
        ) {
            super(type, valueCodec);
        }

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Map<String, V> value,
                Object out
        ) {
            final Schema schema = value.values().stream()
                    .map(t -> (Schema)valueCodec.encode(core, t, out.toString() + ".map"))
                    .reduce(SchemaMerge::merge)
                    .orElseGet(() -> Schema.create(Schema.Type.NULL));
            return Schema.createUnion(
                    Schema.createMap(schema),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }
}
