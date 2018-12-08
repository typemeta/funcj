package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.typemeta.funcj.codec.xml.XmlTypes.*;

import java.util.Map;

public abstract class XmlMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, InStream, OutStream, Config> {

        public MapCodec(
                Class<Map<K, V>> mapType,
                Codec<K, InStream, OutStream, Config> keyCodec,
                Codec<V, InStream, OutStream, Config> valueCodec) {
            super(mapType, keyCodec, valueCodec);
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, Map<K, V> value, OutStream out) {

            value.forEach((k, v) -> {
                out.startElement(core.config().entryElemName());
                out.startElement(core.config().keyElemName());
                keyCodec.encodeWithCheck(core, k, out);
                out.endElement();
                out.startElement(core.config().valueElemName());
                valueCodec.encodeWithCheck(core, v, out);
                out.endElement();
                out.endElement();
            });

            return out;
        }

        @Override
        public Map<K, V> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final MapProxy<K, V> mapProxy = getMapProxy(core);

            while(in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                in.startElement(core.config().entryElemName());
                in.startElement(core.config().keyElemName());
                final K key = keyCodec.decodeWithCheck(core, in);
                in.endElement();
                in.startElement(core.config().valueElemName());
                final V val = valueCodec.decodeWithCheck(core, in);
                in.endElement();
                in.endElement();
                mapProxy.put(key, val);
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
            value.forEach((key, val) -> {
                valueCodec.encodeWithCheck(core, val, out.startElement(key));
                out.endElement();
            });

            return out;
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final MapProxy<String, V> mapProxy = getMapProxy(core);

            while (in.hasNext()) {
                if (!in.type().equals(InStream.Type.START_ELEMENT)) {
                    break;
                }

                final String key = in.startElement();
                final V val = valueCodec.decodeWithCheck(core, in);
                in.endElement();
                mapProxy.put(key, val);
            }

            return mapProxy.construct();
        }
    }
}
