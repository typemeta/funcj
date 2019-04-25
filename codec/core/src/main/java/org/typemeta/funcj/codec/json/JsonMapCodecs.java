package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.Codec;
import org.typemeta.funcj.codec.CodecCoreEx;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.typemeta.funcj.codec.json.JsonTypes.*;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.json.parser.JsonEvent;

import java.util.Map;

public abstract class JsonMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, InStream, OutStream, Config> {

        public MapCodec(
                Class<Map<K, V>> mapType,
                Codec<K, InStream, OutStream, Config> keyCodec,
                Codec<V, InStream, OutStream, Config> valueCodec) {
            super(mapType, keyCodec, valueCodec);
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, Map<K, V> value, OutStream out) {
            final String keyFieldName = core.config().keyFieldName();
            final String valueFieldName = core.config().valueFieldName();

            out.startArray();

            value.forEach((k, v) -> {
                out.startObject();
                out.writeField(keyFieldName);
                keyCodec.encodeWithCheck(core, k, out);
                out.writeField(valueFieldName);
                valueCodec.encodeWithCheck(core, v, out);
                out.endObject();
            });

            return out.endArray();
        }

        @Override
        public Map<K, V> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final String keyFieldName = core.config().keyFieldName();
            final String valueFieldName = core.config().valueFieldName();

            final MapProxy<K, V> mapProxy = getMapProxy(core);

            in.startArray();

            while(in.notEOF() && in.currentEventType() == JsonEvent.Type.OBJECT_START) {
                K key = null;
                V val = null;

                in.startObject();

                while (key == null || val == null) {
                    final String name = in.readFieldName();
                    if (name.equals(keyFieldName)) {
                        if (key == null) {
                            key = keyCodec.decodeWithCheck(core, in);
                        } else {
                            throw new CodecException("Duplicate fields called " + keyFieldName);
                        }
                    } else if (name.equals(valueFieldName)) {
                        if (val == null) {
                            val = valueCodec.decodeWithCheck(core, in);
                        } else {
                            throw new CodecException("Duplicate fields called " + valueFieldName);
                        }
                    }
                }

                mapProxy.put(key, val);

                in.endObject();
            }

            in.endArray();

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
            out.startObject();

            value.forEach((key, val) -> {
                out.writeField(key);
                valueCodec.encodeWithCheck(core, val, out);
            });

            return out.endObject();
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            in.startObject();

            final MapProxy<String, V> mapProxy = getMapProxy(core);

            while(in.notEOF() && in.currentEventType() == JsonEvent.Type.FIELD_NAME) {
                final String key = in.readFieldName();
                final V val = valueCodec.decodeWithCheck(core, in);
                mapProxy.put(key, val);
            }

            in.endObject();

            return mapProxy.construct();
        }
    }
}
