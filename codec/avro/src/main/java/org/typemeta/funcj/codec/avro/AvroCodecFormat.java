package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.apache.avro.generic.*;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.impl.CollectionCodec;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

/**
 * Encoding via JSON nodes.
 */
@SuppressWarnings("unchecked")
public class AvroCodecFormat implements CodecFormat<WithSchema, Object, Config> {

    protected final Config config;

    protected static Schema findType(Schema schema, Schema.Type type) {
        if (schema.isUnion()) {
            for (Schema subSchema : schema.getTypes()) {
                final Schema match = findType(subSchema, type);
                if (match != null) {
                    return match;
                }
            }
            return null;
        } else {
            return schema.getType() == type ? schema : null;
        }
    }

    protected static Schema checkSchemaType(Schema schema, Schema.Type type) {
        final Schema match = findType(schema, type);
        if (match != null) {
            return match;
        } else {
            throw new CodecException(
                    "Expecting a schema of type " + type + " but got " + schema.getType() +
                            ", from schema '" + schema.getFullName() + "'"
            );
        }
    }

    protected static Schema checkArraySchemaType(Schema schema, Schema.Type elemType) {
        final Schema match = findType(schema, Schema.Type.ARRAY);
        if (match != null) {
            final Schema elemMatch = findType(match, elemType);
            if (elemMatch != null) {
                return elemMatch;
            }
        }

        throw new CodecException(
                "Expecting an ARRAY schema with element type " + elemType + " but got " + schema.getType() +
                        ", from schema '" + schema.getFullName() + "'"
        );
    }

    public AvroCodecFormat(Config config) {
        this.config = config;
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> WasEncoded<Object> encodeNull(T val, Object out) {
        if (val == null) {
            final Schema match = findType((Schema)out, Schema.Type.NULL);
            if (match == null) {
                throw new CodecException("Can't encode null value as schema doesn't allow NULL - " + out);
            }
            return WasEncoded.of(true, null);
        } else {
            return WasEncoded.of(false, null);
        }
    }

    @Override
    public boolean decodeNull(WithSchema in) {
        return in.schema().getType() == Schema.Type.NULL || in.value() == null;
    }

    @Override
    public <T> WasEncoded<Object> encodeDynamicType(
            CodecCoreEx<WithSchema, Object, Config> core,
            Codec<T, WithSchema, Object, Config> codec,
            T val,
            Object out,
            Functions.F<Class<T>, Codec<T, WithSchema, Object, Config>> getDynCodec
    ) {
        final Class<T> dynType = (Class<T>) val.getClass();
        final Codec<T, WithSchema, Object, Config> dynCodec = getDynCodec.apply(dynType);
        final Object gc = dynCodec.encode(core, val, out);
        return WasEncoded.of(true, gc);
    }

    @Override
    public <T> T decodeDynamicType(WithSchema in, Functions.F2<String, WithSchema, T> decoder) {
        return null;
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<WithSchema, Object, Config> {

        @Override
        public Object encodePrim(boolean val, Object out) {
            checkSchemaType((Schema)out, Schema.Type.BOOLEAN);
            return val;
        }

        @Override
        public boolean decodePrim(WithSchema in) {
            checkSchemaType(in.schema(), Schema.Type.BOOLEAN);
            return in.value();
        }
    }

    protected final Codec.BooleanCodec<WithSchema, Object, Config> booleanCodec = new BooleanCodec();

    @Override
    public Codec.BooleanCodec<WithSchema, Object, Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], WithSchema, Object, Config> booleanArrayCodec =
            new Codec<boolean[], WithSchema, Object, Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, boolean[] value, Object out) {
            checkArraySchemaType((Schema)out, Schema.Type.BOOLEAN);

            final List<Boolean> list = new ArrayList<>(value.length);
            for (boolean val : value) {
                list.add(val);
            }
            return list;
        }

