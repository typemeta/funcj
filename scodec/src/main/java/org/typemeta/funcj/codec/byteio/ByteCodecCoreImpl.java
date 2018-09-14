package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.byteio.ByteIO.Input;
import org.typemeta.funcj.codec.byteio.ByteIO.Output;
import org.typemeta.funcj.codec.utils.OperationNotImplementedException;
import org.typemeta.funcj.util.Folds;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("unchecked")
public class ByteCodecCoreImpl extends BaseCodecCore<Input, Output> implements ByteCodecCore {

    public ByteCodecCoreImpl() {
    }

    public <T> Output encode(T val) {
        return encode((Class<T>)val.getClass(), val);
    }

    public <T> Output encode(Class<T> type, T val) {
        return super.encode(type, val, null);
    }

    @Override
    public Codec.NullCodec<Input, Output> nullCodec() {
        throw new OperationNotImplementedException();
    }

    protected final Codec.BooleanCodec<Input, Output> booleanCodec = new Codec.BooleanCodec<Input, Output>() {

        @Override
        public Output encodePrim(boolean val, Output out) {
            CodecException.wrap(() -> out.output().writeBoolean(val));
            return out;
        }

        @Override
        public boolean decodePrim(Input in) {
            return CodecException.wrap(() -> in.input().readBoolean());
        }
    };

