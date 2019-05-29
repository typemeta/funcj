package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.tuples.Tuple2;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.typemeta.funcj.codec.jsonnode.JsonNTypes.Config;
import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

/**
 * Encoding via JSON nodes.
 */
@SuppressWarnings("unchecked")
public class JsonNCodecFormat implements CodecFormat<JsValue, JsValue, Config> {

    protected final Config config;

    public JsonNCodecFormat(Config config) {
        this.config = config;
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> Tuple2<Boolean, JsValue> encodeNull(T val, JsValue out) {
        if (val == null) {
            return Tuple2.of(true, JSAPI.nul());
        } else {
            return Tuple2.of(false, out);
        }
    }

    @Override
    public boolean decodeNull(JsValue in) {
        return in.isNull();
    }

    @Override
    public <T> Tuple2<Boolean, JsValue> encodeDynamicType(
            CodecCoreEx<JsValue, JsValue, Config> core,
            Codec<T, JsValue, JsValue, Config> codec,
            T val,
            JsValue out,
            Functions.F<Class<T>, Codec<T, JsValue, JsValue, Config>> getDynCodec
    ) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (config().dynamicTypeMatch(codec.type(), dynType)) {
            return Tuple2.of(false, out);
        } else if (!config().dynamicTypeTags()) {
            final Codec<T, JsValue, JsValue, Config> dynCodec = getDynCodec.apply(dynType);
            dynCodec.encode(core, val, out);
            return Tuple2.of(true, out);
        } else {
            final Codec<T, JsValue, JsValue, Config> dynCodec = getDynCodec.apply(dynType);

            final JsValue jsv =
                    JSAPI.obj(
                            JSAPI.field(config.typeFieldName(), JSAPI.str(config().classToName(dynType))),
                            JSAPI.field(config.valueFieldName(), dynCodec.encode(core, val, out))
                    );
            return Tuple2.of(true, jsv);
        }
    }

    @Override
    public <T> T decodeDynamicType(JsValue in, Functions.F2<String, JsValue, T> decoder) {
        if (!config().dynamicTypeTags()) {
            return null;
        } else if (in.isObject()) {
            final JsObject jso = in.asObject();

            final String typeFieldName = config.typeFieldName();
            final String valueFieldName = config.valueFieldName();

            if (jso.containsName(typeFieldName) && jso.containsName(valueFieldName)) {
                final String typeName = jso.get(typeFieldName).asString().value();
                final JsValue value = jso.get(valueFieldName);
                return decoder.apply(typeName, value);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<JsValue, JsValue, Config> {

        @Override
        public JsValue encodePrim(boolean val, JsValue out) {
            return JSAPI.bool(val);
        }

        @Override
        public boolean decodePrim(JsValue in) {
            return in.asBool().value();
        }
    }

    protected final Codec.BooleanCodec<JsValue, JsValue, Config> booleanCodec = new BooleanCodec();

    @Override
    public Codec.BooleanCodec<JsValue, JsValue, Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], JsValue, JsValue, Config> booleanArrayCodec =
            new Codec<boolean[], JsValue, JsValue, Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, boolean[] value, JsValue out) {
            final JsValue[] jsvs = new JsValue[value.length];
            for (int i = 0; i < value.length; ++i) {
                jsvs[i] = booleanCodec().encodePrim(value[i], out);
            }
            return JSAPI.arr(jsvs);
        }

        @Override
        public boolean[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsArray jsa = in.asArray();
            final boolean[] arr = new boolean[jsa.size()];
            for (int i = 0; i < jsa.size(); ++i) {
                arr[i] = booleanCodec().decodePrim(jsa.get(i));
            }
            return arr;
        }
    };

    @Override
    public Codec<boolean[], JsValue, JsValue, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<JsValue, JsValue, Config> {

        @Override
        public JsValue encodePrim(byte value, JsValue out) {
            return JSAPI.num(value);
        }

        @Override
        public byte decodePrim(JsValue in) {
            return in.asNumber().byteValue();
        }
    }

    protected final Codec.ByteCodec<JsValue, JsValue, Config> byteCodec = new ByteCodec();

