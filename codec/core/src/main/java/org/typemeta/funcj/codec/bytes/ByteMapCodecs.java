package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.impl.MapCodecs.*;
import org.typemeta.funcj.codec.bytes.ByteTypes.*;

import java.util.Map;

public abstract class ByteMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, InStream, OutStream, Config> {

        public MapCodec(
                Class<Map<K, V>> mapType,
                Codec<K, InStream, OutStream, Config> keyCodec,
                Codec<V, InStream, OutStream, Config> valueCodec) {
            super(mapType, keyCodec, valueCodec);
        }

        @Override
        public OutStream encodeWithCheck(
                CodecCoreEx<InStream, OutStream, Config> core,
                Map<K, V> value,
                OutStream out
        ) {
            if (core.format().encodeNull(value, out).isNull) {
                return out;
            } else if (!core.format().encodeDynamicType(
                    core,this,
                    value, out,
                    clazz -> getCodec(core, clazz)).isNull) {
                return encode(core, value, out);
            } else {
                return out;
            }
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                Map<K, V> value,
                OutStream out
        ) {
            core.format().intCodec().encodePrim(value.size(), out);

            for (Map.Entry<K, V> entry : value.entrySet()) {
                keyCodec.encodeWithCheck(core, entry.getKey(), out);
                valueCodec.encodeWithCheck(core, entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<K, V> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = core.format().intCodec().decodePrim(in);

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
        public OutStream encodeWithCheck(
                CodecCoreEx<InStream, OutStream, Config> core,
                Map<String, V> value,
                OutStream out
        ) {
            if (core.format().encodeNull(value, out).isNull) {
                return out;
            } else if (!core.format().encodeDynamicType(
                    core,
                    this,
                    value,
                    out,
                    clazz -> getCodec(core, clazz)).isNull) {
                return encode(core, value, out);
            } else {
                return out;
            }
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                Map<String, V> value,
                OutStream out
        ) {
            core.format().intCodec().encodePrim(value.size(), out);

            for (Map.Entry<String, V> entry : value.entrySet()) {
                core.format().stringCodec().encode(core, entry.getKey(), out);
                valueCodec.encodeWithCheck(core, entry.getValue(), out);
            }

            return out;
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = core.format().intCodec().decodePrim(in);

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
