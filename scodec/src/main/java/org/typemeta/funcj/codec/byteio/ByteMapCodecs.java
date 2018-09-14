package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.Codec;
import org.typemeta.funcj.codec.byteio.ByteIO.Input;
import org.typemeta.funcj.codec.byteio.ByteIO.Output;

import java.util.Map;

public abstract class ByteMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, Input, Output> {
        private final ByteCodecCoreImpl core;
        private final Codec<K, Input, Output> keyCodec;
        private final Codec<V, Input, Output> valueCodec;

        public MapCodec(
                ByteCodecCoreImpl core,
                Codec<K, Input, Output> keyCodec,
                Codec<V, Input, Output> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Output encode(Map<K, V> map, Output out) {
            core.intCodec().encodePrim(map.size(), out);

            for (Map.Entry<K, V> entry : map.entrySet()) {
                keyCodec.encode(entry.getKey(), out);
                valueCodec.encode(entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, Input in) {
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
