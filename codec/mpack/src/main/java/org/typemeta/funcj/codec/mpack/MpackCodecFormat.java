package org.typemeta.funcj.codec.mpack;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.stream.StreamCodecFormat;
import org.typemeta.funcj.functions.Functions;
import org.typemeta.funcj.util.Folds;

import java.lang.reflect.*;
import java.util.*;

/**
 * Encoding via byte streams.
 */
@SuppressWarnings("unchecked")
public class MpackCodecFormat
        implements StreamCodecFormat<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

    protected final MpackTypes.Config config;

    public MpackCodecFormat(MpackTypes.Config config) {
        this.config = config;
    }

    public MpackCodecFormat() {
        this(new MpackConfigImpl());
    }

    @Override
    public MpackTypes.Config config() {
        return config;
    }

    @Override
    public <T> boolean encodeNull(T val, MpackTypes.OutStream out) {
        final boolean isNull = val == null;
        out.writeBoolean(isNull);
        return isNull;
    }

    @Override
    public boolean decodeNull(MpackTypes.InStream in) {
        return in.readBoolean();
    }

    @Override
    public <T> boolean encodeDynamicType(
            CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core,
            Codec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> codec,
            T val,
            MpackTypes.OutStream out,
            Functions.F<Class<T>, Codec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (config().dynamicTypeMatch(codec.type(), dynType)) {
            out.writeBoolean(false);
            return false;
        } else {
            out.writeBoolean(true);
            final Codec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> dynCodec = getDynCodec.apply(dynType);
            out.writeString(config().classToName(dynType));
            dynCodec.encode(core, val, out);
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(MpackTypes.InStream in, Functions.F<String, T> decoder) {
        if (in.readBoolean()) {
            final String typeName = in.readString();
            return decoder.apply(typeName);
        } else {
            return null;
        }
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public MpackTypes.OutStream encodePrim(boolean val, MpackTypes.OutStream out) {
            return out.writeBoolean(val);
        }

        @Override
        public boolean decodePrim(MpackTypes.InStream in) {
            return in.readBoolean();
        }
    }

    protected final Codec.BooleanCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> booleanCodec = new BooleanCodec();

    @Override
    public Codec.BooleanCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> booleanArrayCodec =
            new Codec<boolean[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, boolean[] value, MpackTypes.OutStream out) {
            out.writeInt(value.length);
            for (boolean val : value) {
                booleanCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public boolean[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            final int l = in.readInt();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = booleanCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public MpackTypes.OutStream encodePrim(byte val, MpackTypes.OutStream out) {
            return out.writeByte(val);
        }

        @Override
        public byte decodePrim(MpackTypes.InStream in) {
            return in.readByte();
        }
    }

    protected final Codec.ByteCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> byteCodec = new ByteCodec();

    @Override
    public Codec.ByteCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> byteArrayCodec =
            new Codec<byte[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, byte[] value, MpackTypes.OutStream out) {
            out.writeInt(value.length);
            for (byte val : value) {
                byteCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public byte[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            final int l = in.readInt();
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = byteCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public MpackTypes.OutStream encodePrim(char val, MpackTypes.OutStream out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(MpackTypes.InStream in ) {
            return in.readChar();
        }
    }

    protected final Codec.CharCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> charCodec = new CharCodec();

    @Override
    public Codec.CharCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> charArrayCodec =
            new Codec<char[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, char[] value, MpackTypes.OutStream out) {
            out.writeInt(value.length);
            for (char val : value) {
                charCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public char[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            final int l = in.readInt();
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = charCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public MpackTypes.OutStream encodePrim(short val, MpackTypes.OutStream out) {
            return out.writeShort(val);
        }

        @Override
        public short decodePrim(MpackTypes.InStream in ) {
            return in.readShort();
        }
    }

    protected final Codec.ShortCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> shortCodec = new ShortCodec();

    @Override
    public Codec.ShortCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> shortArrayCodec =
            new Codec<short[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, short[] value, MpackTypes.OutStream out) {
            out.writeInt(value.length);
            for (short val : value) {
                shortCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public short[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            final int l = in.readInt();
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = shortCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public MpackTypes.OutStream encodePrim(int val, MpackTypes.OutStream out) {
            return out.writeInt(val);
        }

        @Override
        public int decodePrim(MpackTypes.InStream in ) {
            return in.readInt();
        }
    }

    protected final Codec.IntCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> intCodec = new IntCodec();

    @Override
    public Codec.IntCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> intArrayCodec =
            new Codec<int[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, int[] value, MpackTypes.OutStream out) {
            out.writeInt(value.length);
            for (int val : value) {
                intCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public int[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            final int l = in.readInt();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = intCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public MpackTypes.OutStream encodePrim(long val, MpackTypes.OutStream out) {
            return out.writeLong(val);
        }

        @Override
        public long decodePrim(MpackTypes.InStream in) {
            return in.readLong();
        }
    }

    protected final Codec.LongCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> longCodec = new LongCodec();

    @Override
    public Codec.LongCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> longArrayCodec =
            new Codec<long[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, long[] value, MpackTypes.OutStream out) {
            out.writeInt(value.length);
            for (long val : value) {
                longCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public long[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            final int l = in.readInt();
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = longCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public MpackTypes.OutStream encodePrim(float val, MpackTypes.OutStream out) {
            return out.writeFloat(val);
        }

        @Override
        public float decodePrim(MpackTypes.InStream in ) {
            return in.readFloat();
        }
    }

    protected final Codec.FloatCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> floatCodec = new FloatCodec();

    @Override
    public Codec.FloatCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> floatArrayCodec =
            new Codec<float[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, float[] value, MpackTypes.OutStream out) {
            out.writeInt(value.length);
            for (float val : value) {
                floatCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public float[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            final int l = in.readInt();
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = floatCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public MpackTypes.OutStream encodePrim(double value, MpackTypes.OutStream out) {
            return out.writeDouble(value);
        }

        @Override
        public double decodePrim(MpackTypes.InStream in ) {
            return in.readDouble();
        }
    }

    protected final Codec.DoubleCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> doubleCodec = new DoubleCodec();

    @Override
    public Codec.DoubleCodec<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> doubleArrayCodec =
            new Codec<double[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, double[] value, MpackTypes.OutStream out) {
            out.writeInt(value.length);
            for (double val : value) {
                doubleCodec().encodePrim(val, out);
            }
            return out;
        }

        @Override
        public double[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            final int l = in.readInt();
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = doubleCodec().decodePrim(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected static class StringCodec implements Codec<String, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, String value, MpackTypes.OutStream out) {
            return out.writeString(value);
        }

        @Override
        public String decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            return in.readString();
        }
    }

    protected final Codec<String, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> stringCodec = new StringCodec();

    @Override
    public Codec<String, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <V> Codec<Map<String, V>, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> valueCodec) {
        return new MpackMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> keyCodec,
            Codec<V, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> valueCodec) {
        return new MpackMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> elemCodec) {
        return new CollectionCodec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>(collType, elemCodec) {

            @Override
            public MpackTypes.OutStream encodeWithCheck(
                    CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core,
                    Collection<T> value,
                    MpackTypes.OutStream out) {
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
            public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, Collection<T> value, MpackTypes.OutStream out) {
                out.writeInt(value.size());
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out;
            }

            @Override
            public Collection<T> decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
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
    public <T> Codec<T[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> elemCodec) {
        return new Codec<T[], MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config>() {

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, T[] value, MpackTypes.OutStream out) {
                out.writeInt(value.length);
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out;
            }

            @Override
            public T[] decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
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
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, MpackTypes.InStream, MpackTypes.OutStream, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            implements Codec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        private final Class<T> type;
        private final ObjectMeta<T, MpackTypes.InStream, MpackTypes.OutStream, RA> objMeta;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, MpackTypes.InStream, MpackTypes.OutStream, RA> objMeta) {
            this.type = type;
            this.objMeta = objMeta;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public MpackTypes.OutStream encode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, T value, MpackTypes.OutStream out) {
            objMeta.forEach(field ->
                    field.encodeField(value, out)
            );
            return out;
        }

        @Override
        public T decode(CodecCoreEx<MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> core, MpackTypes.InStream in) {
            return Folds.foldLeft(
                    (acc, field) -> field.decodeField(acc, in),
                    objMeta.startDecode(),
                    objMeta
            ).construct();
        }
    }

    protected static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, MpackTypes.InStream, MpackTypes.OutStream, MpackTypes.Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, MpackTypes.InStream, MpackTypes.OutStream, RA> objMeta) {
            super(type, objMeta);
        }
    }
}
