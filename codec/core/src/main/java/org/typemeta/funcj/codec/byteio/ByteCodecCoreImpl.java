package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.utils.OperationNotImplementedException;
import org.typemeta.funcj.util.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.util.Exceptions.wrap;

@SuppressWarnings("unchecked")
public class ByteCodecCoreImpl extends BaseCodecCore<ByteIO> implements ByteCodecCore {

    public ByteCodecCoreImpl() {
    }

    public <T> ByteIO encode(T val) throws Exception {
        return encode((Class<T>)val.getClass(), val);
    }

    public <T> ByteIO encode(Class<T> type, T val) throws Exception {
        return super.encode(type, val, null);
    }

    @Override
    public Codec.NullCodec<ByteIO> nullCodec() {
        throw new OperationNotImplementedException();
    }

    protected final Codec.BooleanCodec<ByteIO> booleanCodec = new Codec.BooleanCodec<ByteIO>() {

        @Override
        public ByteIO encodePrim(boolean val, ByteIO enc) throws IOException {
            enc.output().writeBoolean(val);
            return enc;
        }

        @Override
        public boolean decodePrim(ByteIO enc) throws IOException {
            return enc.input().readBoolean();
        }
    };

    @Override
    public Codec.BooleanCodec<ByteIO> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], ByteIO> booleanArrayCodec = new Codec<boolean[], ByteIO>() {

        @Override
        public ByteIO encode(boolean[] vals, ByteIO enc) throws Exception {
            enc.output().writeInt(vals.length);

            for (boolean val : vals) {
                booleanCodec().encode(val, enc);
            }

            return enc;
        }

        @Override
        public boolean[] decode(ByteIO enc) throws Exception {
            final int l = enc.input().readInt();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = booleanCodec().decode(enc);
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], ByteIO> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<ByteIO> byteCodec = new Codec.ByteCodec<ByteIO>() {

        @Override
        public ByteIO encodePrim(byte val, ByteIO enc) throws IOException {
            enc.output().writeByte(val);
            return enc;
        }

        @Override
        public byte decodePrim(ByteIO enc) throws IOException {
            return enc.input().readByte();
        }
    };

    @Override
    public Codec.ByteCodec<ByteIO> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], ByteIO> byteArrayCodec = new Codec<byte[], ByteIO>() {

        @Override
        public ByteIO encode(byte[] vals, ByteIO enc) throws Exception {
            enc.output().writeInt(vals.length);

            for (byte val : vals) {
                byteCodec().encode(val, enc);
            }

            return enc;
        }

        @Override
        public byte[] decode(ByteIO enc) throws Exception {
            final int l = enc.input().readInt();
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = byteCodec().decode(enc);
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], ByteIO> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<ByteIO> charCodec = new Codec.CharCodec<ByteIO>() {

        @Override
        public ByteIO encodePrim(char val, ByteIO enc) throws IOException {
            enc.output().writeChar(val);
            return enc;
        }

        @Override
        public char decodePrim(ByteIO enc) throws IOException {
            return enc.input().readChar();
        }
    };

    @Override
    public Codec.CharCodec<ByteIO> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], ByteIO> charArrayCodec = new Codec<char[], ByteIO>() {

        @Override
        public ByteIO encode(char[] vals, ByteIO enc) throws Exception {
            enc.output().writeInt(vals.length);

            for (char val : vals) {
                charCodec().encode(val, enc);
            }

            return enc;
        }

        @Override
        public char[] decode(ByteIO enc) throws Exception {
            final int l = enc.input().readInt();
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = charCodec().decode(enc);
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], ByteIO> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<ByteIO> shortCodec = new Codec.ShortCodec<ByteIO>() {

        @Override
        public ByteIO encodePrim(short val, ByteIO enc) throws IOException {
            enc.output().writeShort(val);
            return enc;
        }

        @Override
        public short decodePrim(ByteIO enc) throws IOException {
            return enc.input().readShort();
        }
    };

    @Override
    public Codec.ShortCodec<ByteIO> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], ByteIO> shortArrayCodec = new Codec<short[], ByteIO>() {

        @Override
        public ByteIO encode(short[] vals, ByteIO enc) throws Exception {
            enc.output().writeInt(vals.length);

            for (short val : vals) {
                shortCodec().encode(val, enc);
            }

            return enc;
        }

        @Override
        public short[] decode(ByteIO enc) throws Exception {
            final int l = enc.input().readInt();
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = shortCodec().decode(enc);
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], ByteIO> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<ByteIO> intCodec = new Codec.IntCodec<ByteIO>() {

        @Override
        public ByteIO encodePrim(int val, ByteIO enc) throws IOException {
            enc.output().writeInt(val);
            return enc;
        }

        @Override
        public int decodePrim(ByteIO enc) throws IOException {
            return enc.input().readInt();
        }
    };

    @Override
    public Codec.IntCodec<ByteIO> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], ByteIO> intArrayCodec = new Codec<int[], ByteIO>() {

        @Override
        public ByteIO encode(int[] vals, ByteIO enc) throws Exception {
            enc.output().writeInt(vals.length);

            for (int val : vals) {
                intCodec().encode(val, enc);
            }

            return enc;
        }

        @Override
        public int[] decode(ByteIO enc) throws Exception {
            final int l = enc.input().readInt();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = intCodec().decode(enc);
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], ByteIO> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<ByteIO> longCodec = new Codec.LongCodec<ByteIO>() {

        @Override
        public ByteIO encodePrim(long val, ByteIO enc) throws IOException {
            enc.output().writeLong(val);
            return enc;
        }

        @Override
        public long decodePrim(ByteIO enc) throws IOException {
            return enc.input().readLong();
        }
    };

    @Override
    public Codec.LongCodec<ByteIO> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], ByteIO> longArrayCodec = new Codec<long[], ByteIO>() {

        @Override
        public ByteIO encode(long[] vals, ByteIO enc) throws Exception {
            enc.output().writeInt(vals.length);

            for (long val : vals) {
                longCodec().encode(val, enc);
            }

            return enc;
        }

        @Override
        public long[] decode(ByteIO enc) throws Exception {
            final int l = enc.input().readInt();
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = longCodec().decode(enc);
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], ByteIO> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<ByteIO> floatCodec = new Codec.FloatCodec<ByteIO>() {

        @Override
        public ByteIO encodePrim(float val, ByteIO enc) throws IOException {
            enc.output().writeFloat(val);
            return enc;
        }

        @Override
        public float decodePrim(ByteIO enc) throws IOException {
            return enc.input().readFloat();
        }
    };

    @Override
    public Codec.FloatCodec<ByteIO> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], ByteIO> floatArrayCodec = new Codec<float[], ByteIO>() {

        @Override
        public ByteIO encode(float[] vals, ByteIO enc) throws Exception {
            enc.output().writeInt(vals.length);

            for (float val : vals) {
                floatCodec().encode(val, enc);
            }

            return enc;
        }

        @Override
        public float[] decode(ByteIO enc) throws Exception {
            final int l = enc.input().readInt();
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = floatCodec().decode(enc);
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], ByteIO> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<ByteIO> doubleCodec = new Codec.DoubleCodec<ByteIO>() {

        @Override
        public ByteIO encodePrim(double val, ByteIO enc) throws IOException {
            enc.output().writeDouble(val);
            return enc;
        }

        @Override
        public double decodePrim(ByteIO enc) throws IOException {
            return enc.input().readDouble();
        }
    };

    @Override
    public Codec.DoubleCodec<ByteIO> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], ByteIO> doubleArrayCodec = new Codec<double[], ByteIO>() {

        @Override
        public ByteIO encode(double[] vals, ByteIO enc) throws Exception {
            enc.output().writeInt(vals.length);

            for (double val : vals) {
                doubleCodec().encode(val, enc);
            }

            return enc;
        }

        @Override
        public double[] decode(ByteIO enc) throws Exception {
            final int l = enc.input().readInt();
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = doubleCodec().decode(enc);
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], ByteIO> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, ByteIO> stringCodec = new Codec<String, ByteIO>() {
        @Override
        public ByteIO encode(String val, ByteIO enc) throws Exception {
            enc.writeString(val);
            return enc;
        }

        @Override
        public String decode(ByteIO enc) throws Exception {
            return enc.readString();
        }
    };

    @Override
    public Codec<String, ByteIO> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, ByteIO> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, ByteIO>() {
            @Override
            public ByteIO encode(EM val, ByteIO enc) throws Exception {
                return stringCodec().encode(val.name(), enc);
            }

            @Override
            public EM decode(Class<EM> dynType, ByteIO enc) throws Exception {
                Class<EM> type = dynType != null ? dynType : (Class<EM>)enumType;
                return EM.valueOf(type, stringCodec().decode(enc));
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, ByteIO> mapCodec(Codec<V, ByteIO> valueCodec) {
        return new ByteMapCodecs.MapCodec<String, V>(this, stringCodec(), valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, ByteIO> mapCodec(Codec<K, ByteIO> keyCodec, Codec<V, ByteIO> valueCodec) {
        return new ByteMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, ByteIO> collCodec(Class<T> elemType, Codec<T, ByteIO> elemCodec) {
        return new Codec<Collection<T>, ByteIO>() {
            @Override
            public ByteIO encode(Collection<T> vals, ByteIO enc) throws Exception {
                intCodec().encodePrim(vals.size(), enc);

                for (T val : vals) {
                    elemCodec.encode(val, enc);
                }

                return enc;
            }

            @Override
            public Collection<T> decode(Class<Collection<T>> dynType, ByteIO enc) throws Exception {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();

                final int l = intCodec().decodePrim(enc);

                final Collection<T> vals = getTypeConstructor(dynType).construct();
                if (vals instanceof ArrayList) {
                    ((ArrayList<T>) vals).ensureCapacity(l);
                }

                for (int i = 0; i < l; ++i) {
                    vals.add(elemCodec.decode(dynElemType, enc));
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], ByteIO> objectArrayCodec(Class<T> elemType, Codec<T, ByteIO> elemCodec) {
        return new Codec<T[], ByteIO>() {
            @Override
            public ByteIO encode(T[] vals, ByteIO enc) throws Exception {
                intCodec().encodePrim(vals.length, enc);

                for (T val : vals) {
                    elemCodec.encode(val, enc);
                }

                return enc;
            }

            @Override
            public T[] decode(Class<T[]> dynType, ByteIO enc) throws Exception {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();

                final int l = intCodec().decodePrim(enc);

                final T[] vals = (T[]) Array.newInstance(elemType, l);

                for (int i = 0; i < l; ++i) {
                    vals[i] = elemCodec.decode(dynElemType, enc);
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T, ByteIO> dynamicCodec(Class<T> stcType) {
        return new Codec<T, ByteIO>() {
            @Override
            public ByteIO encode(T val, ByteIO enc) throws Exception {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                final boolean typeMatch = dynType.equals(stcType);
                booleanCodec().encode(typeMatch, enc);
                if (!typeMatch) {
                    stringCodec().encode(classToName(dynType), enc);
                }
                return encode2(ByteCodecCoreImpl.this.getNullUnsafeCodec(dynType), val, enc);
            }

            protected <S extends T> ByteIO encode2(Codec<S, ByteIO> codec, T val, ByteIO enc) throws Exception {
                return codec.encode((S)val, enc);
            }

            @Override
            public T decode(ByteIO enc) throws Exception {
                final boolean typeMatch = booleanCodec().decode(enc);

                final Class<T> dynType;
                if (typeMatch) {
                    dynType = stcType;
                } else {
                    final String typeName = stringCodec().decode(enc);
                    dynType = nameToClass(typeName);
                }

                final Codec<T, ByteIO> codec = ByteCodecCoreImpl.this.getNullUnsafeCodec(dynType);
                return codec.decode(dynType, enc);
            }
        };
    }

    @Override
    public <T> Codec<T, ByteIO> dynamicCodec(Codec<T, ByteIO> codec, Class<T> stcType) {
        return new Codec<T, ByteIO>() {
            @Override
            public ByteIO encode(T val, ByteIO enc) throws Exception {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                final boolean typeMatch = dynType.equals(stcType);
                booleanCodec().encode(typeMatch, enc);
                if (!typeMatch) {
                    stringCodec().encode(classToName(dynType), enc);
                }
                return codec.encode(val, enc);
            }

            @Override
            public T decode(ByteIO enc) throws Exception {
                final boolean typeMatch = booleanCodec().decode(enc);

                final Class<T> dynType;
                if (typeMatch) {
                    dynType = stcType;
                } else {
                    final String typeName = stringCodec().decode(enc);
                    dynType = nameToClass(typeName);
                }

                return codec.decode(dynType, enc);
            }
        };
    }

    @Override
    public <T> Codec<T, ByteIO> makeNullSafeCodec(Codec<T, ByteIO> codec) {
        return new Codec<T, ByteIO>() {
            @Override
            public ByteIO encode(T val, ByteIO enc) throws Exception {
                booleanCodec().encodePrim(val == null, enc);
                if (val != null) {
                    return codec.encode(val, enc);
                } else {
                    return enc;
                }
            }

            @Override
            public T decode(Class<T> dynType, ByteIO enc) throws Exception {
                if (!booleanCodec().decodePrim(enc)) {
                    return codec.decode(dynType, enc);
                } else {
                    return null;
                }
            }

            @Override
            public T decode(ByteIO enc) throws Exception {
                if (!booleanCodec().decodePrim(enc)) {
                    return codec.decode(enc);
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, ByteIO> createObjectCodec(ObjectMeta<T, ByteIO, RA> objMeta) {
        return new Codec<T, ByteIO>() {

            @Override
            public ByteIO encode(T val, ByteIO enc) throws Exception {
                Exceptions.<Exception>unwrap(() ->
                        objMeta.forEach(
                                field -> wrap(() -> {
                                    field.encodeField(val, enc);
                                })));
                return enc;
            }

            @Override
            public T decode(Class<T> dynType, ByteIO enc) throws Exception {
                return Exceptions.<T, Exception>unwrap(() ->
                        Folds.foldLeft(
                                (acc, field) -> wrap(() -> field.decodeField(acc, enc)),
                                wrap(() -> objMeta.startDecode(dynType)),
                                objMeta
                        ).construct()
                );
            }
        };
    }

    @Override
    public String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "_" + name;
        }
        return name;
    }
}