    @Override
    public Codec.BooleanCodec<Input, Output> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], Input, Output> booleanArrayCodec = new Codec<boolean[], Input, Output>() {

        @Override
        public Output encode(boolean[] vals, Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (boolean val : vals) {
                booleanCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public boolean[] decode(Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
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

    protected final Codec.ByteCodec<Input, Output> byteCodec = new Codec.ByteCodec<Input, Output>() {

        @Override
        public Output encodePrim(byte val, Output out) {
            CodecException.wrap(() -> out.output().writeByte(val));
            return out;
        }

        @Override
        public byte decodePrim(Input in) {
            return CodecException.wrap(() -> in.input().readByte());
        }
    };

    @Override
    public Codec.ByteCodec<Input, Output> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], Input, Output> byteArrayCodec = new Codec<byte[], Input, Output>() {

        @Override
        public Output encode(byte[] vals, Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (byte val : vals) {
                byteCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public byte[] decode(Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
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

    protected final Codec.CharCodec<Input, Output> charCodec = new Codec.CharCodec<Input, Output>() {

        @Override
        public Output encodePrim(char val, Output out) {
            CodecException.wrap(() -> out.output().writeChar(val));
            return out;
        }

        @Override
        public char decodePrim(Input in) {
            return CodecException.wrap(() -> in.input().readChar());
        }
    };

    @Override
    public Codec.CharCodec<Input, Output> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], Input, Output> charArrayCodec = new Codec<char[], Input, Output>() {

        @Override
        public Output encode(char[] vals, Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (char val : vals) {
                charCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public char[] decode(Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
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

    protected final Codec.ShortCodec<Input, Output> shortCodec = new Codec.ShortCodec<Input, Output>() {

        @Override
        public Output encodePrim(short val, Output out) {
            CodecException.wrap(() -> out.output().writeShort(val));
            return out;
        }

        @Override
        public short decodePrim(Input in) {
            return CodecException.wrap(() -> in.input().readShort());
        }
    };

    @Override
    public Codec.ShortCodec<Input, Output> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], Input, Output> shortArrayCodec = new Codec<short[], Input, Output>() {

        @Override
        public Output encode(short[] vals, Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (short val : vals) {
                shortCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public short[] decode(Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
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

    protected final Codec.IntCodec<Input, Output> intCodec = new Codec.IntCodec<Input, Output>() {

        @Override
        public Output encodePrim(int val, Output out) {
            CodecException.wrap(() -> out.output().writeInt(val));
            return out;
        }

        @Override
        public int decodePrim(Input in) {
            return CodecException.wrap(() -> in.input().readInt());
        }
    };

    @Override
    public Codec.IntCodec<Input, Output> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], Input, Output> intArrayCodec = new Codec<int[], Input, Output>() {

        @Override
        public Output encode(int[] vals, Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (int val : vals) {
                intCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public int[] decode(Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
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

    protected final Codec.LongCodec<Input, Output> longCodec = new Codec.LongCodec<Input, Output>() {

        @Override
        public Output encodePrim(long val, Output out) {
            CodecException.wrap(() -> out.output().writeLong(val));
            return out;
        }

        @Override
        public long decodePrim(Input in) {
            return CodecException.wrap(() -> in.input().readLong());
        }
    };

    @Override
    public Codec.LongCodec<Input, Output> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], Input, Output> longArrayCodec = new Codec<long[], Input, Output>() {

        @Override
        public Output encode(long[] vals, Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (long val : vals) {
                longCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public long[] decode(Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
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

    protected final Codec.FloatCodec<Input, Output> floatCodec = new Codec.FloatCodec<Input, Output>() {

        @Override
        public Output encodePrim(float val, Output out) {
            CodecException.wrap(() -> out.output().writeFloat(val));
            return out;
        }

        @Override
        public float decodePrim(Input in) {
            return CodecException.wrap(() -> in.input().readFloat());
        }
    };

    @Override
    public Codec.FloatCodec<Input, Output> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], Input, Output> floatArrayCodec = new Codec<float[], Input, Output>() {

        @Override
        public Output encode(float[] vals, Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (float val : vals) {
                floatCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public float[] decode(Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
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

    protected final Codec.DoubleCodec<Input, Output> doubleCodec = new Codec.DoubleCodec<Input, Output>() {

        @Override
        public Output encodePrim(double val, Output out) {
            CodecException.wrap(() -> out.output().writeDouble(val));
            return out;
        }

        @Override
        public double decodePrim(Input in) {
            return CodecException.wrap(() -> in.input().readDouble());
        }
    };

    @Override
    public Codec.DoubleCodec<Input, Output> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], Input, Output> doubleArrayCodec = new Codec<double[], Input, Output>() {

        @Override
        public Output encode(double[] vals, Output out) {
            CodecException.wrap(() -> out.output().writeInt(vals.length));

            for (double val : vals) {
                doubleCodec().encode(val, out);
            }

            return out;
        }

        @Override
        public double[] decode(Input in) {
            final int l = CodecException.wrap(() -> in.input().readInt());
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

    protected final Codec<String, Input, Output> stringCodec = new Codec<String, Input, Output>() {
        @Override
        public Output encode(String val, Output out) {
            CodecException.wrap(() -> out.writeString(val));
            return out;
        }

        @Override
        public String decode(Input in) {
            return CodecException.wrap(() -> in.readString());
        }
    };

    @Override
    public Codec<String, Input, Output> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, Input, Output> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, Input, Output>() {
            @Override
            public Output encode(EM val, Output out) {
                return stringCodec().encode(val.name(), out);
            }

            @Override
            public EM decode(Class<EM> dynType, Input in) {
                Class<EM> type = dynType != null ? dynType : (Class<EM>)enumType;
                return EM.valueOf(type, stringCodec().decode(in));
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, Input, Output> mapCodec(Codec<V, Input, Output> valueCodec) {
        return new ByteMapCodecs.MapCodec<String, V>(this, stringCodec(), valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Input, Output> mapCodec(Codec<K, Input, Output> keyCodec, Codec<V, Input, Output> valueCodec) {
        return new ByteMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, Input, Output> collCodec(Class<T> elemType, Codec<T, Input, Output> elemCodec) {
        return new Codec<Collection<T>, Input, Output>() {
            @Override
            public Output encode(Collection<T> vals, Output out) {
                intCodec().encodePrim(vals.size(), out);

                for (T val : vals) {
                    elemCodec.encode(val, out);
                }

                return out;
            }

            @Override
            public Collection<T> decode(Class<Collection<T>> dynType, Input in) {
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
    public <T> Codec<T[], Input, Output> objectArrayCodec(Class<T> elemType, Codec<T, Input, Output> elemCodec) {
        return new Codec<T[], Input, Output>() {
            @Override
            public Output encode(T[] vals, Output out) {
                intCodec().encodePrim(vals.length, out);

                for (T val : vals) {
                    elemCodec.encode(val, out);
                }

                return out;
            }

            @Override
            public T[] decode(Class<T[]> dynType, Input in) {
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
    public <T> Codec<T, Input, Output> dynamicCodec(Class<T> stcType) {
        return new Codec<T, Input, Output>() {
            @Override
            public Output encode(T val, Output out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                final boolean typeMatch = dynType.equals(stcType);
                booleanCodec().encode(typeMatch, out);
                if (!typeMatch) {
                    stringCodec().encode(classToName(dynType), out);
                }
                return encode2(ByteCodecCoreImpl.this.getNullUnsafeCodecDyn(dynType), val, out);
            }

            protected <S extends T> Output encode2(Codec<S, Input, Output> codec, T val, Output out) {
                return codec.encode((S)val, out);
            }

            @Override
            public T decode(Input in) {
                final boolean typeMatch = booleanCodec().decode(in);

                final Class<T> dynType;
                if (typeMatch) {
                    dynType = stcType;
                } else {
                    final String typeName = stringCodec().decode(in);
                    dynType = nameToClass(typeName);
                }

                final Codec<T, Input, Output> codec = ByteCodecCoreImpl.this.getNullUnsafeCodecDyn(dynType);
                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    public <T> Codec<T, Input, Output> dynamicCodec(Codec<T, Input, Output> codec, Class<T> stcType) {
        return new Codec<T, Input, Output>() {
            @Override
            public Output encode(T val, Output out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                final boolean typeMatch = dynType.equals(stcType);
                booleanCodec().encode(typeMatch, out);
                if (!typeMatch) {
                    stringCodec().encode(classToName(dynType), out);
                }
                return codec.encode(val, out);
            }

            @Override
            public T decode(Input in) {
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
    public <T> Codec<T, Input, Output> makeNullSafeCodec(Codec<T, Input, Output> codec) {
        return new Codec<T, Input, Output>() {
            @Override
            public Output encode(T val, Output out) {
                booleanCodec().encodePrim(val == null, out);
                if (val != null) {
                    return codec.encode(val, out);
                } else {
                    return out;
                }
            }

            @Override
            public T decode(Class<T> dynType, Input in) {
                if (!booleanCodec().decodePrim(in)) {
                    return codec.decode(dynType, in);
                } else {
                    return null;
                }
            }

            @Override
            public T decode(Input in) {
                if (!booleanCodec().decodePrim(in)) {
                    return codec.decode(in);
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, Input, Output> createObjectCodec(
            ObjectMeta<T, Input, Output, RA> objMeta
    ) {
        return new Codec<T, Input, Output>() {

            @Override
            public Output encode(T val, Output out) {
                objMeta.forEach(field -> field.encodeField(val, out));
                return out;
            }

            @Override
            public T decode(Class<T> dynType, Input in) {
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
