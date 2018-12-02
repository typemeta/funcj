package org.typemeta.funcj.codec.mpack;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.typemeta.funcj.codec.mpack.MpackTypes.*;

import java.util.Map;

public abstract class MpackMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, InStream, OutStream, Config> {

        public MapCodec(
                Class<Map<K, V>> mapType,
                Codec<K, InStream, OutStream, Config> keyCodec,
                Codec<V, InStream, OutStream, Config> valueCodec) {
            super(mapType, keyCodec, valueCodec);
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, Map<K, V> value, OutStream out) {
            out.startMap(value.size());

            for (Map.Entry<K, V> entry : value.entrySet()) {
                keyCodec.encodeWithCheck(core, entry.getKey(), out);
                valueCodec.encodeWithCheck(core, entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<K, V> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.startMap();

            final MapProxy<K, V> mapProxy = getMapProxy(core);

            for (int i = 0; i < l; ++i) {
                final K key = keyCodec.decodeWithCheck(core, in);
                final V value = valueCodec.decodeWithCheck(core, in);
                mapProxy.put(key, value);
            }

            return mapProxy.construct();
        }
    }

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, InStream, OutStream, Config> {

        public StringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, InStream, OutStream, Config> valueCodec) {
            super(type, valueCodec);
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, Map<String, V> value, OutStream out) {
            out.startMap(value.size());

            for (Map.Entry<String, V> entry : value.entrySet()) {
                core.format().stringCodec().encode(core, entry.getKey(), out);
                valueCodec.encodeWithCheck(core, entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.startMap();

            final MapProxy<String, V> mapProxy = getMapProxy(core);

            for (int i = 0; i < l; ++i) {
                final String key = core.format().stringCodec().decode(core, in);
                final V value = valueCodec.decodeWithCheck(core, in);
                mapProxy.put(key, value);
            }

            return mapProxy.construct();
        }
    }
}
