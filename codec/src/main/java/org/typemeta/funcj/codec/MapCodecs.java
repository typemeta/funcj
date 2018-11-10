package org.typemeta.funcj.codec;

import java.util.Map;

public abstract class MapCodecs {

    public static abstract class AbstractMapCodec<K, V, IN, OUT, CFG extends CodecConfig>
            implements Codec<Map<K, V>, IN, OUT, CFG> {
        protected final Class<Map<K, V>> type;
        protected final Codec<K, IN, OUT, CFG> keyCodec;
        protected final Codec<V, IN, OUT, CFG> valueCodec;

        protected AbstractMapCodec(
                Class<Map<K, V>> type,
                Codec<K, IN, OUT, CFG> keyCodec,
                Codec<V, IN, OUT, CFG> valueCodec) {
            this.type = type;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Class<Map<K, V>> type() {
            return type;
        }

        protected Codec<Map<K, V>, IN, OUT, CFG> getCodec(CodecCoreEx<IN, OUT, CFG> core, Class<Map<K, V>> clazz) {
            return core.getMapCodec(clazz, keyCodec, valueCodec);
        }

        @Override
        public OUT encodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, Map<K, V> value, OUT out) {
            if (core.format().encodeNull(value, out)) {
                return out;
            } else {
                if (!core.format().encodeDynamicType(core,this, value, out, clazz -> getCodec(core, clazz))) {
                    return encode(core, value, out);
                } else {
                    return out;
                }
            }
        }

        @Override
        public Map<K, V> decodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            if (core.format().decodeNull(in)) {
                return null;
            } else {
                final Map<K, V> val = core.format().decodeDynamicType(
                        in,
                        clazz -> getCodec(core, core.config().nameToClass(clazz)).decode(core, in)
                );
                if (val != null) {
                    return val;
                } else {
                    return decode(core, in);
                }
            }
        }
    }

    public static abstract class AbstractStringMapCodec<V, IN, OUT, CFG extends CodecConfig>
            implements Codec<Map<String, V>, IN, OUT, CFG> {
        protected final Class<Map<String, V>> type;
        protected final Codec<V, IN, OUT, CFG> valueCodec;

        protected AbstractStringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, IN, OUT, CFG> valueCodec) {
            this.type = type;
            this.valueCodec = valueCodec;
        }

        @Override
        public Class<Map<String, V>> type() {
            return type;
        }

        protected Codec<Map<String, V>, IN, OUT, CFG> getCodec(
                CodecCoreEx<IN, OUT, CFG> core,
                Class<Map<String, V>> clazz) {
            return core.getMapCodec(clazz, valueCodec);
        }

        @Override
        public OUT encodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, Map<String, V> value, OUT out) {
            if (core.format().encodeNull(value, out)) {
                return out;
            } else {
                if (!core.format().encodeDynamicType(core, this, value, out, clazz -> getCodec(core, clazz))) {
                    return encode(core, value, out);
                } else {
                    return out;
                }
            }
        }

        @Override
        public Map<String, V> decodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            if (core.format().decodeNull(in)) {
                return null;
            } else {
                final Map<String, V> val = core.format().decodeDynamicType(
                        in,
                        clazz -> getCodec(core, core.config().nameToClass(clazz)).decode(core, in)
                );
                if (val != null) {
                    return val;
                } else {
                    return decode(core, in);
                }
            }
        }
    }
}
