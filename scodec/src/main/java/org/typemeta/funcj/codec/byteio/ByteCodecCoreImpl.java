package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.utils.OperationNotImplementedException;
import org.typemeta.funcj.util.*;

import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.util.Exceptions.wrap;

@SuppressWarnings("unchecked")
public class ByteCodecCoreImpl extends BaseCodecCore<ByteIO.Input, ByteIO.Output> implements ByteCodecCore {

    public ByteCodecCoreImpl() {
    }

    public <T> ByteIO.Output encode(T val) {
        return encode((Class<T>)val.getClass(), val);
    }

    public <T> ByteIO.Output encode(Class<T> type, T val) {
        return super.encode(type, val, null);
    }

    @Override
    public Codec.NullCodec<ByteIO.Input, ByteIO.Output> nullCodec() {
        throw new OperationNotImplementedException();
    }

    protected final Codec.BooleanCodec<ByteIO.Input, ByteIO.Output> booleanCodec = new Codec.BooleanCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encodePrim(boolean val, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeBoolean(val));
            return out;
        }

        @Override
        public boolean decodePrim(ByteIO.Input in) {
            return CodecException.wrap(() -> in.input().readBoolean());
        }
    };

    @Override
    public Codec.BooleanCodec<ByteIO.Input, ByteIO.Output> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], ByteIO.Input, ByteIO.Output> booleanArrayCodec = new Codec<boolean[], ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encode(boolean[] vals, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (boolean val : vals) {
                booleanCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public boolean[] decode(ByteIO.Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = booleanCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], ByteIO.Input, ByteIO.Output> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<ByteIO.Input, ByteIO.Output> byteCodec = new Codec.ByteCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encodePrim(byte val, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeByte(val));
            return out;
        }

        @Override
        public byte decodePrim(ByteIO.Input in) {
            return CodecException.wrap(() -> in.input().readByte());
        }
    };

    @Override
    public Codec.ByteCodec<ByteIO.Input, ByteIO.Output> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], ByteIO.Input, ByteIO.Output> byteArrayCodec = new Codec<byte[], ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encode(byte[] vals, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (byte val : vals) {
                byteCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public byte[] decode(ByteIO.Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = byteCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], ByteIO.Input, ByteIO.Output> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<ByteIO.Input, ByteIO.Output> charCodec = new Codec.CharCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encodePrim(char val, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeChar(val));
            return out;
        }

        @Override
        public char decodePrim(ByteIO.Input in) {
            return CodecException.wrap(() -> in.input().readChar());
        }
    };

    @Override
    public Codec.CharCodec<ByteIO.Input, ByteIO.Output> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], ByteIO.Input, ByteIO.Output> charArrayCodec = new Codec<char[], ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encode(char[] vals, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (char val : vals) {
                charCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public char[] decode(ByteIO.Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = charCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], ByteIO.Input, ByteIO.Output> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<ByteIO.Input, ByteIO.Output> shortCodec = new Codec.ShortCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encodePrim(short val, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeShort(val));
            return out;
        }

        @Override
        public short decodePrim(ByteIO.Input in) {
            return CodecException.wrap(() -> in.input().readShort());
        }
    };

    @Override
    public Codec.ShortCodec<ByteIO.Input, ByteIO.Output> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], ByteIO.Input, ByteIO.Output> shortArrayCodec = new Codec<short[], ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encode(short[] vals, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (short val : vals) {
                shortCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public short[] decode(ByteIO.Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = shortCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], ByteIO.Input, ByteIO.Output> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<ByteIO.Input, ByteIO.Output> intCodec = new Codec.IntCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encodePrim(int val, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(val));
            return out;
        }

        @Override
        public int decodePrim(ByteIO.Input in) {
            return CodecException.wrap(() -> in.input().readInt());
        }
    };

    @Override
    public Codec.IntCodec<ByteIO.Input, ByteIO.Output> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], ByteIO.Input, ByteIO.Output> intArrayCodec = new Codec<int[], ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encode(int[] vals, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (int val : vals) {
                intCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public int[] decode(ByteIO.Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = intCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], ByteIO.Input, ByteIO.Output> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<ByteIO.Input, ByteIO.Output> longCodec = new Codec.LongCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encodePrim(long val, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeLong(val));
            return out;
        }

        @Override
        public long decodePrim(ByteIO.Input in) {
            return CodecException.wrap(() -> in.input().readLong());
        }
    };

    @Override
    public Codec.LongCodec<ByteIO.Input, ByteIO.Output> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], ByteIO.Input, ByteIO.Output> longArrayCodec = new Codec<long[], ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encode(long[] vals, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (long val : vals) {
                longCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public long[] decode(ByteIO.Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = longCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], ByteIO.Input, ByteIO.Output> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<ByteIO.Input, ByteIO.Output> floatCodec = new Codec.FloatCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encodePrim(float val, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeFloat(val));
            return out;
        }

        @Override
        public float decodePrim(ByteIO.Input in) {
            return CodecException.wrap(() -> in.input().readFloat());
        }
    };

    @Override
    public Codec.FloatCodec<ByteIO.Input, ByteIO.Output> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], ByteIO.Input, ByteIO.Output> floatArrayCodec = new Codec<float[], ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encode(float[] vals, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (float val : vals) {
                floatCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public float[] decode(ByteIO.Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = floatCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], ByteIO.Input, ByteIO.Output> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<ByteIO.Input, ByteIO.Output> doubleCodec = new Codec.DoubleCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encodePrim(double val, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeDouble(val));
            return out;
        }

        @Override
        public double decodePrim(ByteIO.Input in) {
            return CodecException.wrap(() -> in.input().readDouble());
        }
    };

    @Override
    public Codec.DoubleCodec<ByteIO.Input, ByteIO.Output> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], ByteIO.Input, ByteIO.Output> doubleArrayCodec = new Codec<double[], ByteIO.Input, ByteIO.Output>() {

        @Override
        public ByteIO.Output encode(double[] vals, ByteIO.Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (double val : vals) {
                doubleCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public double[] decode(ByteIO.Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                vals[i] = doubleCodec().decode(in);
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], ByteIO.Input, ByteIO.Output> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, ByteIO.Input, ByteIO.Output> stringCodec = new Codec<String, ByteIO.Input, ByteIO.Output>() {
        @Override
        public ByteIO.Output encode(String val, ByteIO.Output out) {
            CodecException.wrap(() -> out.writeString(val));
            return out;
        }

        @Override
        public String decode(ByteIO.Input in) {
            return CodecException.wrap(() -> in.readString());
        }
    };

    @Override
    public Codec<String, ByteIO.Input, ByteIO.Output> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, ByteIO.Input, ByteIO.Output> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, ByteIO.Input, ByteIO.Output>() {
            @Override
            public ByteIO.Output encode(EM val, ByteIO.Output out) {
                return stringCodec().encode(val.name(), out);
            }

            @Override
            public EM decode(Class<EM> dynType, ByteIO.Input in) {
                Class<EM> type = dynType != null ? dynType : (Class<EM>)enumType;
                return EM.valueOf(type, stringCodec().decode(in));
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, ByteIO.Input, ByteIO.Output> mapCodec(Codec<V, ByteIO.Input, ByteIO.Output> valueCodec) {
        return new ByteMapCodecs.MapCodec<String, V>(this, stringCodec(), valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, ByteIO.Input, ByteIO.Output> mapCodec(Codec<K, ByteIO.Input, ByteIO.Output> keyCodec, Codec<V, ByteIO.Input, ByteIO.Output> valueCodec) {
        return new ByteMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, ByteIO.Input, ByteIO.Output> collCodec(Class<T> elemType, Codec<T, ByteIO.Input, ByteIO.Output> elemCodec) {
        return new Codec<Collection<T>, ByteIO.Input, ByteIO.Output>() {
            @Override
            public ByteIO.Output encode(Collection<T> vals, ByteIO.Output out) {
                intCodec().encodePrim(vals.size(), out);

                for (T val : vals) {
                    elemCodec.encode(val, out);
                }

                return out;
            }

            @Override
            public Collection<T> decode(Class<Collection<T>> dynType, ByteIO.Input in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();

                final int l = intCodec().decodePrim(in);

                final Collection<T> vals = getTypeConstructor(dynType).construct();
                if (vals instanceof ArrayList) {
                    ((ArrayList<T>) vals).ensureCapacity(l);
                }

                for (int i = 0; i < l; ++i) {
                    vals.add(elemCodec.decode(dynElemType, in));
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], ByteIO.Input, ByteIO.Output> objectArrayCodec(Class<T> elemType, Codec<T, ByteIO.Input, ByteIO.Output> elemCodec) {
        return new Codec<T[], ByteIO.Input, ByteIO.Output>() {
            @Override
            public ByteIO.Output encode(T[] vals, ByteIO.Output out) {
                intCodec().encodePrim(vals.length, out);

                for (T val : vals) {
                    elemCodec.encode(val, out);
                }

                return out;
            }

            @Override
            public T[] decode(Class<T[]> dynType, ByteIO.Input in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();

                final int l = intCodec().decodePrim(in);

                final T[] vals = (T[]) Array.newInstance(elemType, l);

                for (int i = 0; i < l; ++i) {
                    vals[i] = elemCodec.decode(dynElemType, in);
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T, ByteIO.Input, ByteIO.Output> dynamicCodec(Class<T> stcType) {
        return new Codec<T, ByteIO.Input, ByteIO.Output>() {
            @Override
            public ByteIO.Output encode(T val, ByteIO.Output out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                final boolean typeMatch = dynType.equals(stcType);
                booleanCodec().encode(typeMatch, out);
                if (!typeMatch) {
                    stringCodec().encode(classToName(dynType), out);
                }
                return encode2(ByteCodecCoreImpl.this.getNullUnsafeCodec(dynType), val, out);
            }

            protected <S extends T> ByteIO.Output encode2(Codec<S, ByteIO.Input, ByteIO.Output> codec, T val, ByteIO.Output out) {
                return codec.encode((S)val, out);
            }

            @Override
            public T decode(ByteIO.Input in) {
                final boolean typeMatch = booleanCodec().decode(in);

                final Class<T> dynType;
                if (typeMatch) {
                    dynType = stcType;
                } else {
                    final String typeName = stringCodec().decode(in);
                    dynType = nameToClass(typeName);
                }

                final Codec<T, ByteIO.Input, ByteIO.Output> codec = ByteCodecCoreImpl.this.getNullUnsafeCodec(dynType);
                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    public <T> Codec<T, ByteIO.Input, ByteIO.Output> dynamicCodec(Codec<T, ByteIO.Input, ByteIO.Output> codec, Class<T> stcType) {
        return new Codec<T, ByteIO.Input, ByteIO.Output>() {
            @Override
            public ByteIO.Output encode(T val, ByteIO.Output out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                final boolean typeMatch = dynType.equals(stcType);
                booleanCodec().encode(typeMatch, out);
                if (!typeMatch) {
                    stringCodec().encode(classToName(dynType), out);
                }
                return codec.encode(val, out);
            }

            @Override
            public T decode(ByteIO.Input in) {
                final boolean typeMatch = booleanCodec().decode(in);

                final Class<T> dynType;
                if (typeMatch) {
                    dynType = stcType;
                } else {
                    final String typeName = stringCodec().decode(in);
                    dynType = nameToClass(typeName);
                }

                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    public <T> Codec<T, ByteIO.Input, ByteIO.Output> makeNullSafeCodec(Codec<T, ByteIO.Input, ByteIO.Output> codec) {
        return new Codec<T, ByteIO.Input, ByteIO.Output>() {
            @Override
            public ByteIO.Output encode(T val, ByteIO.Output out) {
                booleanCodec().encodePrim(val == null, out);
                if (val != null) {
                    return codec.encode(val, out);
                } else {
                    return out;
                }
            }

            @Override
            public T decode(Class<T> dynType, ByteIO.Input in) {
                if (!booleanCodec().decodePrim(in)) {
                    return codec.decode(dynType, in);
                } else {
                    return null;
                }
            }

            @Override
            public T decode(ByteIO.Input in) {
                if (!booleanCodec().decodePrim(in)) {
                    return codec.decode(in);
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, ByteIO.Input, ByteIO.Output> createObjectCodec(ObjectMeta<T, ByteIO.Input, ByteIO.Output, RA> objMeta) {
        return new Codec<T, ByteIO.Input, ByteIO.Output>() {

            @Override
            public ByteIO.Output encode(T val, ByteIO.Output out) {
                objMeta.forEach(field -> field.encodeField(val, out));
                return out;
            }

            @Override
            public T decode(Class<T> dynType, ByteIO.Input in) {
                return 
                    Folds.foldLeft(
                            (acc, field) -> field.decodeField(acc, in),
                            objMeta.startDecode(dynType),
                            objMeta
                    ).construct();
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