        @Override
        public boolean[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkArraySchemaType(in.schema(), Schema.Type.BOOLEAN);

            final Schema elemSchema = in.schema().getElementType();

            final List<Boolean> values = in.value();
            final boolean[] arr = new boolean[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                arr[i] = booleanCodec().decodePrim(WithSchema.of(values.get(i), elemSchema));
            }
            return arr;
        }
    };

    @Override
    public Codec<boolean[], WithSchema, Object, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<WithSchema, Object, Config> {

        @Override
        public Object encodePrim(byte value, Object out) {
            final Schema schema = checkSchemaType((Schema)out, Schema.Type.FIXED);
            return new GenericData.Fixed(schema, new byte[]{value});
        }

        @Override
        public byte decodePrim(WithSchema in) {
            final Schema schema = checkSchemaType(in.schema(), Schema.Type.FIXED);
            final GenericData.Fixed fixed = in.value();
            return fixed.bytes()[0];
        }
    }

    protected final Codec.ByteCodec<WithSchema, Object, Config> byteCodec = new ByteCodec();

    @Override
    public Codec.ByteCodec<WithSchema, Object, Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], WithSchema, Object, Config> byteArrayCodec =
            new Codec<byte[], WithSchema, Object, Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, byte[] value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.BYTES);
            return ByteBuffer.wrap(value);
        }

        @Override
        public byte[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkSchemaType(in.schema(), Schema.Type.BYTES);
            final ByteBuffer buff = in.value();
            return buff.array();
        }
    };

    @Override
    public Codec<byte[], WithSchema, Object, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<WithSchema, Object, Config> {

        @Override
        public Object encodePrim(char value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.STRING);
            return String.valueOf(value);
        }

        @Override
        public char decodePrim(WithSchema in) {
            checkSchemaType(in.schema(), Schema.Type.STRING);
            final String s = in.value();
            return s.charAt(0);
        }
    }

    protected final Codec.CharCodec<WithSchema, Object, Config> charCodec = new CharCodec();

    @Override
    public Codec.CharCodec<WithSchema, Object, Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], WithSchema, Object, Config> charArrayCodec =
            new Codec<char[], WithSchema, Object, Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, char[] value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.STRING);
            return new String(value);
        }

        @Override
        public char[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkSchemaType(in.schema(), Schema.Type.STRING);
            final String s = in.value();
            return s.toCharArray();
        }
    };

    @Override
    public Codec<char[], WithSchema, Object, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<WithSchema, Object, Config> {

        @Override
        public Object encodePrim(short value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.INT);
            return value;
        }

        @Override
        public short decodePrim(WithSchema in) {
            checkSchemaType(in.schema(), Schema.Type.INT);
            return in.value();
        }
    }

    protected final Codec.ShortCodec<WithSchema, Object, Config> shortCodec = new ShortCodec();

    @Override
    public Codec.ShortCodec<WithSchema, Object, Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], WithSchema, Object, Config> shortArrayCodec =
            new Codec<short[], WithSchema, Object, Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, short[] value, Object out) {
            checkArraySchemaType((Schema)out, Schema.Type.INT);

            final List<Short> list = new ArrayList<>(value.length);
            for (short val : value) {
                list.add(val);
            }
            return list;
        }

        @Override
        public short[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkArraySchemaType(in.schema(), Schema.Type.INT);

            final Schema elemSchema = in.schema().getElementType();

            final List<Short> values = in.value();
            final short[] arr = new short[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                arr[i] = shortCodec().decodePrim(WithSchema.of(values.get(i), elemSchema));
            }
            return arr;
        }
    };

    @Override
    public Codec<short[], WithSchema, Object, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<WithSchema, Object, Config> {

        @Override
        public Object encodePrim(int value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.INT);
            return value;
        }

        @Override
        public int decodePrim(WithSchema in ) {
            checkSchemaType(in.schema(), Schema.Type.INT);
            return in.value();
        }
    }

    protected final Codec.IntCodec<WithSchema, Object, Config> intCodec = new IntCodec();

    @Override
    public Codec.IntCodec<WithSchema, Object, Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], WithSchema, Object, Config> intArrayCodec =
            new Codec<int[], WithSchema, Object, Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, int[] value, Object out) {
            checkArraySchemaType((Schema)out, Schema.Type.INT);

            final List<Integer> list = new ArrayList<>(value.length);
            for (int val : value) {
                list.add(val);
            }
            return list;
        }

        @Override
        public int[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkArraySchemaType(in.schema(), Schema.Type.INT);

            final Schema elemSchema = in.schema().getElementType();

            final List<Integer> values = in.value();
            final int[] arr = new int[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                arr[i] = intCodec().decodePrim(WithSchema.of(values.get(i), elemSchema));
            }
            return arr;
        }
    };

    @Override
    public Codec<int[], WithSchema, Object, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<WithSchema, Object, Config> {

        @Override
        public Object encodePrim(long value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.LONG);
            return value;
        }

        @Override
        public long decodePrim(WithSchema in) {
            checkSchemaType(in.schema(), Schema.Type.LONG);
            return in.value();
        }
    }

    protected final Codec.LongCodec<WithSchema, Object, Config> longCodec = new LongCodec();

    @Override
    public Codec.LongCodec<WithSchema, Object, Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], WithSchema, Object, Config> longArrayCodec =
            new Codec<long[], WithSchema, Object, Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, long[] value, Object out) {
            checkArraySchemaType((Schema)out, Schema.Type.LONG);

            final List<Long> list = new ArrayList<>(value.length);
            for (long val : value) {
                list.add(val);
            }
            return list;
        }

        @Override
        public long[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkArraySchemaType(in.schema(), Schema.Type.LONG);

            final Schema elemSchema = in.schema().getElementType();

            final List<Long> values = in.value();
            final long[] arr = new long[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                arr[i] = longCodec().decodePrim(WithSchema.of(values.get(i), elemSchema));
            }
            return arr;
        }
    };

    @Override
    public Codec<long[], WithSchema, Object, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<WithSchema, Object, Config> {

        @Override
        public Object encodePrim(float value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.FLOAT);
            return value;
        }

        @Override
        public float decodePrim(WithSchema in ) {
            checkSchemaType(in.schema(), Schema.Type.FLOAT);
            return in.value();
        }
    }

    protected final Codec.FloatCodec<WithSchema, Object, Config> floatCodec = new FloatCodec();

    @Override
    public Codec.FloatCodec<WithSchema, Object, Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], WithSchema, Object, Config> floatArrayCodec =
            new Codec<float[], WithSchema, Object, Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, float[] value, Object out) {
            checkArraySchemaType((Schema)out, Schema.Type.FLOAT);

            final List<Float> list = new ArrayList<>(value.length);
            for (float val : value) {
                list.add(val);
            }
            return list;
        }

        @Override
        public float[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkArraySchemaType(in.schema(), Schema.Type.FLOAT);

            final Schema elemSchema = in.schema().getElementType();

            final List<Float> values = in.value();
            final float[] arr = new float[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                arr[i] = floatCodec().decodePrim(WithSchema.of(values.get(i), elemSchema));
            }
            return arr;
        }
    };

    @Override
    public Codec<float[], WithSchema, Object, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<WithSchema, Object, Config> {

        @Override
        public Object encodePrim(double value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.DOUBLE);
            return value;
        }

        @Override
        public double decodePrim(WithSchema in ) {
            checkSchemaType(in.schema(), Schema.Type.DOUBLE);
            return in.value();
        }
    }

    protected final Codec.DoubleCodec<WithSchema, Object, Config> doubleCodec = new DoubleCodec();

    @Override
    public Codec.DoubleCodec<WithSchema, Object, Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], WithSchema, Object, Config> doubleArrayCodec =
            new Codec<double[], WithSchema, Object, Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, double[] value, Object out) {
            checkArraySchemaType((Schema)out, Schema.Type.DOUBLE);

            final List<Double> list = new ArrayList<>(value.length);
            for (double val : value) {
                list.add(val);
            }
            return list;
        }

        @Override
        public double[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkArraySchemaType(in.schema(), Schema.Type.DOUBLE);

            final Schema elemSchema = in.schema().getElementType();

            final List<Double> values = in.value();
            final double[] arr = new double[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                arr[i] = doubleCodec().decodePrim(WithSchema.of(values.get(i), elemSchema));
            }
            return arr;
        }
    };

    @Override
    public Codec<double[], WithSchema, Object, Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected static class StringCodec implements Codec<String, WithSchema, Object, Config> {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, String value, Object out) {
            checkSchemaType((Schema)out, Schema.Type.STRING);
            return value;
        }

        @Override
        public String decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            checkSchemaType(in.schema(), Schema.Type.STRING);
            return in.value();
        }
    }

    protected final Codec<String, WithSchema, Object, Config> stringCodec = new StringCodec();

    @Override
    public Codec<String, WithSchema, Object, Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, WithSchema, Object, Config> enumCodec(Class<EM> enumType) {
        return new Codec.FinalCodec<EM, WithSchema, Object, Config>() {

            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public Object encode(
                    CodecCoreEx<WithSchema, Object, Config> core,
                    EM value,
                    Object out
            ) {
                final Schema schema = checkSchemaType((Schema)out, Schema.Type.ENUM);
                return new GenericData.EnumSymbol(schema, value);
            }

            @Override
            public EM decode(
                    CodecCoreEx<WithSchema, Object, Config> core,
                    WithSchema in
            ) {
                checkSchemaType(in.schema(), Schema.Type.ENUM);
                final GenericData.EnumSymbol enumSym = in.value();
                return EM.valueOf(enumType, enumSym.toString());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, WithSchema, Object, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, WithSchema, Object, Config> valueCodec
    ) {
        return new AvroMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, WithSchema, Object, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, WithSchema, Object, Config> keyCodec,
            Codec<V, WithSchema, Object, Config> valueCodec
    ) {
        throw new CodecException("AvroCodecformat does not handle Maps with a non-String key type");
    }

    @Override
    public <T> Codec<Collection<T>, WithSchema, Object, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, WithSchema, Object, Config> elemCodec
    ) {
        return new CollectionCodec<T, WithSchema, Object, Config>(collType, elemCodec) {

            @Override
            public Object encode(CodecCoreEx<WithSchema, Object, Config> core, Collection<T> value, Object out) {
                final Schema elemSchema = checkArraySchemaType((Schema)out, Schema.Type.ARRAY)
                        .getElementType();

                final List<Object> list = new ArrayList<>(value.size());
                for (T val : value) {
                    list.add(elemCodec.encodeWithCheck(core, val, elemSchema));
                }
                return list;
            }

            @Override
            public Collection<T> decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
                final Schema elemSchema = checkArraySchemaType(in.schema(), Schema.Type.ARRAY)
                        .getElementType();

                final List<Object> values = in.value();

                final List<T> list = new ArrayList<T>(values.size());

                for (Object value : values) {
                    list.add(elemCodec.decodeWithCheck(core, WithSchema.of(value, elemSchema)));
                }

                return list;
            }
        };
    }

    @Override
    public <T> Codec<T[], WithSchema, Object, Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, WithSchema, Object, Config> elemCodec
    ) {
        return new Codec<T[], WithSchema, Object, Config>() {
            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public Object encode(CodecCoreEx<WithSchema, Object, Config> core, T[] value, Object out) {
                final Schema elemSchema = checkArraySchemaType((Schema)out, Schema.Type.ARRAY)
                        .getElementType();

                final List<Object> list = new ArrayList<>(value.length);
                for (T val : value) {
                    list.add(elemCodec.encodeWithCheck(core, val, elemSchema));
                }
                return list;
            }

            @Override
            public T[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
                final Schema elemSchema = checkArraySchemaType(in.schema(), Schema.Type.ARRAY)
                        .getElementType();

                final List<Object> values = in.value();

                final T[] arr = (T[]) Array.newInstance(elemType, values.size());

                for (int i = 0; i < values.size(); ++i) {
                    arr[i] = elemCodec.decodeWithCheck(core, WithSchema.of(values.get(i), elemSchema));
                }

                return arr;
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.Builder<T>> Codec<T, WithSchema, Object, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, WithSchema, Object, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected class ObjectCodec<T, RA extends ObjectMeta.Builder<T>>
            implements Codec<T, WithSchema, Object, Config> {

        private final Class<T> type;
        private final ObjectMeta<T, WithSchema, Object, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, WithSchema, Object, RA>> fields;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, WithSchema, Object, RA> objMeta
        ) {
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
        public Object encode(CodecCoreEx<WithSchema, Object, Config> core, T value, Object out) {
            final Schema schema = checkSchemaType((Schema)out, Schema.Type.RECORD);

            final GenericData.Record record = new GenericData.Record(schema);
            fields.forEach((name, field) -> {
                record.put(name, field.encodeField(value, schema.getField(name).schema()));
            });
            return record;
        }

        @Override
        public T decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            final Schema schema = checkSchemaType(in.schema(), Schema.Type.RECORD);

            final GenericData.Record record = in.value();

            final Set<String> expNames = fields.keySet();
            final Set<String> actNames = new HashSet<>();
            final RA ra = objMeta.startDecode();

            schema.getFields().forEach(field -> {
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
                    final Object value = record.get(name);
                    final Schema fieldSchema = field.schema();
                    fields.get(name).decodeField(ra, WithSchema.of(value, fieldSchema));
                }
            });

            return ra.construct();
        }
    }

    protected class FinalObjectCodec<T, RA extends ObjectMeta.Builder<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, WithSchema, Object, Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, WithSchema, Object, RA> objMeta
        ) {
            super(type, objMeta);
        }
    }
}
