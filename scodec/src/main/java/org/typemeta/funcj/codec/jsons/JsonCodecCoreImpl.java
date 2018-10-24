package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.functions.Functions;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

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

    @Override
    public <T> boolean encodeNull(T val, JsonIO.Output out) {
        if (val == null) {
            out.writeNull();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean decodeNull(JsonIO.Input in) {
        if (in.currentEventType().equals(JsonIO.Input.Event.Type.NULL)) {
            in.readNull();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public <T> boolean encodeDynamicType(
            Codec<T, JsonIO.Input, JsonIO.Output> codec,
            T val,
            JsonIO.Output out,
            Functions.F<Class<T>, Codec<T, JsonIO.Input, JsonIO.Output>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (dynType.equals(codec.type())) {
            return false;
        } else {
            final Codec<T, JsonIO.Input, JsonIO.Output> dynCodec = getDynCodec.apply(dynType);
            out.startObject();

            out.writeField(typeFieldName())
                    .writeStr(classToName(dynType));
            out.writeField(valueFieldName());
            dynCodec.encode(val, out);

            out.endObject();
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(JsonIO.Input in, Functions.F<String, T> decoder) {
        if (in.notEOF() && in.currentEventType() == JsonIO.Input.Event.Type.OBJECT_START) {
            final String typeFieldName = typeFieldName();
            final JsonIO.Input.Event.FieldName typeField = new JsonIO.Input.Event.FieldName(typeFieldName);
            final String valueFieldName = valueFieldName();

            final JsonIO.Input.Event next = in.event(1);
            if (next.equals(typeField)) {
                in.startObject();

                in.readFieldName();
                final String typeName = in.readStr();

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
    public <T> T decodeDynamicType(JsonIO.Input in) {
        return decodeDynamicType(in, name -> getCodec(this.<T>nameToClass(name)).decode(in));
    }

    protected final Codec.BooleanCodec<JsonIO.Input, JsonIO.Output> booleanCodec =
            new Codec.BooleanCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
            return JsonCodecCoreImpl.this;
        }

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

    protected final Codec<boolean[], JsonIO.Input, JsonIO.Output> booleanArrayCodec =
            new Codec.Base<boolean[], JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

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
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
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

    protected final Codec.ByteCodec<JsonIO.Input, JsonIO.Output> byteCodec =
            new Codec.ByteCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
            return JsonCodecCoreImpl.this;
        }

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

    protected final Codec<byte[], JsonIO.Input, JsonIO.Output> byteArrayCodec =
            new Codec.Base<byte[], JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

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
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
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

    protected final Codec.CharCodec<JsonIO.Input, JsonIO.Output> charCodec =
            new Codec.CharCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
            return JsonCodecCoreImpl.this;
        }

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

    protected final Codec<char[], JsonIO.Input, JsonIO.Output> charArrayCodec =
            new Codec.Base<char[], JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

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
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
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

    protected final Codec.ShortCodec<JsonIO.Input, JsonIO.Output> shortCodec =
            new Codec.ShortCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
            return JsonCodecCoreImpl.this;
        }

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

    protected final Codec<short[], JsonIO.Input, JsonIO.Output> shortArrayCodec =
            new Codec.Base<short[], JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

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
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
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

    protected final Codec.IntCodec<JsonIO.Input, JsonIO.Output> intCodec =
            new Codec.IntCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
            return JsonCodecCoreImpl.this;
        }

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

    protected final Codec<int[], JsonIO.Input, JsonIO.Output> intArrayCodec =
            new Codec.Base<int[], JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

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
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
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

    protected final Codec.LongCodec<JsonIO.Input, JsonIO.Output> longCodec =
            new Codec.LongCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
            return JsonCodecCoreImpl.this;
        }

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

    protected final Codec<long[], JsonIO.Input, JsonIO.Output> longArrayCodec =
            new Codec.Base<long[], JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

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
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
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

    protected final Codec.FloatCodec<JsonIO.Input, JsonIO.Output> floatCodec =
            new Codec.FloatCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
            return JsonCodecCoreImpl.this;
        }

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

    protected final Codec<float[], JsonIO.Input, JsonIO.Output> floatArrayCodec =
            new Codec.Base<float[], JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

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
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
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

    protected final Codec.DoubleCodec<JsonIO.Input, JsonIO.Output> doubleCodec =
            new Codec.DoubleCodec<JsonIO.Input, JsonIO.Output>() {

        @Override
        public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
            return JsonCodecCoreImpl.this;
        }

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

    protected final Codec<double[], JsonIO.Input, JsonIO.Output> doubleArrayCodec =
            new Codec.Base<double[], JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

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
            while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
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

    protected final Codec<String, JsonIO.Input, JsonIO.Output> stringCodec =
            new Codec.Base<String, JsonIO.Input, JsonIO.Output>(this) {

        @Override
        public Class<String> type() {
            return String.class;
        }

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
    public <EM extends Enum<EM>> Codec<EM, JsonIO.Input, JsonIO.Output> enumCodec(Class<EM> enumType) {
        return new Codec.Base<EM, JsonIO.Input, JsonIO.Output>(this) {
            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public JsonIO.Output encode(EM val, JsonIO.Output out) {
                return out.writeStr(val.name());
            }

            @Override
            public EM decode(JsonIO.Input in) {
                return EM.valueOf(type(), in.readStr());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, JsonIO.Input, JsonIO.Output> mapCodec(
            Class<Map<String, V>> type,
            Codec<V, JsonIO.Input, JsonIO.Output> valueCodec) {
        return new JsonMapCodecs.StringMapCodec<V>(this, type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, JsonIO.Input, JsonIO.Output> mapCodec(
            Class<Map<K, V>> type,
            Codec<K, JsonIO.Input, JsonIO.Output> keyCodec,
            Codec<V, JsonIO.Input, JsonIO.Output> valueCodec) {
        return new JsonMapCodecs.MapCodec<K, V>(this, type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, JsonIO.Input, JsonIO.Output> collCodec(
            Class<Collection<T>> collType,
            Codec<T, JsonIO.Input, JsonIO.Output> elemCodec) {
        return new CollectionCodec<T, JsonIO.Input, JsonIO.Output>(
            JsonCodecCoreImpl.this,
                elemCodec) {
            @Override
            public Class<Collection<T>> type() {
                return collType;
            }

            @Override
            public JsonIO.Output encode(Collection<T> vals, JsonIO.Output out) {
                out.startArray();
                for (T val : vals) {
                    elemCodec.encodeWithCheck(val, out);
                }
                return out.endArray();
            }

            @Override
            public Collection<T> decode(JsonIO.Input in) {
                final Collection<T> vals = getTypeConstructor(collType).construct();

                in.startArray();

                while(in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
                    vals.add(elemCodec.decodeWithCheck(in));
                }

                in.endArray();

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], JsonIO.Input, JsonIO.Output> objectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, JsonIO.Input, JsonIO.Output> elemCodec) {
        return new Codec<T[], JsonIO.Input, JsonIO.Output>() {
            @Override
            public CodecCoreIntl<JsonIO.Input, JsonIO.Output> core() {
                return JsonCodecCoreImpl.this;
            }

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public JsonIO.Output encode(T[] vals, JsonIO.Output out) {
                out.startArray();
                for (T val : vals) {
                    elemCodec.encodeWithCheck(val, out);
                }
                return out.endArray();
            }

            @Override
            public T[] decode(JsonIO.Input in) {
                T[] arr = (T[]) Array.newInstance(elemType, defaultArrSize());

                in.startArray();
                int i = 0;
                while (in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.ARRAY_END) {
                    if (i == arr.length) {
                        arr = Arrays.copyOf(arr, arr.length * 2);
                    }
                    arr[i++] = elemCodec.decodeWithCheck(in);
                }
                in.endArray();

                return Arrays.copyOf(arr, i);
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, JsonIO.Input, JsonIO.Output> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, JsonIO.Input, JsonIO.Output, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(this, type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(this, type, objMeta);
        }
    }

    private static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends Codec.Base<T, JsonIO.Input, JsonIO.Output> {

        private final Class<T> type;
        private final ObjectMeta<T, JsonIO.Input, JsonIO.Output, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, JsonIO.Input, JsonIO.Output, RA>> fields;

        private ObjectCodec(
                CodecCoreIntl<JsonIO.Input, JsonIO.Output> core,
                Class<T> type,
                ObjectMeta<T, JsonIO.Input, JsonIO.Output, RA> objMeta) {
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
        public JsonIO.Output encode(T val, JsonIO.Output out) {
            out.startObject();

            fields.forEach((name, field) -> {
                out.writeField(field.name());
                field.encodeField(val, out);
            });

            return out.endObject();
        }

        @Override
        public T decode(JsonIO.Input in) {
            in.startObject();

            final Set<String> expKeys = fields.keySet();
            final Set<String> setFields = new HashSet<>();
            final RA ra = objMeta.startDecode();

            while(in.notEOF() && in.currentEventType() != JsonIO.Input.Event.Type.OBJECT_END) {
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
                // TODO more informative error message.
                throw new CodecException("Encountered fields differs to expected fields for type " + type);
            }

            in.endObject();

            return ra.construct();
        }
    }

    private static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, JsonIO.Input, JsonIO.Output> {

        private FinalObjectCodec(
                CodecCoreIntl<JsonIO.Input, JsonIO.Output> core,
                Class<T> type,
                ObjectMeta<T, JsonIO.Input, JsonIO.Output, RA> objMeta) {
            super(core, type, objMeta);
        }
    }
}
