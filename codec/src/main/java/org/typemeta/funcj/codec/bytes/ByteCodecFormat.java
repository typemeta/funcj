package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.ObjectMeta;
import org.typemeta.funcj.codec.bytes.io.ByteIO.Input;
import org.typemeta.funcj.codec.bytes.io.ByteIO.Output;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Folds;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ByteCodecFormat implements CodecFormat<Input, Output, Config> {

    protected final Config config;

    public ByteCodecFormat(Config config) {
        this.config = config;
    }

    public ByteCodecFormat() {
        this(new ConfigImpl());
    }

    @Override
    public Config config() {
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
            CodecCoreEx<Input, Output, Config> core,
            Codec<T, Input, Output, Config> codec,
            T val,
            Output out,
            Functions.F<Class<T>, Codec<T, Input, Output, Config>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (dynType.equals(codec.type())) {
            out.writeBoolean(false);
            return false;
        } else {
            out.writeBoolean(true);
            final Codec<T, Input, Output, Config> dynCodec = getDynCodec.apply(dynType);
            out.writeString(config().classToName(dynType));
            dynCodec.encode(core, val, out);
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
    public <T> T decodeDynamicType(CodecCoreEx<Input, Output, Config> core, Input in) {
        return decodeDynamicType(in, name -> core.getCodec(this.config().<T>nameToClass(name)).decode(core, in));
    }

    protected final Codec.BooleanCodec<Input, Output, Config> booleanCodec =
            new Codec.BooleanCodec<Input, Output, Config>() {

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
    public Codec.BooleanCodec<Input, Output, Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], Input, Output, Config> booleanArrayCodec =
            new Codec<boolean[], Input, Output, Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, boolean[] value, Output out) {
            out.writeInt(value.length);
            for (boolean val : value) {
                booleanCodec().encode(core, val, out);
            }
            return out;
        }

        @Override
        public boolean[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = in.readInt();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = booleanCodec().decode(core, in);
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], Input, Output, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<Input, Output, Config> byteCodec =
            new Codec.ByteCodec<Input, Output, Config>() {

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
    public Codec.ByteCodec<Input, Output, Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], Input, Output, Config> byteArrayCodec =
            new Codec<byte[], Input, Output, Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, byte[] value, Output out) {
            out.writeInt(value.length);
            for (byte val : value) {
                byteCodec().encode(core, val, out);
            }
            return out;
        }

        @Override
        public byte[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = in.readInt();
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = byteCodec().decode(core, in);
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], Input, Output, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<Input, Output, Config> charCodec =
            new Codec.CharCodec<Input, Output, Config>() {

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
    public Codec.CharCodec<Input, Output, Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], Input, Output, Config> charArrayCodec =
            new Codec<char[], Input, Output, Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, char[] value, Output out) {
            out.writeInt(value.length);
            for (char val : value) {
                charCodec().encode(core, val, out);
            }
            return out;
        }

        @Override
        public char[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = in.readInt();
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = charCodec().decode(core, in);
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], Input, Output, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<Input, Output, Config> shortCodec =
            new Codec.ShortCodec<Input, Output, Config>() {

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
    public Codec.ShortCodec<Input, Output, Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], Input, Output, Config> shortArrayCodec =
            new Codec<short[], Input, Output, Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, short[] value, Output out) {
            out.writeInt(value.length);
            for (short val : value) {
                shortCodec().encode(core, val, out);
            }
            return out;
        }

        @Override
        public short[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = in.readInt();
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = shortCodec().decode(core, in);
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], Input, Output, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<Input, Output, Config> intCodec =
            new Codec.IntCodec<Input, Output, Config>() {

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
    public Codec.IntCodec<Input, Output, Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], Input, Output, Config> intArrayCodec =
            new Codec<int[], Input, Output, Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, int[] value, Output out) {
            out.writeInt(value.length);
            for (int val : value) {
                intCodec().encode(core, val, out);
            }
            return out;
        }

        @Override
        public int[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = in.readInt();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = intCodec().decode(core, in);
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], Input, Output, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<Input, Output, Config> longCodec =
            new Codec.LongCodec<Input, Output, Config>() {

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
    public Codec.LongCodec<Input, Output, Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], Input, Output, Config> longArrayCodec =
            new Codec<long[], Input, Output, Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, long[] value, Output out) {
            out.writeInt(value.length);
            for (long val : value) {
                longCodec().encode(core, val, out);
            }
            return out;
        }

        @Override
        public long[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = in.readInt();
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = longCodec().decode(core, in);
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], Input, Output, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<Input, Output, Config> floatCodec =
            new Codec.FloatCodec<Input, Output, Config>() {

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
    public Codec.FloatCodec<Input, Output, Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], Input, Output, Config> floatArrayCodec =
            new Codec<float[], Input, Output, Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, float[] value, Output out) {
            out.writeInt(value.length);
            for (float val : value) {
                floatCodec().encode(core, val, out);
            }
            return out;
        }

        @Override
        public float[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = in.readInt();
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = floatCodec().decode(core, in);
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], Input, Output, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<Input, Output, Config> doubleCodec =
            new Codec.DoubleCodec<Input, Output, Config>() {

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
    public Codec.DoubleCodec<Input, Output, Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], Input, Output, Config> doubleArrayCodec =
            new Codec<double[], Input, Output, Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, double[] value, Output out) {
            out.writeInt(value.length);
            for (double val : value) {
                doubleCodec().encode(core, val, out);
            }
            return out;
        }

        @Override
        public double[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            final int l = in.readInt();
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = doubleCodec().decode(core, in);
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], Input, Output, Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, Input, Output, Config> stringCodec =
            new Codec<String, Input, Output, Config>() {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, String value, Output out) {
            return out.writeString(value);
        }

        @Override
        public String decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            return in.readString();
        }
    };

    @Override
    public Codec<String, Input, Output, Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, Input, Output, Config> enumCodec(Class<EM> enumType) {
        return new Codec<EM, Input, Output, Config>() {
            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public Output encode(CodecCoreEx<Input, Output, Config> core, EM value, Output out) {
                return out.writeString(value.name());
            }

            @Override
            public EM decode(CodecCoreEx<Input, Output, Config> core, Input in) {
                return EM.valueOf(type(), in.readString());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, Input, Output, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, Input, Output, Config> valueCodec) {
        return new ByteMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Input, Output, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, Input, Output, Config> keyCodec,
            Codec<V, Input, Output, Config> valueCodec) {
        return new ByteMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, Input, Output, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, Input, Output, Config> elemCodec) {
        return new CollectionCodec<T, Input, Output, Config>(elemCodec) {
            @Override
            public Class<Collection<T>> type() {
                return collType;
            }

            @Override
            public Output encode(CodecCoreEx<Input, Output, Config> core, Collection<T> value, Output out) {
                out.writeInt(value.size());
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out;
            }

            @Override
            public Collection<T> decode(CodecCoreEx<Input, Output, Config> core, Input in) {
                final int l = in.readInt();
                final CollProxy<T> collProxy = getCollectionProxy(core, collType);

                for (int i = 0; i < l; ++i) {
                    collProxy.add(elemCodec.decodeWithCheck(core, in));
                }

                return collProxy.construct();
            }
        };
    }

    @Override
    public <T> Codec<T[], Input, Output, Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, Input, Output, Config> elemCodec) {
        return new Codec<T[], Input, Output, Config>() {

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public Output encode(CodecCoreEx<Input, Output, Config> core, T[] value, Output out) {
                out.writeInt(value.length);
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out;
            }

            @Override
            public T[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
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
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, Input, Output, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, Input, Output, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    private static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            implements Codec<T, Input, Output, Config> {

        private final Class<T> type;
        private final ObjectMeta<T, Input, Output, RA> objMeta;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, Input, Output, RA> objMeta) {
            this.type = type;
            this.objMeta = objMeta;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public Output encode(CodecCoreEx<Input, Output, Config> core, T value, Output out) {
            objMeta.forEach(field ->
                    field.encodeField(value, out)
            );
            return out;
        }

        @Override
        public T decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            return Folds.foldLeft(
                    (acc, field) -> field.decodeField(acc, in),
                    objMeta.startDecode(),
                    objMeta
            ).construct();
        }
    }

    private static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, Input, Output, Config> {

        private FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, Input, Output, RA> objMeta) {
            super(type, objMeta);
        }
    }
}
