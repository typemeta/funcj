package org.typemeta.funcj.codec.xmls;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.xmls.io.XmlIO;
import org.typemeta.funcj.codec.xmls.io.XmlIO.Input;
import org.typemeta.funcj.codec.xmls.io.XmlIO.Output;

import java.util.HashMap;
import java.util.Map;

public abstract class XmlMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, Input, Output> {
        private final XmlCodecCoreImpl core;
        private final Class<Map<K, V>> type;
        private final Codec<K, Input, Output> keyCodec;
        private final Codec<V, Input, Output> valueCodec;

        public MapCodec(
                XmlCodecCoreImpl core,
                Class<Map<K, V>> type,
                Codec<K, Input, Output> keyCodec,
                Codec<V, Input, Output> valueCodec) {
            this.core = core;
            this.type = type;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public CodecCoreIntl<Input, Output> core() {
            return core;
        }

        @Override
        public Class<Map<K, V>> type() {
            return type;
        }

        private Codec<Map<K, V>, Input, Output> getCodec(Class<Map<K, V>> type) {
            return  core().mapCodec(type, keyCodec, valueCodec);
        }

        @Override
        public Output encode(Map<K, V> map, Output out) {

            map.forEach((k, v) -> {
                out.startElement(core.entryElemName());
                out.startElement(core.keyElemName());
                keyCodec.encodeWithCheck(k, out);
                out.endElement();
                out.startElement(core.valueElemName());
                valueCodec.encodeWithCheck(v, out);
                out.endElement();
                out.endElement();
            });

            return out;
        }

        @Override
        public Map<K, V> decode(Input in) {
            final Map<K, V> map = core.getTypeConstructor(type).construct();

            while(in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                in.startElement(core.entryElemName());
                in.startElement(core.keyElemName());
                final K key = keyCodec.decodeWithCheck(in);
                in.endElement();
                in.startElement(core.valueElemName());
                final V val = valueCodec.decodeWithCheck(in);
                in.endElement();
                in.endElement();
                map.put(key, val);
            }

            return map;
        }

        @Override
        public Output encodeWithCheck(Map<K, V> val, Output out) {
            if (core().encodeNull(val, out)) {
                return out;
            } else {
                if (!core().encodeDynamicType(this, val, out, this::getCodec)) {
                    return encode(val, out);
                } else {
                    return out;
                }
            }
        }

        @Override
        public Map<K, V> decodeWithCheck(Input in) {
            if (core().decodeNull(in)) {
                return null;
            } else {
                final Map<K, V> val = core().decodeDynamicType(in);
                if (val != null) {
                    return val;
                } else {
                    return decode(in);
                }
            }
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, Input, Output> {
        private final XmlCodecCoreImpl core;
        private final Class<Map<String, V>> type;
        private final Codec<V, Input, Output> valueCodec;

        public StringMapCodec(
                XmlCodecCoreImpl core,
                Class<Map<String, V>> type,
                Codec<V, Input, Output> valueCodec) {
            this.core = core;
            this.type = type;
            this.valueCodec = valueCodec;
        }

        @Override
        public CodecCoreIntl<Input, Output> core() {
            return core;
        }

        @Override
        public Class<Map<String, V>> type() {
            return type;
        }

        private Codec<Map<String, V>, Input, Output> getCodec(Class<Map<String, V>> type) {
            return  core().mapCodec(type, valueCodec);
        }

        @Override
        public Output encode(Map<String, V> map, Output out) {
           map.forEach((key, val) -> {
                valueCodec.encodeWithCheck(val, out.startElement(key));
                out.endElement();
            });

            return out;
        }

        @Override
        public Map<String, V> decode(Input in) {
            final Map<String, V> map = core.getTypeConstructor(type).construct();

            while(in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                final String key = in.startElement();
                final V val = valueCodec.decodeWithCheck(in);
                in.endElement();
                map.put(key, val);
            }

            return map;
        }

        @Override
        public Output encodeWithCheck(Map<String, V> val, Output out) {
            if (core().encodeNull(val, out)) {
                return out;
            } else {
                if (!core().encodeDynamicType(this, val, out, this::getCodec)) {
                    return encode(val, out);
                } else {
                    return out;
                }
            }
        }

        @Override
        public Map<String, V> decodeWithCheck(Input in) {
            if (core().decodeNull(in)) {
                return null;
            } else {
                final Map<String, V> val = core().decodeDynamicType(
                        in,
                        type -> core().mapCodec(
                                core().nameToClass(type),
                                valueCodec
                        ).decode(in)
                );
                if (val != null) {
                    return val;
                } else {
                    return decode(in);
                }
            }
        }
    }
}
