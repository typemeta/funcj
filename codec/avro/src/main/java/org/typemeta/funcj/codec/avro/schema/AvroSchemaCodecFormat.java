package org.typemeta.funcj.codec.avro.schema;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.AvroTypes.*;
import org.typemeta.funcj.codec.impl.CollectionCodec;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.control.Either;
import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

/**
 * Encoding via JSON nodes.
 */
@SuppressWarnings("unchecked")
public class AvroSchemaCodecFormat implements CodecFormat<Unit, Either<String, Schema>, Config> {

    protected final Config config;

    public AvroSchemaCodecFormat(Config config) {
        this.config = config;
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> WasEncoded<Either<String, Schema>> encodeNull(T val, Either<String, Schema> out) {
        if (val == null) {
            return WasEncoded.of(true, Either.right(Schema.create(Schema.Type.NULL)));
        } else {
            return WasEncoded.of(false, null);
        }
    }

    @Override
    public boolean decodeNull(Unit in) {
        throw AvroSchemaTypes.notImplemented();
    }

    @Override
    public <T> WasEncoded<Either<String, Schema>> encodeDynamicType(
            CodecCoreEx<Unit, Either<String, Schema>, Config> core,
            Codec<T, Unit, Either<String, Schema>, Config> codec,
            T val,
            Either<String, Schema> out,
            Functions.F<Class<T>, Codec<T, Unit, Either<String, Schema>, Config>> getDynCodec
    ) {
        return WasEncoded.of(false, null);
    }

    @Override
    public <T> T decodeDynamicType(Unit in, Functions.F2<String, Unit, T> decoder) {
        throw AvroSchemaTypes.notImplemented();
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<Unit, Either<String, Schema>, Config> {

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Boolean value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    encodePrim(value, out).right(),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Either<String, Schema> encodePrim(boolean val, Either<String, Schema> out) {
            return Either.right(Schema.create(Schema.Type.BOOLEAN));
        }

        @Override
        public boolean decodePrim(Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec.BooleanCodec<Unit, Either<String, Schema>, Config> booleanCodec = new BooleanCodec();

    @Override
    public Codec.BooleanCodec<Unit, Either<String, Schema>, Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], Unit, Either<String, Schema>, Config> booleanArrayCodec =
            new Codec<boolean[], Unit, Either<String, Schema>, Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                boolean[] value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.BOOLEAN)),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public boolean[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<boolean[], Unit, Either<String, Schema>, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<Unit, Either<String, Schema>, Config> {

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Byte value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    encodePrim(value, out).right(),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Either<String, Schema> encodePrim(byte value, Either<String, Schema> out) {
            return Either.right(Schema.createFixed(out.left(), null, null, 1));
        }

        @Override
        public byte decodePrim(Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec.ByteCodec<Unit, Either<String, Schema>, Config> byteCodec = new ByteCodec();

    @Override
    public Codec.ByteCodec<Unit, Either<String, Schema>, Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], Unit, Either<String, Schema>, Config> byteArrayCodec =
            new Codec<byte[], Unit, Either<String, Schema>, Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                byte[] value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.create(Schema.Type.BYTES),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public byte[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<byte[], Unit, Either<String, Schema>, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<Unit, Either<String, Schema>, Config> {

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Character value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    encodePrim(value, out).right(),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Either<String, Schema> encodePrim(char value, Either<String, Schema> out) {
            return Either.right(Schema.create(Schema.Type.STRING));
        }

        @Override
        public char decodePrim(Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec.CharCodec<Unit, Either<String, Schema>, Config> charCodec = new CharCodec();

    @Override
    public Codec.CharCodec<Unit, Either<String, Schema>, Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], Unit, Either<String, Schema>, Config> charArrayCodec =
            new Codec<char[], Unit, Either<String, Schema>, Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                char[] value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.create(Schema.Type.STRING),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public char[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<char[], Unit, Either<String, Schema>, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<Unit, Either<String, Schema>, Config> {

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Short value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    encodePrim(value, out).right(),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Either<String, Schema> encodePrim(short value, Either<String, Schema> out) {
            return Either.right(Schema.create(Schema.Type.INT));
        }

        @Override
        public short decodePrim(Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec.ShortCodec<Unit, Either<String, Schema>, Config> shortCodec = new ShortCodec();

    @Override
    public Codec.ShortCodec<Unit, Either<String, Schema>, Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], Unit, Either<String, Schema>, Config> shortArrayCodec =
            new Codec<short[], Unit, Either<String, Schema>, Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                short[] value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.INT)),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public short[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<short[], Unit, Either<String, Schema>, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<Unit, Either<String, Schema>, Config> {

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Integer value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    encodePrim(value, out).right(),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Either<String, Schema> encodePrim(int value, Either<String, Schema> out) {
            return Either.right(Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.INT)),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public int decodePrim(Unit in ) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec.IntCodec<Unit, Either<String, Schema>, Config> intCodec = new IntCodec();

    @Override
    public Codec.IntCodec<Unit, Either<String, Schema>, Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], Unit, Either<String, Schema>, Config> intArrayCodec =
            new Codec<int[], Unit, Either<String, Schema>, Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                int[] value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.INT)),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public int[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<int[], Unit, Either<String, Schema>, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<Unit, Either<String, Schema>, Config> {

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Long value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    encodePrim(value, out).right(),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Either<String, Schema> encodePrim(long value, Either<String, Schema> out) {
            return Either.right(Schema.create(Schema.Type.LONG));
        }

        @Override
        public long decodePrim(Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec.LongCodec<Unit, Either<String, Schema>, Config> longCodec = new LongCodec();

    @Override
    public Codec.LongCodec<Unit, Either<String, Schema>, Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], Unit, Either<String, Schema>, Config> longArrayCodec =
            new Codec<long[], Unit, Either<String, Schema>, Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                long[] value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.LONG)),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public long[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<long[], Unit, Either<String, Schema>, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<Unit, Either<String, Schema>, Config> {

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Float value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    encodePrim(value, out).right(),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Either<String, Schema> encodePrim(float value, Either<String, Schema> out) {
            return Either.right(Schema.create(Schema.Type.FLOAT));
        }

        @Override
        public float decodePrim(Unit in ) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec.FloatCodec<Unit, Either<String, Schema>, Config> floatCodec = new FloatCodec();

    @Override
    public Codec.FloatCodec<Unit, Either<String, Schema>, Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], Unit, Either<String, Schema>, Config> floatArrayCodec =
            new Codec<float[], Unit, Either<String, Schema>, Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                float[] value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.FLOAT)),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public float[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<float[], Unit, Either<String, Schema>, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<Unit, Either<String, Schema>, Config> {

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                Double value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    encodePrim(value, out).right(),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public Either<String, Schema> encodePrim(double value, Either<String, Schema> out) {
            return Either.right(Schema.create(Schema.Type.DOUBLE));
        }

        @Override
        public double decodePrim(Unit in ) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec.DoubleCodec<Unit, Either<String, Schema>, Config> doubleCodec = new DoubleCodec();

    @Override
    public Codec.DoubleCodec<Unit, Either<String, Schema>, Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], Unit, Either<String, Schema>, Config> doubleArrayCodec =
            new Codec<double[], Unit, Either<String, Schema>, Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                double[] value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.DOUBLE)),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public double[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    };

    @Override
    public Codec<double[], Unit, Either<String, Schema>, Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected static class StringCodec implements Codec<String, Unit, Either<String, Schema>, Config> {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public Either<String, Schema> encode(
                CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                String value,
                Either<String, Schema> out
        ) {
            return Either.right(Schema.createUnion(
                    Schema.create(Schema.Type.STRING),
                    Schema.create(Schema.Type.NULL)
            ));
        }

        @Override
        public String decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected final Codec<String, Unit, Either<String, Schema>, Config> stringCodec = new StringCodec();

    @Override
    public Codec<String, Unit, Either<String, Schema>, Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, Unit, Either<String, Schema>, Config> enumCodec(Class<EM> enumType) {
        return new Codec.FinalCodec<EM, Unit, Either<String, Schema>, Config>() {

            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public Either<String, Schema> encode(
                    CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                    EM value,
                    Either<String, Schema> out
            ) {
                final List<String> enumValues = Arrays.stream(enumType.getEnumConstants()).map(Object::toString).collect(toList());
                return Either.right(Schema.createUnion(
                        Schema.createEnum(out.left(), null, null, enumValues),
                        Schema.create(Schema.Type.NULL)
                ));
            }

            @Override
            public EM decode(
                    CodecCoreEx<Unit, Either<String, Schema>, Config> core,
                    Unit in
            ) {
                throw AvroSchemaTypes.notImplemented();
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, Unit, Either<String, Schema>, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, Unit, Either<String, Schema>, Config> valueCodec
    ) {
        return new AvroSchemaMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Unit, Either<String, Schema>, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, Unit, Either<String, Schema>, Config> keyCodec,
            Codec<V, Unit, Either<String, Schema>, Config> valueCodec
    ) {
        throw new CodecException("AvroCodecformat does not handle Maps with a non-String key type");
    }

    @Override
    public <T> Codec<Collection<T>, Unit, Either<String, Schema>, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, Unit, Either<String, Schema>, Config> elemCodec
    ) {
        return new CollectionCodec<T, Unit, Either<String, Schema>, Config>(collType, elemCodec) {

            @Override
            public Either<String, Schema> encode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Collection<T> value, Either<String, Schema> out) {
                return Schema.createUnion(
                        Schema.createArray(Schema.create(Schema.Type.BOOLEAN)),
                        Schema.create(Schema.Type.NULL)
                );
            }

            @Override
            public Collection<T> decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
                throw AvroSchemaTypes.notImplemented();
            }
        };
    }

    @Override
    public <T> Codec<T[], Unit, Either<String, Schema>, Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, Unit, Either<String, Schema>, Config> elemCodec
    ) {
        return new Codec<T[], Unit, Either<String, Schema>, Config>() {
            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public Either<String, Schema> encode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, T[] value, Either<String, Schema> out) {
                return Schema.createUnion(
                        Schema.createArray(Schema.create(Schema.Type.BOOLEAN)),
                        Schema.create(Schema.Type.NULL)
                );
            }

            @Override
            public T[] decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
                throw AvroSchemaTypes.notImplemented();
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.Builder<T>> Codec<T, Unit, Either<String, Schema>, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, Unit, Either<String, Schema>, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected class ObjectCodec<T, RA extends ObjectMeta.Builder<T>>
            implements Codec<T, Unit, Either<String, Schema>, Config> {

        private final Class<T> type;
        private final ObjectMeta<T, Unit, Either<String, Schema>, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, Unit, Either<String, Schema>, RA>> fields;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, Unit, Either<String, Schema>, RA> objMeta
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
        public Either<String, Schema> encode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, T value, Either<String, Schema> out) {
            final Schema schema = checkSchemaType((Schema)out, Schema.Type.RECORD);

            final GenericData.Record record = new GenericData.Record(schema);
            fields.forEach((name, field) -> {
                record.put(name, field.encodeField(value, schema.getField(name).schema()));
            });
            return record;
        }

        @Override
        public T decode(CodecCoreEx<Unit, Either<String, Schema>, Config> core, Unit in) {
            throw AvroSchemaTypes.notImplemented();
        }
    }

    protected class FinalObjectCodec<T, RA extends ObjectMeta.Builder<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, Unit, Either<String, Schema>, Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, Unit, Either<String, Schema>, RA> objMeta
        ) {
            super(type, objMeta);
        }
    }
}