    @Override
    public Codec.ByteCodec<JsValue, JsValue, Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], JsValue, JsValue, Config> byteArrayCodec =
            new Codec<byte[], JsValue, JsValue, Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, byte[] value, JsValue out) {
            final JsValue[] jsvs = new JsValue[value.length];
            for (int i = 0; i < value.length; ++i) {
                jsvs[i] = byteCodec().encodePrim(value[i], out);
            }
            return JSAPI.arr(jsvs);
        }

        @Override
        public byte[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsArray jsa = in.asArray();
            final byte[] arr = new byte[jsa.size()];
            for (int i = 0; i < jsa.size(); ++i) {
                arr[i] = byteCodec().decodePrim(jsa.get(i));
            }
            return arr;
        }
    };

    @Override
    public Codec<byte[], JsValue, JsValue, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<JsValue, JsValue, Config> {

        @Override
        public JsValue encodePrim(char value, JsValue out) {
            return JSAPI.str("" + value);
        }

        @Override
        public char decodePrim(JsValue in ) {
            return in.asString().value().charAt(0);
        }
    }

    protected final Codec.CharCodec<JsValue, JsValue, Config> charCodec = new CharCodec();

    @Override
    public Codec.CharCodec<JsValue, JsValue, Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], JsValue, JsValue, Config> charArrayCodec =
            new Codec<char[], JsValue, JsValue, Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, char[] value, JsValue out) {
            final JsValue[] jsvs = new JsValue[value.length];
            for (int i = 0; i < value.length; ++i) {
                jsvs[i] = charCodec().encodePrim(value[i], out);
            }
            return JSAPI.arr(jsvs);
        }

        @Override
        public char[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsArray jsa = in.asArray();
            final char[] arr = new char[jsa.size()];
            for (int i = 0; i < jsa.size(); ++i) {
                arr[i] = charCodec().decodePrim(jsa.get(i));
            }
            return arr;
        }
    };

    @Override
    public Codec<char[], JsValue, JsValue, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<JsValue, JsValue, Config> {

        @Override
        public JsValue encodePrim(short value, JsValue out) {
            return JSAPI.num(value);
        }

        @Override
        public short decodePrim(JsValue in) {
            return in.asNumber().shortValue();
        }
    }

    protected final Codec.ShortCodec<JsValue, JsValue, Config> shortCodec = new ShortCodec();

    @Override
    public Codec.ShortCodec<JsValue, JsValue, Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], JsValue, JsValue, Config> shortArrayCodec =
            new Codec<short[], JsValue, JsValue, Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, short[] value, JsValue out) {
            final JsValue[] jsvs = new JsValue[value.length];
            for (int i = 0; i < value.length; ++i) {
                jsvs[i] = shortCodec().encodePrim(value[i], out);
            }
            return JSAPI.arr(jsvs);
        }

        @Override
        public short[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsArray jsa = in.asArray();
            final short[] arr = new short[jsa.size()];
            for (int i = 0; i < jsa.size(); ++i) {
                arr[i] = shortCodec().decodePrim(jsa.get(i));
            }
            return arr;
        }
    };

    @Override
    public Codec<short[], JsValue, JsValue, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<JsValue, JsValue, Config> {

        @Override
        public JsValue encodePrim(int value, JsValue out) {
            return JSAPI.num(value);
        }

        @Override
        public int decodePrim(JsValue in ) {
            return in.asNumber().intValue();
        }
    }

    protected final Codec.IntCodec<JsValue, JsValue, Config> intCodec = new IntCodec();

    @Override
    public Codec.IntCodec<JsValue, JsValue, Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], JsValue, JsValue, Config> intArrayCodec =
            new Codec<int[], JsValue, JsValue, Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, int[] value, JsValue out) {
            final JsValue[] jsvs = new JsValue[value.length];
            for (int i = 0; i < value.length; ++i) {
                jsvs[i] = intCodec().encodePrim(value[i], out);
            }
            return JSAPI.arr(jsvs);
        }

        @Override
        public int[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsArray jsa = in.asArray();
            final int[] arr = new int[jsa.size()];
            for (int i = 0; i < jsa.size(); ++i) {
                arr[i] = intCodec().decodePrim(jsa.get(i));
            }
            return arr;
        }
    };

    @Override
    public Codec<int[], JsValue, JsValue, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<JsValue, JsValue, Config> {

        @Override
        public JsValue encodePrim(long value, JsValue out) {
            return JSAPI.num(value);
        }

        @Override
        public long decodePrim(JsValue in) {
            return in.asNumber().longValue();
        }
    }

    protected final Codec.LongCodec<JsValue, JsValue, Config> longCodec = new LongCodec();

    @Override
    public Codec.LongCodec<JsValue, JsValue, Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], JsValue, JsValue, Config> longArrayCodec =
            new Codec<long[], JsValue, JsValue, Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, long[] value, JsValue out) {
            final JsValue[] jsvs = new JsValue[value.length];
            for (int i = 0; i < value.length; ++i) {
                jsvs[i] = longCodec().encodePrim(value[i], out);
            }
            return JSAPI.arr(jsvs);
        }

        @Override
        public long[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsArray jsa = in.asArray();
            final long[] arr = new long[jsa.size()];
            for (int i = 0; i < jsa.size(); ++i) {
                arr[i] = longCodec().decodePrim(jsa.get(i));
            }
            return arr;
        }
    };

    @Override
    public Codec<long[], JsValue, JsValue, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<JsValue, JsValue, Config> {

        @Override
        public JsValue encodePrim(float value, JsValue out) {
            return JSAPI.num(value);
        }

        @Override
        public float decodePrim(JsValue in ) {
            return in.asNumber().floatValue();
        }
    }

    protected final Codec.FloatCodec<JsValue, JsValue, Config> floatCodec = new FloatCodec();

    @Override
    public Codec.FloatCodec<JsValue, JsValue, Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], JsValue, JsValue, Config> floatArrayCodec =
            new Codec<float[], JsValue, JsValue, Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, float[] value, JsValue out) {
            final JsValue[] jsvs = new JsValue[value.length];
            for (int i = 0; i < value.length; ++i) {
                jsvs[i] = floatCodec().encodePrim(value[i], out);
            }
            return JSAPI.arr(jsvs);
        }

        @Override
        public float[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsArray jsa = in.asArray();
            final float[] arr = new float[jsa.size()];
            for (int i = 0; i < jsa.size(); ++i) {
                arr[i] = floatCodec().decodePrim(jsa.get(i));
            }
            return arr;
        }
    };

    @Override
    public Codec<float[], JsValue, JsValue, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<JsValue, JsValue, Config> {

        @Override
        public JsValue encodePrim(double value, JsValue out) {
            return JSAPI.num(value);
        }

        @Override
        public double decodePrim(JsValue in ) {
            return in.asNumber().doublealue();
        }
    }

    protected final Codec.DoubleCodec<JsValue, JsValue, Config> doubleCodec = new DoubleCodec();

    @Override
    public Codec.DoubleCodec<JsValue, JsValue, Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], JsValue, JsValue, Config> doubleArrayCodec =
            new Codec<double[], JsValue, JsValue, Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, double[] value, JsValue out) {
            final JsValue[] jsvs = new JsValue[value.length];
            for (int i = 0; i < value.length; ++i) {
                jsvs[i] = doubleCodec().encodePrim(value[i], out);
            }
            return JSAPI.arr(jsvs);
        }

        @Override
        public double[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsArray jsa = in.asArray();
            final double[] arr = new double[jsa.size()];
            for (int i = 0; i < jsa.size(); ++i) {
                arr[i] = doubleCodec().decodePrim(jsa.get(i));
            }
            return arr;
        }
    };

    @Override
    public Codec<double[], JsValue, JsValue, Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected static class StringCodec implements Codec<String, JsValue, JsValue, Config> {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, String value, JsValue out) {
            return JSAPI.str(value);
        }

        @Override
        public String decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            return in.asString().value();
        }
    }

    protected final Codec<String, JsValue, JsValue, Config> stringCodec = new StringCodec();

    @Override
    public Codec<String, JsValue, JsValue, Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, JsValue, JsValue, Config> enumCodec(Class<EM> enumType) {
        return new Codec.FinalCodec<EM, JsValue, JsValue, Config>() {

            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public JsValue encode(
                    CodecCoreEx<JsValue, JsValue, Config> core,
                    EM value,
                    JsValue out
            ) {
                return core.format().stringCodec().encode(core, value.name(), out);
            }

            @Override
            public EM decode(
                    CodecCoreEx<JsValue, JsValue, Config> core,
                    JsValue in
            ) {
                return EM.valueOf(enumType, core.format().stringCodec().decode(core, in));
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, JsValue, JsValue, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, JsValue, JsValue, Config> valueCodec) {
        return new JsonNMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, JsValue, JsValue, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, JsValue, JsValue, Config> keyCodec,
            Codec<V, JsValue, JsValue, Config> valueCodec) {
        return new JsonNMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, JsValue, JsValue, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, JsValue, JsValue, Config> elemCodec) {
        return new CollectionCodec<T, JsValue, JsValue, Config>(collType, elemCodec) {

            @Override
            public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, Collection<T> value, JsValue out) {
                final JsValue[] jsvs = new JsValue[value.size()];
                int i = 0;
                for (T val : value) {
                    jsvs[i++] = elemCodec.encodeWithCheck(core, val, out);
                }
                return JSAPI.arr(jsvs);
            }

            @Override
            public Collection<T> decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
                final JsArray jsa = in.asArray();

                final CollProxy<T> collProxy = getCollectionProxy(core);

                for (int i = 0; i < jsa.size(); ++i) {
                    collProxy.add(elemCodec.decodeWithCheck(core, jsa.get(i)));
                }

                return collProxy.construct();
            }
        };
    }

    @Override
    public <T> Codec<T[], JsValue, JsValue, Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, JsValue, JsValue, Config> elemCodec) {
        return new Codec<T[], JsValue, JsValue, Config>() {
            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, T[] value, JsValue out) {
                final JsValue[] jsvs = new JsValue[value.length];
                for (int i = 0; i < value.length; ++i) {
                    jsvs[i] = elemCodec.encodeWithCheck(core, value[i], out);
                }
                return JSAPI.arr(jsvs);
            }

            @Override
            public T[] decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
                final JsArray jsa = in.asArray();

                final T[] arr = (T[]) Array.newInstance(elemType, jsa.size());

                for (int i = 0; i < jsa.size(); ++i) {
                    arr[i] = elemCodec.decodeWithCheck(core, jsa.get(i));
                }

                return arr;
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, JsValue, JsValue, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, JsValue, JsValue, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            implements Codec<T, JsValue, JsValue, Config> {

        private final Class<T> type;
        private final ObjectMeta<T, JsValue, JsValue, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, JsValue, JsValue, RA>> fields;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, JsValue, JsValue, RA> objMeta) {
            this.type = type;
            this.objMeta = objMeta;
            this.fields = objMeta.stream()
                    .collect(toLinkedHashMap(
                            ObjectMeta.Field::name,
                            f -> f
                    ));
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, Config> core, T value, JsValue out) {
            final LinkedHashMap<String, JsValue> jsFields = new LinkedHashMap<>();
            fields.forEach((name, field) -> {
                jsFields.put(name, field.encodeField(value, out));
            });
            return JSAPI.obj(jsFields);
        }

        @Override
        public T decode(CodecCoreEx<JsValue, JsValue, Config> core, JsValue in) {
            final JsObject jso = in.asObject();

            final Set<String> expNames = fields.keySet();
            final Set<String> actNames = new HashSet<>();
            final RA ra = objMeta.startDecode();

            jso.forEach(field -> {
                final String name = field.name();
                if (!expNames.contains(name)) {
                    if (config().failOnUnrecognisedFields()) {
                        throw new CodecException(
                                "Field name '" + name + "' unexpected for type " + type
                        );
                    }
                } else if (actNames.contains(name)) {
                    throw new CodecException(
                            "Duplicate field name '" + name + "' for type " + type
                    );
                } else {
                    actNames.add(name);
                    fields.get(name).decodeField(ra, field.value());
                }
            });

            return ra.construct();
        }
    }

    protected class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, JsValue, JsValue, Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, JsValue, JsValue, RA> objMeta) {
            super(type, objMeta);
        }
    }
}
