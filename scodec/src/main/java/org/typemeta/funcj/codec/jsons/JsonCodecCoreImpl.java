package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.*;

import java.lang.reflect.Array;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@SuppressWarnings("unchecked")
public class JsonCodecCoreImpl extends BaseCodecCore<JsonIO.Input, JsonIO.Output> implements JsonCodecCore {

    public JsonCodecCoreImpl() {
    }

    public String typeFieldName() {
        return "@type";
    }

    public String keyFieldName() {
        return "@key";
    }

    public String valueFieldName() {
        return "@value";
    }

    protected int defaultArrSize() {
        return 16;
    }

    private final Codec.NullCodec<JsonIO.Input, JsonIO.Output> nullCodec = new Codec.NullCodec<JsonIO.Input, JsonIO.Output>() {
        @Override
        public boolean isNull(JsonIO.Input in) {
            return in.currentEventType().equals(JsonIO.Input.Type.NULL);
        }

        @Override
        public JsonIO.Output encode(Object val, JsonIO.Output out) {
            return out.writeNull();
        }

        @Override
        public Object decode(JsonIO.Input in) {
            return in.readNull();
        }
    };

    public <T> JsonIO.Output encode(T val) {
        return encode((Class<T>)val.getClass(), val);
    }

    public <T> JsonIO.Output encode(Class<T> type, T val) {
        return super.encode(type, val, null);
    }

    @Override
    public Codec.NullCodec<JsonIO.Input, JsonIO.Output> nullCodec() {
        return nullCodec;
    }

    protected final Codec.BooleanCodec<JsonIO.Input, JsonIO.Output> booleanCodec = new Codec.BooleanCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encodePrim(boolean val, JsonIO.Output out) {
            return out.writeBool(val);
        }

