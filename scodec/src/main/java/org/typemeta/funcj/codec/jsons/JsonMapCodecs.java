package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.*;

import java.util.*;

public abstract class JsonMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, JsonIO.Input, JsonIO.Output> {
        private final JsonCodecCoreImpl core;
        private final Codec<K, JsonIO.Input, JsonIO.Output> keyCodec;
        private final Codec<V, JsonIO.Input, JsonIO.Output> valueCodec;

        public MapCodec(
                JsonCodecCoreImpl core,
                Codec<K, JsonIO.Input, JsonIO.Output> keyCodec,
                Codec<V, JsonIO.Input, JsonIO.Output> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public JsonIO.Output encode(Map<K, V> map, JsonIO.Output out) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            out.startArray();

            map.forEach((k, v) -> {
                out.startObject();
                out.writeField(keyFieldName);
                keyCodec.encode(k, out);
                out.writeField(valueFieldName);
                valueCodec.encode(v, out);
                out.endObject();
            });

            return out.endArray();
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, JsonIO.Input in) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            final Map<K, V> map = core.getTypeConstructor(dynType).construct();

            in.startArray();

            while(in.notEOF() && in.currentEventType() == JsonIO.Input.Event.Type.OBJECT_START) {
                K key = null;
                V val = null;

                in.startObject();

                while (key == null || val == null) {
                    final String name = in.readFieldName();
                    if (name.equals(keyFieldName)) {
                        if (key == null) {
                            key = keyCodec.decode(in);
                        } else {
                            throw new CodecException("Duplicate fields called " + keyFieldName);
                        }
                    } else if (name.equals(valueFieldName)) {
                        if (val == null) {
                            val = valueCodec.decode(in);
                        } else {
                            throw new CodecException("Duplicate fields called " + valueFieldName);
                        }
                    }
                }

                map.put(key, val);

                in.endObject();
            }

            in.endArray();

            return map;
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, JsonIO.Input, JsonIO.Output> {
        private final JsonCodecCoreImpl core;
        private final Codec<V, JsonIO.Input, JsonIO.Output> valueCodec;

        public StringMapCodec(JsonCodecCoreImpl core, Codec<V, JsonIO.Input, JsonIO.Output> valueCodec) {
            this.core = core;
            this.valueCodec = valueCodec;
        }

        @Override
        public JsonIO.Output encode(Map<String, V> map, JsonIO.Output out) {
            out.startObject();

            map.forEach((key, val) -> {
                out.writeField(key);
                valueCodec.encode(val, out);
            });

            return out.endObject();
        }

        @Override
        public Map<String, V> decode(Class<Map<String, V>> dynType, JsonIO.Input in) {
            in.startObject();

            final Map<String, V> map = core.getTypeConstructor(dynType).construct();

            while(in.notEOF() && in.currentEventType() == JsonIO.Input.Event.Type.FIELD_NAME) {
                final String key = in.readFieldName();
                final V val = valueCodec.decode(in);
                map.put(key, val);
            }

            in.endObject();

            return map;
        }
    }
}
