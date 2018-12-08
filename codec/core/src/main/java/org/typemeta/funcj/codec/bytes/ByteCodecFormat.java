package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.bytes.ByteTypes.*;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Folds;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * Encoding via byte streams.
 */
@SuppressWarnings("unchecked")
public class ByteCodecFormat implements CodecFormat<InStream, OutStream, Config> {

    protected final Config config;

    public ByteCodecFormat(Config config) {
        this.config = config;
    }

    public ByteCodecFormat() {
        this(new ByteConfigImpl());
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> boolean encodeNull(T val, OutStream out) {
        final boolean isNull = val == null;
        out.writeBoolean(isNull);
        return isNull;
    }

    @Override
    public boolean decodeNull(InStream in) {
        return in.readBoolean();
    }

    @Override
    public <T> boolean encodeDynamicType(
            CodecCoreEx<InStream, OutStream, Config> core,
            Codec<T, InStream, OutStream, Config> codec,
            T val,
            OutStream out,
            Functions.F<Class<T>, Codec<T, InStream, OutStream, Config>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (config().dynamicTypeMatch(codec.type(), dynType)) {
            out.writeBoolean(false);
            return false;
        } else {
            out.writeBoolean(true);
            final Codec<T, InStream, OutStream, Config> dynCodec = getDynCodec.apply(dynType);
            out.writeString(config().classToName(dynType));
            dynCodec.encode(core, val, out);
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(InStream in, Functions.F<String, T> decoder) {
        if (in.readBoolean()) {
            final String typeName = in.readString();
            return decoder.apply(typeName);
        } else {
            return null;
        }
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(boolean val, OutStream out) {
            return out.writeBoolean(val);
        }

        @Override
        public boolean decodePrim(InStream in) {
            return in.readBoolean();
        }
    }

    protected final Codec.BooleanCodec<InStream, OutStream, Config> booleanCodec = new BooleanCodec();

    @Override
    public Codec.BooleanCodec<InStream, OutStream, Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], InStream, OutStream, Config> booleanArrayCodec =
            new Codec<boolean[], InStream, OutStream, Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, boolean[] value, OutStream out) {
            out.writeInt(value.length);
            for (boolean val : value) {
                booleanCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public boolean[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.readInt();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = booleanCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], InStream, OutStream, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(byte val, OutStream out) {
            return out.writeByte(val);
        }

        @Override
        public byte decodePrim(InStream in) {
            return in.readByte();
        }
    }

    protected final Codec.ByteCodec<InStream, OutStream, Config> byteCodec = new ByteCodec();

    @Override
    public Codec.ByteCodec<InStream, OutStream, Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], InStream, OutStream, Config> byteArrayCodec =
            new Codec<byte[], InStream, OutStream, Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, byte[] value, OutStream out) {
            out.writeInt(value.length);
            for (byte val : value) {
                byteCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public byte[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.readInt();
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = byteCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], InStream, OutStream, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(char val, OutStream out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(InStream in ) {
            return in.readChar();
        }
    }

    protected final Codec.CharCodec<InStream, OutStream, Config> charCodec = new CharCodec();

    @Override
    public Codec.CharCodec<InStream, OutStream, Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], InStream, OutStream, Config> charArrayCodec =
            new Codec<char[], InStream, OutStream, Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, char[] value, OutStream out) {
            out.writeInt(value.length);
            for (char val : value) {
                charCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public char[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.readInt();
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = charCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], InStream, OutStream, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(short val, OutStream out) {
            return out.writeShort(val);
        }

        @Override
        public short decodePrim(InStream in ) {
            return in.readShort();
        }
    }

    protected final Codec.ShortCodec<InStream, OutStream, Config> shortCodec = new ShortCodec();

    @Override
    public Codec.ShortCodec<InStream, OutStream, Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], InStream, OutStream, Config> shortArrayCodec =
            new Codec<short[], InStream, OutStream, Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, short[] value, OutStream out) {
            out.writeInt(value.length);
            for (short val : value) {
                shortCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public short[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.readInt();
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = shortCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], InStream, OutStream, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(int val, OutStream out) {
            return out.writeInt(val);
        }

        @Override
        public int decodePrim(InStream in ) {
            return in.readInt();
        }
    }

    protected final Codec.IntCodec<InStream, OutStream, Config> intCodec = new IntCodec();

    @Override
    public Codec.IntCodec<InStream, OutStream, Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], InStream, OutStream, Config> intArrayCodec =
            new Codec<int[], InStream, OutStream, Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, int[] value, OutStream out) {
            out.writeInt(value.length);
            for (int val : value) {
                intCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public int[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.readInt();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = intCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], InStream, OutStream, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(long val, OutStream out) {
            return out.writeLong(val);
        }

        @Override
        public long decodePrim(InStream in) {
            return in.readLong();
        }
    }

    protected final Codec.LongCodec<InStream, OutStream, Config> longCodec = new LongCodec();

    @Override
    public Codec.LongCodec<InStream, OutStream, Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], InStream, OutStream, Config> longArrayCodec =
            new Codec<long[], InStream, OutStream, Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, long[] value, OutStream out) {
            out.writeInt(value.length);
            for (long val : value) {
                longCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public long[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.readInt();
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = longCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], InStream, OutStream, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(float val, OutStream out) {
            return out.writeFloat(val);
        }

        @Override
        public float decodePrim(InStream in ) {
            return in.readFloat();
        }
    }

    protected final Codec.FloatCodec<InStream, OutStream, Config> floatCodec = new FloatCodec();

    @Override
    public Codec.FloatCodec<InStream, OutStream, Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], InStream, OutStream, Config> floatArrayCodec =
            new Codec<float[], InStream, OutStream, Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, float[] value, OutStream out) {
            out.writeInt(value.length);
            for (float val : value) {
                floatCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public float[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.readInt();
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = floatCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], InStream, OutStream, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<InStream, OutStream, Config> {

        @Override
        public OutStream encodePrim(double value, OutStream out) {
            return out.writeDouble(value);
        }

        @Override
        public double decodePrim(InStream in ) {
            return in.readDouble();
        }
    }

    protected final Codec.DoubleCodec<InStream, OutStream, Config> doubleCodec = new DoubleCodec();

    @Override
    public Codec.DoubleCodec<InStream, OutStream, Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], InStream, OutStream, Config> doubleArrayCodec =
            new Codec<double[], InStream, OutStream, Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, double[] value, OutStream out) {
            out.writeInt(value.length);
            for (double val : value) {
                doubleCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public double[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            final int l = in.readInt();
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = doubleCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], InStream, OutStream, Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected static class StringCodec implements Codec<String, InStream, OutStream, Config> {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, String value, OutStream out) {
            return out.writeString(value);
        }

        @Override
        public String decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            return in.readString();
        }
    }

    protected final Codec<String, InStream, OutStream, Config> stringCodec = new StringCodec();

    @Override
    public Codec<String, InStream, OutStream, Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <V> Codec<Map<String, V>, InStream, OutStream, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, InStream, OutStream, Config> valueCodec) {
        return new ByteMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, InStream, OutStream, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, InStream, OutStream, Config> keyCodec,
            Codec<V, InStream, OutStream, Config> valueCodec) {
        return new ByteMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, InStream, OutStream, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, InStream, OutStream, Config> elemCodec) {
        return new CollectionCodec<T, InStream, OutStream, Config>(collType, elemCodec) {

            @Override
            public OutStream encodeWithCheck(
                    CodecCoreEx<InStream, OutStream, Config> core,
                    Collection<T> value,
                    OutStream out) {
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
            public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, Collection<T> value, OutStream out) {
                out.writeInt(value.size());
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out;
            }

            @Override
            public Collection<T> decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
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
    public <T> Codec<T[], InStream, OutStream, Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, InStream, OutStream, Config> elemCodec) {
        return new Codec<T[], InStream, OutStream, Config>() {

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, T[] value, OutStream out) {
                out.writeInt(value.length);
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out;
            }

            @Override
            public T[] decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
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
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, InStream, OutStream, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, InStream, OutStream, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            implements Codec<T, InStream, OutStream, Config> {

        private final Class<T> type;
        private final ObjectMeta<T, InStream, OutStream, RA> objMeta;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, InStream, OutStream, RA> objMeta) {
            this.type = type;
            this.objMeta = objMeta;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, T value, OutStream out) {
            objMeta.forEach(field ->
                    field.encodeField(value, out)
            );
            return out;
        }

        @Override
        public T decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
            return Folds.foldLeft(
                    (acc, field) -> field.decodeField(acc, in),
                    objMeta.startDecode(),
                    objMeta
            ).construct();
        }
    }

    protected static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, InStream, OutStream, Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, InStream, OutStream, RA> objMeta) {
            super(type, objMeta);
        }
    }
}
