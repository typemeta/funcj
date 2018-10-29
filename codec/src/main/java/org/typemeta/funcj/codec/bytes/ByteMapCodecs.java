package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.typemeta.funcj.codec.bytes.io.ByteIO.Input;

import java.util.Map;

import static org.typemeta.funcj.codec.bytes.io.ByteIO.Output;

public abstract class ByteMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, Input, Output> {
        private final ByteCodecCoreImpl core;

        public MapCodec(
                ByteCodecCoreImpl core,
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
        public Output encode(Map<K, V> value, Output out) {
            core.intCodec().encodePrim(value.size(), out);

            for (Map.Entry<K, V> entry : value.entrySet()) {
                keyCodec.encodeWithCheck(entry.getKey(), out);
                valueCodec.encodeWithCheck(entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<K, V> decode(Input in) {
            final int l = core.intCodec().decodePrim(in);

            final Map<K, V> map = core.getTypeConstructor(type).construct();

            for (int i = 0; i < l; ++i) {
                final K key = keyCodec.decodeWithCheck(in);
                final V value = valueCodec.decodeWithCheck(in);
                map.put(key, value);
            }

            return map;
        }
    }

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, Input, Output> {
        private final ByteCodecCoreImpl core;

        public StringMapCodec(
                ByteCodecCoreImpl core,
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
        public Output encode(Map<String, V> value, Output out) {
            core.intCodec().encodePrim(value.size(), out);

            for (Map.Entry<String, V> entry : value.entrySet()) {
                core.stringCodec().encode(entry.getKey(), out);
                valueCodec.encodeWithCheck(entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<String, V> decode(Input in) {
            final int l = core.intCodec().decodePrim(in);

            final Map<String, V> map = core.getTypeConstructor(type).construct();

            for (int i = 0; i < l; ++i) {
                final String key = core.stringCodec().decode(in);
                final V value = valueCodec.decodeWithCheck(in);
                map.put(key, value);
            }

            return map;
        }
    }
}
