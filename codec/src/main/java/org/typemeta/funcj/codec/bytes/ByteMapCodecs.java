package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.Codec;
import org.typemeta.funcj.codec.CodecCoreEx;
import org.typemeta.funcj.codec.MapCodecs.AbstractMapCodec;
import org.typemeta.funcj.codec.MapCodecs.AbstractStringMapCodec;
import org.typemeta.funcj.codec.bytes.io.ByteIO.Input;

import java.util.Map;

import static org.typemeta.funcj.codec.bytes.io.ByteIO.Output;

public abstract class ByteMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, Input, Output, Config> {

        public MapCodec(
                Class<Map<K, V>> type,
                Codec<K, Input, Output, Config> keyCodec,
                Codec<V, Input, Output, Config> valueCodec) {
            super(type, keyCodec, valueCodec);
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, Map<K, V> value, Output out) {
            core.format().intCodec().encodePrim(value.size(), out);

            for (Map.Entry<K, V> entry : value.entrySet()) {
                keyCodec.encodeWithCheck(core, entry.getKey(), out);
                valueCodec.encodeWithCheck(core, entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<K, V> decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = core.format().intCodec().decodePrim(in);

            final Map<K, V> map = core.getNoArgsCtor(type).construct();

            for (int i = 0; i < l; ++i) {
                final K key = keyCodec.decodeWithCheck(core, in);
                final V value = valueCodec.decodeWithCheck(core, in);
                map.put(key, value);
            }

            return map;
        }
    }

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, Input, Output, Config> {

        public StringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, Input, Output, Config> valueCodec) {
            super(type, valueCodec);
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, Map<String, V> value, Output out) {
            core.format().intCodec().encodePrim(value.size(), out);

            for (Map.Entry<String, V> entry : value.entrySet()) {
                core.format().stringCodec().encode(core, entry.getKey(), out);
                valueCodec.encodeWithCheck(core, entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = core.format().intCodec().decodePrim(in);

            final Map<String, V> map = core.getNoArgsCtor(type).construct();

            for (int i = 0; i < l; ++i) {
                final String key = core.format().stringCodec().decode(core, in);
                final V value = valueCodec.decodeWithCheck(core, in);
                map.put(key, value);
            }

            return map;
        }
    }
}
