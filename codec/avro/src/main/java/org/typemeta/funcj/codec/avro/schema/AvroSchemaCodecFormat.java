package org.typemeta.funcj.codec.avro.schema;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.avro.AvroTypes.WithSchema;
import org.typemeta.funcj.codec.impl.CollectionCodec;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.Modifier;

import static java.util.stream.Collectors.toList;
import static org.typemeta.funcj.codec.avro.AvroTypes.Config;
import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;
/**
 * Encoding via JSON nodes.
 */
@SuppressWarnings("unchecked")
public class AvroSchemaCodecFormat implements CodecFormat<WithSchema, Object, Config> {

    protected final Config config;

    public AvroSchemaCodecFormat(Config config) {
        this.config = config;
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> WasEncoded<Object> encodeNull(T val, Object out) {
        if (val == null) {
            return WasEncoded.of(true, Schema.create(Schema.Type.NULL));
        } else {
            return WasEncoded.of(false, null);
        }
    }

    @Override
    public boolean decodeNull(WithSchema in) {
        throw AvroSchemaTypes.notImplemented();
    }

    @Override
    public <T> WasEncoded<Object> encodeDynamicType(
            CodecCoreEx<WithSchema, Object, Config> core,
            Codec<T, WithSchema, Object, Config> codec,
            T val,
            Object out,
            Functions.F<Class<T>, Codec<T, WithSchema, Object, Config>> getDynCodec
    ) {
        return WasEncoded.of(false, null);
    }

    @Override
    public <T> T decodeDynamicType(WithSchema in, Functions.F2<String, WithSchema, T> decoder) {
        throw AvroSchemaTypes.notImplemented();
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<WithSchema, Object, Config> {

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Boolean value,
                Object out
        ) {
            return Schema.createUnion(
                    (Schema)encodePrim(value, out),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Object encodePrim(boolean val, Object out) {
            return Schema.create(Schema.Type.BOOLEAN);
        }

        @Override
        public boolean decodePrim(WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                boolean[] value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.BOOLEAN)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public boolean[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<boolean[], WithSchema, Object, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<WithSchema, Object, Config> {

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Byte value,
                Object out
        ) {
            return Schema.createUnion(
                    (Schema)encodePrim(value, out),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Object encodePrim(byte value, Object out) {
            return Schema.createFixed((String)out, null, null, 1);
        }

        @Override
        public byte decodePrim(WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                byte[] value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.BYTES),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public byte[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<byte[], WithSchema, Object, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<WithSchema, Object, Config> {

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Character value,
                Object out
        ) {
            return Schema.createUnion(
                    (Schema)encodePrim(value, out),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Object encodePrim(char value, Object out) {
            return Schema.create(Schema.Type.STRING);
        }

        @Override
        public char decodePrim(WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                char[] value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.STRING),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public char[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<char[], WithSchema, Object, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<WithSchema, Object, Config> {

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Short value,
                Object out
        ) {
            return Schema.createUnion(
                    (Schema)encodePrim(value, out),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Object encodePrim(short value, Object out) {
            return Schema.create(Schema.Type.INT);
        }

        @Override
        public short decodePrim(WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                short[] value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.INT)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public short[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<short[], WithSchema, Object, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<WithSchema, Object, Config> {

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Integer value,
                Object out
        ) {
            return Schema.createUnion(
                    (Schema)encodePrim(value, out),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Object encodePrim(int value, Object out) {
            return Schema.create(Schema.Type.INT);
        }

        @Override
        public int decodePrim(WithSchema in ) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                int[] value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.INT)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public int[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<int[], WithSchema, Object, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<WithSchema, Object, Config> {

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Long value,
                Object out
        ) {
            return Schema.createUnion(
                    (Schema)encodePrim(value, out),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Object encodePrim(long value, Object out) {
            return Schema.create(Schema.Type.LONG);
        }

        @Override
        public long decodePrim(WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                long[] value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.LONG)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public long[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<long[], WithSchema, Object, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<WithSchema, Object, Config> {

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Float value,
                Object out
        ) {
            return Schema.createUnion(
                    (Schema)encodePrim(value, out),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Object encodePrim(float value, Object out) {
            return Schema.create(Schema.Type.FLOAT);
        }

        @Override
        public float decodePrim(WithSchema in ) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                float[] value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.FLOAT)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public float[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<float[], WithSchema, Object, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<WithSchema, Object, Config> {

        @Override
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                Double value,
                Object out
        ) {
            return Schema.createUnion(
                    (Schema)encodePrim(value, out),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Object encodePrim(double value, Object out) {
            return Schema.create(Schema.Type.DOUBLE);
        }

        @Override
        public double decodePrim(WithSchema in ) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                double[] value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.DOUBLE)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public double[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                String value,
                Object out
        ) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.STRING),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public String decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
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
                final List<String> enumValues =
                        Arrays.stream(enumType.getEnumConstants())
                                .map(Object::toString)
                                .collect(toList());
                return Schema.createUnion(
                        Schema.createEnum(enumType.getCanonicalName(), null, null, enumValues),
                        Schema.create(Schema.Type.NULL)
                );
            }

            @Override
            public EM decode(
                    CodecCoreEx<WithSchema, Object, Config> core,
                    WithSchema in
            ) {
                throw AvroSchemaTypes.notImplemented();
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, WithSchema, Object, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, WithSchema, Object, Config> valueCodec
    ) {
        return new AvroSchemaMapCodecs.StringMapCodec<V>(type, valueCodec);
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
            public Object encode(
                    CodecCoreEx<WithSchema, Object, Config> core,
                    Collection<T> value,
                    Object out
            ) {
                final Schema schema = value.stream()
                        .map(t -> (Schema)elemCodec.encode(core, t, out.toString() + ".coll"))
                        .reduce(SchemaMerge::merge)
                        .orElseGet(() -> Schema.create(Schema.Type.NULL));
                return Schema.createUnion(
                        Schema.createArray(schema),
                        Schema.create(Schema.Type.NULL)
                );
            }

            @Override
            public Collection<T> decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
                throw AvroSchemaTypes.notImplemented();
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
            public Object encode(
                    CodecCoreEx<WithSchema, Object, Config> core,
                    T[] value,
                    Object out
            ) {
                final Schema schema = Arrays.stream(value)
                        .map(t -> (Schema)elemCodec.encode(core, t, out.toString() + ".array"))
                        .reduce(SchemaMerge::merge)
                        .orElseGet(() -> Schema.create(Schema.Type.NULL));
                return Schema.createUnion(
                        Schema.createArray(schema),
                        Schema.create(Schema.Type.NULL)
                );
            }

            @Override
            public T[] decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
                throw AvroSchemaTypes.notImplemented();
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
        public Object encode(
                CodecCoreEx<WithSchema, Object, Config> core,
                T value,
                Object out
        ) {
            final String path = out + "." + type.getSimpleName();
            final List<Schema.Field> fieldSchema =
                    fields.entrySet().stream()
                            .map(en -> new Schema.Field(
                                    en.getKey(),
                                    (Schema)en.getValue().encodeField(value, path)))
                            .collect(toList());
            final Schema schema = Schema.createRecord(path, null, null, false);
            schema.setFields(fieldSchema);
            return schema;
        }

        @Override
        public T decode(CodecCoreEx<WithSchema, Object, Config> core, WithSchema in) {
            throw AvroSchemaTypes.notImplemented();
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
