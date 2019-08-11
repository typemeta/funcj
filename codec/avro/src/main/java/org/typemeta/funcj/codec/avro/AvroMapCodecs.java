package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.apache.avro.util.Utf8;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.impl.MapCodecs.AbstractStringMapCodec;

import java.util.*;

import static org.typemeta.funcj.codec.avro.AvroCodecFormat.checkSchemaType;

public abstract class AvroMapCodecs {
//
//    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, WithSchema, Object, Config> {
//
//        public MapCodec(
//                Class<Map<K, V>> mapType,
//                Codec<K, WithSchema, Object, Config> keyCodec,
//                Codec<V, WithSchema, Object, Config> valueCodec
//        ) {
//            super(mapType, keyCodec, valueCodec);
//        }
//
//        @Override
//        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, Map<K, V> value, Object out) {
//            final String keyFieldName = core.config().keyFieldName();
//            final String valueFieldName = core.config().valueFieldName();
//
//            out.startArray();
//
//            value.forEach((k, v) -> {
//                out.startObject();
//                out.writeField(keyFieldName);
//                keyCodec.encodeWithCheck(core, k, out);
//                out.writeField(valueFieldName);
//                valueCodec.encodeWithCheck(core, v, out);
//                out.endObject();
//            });
//
//            return out.endArray();
//        }
//
//        @Override
//        public Map<K, V> decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
//            final String keyFieldName = core.config().keyFieldName();
//            final String valueFieldName = core.config().valueFieldName();
//
//            final MapProxy<K, V> mapProxy = getMapProxy(core);
//
//            in.startArray();
//
//            while(in.notEOF() && in.currentEventType() == JsonEvent.Type.OBJECT_START) {
//                K key = null;
//                V val = null;
//
//                in.startObject();
//
//                while (key == null || val == null) {
//                    final String name = in.readFieldName();
//                    if (name.equals(keyFieldName)) {
//                        if (key == null) {
//                            key = keyCodec.decodeWithCheck(core, in);
//                        } else {
//                            throw new CodecException("Duplicate fields called " + keyFieldName);
//                        }
//                    } else if (name.equals(valueFieldName)) {
//                        if (val == null) {
//                            val = valueCodec.decodeWithCheck(core, in);
//                        } else {
//                            throw new CodecException("Duplicate fields called " + valueFieldName);
//                        }
//                    }
//                }
//
//                mapProxy.put(key, val);
//
//                in.endObject();
//            }
//
//            in.endArray();
//
//            return mapProxy.construct();
//        }
//    }

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, WithSchema, Object, Config> {

        public StringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, WithSchema, Object, Config> valueCodec
        ) {
            super(type, valueCodec);
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, Map<String, V> value, Object out) {
            final Schema schema = checkSchemaType((Schema)out, Schema.Type.MAP);
            final Schema valueSchema = schema.getValueType();

            final Map<CharSequence, Object> map = new HashMap<>();

            value.forEach((key, val) -> {
                map.put(new Utf8(key), valueCodec.encodeWithCheck(core, val, valueSchema));
            });

            return map;
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            final Schema schema = checkSchemaType(in.schema(), Schema.Type.MAP);
            final Schema valueSchema = schema.getValueType();

            final Map<CharSequence, Object> inMap = in.value();

            final Map<String, V> map = new HashMap<>();

            inMap.forEach((key, value) -> {
                final String name = key.toString();
                map.put(name, valueCodec.decodeWithCheck(core, WithSchema.of(value, valueSchema)));
            });

            return map;
        }
    }
}
