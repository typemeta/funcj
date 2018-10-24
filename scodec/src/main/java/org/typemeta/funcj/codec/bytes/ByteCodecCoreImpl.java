package org.typemeta.funcj.codec.bytes;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.jsons.JsonIO;
import org.typemeta.funcj.codec.jsons.JsonMapCodecs;
import org.typemeta.funcj.functions.Functions;

import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

@SuppressWarnings("unchecked")
public class ByteCodecCoreImpl extends BaseCodecCore<ByteIO.Input, ByteIO.Output> implements ByteCodecCore {

    public ByteCodecCoreImpl() {
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
    public <T> boolean encodeNull(T val, ByteIO.Output out) {
        final boolean isNull = val == null;
        out.writeBoolean(isNull);
        return isNull;
    }

    @Override
    public boolean decodeNull(ByteIO.Input in) {
        return in.readBoolean();
    }

    @Override
    public <T> boolean encodeDynamicType(
            Codec<T, ByteIO.Input, ByteIO.Output> codec,
            T val,
            ByteIO.Output out,
            Functions.F<Class<T>, Codec<T, ByteIO.Input, ByteIO.Output>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (dynType.equals(codec.type())) {
            out.writeBoolean(false);
            return false;
        } else {
            out.writeBoolean(true);
            final Codec<T, ByteIO.Input, ByteIO.Output> dynCodec = getDynCodec.apply(dynType);
            out.writeString(classToName(dynType));
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(ByteIO.Input in, Functions.F<String, T> decoder) {
        if (in.readBoolean()) {
            final String typeName = in.readString();
            return decoder.apply(typeName);
        } else {
            return null;
        }
    }

    @Override
    public <T> T decodeDynamicType(ByteIO.Input in) {
        return decodeDynamicType(in, name -> getCodec(this.<T>nameToClass(name)).decode(in));
    }

    protected final Codec.BooleanCodec<ByteIO.Input, ByteIO.Output> booleanCodec =
            new Codec.BooleanCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public ByteIO.Output encodePrim(boolean val, ByteIO.Output out) {
            return out.writeBoolean(val);
        }

        @Override
        public boolean decodePrim(ByteIO.Input in) {
            return in.readBoolean();
        }
    };

    @Override
    public Codec.BooleanCodec<ByteIO.Input, ByteIO.Output> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], ByteIO.Input, ByteIO.Output> booleanArrayCodec =
            new Codec.Base<boolean[], ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public ByteIO.Output encode(boolean[] vals, ByteIO.Output out) {
            out.startArray();
            for (boolean val : vals) {
                booleanCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public boolean[] decode(ByteIO.Input in) {
            boolean[] arr = new boolean[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public Codec<boolean[], ByteIO.Input, ByteIO.Output> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<ByteIO.Input, ByteIO.Output> byteCodec =
            new Codec.ByteCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public ByteIO.Output encodePrim(byte val, ByteIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public byte decodePrim(ByteIO.Input in) {
            return in.readByte();
        }
    };

    @Override
    public Codec.ByteCodec<ByteIO.Input, ByteIO.Output> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], ByteIO.Input, ByteIO.Output> byteArrayCodec =
            new Codec.Base<byte[], ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public ByteIO.Output encode(byte[] vals, ByteIO.Output out) {
            out.startArray();
            for (byte val : vals) {
                byteCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public byte[] decode(ByteIO.Input in) {
            byte[] arr = new byte[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public Codec<byte[], ByteIO.Input, ByteIO.Output> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<ByteIO.Input, ByteIO.Output> charCodec =
            new Codec.CharCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public ByteIO.Output encodePrim(char val, ByteIO.Output out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(ByteIO.Input in ) {
            return in.readChar();
        }
    };

    @Override
    public Codec.CharCodec<ByteIO.Input, ByteIO.Output> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], ByteIO.Input, ByteIO.Output> charArrayCodec =
            new Codec.Base<char[], ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public ByteIO.Output encode(char[] vals, ByteIO.Output out) {
            out.startArray();
            for (char val : vals) {
                charCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public char[] decode(ByteIO.Input in) {
            char[] arr = new char[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public Codec<char[], ByteIO.Input, ByteIO.Output> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<ByteIO.Input, ByteIO.Output> shortCodec =
            new Codec.ShortCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public ByteIO.Output encodePrim(short val, ByteIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public short decodePrim(ByteIO.Input in ) {
            return in.readShort();
        }
    };

    @Override
    public Codec.ShortCodec<ByteIO.Input, ByteIO.Output> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], ByteIO.Input, ByteIO.Output> shortArrayCodec =
            new Codec.Base<short[], ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public ByteIO.Output encode(short[] vals, ByteIO.Output out) {
            out.startArray();
            for (short val : vals) {
                shortCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public short[] decode(ByteIO.Input in) {
            short[] arr = new short[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public Codec<short[], ByteIO.Input, ByteIO.Output> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<ByteIO.Input, ByteIO.Output> intCodec =
            new Codec.IntCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public ByteIO.Output encodePrim(int val, ByteIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public int decodePrim(ByteIO.Input in ) {
            return in.readInt();
        }
    };

    @Override
    public Codec.IntCodec<ByteIO.Input, ByteIO.Output> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], ByteIO.Input, ByteIO.Output> intArrayCodec =
            new Codec.Base<int[], ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public ByteIO.Output encode(int[] vals, ByteIO.Output out) {
            out.startArray();
            for (int val : vals) {
                intCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public int[] decode(ByteIO.Input in) {
            int[] arr = new int[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public Codec<int[], ByteIO.Input, ByteIO.Output> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<ByteIO.Input, ByteIO.Output> longCodec =
            new Codec.LongCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public ByteIO.Output encodePrim(long val, ByteIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public long decodePrim(ByteIO.Input in) {
            return in.readLong();
        }
    };

    @Override
    public Codec.LongCodec<ByteIO.Input, ByteIO.Output> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], ByteIO.Input, ByteIO.Output> longArrayCodec =
            new Codec.Base<long[], ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public ByteIO.Output encode(long[] vals, ByteIO.Output out) {
            out.startArray();
            for (long val : vals) {
                longCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public long[] decode(ByteIO.Input in) {
            long[] arr = new long[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public Codec<long[], ByteIO.Input, ByteIO.Output> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<ByteIO.Input, ByteIO.Output> floatCodec =
            new Codec.FloatCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public ByteIO.Output encodePrim(float val, ByteIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public float decodePrim(ByteIO.Input in ) {
            return in.readFloat();
        }
    };

    @Override
    public Codec.FloatCodec<ByteIO.Input, ByteIO.Output> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], ByteIO.Input, ByteIO.Output> floatArrayCodec =
            new Codec.Base<float[], ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public ByteIO.Output encode(float[] vals, ByteIO.Output out) {
            out.startArray();
            for (float val : vals) {
                floatCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public float[] decode(ByteIO.Input in) {
            float[] arr = new float[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public Codec<float[], ByteIO.Input, ByteIO.Output> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<ByteIO.Input, ByteIO.Output> doubleCodec =
            new Codec.DoubleCodec<ByteIO.Input, ByteIO.Output>() {

        @Override
        public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
            return ByteCodecCoreImpl.this;
        }

        @Override
        public ByteIO.Output encodePrim(double val, ByteIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public double decodePrim(ByteIO.Input in ) {
            return in.readDbl();
        }
    };

    @Override
    public Codec.DoubleCodec<ByteIO.Input, ByteIO.Output> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], ByteIO.Input, ByteIO.Output> doubleArrayCodec =
            new Codec.Base<double[], ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public ByteIO.Output encode(double[] vals, ByteIO.Output out) {
            out.startArray();
            for (double val : vals) {
                doubleCodec().encode(val, out);
            }
            return out.endArray();
        }

        @Override
        public double[] decode(ByteIO.Input in) {
            double[] arr = new double[defaultArrSize()];
            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public Codec<double[], ByteIO.Input, ByteIO.Output> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, ByteIO.Input, ByteIO.Output> stringCodec =
            new Codec.Base<String, ByteIO.Input, ByteIO.Output>(this) {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public ByteIO.Output encode(String val, ByteIO.Output out) {
            return out.writeStr(val);
        }

        @Override
        public String decode(ByteIO.Input in) {
            return in.readStr();
        }
    };

    @Override
    public Codec<String, ByteIO.Input, ByteIO.Output> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, ByteIO.Input, ByteIO.Output> enumCodec(Class<EM> enumType) {
        return new Codec.Base<EM, ByteIO.Input, ByteIO.Output>(this) {
            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public ByteIO.Output encode(EM val, ByteIO.Output out) {
                return out.writeStr(val.name());
            }

            @Override
            public EM decode(ByteIO.Input in) {
                return EM.valueOf(type(), in.readStr());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, ByteIO.Input, ByteIO.Output> mapCodec(
            Class<Map<String, V>> type,
            Codec<V, ByteIO.Input, ByteIO.Output> valueCodec) {
        return new JsonMapCodecs.StringMapCodec<V>(this, type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, ByteIO.Input, ByteIO.Output> mapCodec(
            Class<Map<K, V>> type,
            Codec<K, ByteIO.Input, ByteIO.Output> keyCodec,
            Codec<V, ByteIO.Input, ByteIO.Output> valueCodec) {
        return new JsonMapCodecs.MapCodec<K, V>(this, type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, ByteIO.Input, ByteIO.Output> collCodec(
            Class<Collection<T>> collType,
            Codec<T, ByteIO.Input, ByteIO.Output> elemCodec) {
        return new CollectionCodec<T, ByteIO.Input, ByteIO.Output>(
            ByteCodecCoreImpl.this,
                elemCodec) {
            @Override
            public Class<Collection<T>> type() {
                return collType;
            }

            @Override
            public ByteIO.Output encode(Collection<T> vals, ByteIO.Output out) {
                out.startArray();
                for (T val : vals) {
                    elemCodec.encodeWithCheck(val, out);
                }
                return out.endArray();
            }

            @Override
            public Collection<T> decode(ByteIO.Input in) {
                final Collection<T> vals = getTypeConstructor(collType).construct();

                in.startArray();

                while(in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
                    vals.add(elemCodec.decodeWithCheck(in));
                }

                in.endArray();

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], ByteIO.Input, ByteIO.Output> objectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, ByteIO.Input, ByteIO.Output> elemCodec) {
        return new Codec<T[], ByteIO.Input, ByteIO.Output>() {
            @Override
            public CodecCoreIntl<ByteIO.Input, ByteIO.Output> core() {
                return ByteCodecCoreImpl.this;
            }

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public ByteIO.Output encode(T[] vals, ByteIO.Output out) {
                out.startArray();
                for (T val : vals) {
                    elemCodec.encodeWithCheck(val, out);
                }
                return out.endArray();
            }

            @Override
            public T[] decode(ByteIO.Input in) {
                T[] arr = (T[]) Array.newInstance(elemType, defaultArrSize());

                in.startArray();
                int i = 0;
                while (in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.ARRAY_END) {
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
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, ByteIO.Input, ByteIO.Output> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, ByteIO.Input, ByteIO.Output, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(this, type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(this, type, objMeta);
        }
    }

    private static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends Codec.Base<T, ByteIO.Input, ByteIO.Output> {

        private final Class<T> type;
        private final ObjectMeta<T, ByteIO.Input, ByteIO.Output, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, ByteIO.Input, ByteIO.Output, RA>> fields;

        private ObjectCodec(
                CodecCoreIntl<ByteIO.Input, ByteIO.Output> core,
                Class<T> type,
                ObjectMeta<T, ByteIO.Input, ByteIO.Output, RA> objMeta) {
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
        public ByteIO.Output encode(T val, ByteIO.Output out) {
            out.startObject();

            fields.forEach((name, field) -> {
                out.writeField(field.name());
                field.encodeField(val, out);
            });

            return out.endObject();
        }

        @Override
        public T decode(ByteIO.Input in) {
            in.startObject();

            final Set<String> expKeys = fields.keySet();
            final Set<String> setFields = new HashSet<>();
            final RA ra = objMeta.startDecode();

            while(in.notEOF() && in.currentEventType() != ByteIO.Input.Event.Type.OBJECT_END) {
                final String name = in.readFieldName();
                if (expKeys.contains(name)) {
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
            implements Codec.FinalCodec<T, ByteIO.Input, ByteIO.Output> {

        private FinalObjectCodec(
                CodecCoreIntl<ByteIO.Input, ByteIO.Output> core,
                Class<T> type,
                ObjectMeta<T, ByteIO.Input, ByteIO.Output, RA> objMeta) {
            super(core, type, objMeta);
        }
    }
}
