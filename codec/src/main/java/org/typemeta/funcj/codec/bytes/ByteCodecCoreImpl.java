package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.bytes.io.ByteIO.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Folds;

import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

@SuppressWarnings("unchecked")
public class ByteCodecCoreImpl extends BaseCodecCore<Input, Output> implements ByteCodecCore {

    protected final ByteCodecConfig config;

    public ByteCodecCoreImpl(ByteCodecConfig config) {
        this.config = config;
    }

    public ByteCodecCoreImpl() {
        this(new ByteCodecConfigImpl());
    }

    @Override
    public ByteCodecConfig config() {
        return config;
    }


    @Override
    public <T> boolean encodeNull(T val, Output out) {
        final boolean isNull = val == null;
        out.writeBoolean(isNull);
        return isNull;
    }

    @Override
    public boolean decodeNull(Input in) {
        return in.readBoolean();
    }

    @Override
    public <T> boolean encodeDynamicType(
            Codec<T, Input, Output> codec,
            T val,
            Output out,
            Functions.F<Class<T>, Codec<T, Input, Output>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (dynType.equals(codec.type())) {
            out.writeBoolean(false);
            return false;
        } else {
            out.writeBoolean(true);
            final Codec<T, Input, Output> dynCodec = getDynCodec.apply(dynType);
            out.writeString(config().classToName(dynType));
            dynCodec.encode(val, out);
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(Input in, Functions.F<String, T> decoder) {
        if (in.readBoolean()) {
            final String typeName = in.readString();
            return decoder.apply(typeName);
        } else {
            return null;
        }
    }

    @Override
    public <T> T decodeDynamicType(Input in) {
        return decodeDynamicType(in, name -> getCodec(this.config().<T>nameToClass(name)).decode(in));
    }

    protected final Codec.BooleanCodec<Input, Output> booleanCodec =
            new Codec.BooleanCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(boolean val, Output out) {
            return out.writeBoolean(val);
        }

        @Override
        public boolean decodePrim(Input in) {
            return in.readBoolean();
        }
    };

    @Override
    public Codec.BooleanCodec<Input, Output> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], Input, Output> booleanArrayCodec =
            new Codec.Base<boolean[], Input, Output>(this) {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public Output encode(boolean[] value, Output out) {
            out.writeInt(value.length);
            for (boolean val : value) {
                booleanCodec().encode(val, out);
            }
            return out;
        }

        @Override
        public boolean[] decode(Input in) {
            final int l = in.readInt();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = booleanCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], Input, Output> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<Input, Output> byteCodec =
            new Codec.ByteCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(byte val, Output out) {
            return out.writeByte(val);
        }

        @Override
        public byte decodePrim(Input in) {
            return in.readByte();
        }
    };

