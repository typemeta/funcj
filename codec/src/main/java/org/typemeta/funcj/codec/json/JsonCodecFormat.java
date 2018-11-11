package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.json.JsonCodec.*;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.codec.json.JsonCodec.*;
import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

/**
 * Encoding via JSON streams.
 */
@SuppressWarnings("unchecked")
public class JsonCodecFormat implements CodecFormat<Input, Output, Config> {

    protected final Config config;

    public JsonCodecFormat(Config config) {
        this.config = config;
    }

    @Override
    public Config config() {
        return config;
    }

    @Override
    public <T> boolean encodeNull(T val, Output out) {
        if (val == null) {
            out.writeNull();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean decodeNull(Input in) {
        if (in.currentEventType().equals(Input.Event.Type.NULL)) {
            in.readNull();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public <T> boolean encodeDynamicType(
            CodecCoreEx<Input, Output, Config> core,
            Codec<T, Input, Output, Config> codec,
            T val,
            Output out,
            Functions.F<Class<T>, Codec<T, Input, Output, Config>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (config().dynamicTypeMatch(dynType, codec.type())) {
            return false;
        } else {
            final Codec<T, Input, Output, Config> dynCodec = getDynCodec.apply(dynType);
            out.startObject();

            out.writeField(config.typeFieldName())
                    .writeString(config().classToName(dynType));
            out.writeField(config.valueFieldName());
            dynCodec.encode(core, val, out);

            out.endObject();
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(Input in, Functions.F<String, T> decoder) {
        if (in.notEOF() && in.currentEventType() == Input.Event.Type.OBJECT_START) {
            final String typeFieldName = config.typeFieldName();
            final Input.Event.FieldName typeField = new Input.Event.FieldName(typeFieldName);
            final String valueFieldName = config.valueFieldName();

            final Input.Event next = in.event(1);
            if (next.equals(typeField)) {
                in.startObject();

                in.readFieldName(typeFieldName);
                final String typeName = in.readString();

                final String field2 = in.readFieldName();
                if (!field2.equals(valueFieldName)) {
                    throw new CodecException("Was expecting field '" + valueFieldName + "' but got '" + field2 + "'");
                }

                final T val = decoder.apply(typeName);

                in.endObject();

                return val;
            }
        }
        return null;
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
            out.startArray();
            for (boolean val : value) {
                booleanCodec().encode(core, val, out);
            }
            return out.endArray();
        }

        @Override
        public boolean[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            boolean[] arr = new boolean[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = booleanCodec().decode(core, in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
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
            out.startArray();
            for (byte val : value) {
                byteCodec().encode(core, val, out);
            }
            return out.endArray();
        }

        @Override
        public byte[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            byte[] arr = new byte[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = byteCodec().decode(core, in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
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
            out.startArray();
            for (char val : value) {
                charCodec().encode(core, val, out);
            }
            return out.endArray();
        }

        @Override
        public char[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            char[] arr = new char[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = charCodec().decode(core, in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
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
            out.startArray();
            for (short val : value) {
                shortCodec().encode(core, val, out);
            }
            return out.endArray();
        }

        @Override
        public short[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            short[] arr = new short[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = shortCodec().decode(core, in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
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
            return out.writeint(val);
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
            out.startArray();
            for (int val : value) {
                intCodec().encode(core, val, out);
            }
            return out.endArray();
        }

        @Override
        public int[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            int[] arr = new int[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = intCodec().decode(core, in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
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
            out.startArray();
            for (long val : value) {
                longCodec().encode(core, val, out);
            }
            return out.endArray();
        }

        @Override
        public long[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            long[] arr = new long[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = longCodec().decode(core, in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
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
            out.startArray();
            for (float val : value) {
                floatCodec().encode(core, val, out);
            }
            return out.endArray();
        }

        @Override
        public float[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            float[] arr = new float[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = floatCodec().decode(core, in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
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
            out.startArray();
            for (double val : value) {
                doubleCodec().encode(core, val, out);
            }
            return out.endArray();
        }

        @Override
        public double[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            double[] arr = new double[config.defaultArraySize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }
                arr[i++] = doubleCodec().decode(core, in);
            }
            in.endArray();
            return Arrays.copyOf(arr, i);
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
        return new JsonMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Input, Output, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, Input, Output, Config> keyCodec,
            Codec<V, Input, Output, Config> valueCodec) {
        return new JsonMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
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
                out.startArray();
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out.endArray();
            }

            @Override
            public Collection<T> decode(CodecCoreEx<Input, Output, Config> core, Input in) {
                final CollProxy<T> collProxy = getCollectionProxy(core, collType);

                in.startArray();

                while(in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                    collProxy.add(elemCodec.decodeWithCheck(core, in));
                }

                in.endArray();

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
                out.startArray();
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, out);
                }
                return out.endArray();
            }

            @Override
            public T[] decode(CodecCoreEx<Input, Output, Config> core, Input in) {
                T[] arr = (T[]) Array.newInstance(elemType, config.defaultArraySize());

                in.startArray();
                int i = 0;
                while (in.notEOF() && in.currentEventType() != Input.Event.Type.ARRAY_END) {
                    if (i == arr.length) {
                        arr = Arrays.copyOf(arr, arr.length * 2);
                    }
                    arr[i++] = elemCodec.decodeWithCheck(core, in);
                }
                in.endArray();

                return Arrays.copyOf(arr, i);
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
        private final Map<String, ObjectMeta.Field<T, Input, Output, RA>> fields;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, Input, Output, RA> objMeta) {
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
        public Output encode(CodecCoreEx<Input, Output, Config> core, T value, Output out) {
            out.startObject();

            fields.forEach((name, field) -> {
                out.writeField(field.name());
                field.encodeField(value, out);
            });

            return out.endObject();
        }

        @Override
        public T decode(CodecCoreEx<Input, Output, Config> core, Input in) {
            in.startObject();

            final Set<String> expKeys = fields.keySet();
            final Set<String> setFields = new HashSet<>();
            final RA ra = objMeta.startDecode();

            while (in.notEOF() && in.currentEventType() != Input.Event.Type.OBJECT_END) {
                final String name = in.readFieldName();
                if (!expKeys.contains(name)) {
                    throw new CodecException("Field name '" + name + "' unexpected for type " + type);
                } else if (setFields.contains(name)) {
                    throw new CodecException("Duplicate field name '" + name + "' for type " + type);
                }
                setFields.add(name);
                fields.get(name).decodeField(ra, in);
            }

            if (!setFields.equals(expKeys)) {
                // TODO: more informative error message.
                throw new CodecException("Encountered fields differs to expected fields for type " + type);
            }

            in.endObject();

            return ra.construct();
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