        @Override
        public boolean decodePrim(JsonIO.Input in) {
            return in.readBool();
        }
    };

    @Override
    public Codec.BooleanCodec<JsonIO.Input, JsonIO.Output> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], JsonIO.Input, JsonIO.Output> booleanArrayCodec = new Codec<boolean[], JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encode(boolean[] vals, JsonIO.Output out) {
            out.startArray();
            for (boolean val : vals) {
                booleanCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public boolean[] decode(JsonIO.Input in) {
            boolean[] arr = new boolean[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = booleanCodec().decode(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<boolean[], JsonIO.Input, JsonIO.Output> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<JsonIO.Input, JsonIO.Output> byteCodec = new Codec.ByteCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encodePrim(byte val, JsonIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public byte decodePrim(JsonIO.Input in) {
            return in.readByte();
        }
    };

    @Override
    public Codec.ByteCodec<JsonIO.Input, JsonIO.Output> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], JsonIO.Input, JsonIO.Output> byteArrayCodec = new Codec<byte[], JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encode(byte[] vals, JsonIO.Output out) {
            out.startArray();
            for (byte val : vals) {
                byteCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public byte[] decode(JsonIO.Input in) {
            byte[] arr = new byte[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = byteCodec().decode(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<byte[], JsonIO.Input, JsonIO.Output> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<JsonIO.Input, JsonIO.Output> charCodec = new Codec.CharCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encodePrim(char val, JsonIO.Output out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(JsonIO.Input in ) {
            return in.readChar();
        }
    };

    @Override
    public Codec.CharCodec<JsonIO.Input, JsonIO.Output> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], JsonIO.Input, JsonIO.Output> charArrayCodec = new Codec<char[], JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encode(char[] vals, JsonIO.Output out) {
            out.startArray();
            for (char val : vals) {
                charCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public char[] decode(JsonIO.Input in) {
            char[] arr = new char[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = charCodec().decode(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<char[], JsonIO.Input, JsonIO.Output> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<JsonIO.Input, JsonIO.Output> shortCodec = new Codec.ShortCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encodePrim(short val, JsonIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public short decodePrim(JsonIO.Input in ) {
            return in.readShort();
        }
    };

    @Override
    public Codec.ShortCodec<JsonIO.Input, JsonIO.Output> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], JsonIO.Input, JsonIO.Output> shortArrayCodec = new Codec<short[], JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encode(short[] vals, JsonIO.Output out) {
            out.startArray();
            for (short val : vals) {
                shortCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public short[] decode(JsonIO.Input in) {
            short[] arr = new short[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = shortCodec().decode(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<short[], JsonIO.Input, JsonIO.Output> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<JsonIO.Input, JsonIO.Output> intCodec = new Codec.IntCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encodePrim(int val, JsonIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public int decodePrim(JsonIO.Input in ) {
            return in.readInt();
        }
    };

    @Override
    public Codec.IntCodec<JsonIO.Input, JsonIO.Output> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], JsonIO.Input, JsonIO.Output> intArrayCodec = new Codec<int[], JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encode(int[] vals, JsonIO.Output out) {
            out.startArray();
            for (int val : vals) {
                intCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public int[] decode(JsonIO.Input in) {
            int[] arr = new int[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = intCodec().decode(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<int[], JsonIO.Input, JsonIO.Output> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<JsonIO.Input, JsonIO.Output> longCodec = new Codec.LongCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encodePrim(long val, JsonIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public long decodePrim(JsonIO.Input in) {
            return in.readLong();
        }
    };

    @Override
    public Codec.LongCodec<JsonIO.Input, JsonIO.Output> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], JsonIO.Input, JsonIO.Output> longArrayCodec = new Codec<long[], JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encode(long[] vals, JsonIO.Output out) {
            out.startArray();
            for (long val : vals) {
                longCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public long[] decode(JsonIO.Input in) {
            long[] arr = new long[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = longCodec().decode(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<long[], JsonIO.Input, JsonIO.Output> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<JsonIO.Input, JsonIO.Output> floatCodec = new Codec.FloatCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encodePrim(float val, JsonIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public float decodePrim(JsonIO.Input in ) {
            return in.readFloat();
        }
    };

    @Override
    public Codec.FloatCodec<JsonIO.Input, JsonIO.Output> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], JsonIO.Input, JsonIO.Output> floatArrayCodec = new Codec<float[], JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encode(float[] vals, JsonIO.Output out) {
            out.startArray();
            for (float val : vals) {
                floatCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public float[] decode(JsonIO.Input in) {
            float[] arr = new float[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = floatCodec().decode(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<float[], JsonIO.Input, JsonIO.Output> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<JsonIO.Input, JsonIO.Output> doubleCodec = new Codec.DoubleCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encodePrim(double val, JsonIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public double decodePrim(JsonIO.Input in ) {
            return in.readDbl();
        }
    };

    @Override
    public Codec.DoubleCodec<JsonIO.Input, JsonIO.Output> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], JsonIO.Input, JsonIO.Output> doubleArrayCodec = new Codec<double[], JsonIO.Input, JsonIO.Output>() {

        @Override
        public JsonIO.Output encode(double[] vals, JsonIO.Output out) {
            out.startArray();
            for (double val : vals) {
                doubleCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public double[] decode(JsonIO.Input in) {
            double[] arr = new double[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = doubleCodec().decode(in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<double[], JsonIO.Input, JsonIO.Output> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, JsonIO.Input, JsonIO.Output> stringCodec = new Codec<String, JsonIO.Input, JsonIO.Output>() {
        @Override
        public JsonIO.Output encode(String val, JsonIO.Output out) {
            return out.writeStr(val);
        }

        @Override
        public String decode(JsonIO.Input in) {
            return in.readStr();
        }
    };

    @Override
    public Codec<String, JsonIO.Input, JsonIO.Output> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, JsonIO.Input, JsonIO.Output> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, JsonIO.Input, JsonIO.Output>() {
            @Override
            public JsonIO.Output encode(EM val, JsonIO.Output out) {
                return out.writeStr(val.name());
            }

            @Override
            public EM decode(Class<EM> dynType, JsonIO.Input in) {
                final Class<EM> type = dynType != null ? dynType : (Class<EM>)enumType;
                return EM.valueOf(type, in.readStr());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, JsonIO.Input, JsonIO.Output> mapCodec(
            Codec<V, JsonIO.Input, JsonIO.Output> valueCodec) {
        return new JsonMapCodecs.StringMapCodec<V>(this, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, JsonIO.Input, JsonIO.Output> mapCodec(
            Codec<K, JsonIO.Input, JsonIO.Output> keyCodec,
            Codec<V, JsonIO.Input, JsonIO.Output> valueCodec) {
        return new JsonMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, JsonIO.Input, JsonIO.Output> collCodec(
            Class<T> elemType,
            Codec<T, JsonIO.Input, JsonIO.Output> elemCodec) {
        return new Codec<Collection<T>, JsonIO.Input, JsonIO.Output>() {
            @Override
            public JsonIO.Output encode(Collection<T> vals, JsonIO.Output out) {
                out.startArray();
                for (T val : vals) {
                    elemCodec.encode(val, out);
                }
                return out.endArray();
            }

            @Override
            public Collection<T> decode(Class<Collection<T>> dynType, JsonIO.Input in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();
                final Collection<T> vals = getTypeConstructor(dynType).construct();

                in.startArray();

                while(in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                    vals.add(elemCodec.decode(dynElemType, in));
                }

                in.endArray();

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], JsonIO.Input, JsonIO.Output> objectArrayCodec(
            Class<T> elemType,
            Codec<T, JsonIO.Input, JsonIO.Output> elemCodec) {
        return new Codec<T[], JsonIO.Input, JsonIO.Output>() {
            @Override
            public JsonIO.Output encode(T[] vals, JsonIO.Output out) {
                out.startArray();
                for (T val : vals) {
                    elemCodec.encode(val, out);
                }
                return out.endArray();
            }

            @Override
            public T[] decode(Class<T[]> dynType, JsonIO.Input in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();
                T[] arr = (T[]) Array.newInstance(elemType, defaultArrSize());

                in.startArray();
                int i = 0;
                while (in.notEOF() && in.currentEventType() != JsonIO.Input.Type.ARRAY_END) {
                    if (i == arr.length) {
                        arr = Arrays.copyOf(arr, arr.length * 2);
                    }
                    arr[i++] = elemCodec.decode(dynElemType, in);
                }
                in.endArray();

                return Arrays.copyOf(arr, i);
            }
        };
    }

    @Override
    public <T> Codec<T, JsonIO.Input, JsonIO.Output> dynamicCodec(Class<T> stcType) {
        return new Codec<T, JsonIO.Input, JsonIO.Output>() {
            @Override
            public JsonIO.Output encode(T val, JsonIO.Output out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (dynType.equals(stcType)) {
                    return JsonCodecCoreImpl.this.createNullUnsafeCodecStc(stcType).encode(val, out);
                } else {
                    out.startArray();

                    out.writeStr(classToName(dynType));
                    encode2(JsonCodecCoreImpl.this.createNullUnsafeCodecDyn(dynType), val, out);

                    return out.endArray();
                }
            }

            protected <S extends T> JsonIO.Output encode2(
                    Codec<S, JsonIO.Input,JsonIO.Output> codec,
                    T val,
                    JsonIO.Output out) {
                return codec.encode((S)val, out);
            }

            @Override
            public T decode(JsonIO.Input in) {
                if (in.notEOF() && in.currentEventType() == JsonIO.Input.Type.ARRAY_START) {
                    // TODO: fix this.
                    final String typeFieldName = typeFieldName();
                    final String valueFieldName = valueFieldName();

                    in.startArray();

                    final String type = in.readStr();

                    final T val = decode2(in, nameToClass(type));
                    in.endArray();

                    return val;
                }

                final Codec<T, JsonIO.Input, JsonIO.Output> codec = JsonCodecCoreImpl.this.createNullUnsafeCodecStc(stcType);
                return codec.decode(stcType, in);
            }

            protected <S extends T> S decode2(JsonIO.Input in, Class<S> dynType) {
                final Codec<S, JsonIO.Input, JsonIO.Output> codec = JsonCodecCoreImpl.this.getNullUnsafeCodecDyn(dynType);
                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    public <T> Codec<T, JsonIO.Input, JsonIO.Output> dynamicCodec(
            Codec<T, JsonIO.Input, JsonIO.Output> codec,
            Class<T> stcType) {
        return new Codec<T, JsonIO.Input, JsonIO.Output>() {
            @Override
            public JsonIO.Output encode(T val, JsonIO.Output out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (dynType.equals(stcType)) {
                    return codec.encode(val, out);
                } else {
                    out.startArray();

                    out.writeStr(classToName(dynType));
                    codec.encode(val, out);

                    return out.endArray();
                }
            }

            @Override
            public T decode(JsonIO.Input in) {
                if (in.currentEventType() == JsonIO.Input.Type.ARRAY_START) {
                    in.startArray();

                    final String type = in.readStr();
                    final T val = codec.decode(nameToClass(type), in);

                    in.endArray();

                    return val;
                }

                return codec.decode(stcType, in);
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, JsonIO.Input, JsonIO.Output> createObjectCodec(
            ObjectMeta<T, JsonIO.Input, JsonIO.Output, RA> objMeta) {
        return new Codec<T, JsonIO.Input, JsonIO.Output>() {
            @Override
            public JsonIO.Output encode(T val, JsonIO.Output out) {
                out.startObject();

            objMeta.forEach(field -> {
                out.writeField(field.name());
                field.encodeField(val, out);
            });

                return out.endObject();
            }

            @Override
            public T decode(Class<T> dynType, JsonIO.Input in) {
                in.startObject();

                // TODO: improve
                final Map<String, ObjectMeta.Field<T, JsonIO.Input, JsonIO.Output, RA>> fields =
                        objMeta.stream()
                                .collect(toMap(
                                        ObjectMeta.Field::name,
                                        f -> f
                                ));

                final RA ra = objMeta.startDecode(dynType);

                while(in.notEOF() && in.currentEventType() != JsonIO.Input.Type.OBJECT_END) {
                    final String name = in.readFieldName();
                    fields.get(name).decodeField(ra, in);
                }

                in.endObject();

                return ra.construct();
            }
        };
    }
}