    @Override
    public Codec.ByteCodec<Input, Output> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], Input, Output> byteArrayCodec =
            new Codec.Base<byte[], Input, Output>(this) {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public Output encode(byte[] value, Output out) {
            out.writeInt(value.length);
            for (byte val : value) {
                byteCodec().encode(val, out);
            }
            return out;
        }

        @Override
        public byte[] decode(Input in) {
            final int l = in.readInt();
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = byteCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], Input, Output> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<Input, Output> charCodec =
            new Codec.CharCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(char val, Output out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(Input in ) {
            return in.readChar();
        }
    };

    @Override
    public Codec.CharCodec<Input, Output> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], Input, Output> charArrayCodec =
            new Codec.Base<char[], Input, Output>(this) {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public Output encode(char[] value, Output out) {
            out.writeInt(value.length);
            for (char val : value) {
                charCodec().encode(val, out);
            }
            return out;
        }

        @Override
        public char[] decode(Input in) {
            final int l = in.readInt();
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = charCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], Input, Output> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<Input, Output> shortCodec =
            new Codec.ShortCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(short val, Output out) {
            return out.writeShort(val);
        }

        @Override
        public short decodePrim(Input in ) {
            return in.readShort();
        }
    };

    @Override
    public Codec.ShortCodec<Input, Output> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], Input, Output> shortArrayCodec =
            new Codec.Base<short[], Input, Output>(this) {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public Output encode(short[] value, Output out) {
            out.writeInt(value.length);
            for (short val : value) {
                shortCodec().encode(val, out);
            }
            return out;
        }

        @Override
        public short[] decode(Input in) {
            final int l = in.readInt();
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = shortCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], Input, Output> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<Input, Output> intCodec =
            new Codec.IntCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(int val, Output out) {
            return out.writeInt(val);
        }

        @Override
        public int decodePrim(Input in ) {
            return in.readInt();
        }
    };

    @Override
    public Codec.IntCodec<Input, Output> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], Input, Output> intArrayCodec =
            new Codec.Base<int[], Input, Output>(this) {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public Output encode(int[] value, Output out) {
            out.writeInt(value.length);
            for (int val : value) {
                intCodec().encode(val, out);
            }
            return out;
        }

        @Override
        public int[] decode(Input in) {
            final int l = in.readInt();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = intCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], Input, Output> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<Input, Output> longCodec =
            new Codec.LongCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(long val, Output out) {
            return out.writeLong(val);
        }

        @Override
        public long decodePrim(Input in) {
            return in.readLong();
        }
    };

    @Override
    public Codec.LongCodec<Input, Output> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], Input, Output> longArrayCodec =
            new Codec.Base<long[], Input, Output>(this) {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public Output encode(long[] value, Output out) {
            out.writeInt(value.length);
            for (long val : value) {
                longCodec().encode(val, out);
            }
            return out;
        }

        @Override
        public long[] decode(Input in) {
            final int l = in.readInt();
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = longCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], Input, Output> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<Input, Output> floatCodec =
            new Codec.FloatCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(float val, Output out) {
            return out.writeFloat(val);
        }

        @Override
        public float decodePrim(Input in ) {
            return in.readFloat();
        }
    };

    @Override
    public Codec.FloatCodec<Input, Output> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], Input, Output> floatArrayCodec =
            new Codec.Base<float[], Input, Output>(this) {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public Output encode(float[] value, Output out) {
            out.writeInt(value.length);
            for (float val : value) {
                floatCodec().encode(val, out);
            }
            return out;
        }

        @Override
        public float[] decode(Input in) {
            final int l = in.readInt();
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = floatCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], Input, Output> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<Input, Output> doubleCodec =
            new Codec.DoubleCodec<Input, Output>() {

        @Override
        public CodecCoreInternal<Input, Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public Output encodePrim(double value, Output out) {
            return out.writeDouble(value);
        }

        @Override
        public double decodePrim(Input in ) {
            return in.readDouble();
        }
    };

    @Override
    public Codec.DoubleCodec<Input, Output> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], Input, Output> doubleArrayCodec =
            new Codec.Base<double[], Input, Output>(this) {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public Output encode(double[] value, Output out) {
            out.writeInt(value.length);
            for (double val : value) {
                doubleCodec().encode(val, out);
            }
            return out;
        }

        @Override
        public double[] decode(Input in) {
            final int l = in.readInt();
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = doubleCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], Input, Output> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, Input, Output> stringCodec =
            new Codec.Base<String, Input, Output>(this) {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public Output encode(String value, Output out) {
            return out.writeString(value);
        }

        @Override
        public String decode(Input in) {
            return in.readString();
        }
    };

    @Override
    public Codec<String, Input, Output> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, Input, Output> enumCodec(Class<EM> enumType) {
        return new Codec.Base<EM, Input, Output>(this) {
            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public Output encode(EM value, Output out) {
                return out.writeString(value.name());
            }

            @Override
            public EM decode(Input in) {
                return EM.valueOf(type(), in.readString());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, Input, Output> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, Input, Output> valueCodec) {
        return new ByteMapCodecs.StringMapCodec<V>(this, type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Input, Output> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, Input, Output> keyCodec,
            Codec<V, Input, Output> valueCodec) {
        return new ByteMapCodecs.MapCodec<K, V>(this, type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, Input, Output> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, Input, Output> elemCodec) {
        return new CollectionCodec<T, Input, Output>(
            ByteCodecCoreImpl.this,
                elemCodec) {
            @Override
            public Class<Collection<T>> type() {
                return collType;
            }

            @Override
            public Output encode(Collection<T> value, Output out) {
                out.writeInt(value.size());
                for (T val : value) {
                    elemCodec.encodeWithCheck(val, out);
                }
                return out;
            }

            @Override
            public Collection<T> decode(Input in) {
                final int l = in.readInt();
                final Collection<T> vals = getNoArgsCtor(collType).construct();

                for (int i = 0; i < l; ++i) {
                    vals.add(elemCodec.decodeWithCheck(in));
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], Input, Output> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, Input, Output> elemCodec) {
        return new Codec<T[], Input, Output>() {
            @Override
            public CodecCoreInternal<Input, Output> core() {
                return ByteCodecCoreImpl.this;
            }

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public Output encode(T[] value, Output out) {
                out.writeInt(value.length);
                for (T val : value) {
                    elemCodec.encodeWithCheck(val, out);
                }
                return out;
            }

            @Override
            public T[] decode(Input in) {
                final int l = in.readInt();
                final T[] vals = (T[]) Array.newInstance(elemType, l);

                for (int i = 0; i < l; ++i) {
                    vals[i] = elemCodec.decodeWithCheck(in);
                }

                return vals;
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, Input, Output> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, Input, Output, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(this, type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(this, type, objMeta);
        }
    }

    private static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends Codec.Base<T, Input, Output> {

        private final Class<T> type;
        private final ObjectMeta<T, Input, Output, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, Input, Output, RA>> fields;

        private ObjectCodec(
                CodecCoreInternal<Input, Output> core,
                Class<T> type,
                ObjectMeta<T, Input, Output, RA> objMeta) {
            super(core);
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
        public Output encode(T value, Output out) {
            objMeta.forEach(field ->
                    field.encodeField(value, out)
            );
            return out;
        }

        @Override
        public T decode(Input in) {
            return Folds.foldLeft(
                    (acc, field) -> field.decodeField(acc, in),
                    objMeta.startDecode(),
                    objMeta
            ).construct();
        }
    }

    private static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, Input, Output> {

        private FinalObjectCodec(
                CodecCoreInternal<Input, Output> core,
                Class<T> type,
                ObjectMeta<T, Input, Output, RA> objMeta) {
            super(core, type, objMeta);
        }
    }
}
