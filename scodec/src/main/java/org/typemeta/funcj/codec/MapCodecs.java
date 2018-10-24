package org.typemeta.funcj.codec;

import java.util.Map;

public abstract class MapCodecs {

    public static abstract class AbstractMapCodec<K, V, IN, OUT> implements Codec<Map<K, V>, IN, OUT> {
        protected final Class<Map<K, V>> type;
        protected final Codec<K, IN, OUT> keyCodec;
        protected final Codec<V, IN, OUT> valueCodec;

        protected AbstractMapCodec(
                Class<Map<K, V>> type,
                Codec<K, IN, OUT> keyCodec,
                Codec<V, IN, OUT> valueCodec) {
            this.type = type;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Class<Map<K, V>> type() {
            return type;
        }

        protected Codec<Map<K, V>, IN, OUT> getCodec(Class<Map<K, V>> type) {
            return core().mapCodec(type, keyCodec, valueCodec);
        }

        @Override
        public OUT encodeWithCheck(Map<K, V> val, OUT out) {
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
        public Map<K, V> decodeWithCheck(IN in) {
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

    public static abstract class AbstractStringMapCodec<V, IN, OUT> implements Codec<Map<String, V>, IN, OUT> {
        protected final Class<Map<String, V>> type;
        protected final Codec<V, IN, OUT> valueCodec;

        protected AbstractStringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, IN, OUT> valueCodec) {
            this.type = type;
            this.valueCodec = valueCodec;
        }

        @Override
        public Class<Map<String, V>> type() {
            return type;
        }

        protected Codec<Map<String, V>, IN, OUT> getCodec(Class<Map<String, V>> type) {
            return  core().mapCodec(type, valueCodec);
        }

        @Override
        public OUT encodeWithCheck(Map<String, V> val, OUT out) {
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
        public Map<String, V> decodeWithCheck(IN in) {
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
