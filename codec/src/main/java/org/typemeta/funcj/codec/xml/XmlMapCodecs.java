package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.Codec;
import org.typemeta.funcj.codec.CodecCoreEx;
import org.typemeta.funcj.codec.MapCodecs.AbstractMapCodec;
import org.typemeta.funcj.codec.MapCodecs.AbstractStringMapCodec;
import org.typemeta.funcj.codec.xml.io.XmlIO;
import org.typemeta.funcj.codec.xml.io.XmlIO.Input;
import org.typemeta.funcj.codec.xml.io.XmlIO.Output;

import java.util.Map;

public abstract class XmlMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, Input, Output, Config> {

        public MapCodec(
                Class<Map<K, V>> type,
                Codec<K, Input, Output, Config> keyCodec,
                Codec<V, Input, Output, Config> valueCodec) {
            super(type, keyCodec, valueCodec);
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
            final Map<K, V> map = core.getNoArgsCtor(type).construct();

            while(in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
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
                map.put(key, val);
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
            value.forEach((key, val) -> {
                valueCodec.encodeWithCheck(core, val, out.startElement(key));
                out.endElement();
            });

            return out;
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final Map<String, V> map = core.getNoArgsCtor(type).construct();

            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                final String key = in.startElement();
                final V val = valueCodec.decodeWithCheck(core, in);
                in.endElement();
                map.put(key, val);
            }

            return map;
        }
    }
}
