package org.typemeta.funcj.codec;

import org.typemeta.funcj.tuples.Tuple2;

import java.lang.reflect.Array;
import java.util.*;

public abstract class MapCodecs {

    public static abstract class AbstractMapCodec<K, V, IN, OUT, CFG extends CodecConfig>
            implements Codec<Map<K, V>, IN, OUT, CFG> {

        protected final Class<Map<K, V>> mapType;
        protected final Codec<K, IN, OUT, CFG> keyCodec;
        protected final Codec<V, IN, OUT, CFG> valueCodec;

        protected AbstractMapCodec(
                Class<Map<K, V>> mapType,
                Codec<K, IN, OUT, CFG> keyCodec,
                Codec<V, IN, OUT, CFG> valueCodec) {
            this.mapType = mapType;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Class<Map<K, V>> type() {
            return mapType;
        }

        protected Codec<Map<K, V>, IN, OUT, CFG> getCodec(CodecCoreEx<IN, OUT, CFG> core, Class<Map<K, V>> clazz) {
            return core.getMapCodec(clazz, keyCodec, valueCodec);
        }

        @Override
        public OUT encodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, Map<K, V> value, OUT out) {
            final Tuple2<Boolean, OUT> nullRes = core.format().encodeNull(value, out);
            if (nullRes._1) {
                return nullRes._2;
            } else if (core.config().isDefaultCollectionType(type(), value.getClass())) {
                final Class<Map<K, V>> implCollType = core.config().getDefaultCollectionType(type());
                return getCodec(core, implCollType).encode(core, value, out);
            } else {
                final Tuple2<Boolean, OUT> dynRes =
                        core.format().encodeDynamicType(
                                core,
                                this,
                                value,
                                out,
                                clazz -> getCodec(core, clazz)
                        );
                if (dynRes._1) {
                    return dynRes._2;
                } else {
                    return encode(core, value, out);
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
                        (type, in2) -> getCodec(core, core.config().nameToClass(type)).decode(core, in2)
                );

                if (val != null) {
                    return val;
                } else {
                    final Class<Map<K, V>> dynClass = core.config().getDefaultCollectionType(type());
                    if (dynClass != null) {
                        final Codec<Map<K, V>, IN, OUT, CFG> codec = getCodec(core, dynClass);
                        return codec.decode(core, in);
                    } else {
                        return decode(core, in);
                    }
                }
            }
        }

        protected MapProxy<K, V> getMapProxy(CodecCoreEx<IN, OUT, CFG> core) {
            final ArgArrayTypeCtor<Map<K, V>> argArrCtor = core.getArgArrayCtor(mapType);
            if (argArrCtor != null) {
                return new MapProxy2<K, V>(keyCodec.type(), valueCodec.type(), argArrCtor);
            } else {
                final NoArgsTypeCtor<Map<K, V>> noaCtor = core.getNoArgsCtor(mapType);
                return new MapProxy1<K, V>(noaCtor.construct());
            }
        }
    }

    public static abstract class AbstractStringMapCodec<V, IN, OUT, CFG extends CodecConfig>
            implements Codec<Map<String, V>, IN, OUT, CFG> {

        protected final Class<Map<String, V>> mapType;
        protected final Codec<V, IN, OUT, CFG> valueCodec;

        protected AbstractStringMapCodec(
                Class<Map<String, V>> mapType,
                Codec<V, IN, OUT, CFG> valueCodec) {
            this.mapType = mapType;
            this.valueCodec = valueCodec;
        }

        @Override
        public Class<Map<String, V>> type() {
            return mapType;
        }

        protected Codec<Map<String, V>, IN, OUT, CFG> getCodec(
                CodecCoreEx<IN, OUT, CFG> core,
                Class<Map<String, V>> clazz) {
            return core.getMapCodec(clazz, valueCodec);
        }

        @Override
        public OUT encodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, Map<String, V> value, OUT out) {
            final Tuple2<Boolean, OUT> nullRes = core.format().encodeNull(value, out);
            if (nullRes._1) {
                return nullRes._2;
            } else if (core.config().isDefaultCollectionType(type(), value.getClass())) {
                final Class<Map<String, V>> implCollType = core.config().getDefaultCollectionType(type());
                return getCodec(core, implCollType).encode(core, value, out);
            } else {
                final Tuple2<Boolean, OUT> dynRes =
                        core.format().encodeDynamicType(
                                core,
                                this,
                                value,
                                out,
                                clazz -> getCodec(core, clazz)
                        );
                if (dynRes._1) {
                    return dynRes._2;
                } else {
                    return encode(core, value, out);
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
                        (type, in2) -> getCodec(core, core.config().nameToClass(type)).decode(core, in2)
                );

                if (val != null) {
                    return val;
                } else {
                    final Class<Map<String, V>> dynClass = core.config().getDefaultCollectionType(type());
                    if (dynClass != null) {
                        final Codec<Map<String, V>, IN, OUT, CFG> codec = getCodec(core, dynClass);
                        return codec.decode(core, in);
                    } else {
                        return decode(core, in);
                    }
                }
            }
        }

        protected MapProxy<String, V> getMapProxy(CodecCoreEx<IN, OUT, CFG> core) {
            final ArgArrayTypeCtor<Map<String, V>> argArrCtor = core.getArgArrayCtor(mapType);
            if (argArrCtor != null) {
                return new MapProxy2<String, V>(String.class, valueCodec.type(), argArrCtor);
            } else {
                final NoArgsTypeCtor<Map<String, V>> noaCtor = core.getNoArgsCtor(mapType);
                return new MapProxy1<String, V>(noaCtor.construct());
            }
        }
    }

    public interface MapProxy<K, V> {
        void put(K key, V value);
        Map<K, V> construct();
    }

    protected static class MapProxy1<K, V> implements MapProxy<K, V> {
        protected final Map<K, V> map;

        public MapProxy1(Map<K, V> map) {
            this.map = map;
        }

        @Override
        public void put(K key, V value) {
            map.put(key, value);
        }

        @Override
        public Map<K, V> construct() {
            return map;
        }
    }

    protected static class MapProxy2<K, V> implements MapProxy<K, V> {
        final Class<K> keyType;
        final Class<V> valueType;
        final List<Object> args = new ArrayList<>();
        final ArgArrayTypeCtor<Map<K, V>> argArrCtor;

        public MapProxy2(Class<K> keyType, Class<V> valueType, ArgArrayTypeCtor<Map<K, V>> argArrCtor) {
            this.keyType = keyType;
            this.valueType = valueType;
            this.argArrCtor = argArrCtor;
        }

        @Override
        public void put(K key, V value) {
            args.add(keyType.cast(key));
            args.add(valueType.cast(value));
        }

        @Override
        public Map<K, V> construct() {
            final Object[] arr = (Object[])Array.newInstance(Object.class, args.size());
            args.toArray(arr);
            return argArrCtor.construct(arr);
        }
    }

}
