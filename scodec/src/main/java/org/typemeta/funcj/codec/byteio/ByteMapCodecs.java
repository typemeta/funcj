package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.Codec;

import java.util.Map;

public abstract class ByteMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, ByteIO.Input, ByteIO.Output> {
        private final ByteCodecCoreImpl core;
        private final Codec<K, ByteIO.Input, ByteIO.Output> keyCodec;
        private final Codec<V, ByteIO.Input, ByteIO.Output> valueCodec;

        public MapCodec(
                ByteCodecCoreImpl core,
                Codec<K, ByteIO.Input, ByteIO.Output> keyCodec,
                Codec<V, ByteIO.Input, ByteIO.Output> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public ByteIO.Output encode(Map<K, V> map, ByteIO.Output out) {
            core.intCodec().encodePrim(map.size(), out);

            for (Map.Entry<K, V> entry : map.entrySet()) {
                keyCodec.encode(entry.getKey(), out);
                valueCodec.encode(entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, ByteIO.Input in) {
            final int l = core.intCodec().decodePrim(in);

            final Map<K, V> map = core.getTypeConstructor(dynType).construct();

            for (int i = 0; i < l; ++i) {
                final K key = keyCodec.decode(in);
                final V value = valueCodec.decode(in);
                map.put(key, value);
            }

            return map;
        }
    }
}
