package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.Codec;

import java.util.Map;

public abstract class ByteMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, ByteIO> {
        private final ByteCodecCoreImpl core;
        private final Codec<K, ByteIO> keyCodec;
        private final Codec<V, ByteIO> valueCodec;

        public MapCodec(
                ByteCodecCoreImpl core,
                Codec<K, ByteIO> keyCodec,
                Codec<V, ByteIO> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public ByteIO encode(Map<K, V> map, ByteIO enc) throws Exception {
            core.intCodec().encodePrim(map.size(), enc);

            for (Map.Entry<K, V> entry : map.entrySet()) {
                keyCodec.encode(entry.getKey(), enc);
                valueCodec.encode(entry.getValue(), enc);
            }

            return enc;
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, ByteIO enc) throws Exception {
            final int l = core.intCodec().decodePrim(enc);

            final Map<K, V> map = core.getTypeConstructor(dynType).construct();

            for (int i = 0; i < l; ++i) {
                final K key = keyCodec.decode(enc);
                final V value = valueCodec.decode(enc);
                map.put(key, value);
            }

            return map;
        }
    }
}
