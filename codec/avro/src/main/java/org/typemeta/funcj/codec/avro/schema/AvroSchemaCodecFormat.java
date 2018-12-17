package org.typemeta.funcj.codec.avro.schema;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.schema.AvroSchemaTypes.*;
import org.typemeta.funcj.data.Unit;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.tuples.Tuple2;
import org.typemeta.funcj.util.Folds;

import java.lang.reflect.*;
import java.util.*;

/**
 * Encoding via byte streams.
 */
@SuppressWarnings("unchecked")
public class AvroSchemaCodecFormat implements CodecFormat<Unit, Schema, Config> {

    protected final Config config;

    public AvroSchemaCodecFormat(Config config) {
        this.config = config;
    }

    public AvroSchemaCodecFormat() {
        this(new AvroSchemaConfigImpl());
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> Tuple2<Boolean, Schema> encodeNull(T val, Schema out) {
        final boolean isNull = val == null;
        out.writeBoolean(isNull);
        return isNull;
    }

    @Override
    public boolean decodeNull(Unit in) {
        return in.readBoolean();
    }

    @Override
    public <T> boolean encodeDynamicType(
            CodecCoreEx<Unit, Schema, Config> core,
            Codec<T, Unit, Schema, Config> codec,
            T val,
            Schema out,
            Functions.F<Class<T>, Codec<T, Unit, Schema, Config>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (config().dynamicTypeMatch(codec.type(), dynType)) {
            out.writeBoolean(false);
            return false;
        } else {
            out.writeBoolean(true);
            final Codec<T, Unit, Schema, Config> dynCodec = getDynCodec.apply(dynType);
            out.writeString(config().classToName(dynType));
            dynCodec.encode(core, val, out);
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(Unit in, Functions.F<String, T> decoder) {
        if (in.readBoolean()) {
            final String typeName = in.readString();
            return decoder.apply(typeName);
        } else {
            return null;
        }
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<Unit, Schema, Config> {

        @Override
        public Schema encodePrim(boolean val, Schema out) {
            return out.writeBoolean(val);
        }

        @Override
        public boolean decodePrim(Unit in) {
            return in.readBoolean();
        }
    }

    protected final Codec.BooleanCodec<Unit, Schema, Config> booleanCodec = new BooleanCodec();

    @Override
    public Codec.BooleanCodec<Unit, Schema, Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], Unit, Schema, Config> booleanArrayCodec =
            new Codec<boolean[], Unit, Schema, Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, boolean[] value, Schema out) {
            out.writeInt(value.length);
            for (boolean val : value) {
                booleanCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public boolean[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            final int l = in.readInt();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = booleanCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], Unit, Schema, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<Unit, Schema, Config> {

        @Override
        public Schema encodePrim(byte val, Schema out) {
            return out.writeByte(val);
        }

        @Override
        public byte decodePrim(Unit in) {
            return in.readByte();
        }
    }

    protected final Codec.ByteCodec<Unit, Schema, Config> byteCodec = new ByteCodec();

    @Override
    public Codec.ByteCodec<Unit, Schema, Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], Unit, Schema, Config> byteArrayCodec =
            new Codec<byte[], Unit, Schema, Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, byte[] value, Schema out) {
            out.writeInt(value.length);
            for (byte val : value) {
                byteCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public byte[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            final int l = in.readInt();
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = byteCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], Unit, Schema, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<Unit, Schema, Config> {

        @Override
        public Schema encodePrim(char val, Schema out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(Unit in ) {
            return in.readChar();
        }
    }

    protected final Codec.CharCodec<Unit, Schema, Config> charCodec = new CharCodec();

    @Override
    public Codec.CharCodec<Unit, Schema, Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], Unit, Schema, Config> charArrayCodec =
            new Codec<char[], Unit, Schema, Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, char[] value, Schema out) {
            out.writeInt(value.length);
            for (char val : value) {
                charCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public char[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            final int l = in.readInt();
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = charCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], Unit, Schema, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<Unit, Schema, Config> {

        @Override
        public Schema encodePrim(short val, Schema out) {
            return out.writeShort(val);
        }

        @Override
        public short decodePrim(Unit in ) {
            return in.readShort();
        }
    }

    protected final Codec.ShortCodec<Unit, Schema, Config> shortCodec = new ShortCodec();

    @Override
    public Codec.ShortCodec<Unit, Schema, Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], Unit, Schema, Config> shortArrayCodec =
            new Codec<short[], Unit, Schema, Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, short[] value, Schema out) {
            out.writeInt(value.length);
            for (short val : value) {
                shortCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public short[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            final int l = in.readInt();
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = shortCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], Unit, Schema, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<Unit, Schema, Config> {

        @Override
        public Schema encodePrim(int val, Schema out) {
            return out.writeInt(val);
        }

        @Override
        public int decodePrim(Unit in ) {
            return in.readInt();
        }
    }

    protected final Codec.IntCodec<Unit, Schema, Config> intCodec = new IntCodec();

    @Override
    public Codec.IntCodec<Unit, Schema, Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], Unit, Schema, Config> intArrayCodec =
            new Codec<int[], Unit, Schema, Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, int[] value, Schema out) {
            out.writeInt(value.length);
            for (int val : value) {
                intCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public int[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            final int l = in.readInt();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = intCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], Unit, Schema, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<Unit, Schema, Config> {

        @Override
        public Schema encodePrim(long val, Schema out) {
            return out.writeLong(val);
        }

        @Override
        public long decodePrim(Unit in) {
            return in.readLong();
        }
    }

    protected final Codec.LongCodec<Unit, Schema, Config> longCodec = new LongCodec();

    @Override
    public Codec.LongCodec<Unit, Schema, Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], Unit, Schema, Config> longArrayCodec =
            new Codec<long[], Unit, Schema, Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, long[] value, Schema out) {
            out.writeInt(value.length);
            for (long val : value) {
                longCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public long[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            final int l = in.readInt();
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = longCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], Unit, Schema, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<Unit, Schema, Config> {

        @Override
        public Schema encodePrim(float val, Schema out) {
            return out.writeFloat(val);
        }

        @Override
        public float decodePrim(Unit in ) {
            return in.readFloat();
        }
    }

    protected final Codec.FloatCodec<Unit, Schema, Config> floatCodec = new FloatCodec();

    @Override
    public Codec.FloatCodec<Unit, Schema, Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], Unit, Schema, Config> floatArrayCodec =
            new Codec<float[], Unit, Schema, Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, float[] value, Schema out) {
            out.writeInt(value.length);
            for (float val : value) {
                floatCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public float[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            final int l = in.readInt();
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = floatCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], Unit, Schema, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<Unit, Schema, Config> {

        @Override
        public Schema encodePrim(double value, Schema out) {
            return out.writeDouble(value);
        }

        @Override
        public double decodePrim(Unit in ) {
            return in.readDouble();
        }
    }

    protected final Codec.DoubleCodec<Unit, Schema, Config> doubleCodec = new DoubleCodec();

    @Override
    public Codec.DoubleCodec<Unit, Schema, Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], Unit, Schema, Config> doubleArrayCodec =
            new Codec<double[], Unit, Schema, Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, double[] value, Schema out) {
            out.writeInt(value.length);
            for (double val : value) {
                doubleCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public double[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            final int l = in.readInt();
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = doubleCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], Unit, Schema, Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected static class StringCodec implements Codec<String, Unit, Schema, Config> {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, String value, Schema out) {
            return out.writeString(value);
        }

        @Override
        public String decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            return in.readString();
        }
    }

    protected final Codec<String, Unit, Schema, Config> stringCodec = new StringCodec();

    @Override
    public Codec<String, Unit, Schema, Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <V> Codec<Map<String, V>, Unit, Schema, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, Unit, Schema, Config> valueCodec) {
        return new AvroSchemaMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Unit, Schema, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, Unit, Schema, Config> keyCodec,
            Codec<V, Unit, Schema, Config> valueCodec) {
        return new AvroSchemaMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, Unit, Schema, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, Unit, Schema, Config> elemCodec) {
        return new CollectionCodec<T, Unit, Schema, Config>(collType, elemCodec) {

            @Override
            public Schema encodeWithCheck(
                    CodecCoreEx<Unit, Schema, Config> core,
                    Collection<T> value,
                    Schema out) {
                if (core.format().encodeNull(value, out)) {
                    return out;
                } else if (!core.format().encodeDynamicType(
                        core,
                        this,
                        value,
                        out,
                        type -> getCodec(core, type))) {
                    return encode(core, value, out);
                } else {
                    return out;
                }
            }

            @Override
            public Schema encode(CodecCoreEx<Unit, Schema, Config> core, Collection<T> value, Schema out) {
                out.writeInt(value.size());
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out;
            }

            @Override
            public Collection<T> decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
                final int l = in.readInt();
                final CollProxy<T> collProxy = getCollectionProxy(core);

                for (int i = 0; i < l; ++i) {
                    collProxy.add(elemCodec.decodeWithCheck(core, in));
                }

                return collProxy.construct();
            }
        };
    }

    @Override
    public <T> Codec<T[], Unit, Schema, Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, Unit, Schema, Config> elemCodec) {
        return new Codec<T[], Unit, Schema, Config>() {

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public Schema encode(CodecCoreEx<Unit, Schema, Config> core, T[] value, Schema out) {
                out.writeInt(value.length);
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out;
            }

            @Override
            public T[] decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
                final int l = in.readInt();
                final T[] vals = (T[]) Array.newInstance(elemType, l);

                for (int i = 0; i < l; ++i) {
                    vals[i] = elemCodec.decodeWithCheck(core, in);
                }

                return vals;
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, Unit, Schema, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, Unit, Schema, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            implements Codec<T, Unit, Schema, Config> {

        private final Class<T> type;
        private final ObjectMeta<T, Unit, Schema, RA> objMeta;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, Unit, Schema, RA> objMeta) {
            this.type = type;
            this.objMeta = objMeta;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public Schema encode(CodecCoreEx<Unit, Schema, Config> core, T value, Schema out) {
            objMeta.forEach(field ->
                    field.encodeField(value, out)
            );
            return out;
        }

        @Override
        public T decode(CodecCoreEx<Unit, Schema, Config> core, Unit in) {
            return Folds.foldLeft(
                    (acc, field) -> field.decodeField(acc, in),
                    objMeta.startDecode(),
                    objMeta
            ).construct();
        }
    }

    protected static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, Unit, Schema, Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, Unit, Schema, RA> objMeta) {
            super(type, objMeta);
        }
    }
}
