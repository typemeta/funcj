package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.typemeta.funcj.codec.json.io.JsonIO.Input;
import org.typemeta.funcj.codec.json.io.JsonIO.Output;

import java.util.Map;

public abstract class JsonMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, Input, Output> {
        private final JsonCodecCoreImpl core;

        public MapCodec(
                JsonCodecCoreImpl core,
                Class<Map<K, V>> type,
                Codec<K, Input, Output> keyCodec,
                Codec<V, Input, Output> valueCodec) {
            super(type, keyCodec, valueCodec);
            this.core = core;
        }

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return core;
        }

        @Override
        public Output encode(Map<K, V> map, Output out) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            out.startArray();

            map.forEach((k, v) -> {
                out.startObject();
                out.writeField(keyFieldName);
                keyCodec.encodeWithCheck(k, out);
                out.writeField(valueFieldName);
                valueCodec.encodeWithCheck(v, out);
                out.endObject();
            });

            return out.endArray();
        }

        @Override
        public Map<K, V> decode(Input in) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            final Map<K, V> map = core.getTypeConstructor(type).construct();

            in.startArray();

            while(in.notEOF() && in.currentEventType() == Input.Event.Type.OBJECT_START) {
                K key = null;
                V val = null;

                in.startObject();

                while (key == null || val == null) {
                    final String name = in.readFieldName();
                    if (name.equals(keyFieldName)) {
                        if (key == null) {
                            key = keyCodec.decodeWithCheck(in);
                        } else {
                            throw new CodecException("Duplicate fields called " + keyFieldName);
                        }
                    } else if (name.equals(valueFieldName)) {
                        if (val == null) {
                            val = valueCodec.decodeWithCheck(in);
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

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, Input, Output> {
        private final JsonCodecCoreImpl core;

        public StringMapCodec(
                JsonCodecCoreImpl core,
                Class<Map<String, V>> type,
                Codec<V, Input, Output> valueCodec) {
            super(type, valueCodec);
            this.core = core;
        }

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return core;
        }

        @Override
        public Output encode(Map<String, V> map, Output out) {
            out.startObject();

            map.forEach((key, val) -> {
                out.writeField(key);
                valueCodec.encodeWithCheck(val, out);
            });

            return out.endObject();
        }

        @Override
        public Map<String, V> decode(Input in) {
            in.startObject();

            final Map<String, V> map = core.getTypeConstructor(type).construct();

            while(in.notEOF() && in.currentEventType() == Input.Event.Type.FIELD_NAME) {
                final String key = in.readFieldName();
                final V val = valueCodec.decodeWithCheck(in);
                map.put(key, val);
            }

            in.endObject();

            return map;
        }
    }
}
