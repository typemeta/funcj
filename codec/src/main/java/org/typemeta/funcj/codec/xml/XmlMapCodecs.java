package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.typemeta.funcj.codec.xml.XmlCodec.*;

import java.util.Map;

public abstract class XmlMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, Input, Output, Config> {

        public MapCodec(
                Class<Map<K, V>> mapType,
                Codec<K, Input, Output, Config> keyCodec,
                Codec<V, Input, Output, Config> valueCodec) {
            super(mapType, keyCodec, valueCodec);
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, Map<K, V> value, Output out) {

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
        public Map<K, V> decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final MapProxy<K, V> mapProxy = getMapProxy(core);

            while(in.hasNext()) {
                if (!in.type().equals(XmlCodec.Input.Type.START_ELEMENT)) {
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

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, Input, Output, Config> {

        public StringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, Input, Output, Config> valueCodec) {
            super(type, valueCodec);
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, Map<String, V> value, Output out) {
            value.forEach((key, val) -> {
                valueCodec.encodeWithCheck(core, val, out.startElement(key));
                out.endElement();
            });

            return out;
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final MapProxy<String, V> mapProxy = getMapProxy(core);

            while (in.hasNext()) {
                if (!in.type().equals(XmlCodec.Input.Type.START_ELEMENT)) {
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
