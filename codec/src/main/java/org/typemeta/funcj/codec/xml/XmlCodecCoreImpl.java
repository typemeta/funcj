package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.xml.io.XmlIO;
import org.typemeta.funcj.functions.Functions;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

@SuppressWarnings("unchecked")
public class XmlCodecCoreImpl extends BaseCodecCore<XmlIO.Input, XmlIO.Output> implements XmlCodecCore {

    public XmlCodecCoreImpl() {
    }

    @Override
    public <T> XmlIO.Output encode(
            Class<T> type,
            T val,
            Writer wtr,
            String rootElemName) {
        final  XmlIO.Output out = encode(type, val, XmlIO.of(wtr, rootElemName));
        return out.close();
    }

    @Override
    public <T> T decode(
            Class<T> type,
            Reader rdr,
            String rootElemName) {
        return decode(type, XmlIO.of(rdr, rootElemName));
    }

    public String entryElemName() {
        return "_";
    }

    public String typeAttrName() {
        return "type";
    }

    public String keyAttrName() {
        return "key";
    }

    public String keyElemName() {
        return "key";
    }

    public String valueElemName() {
        return "value";
    }

    public String metaAttrName() {
        return "meta";
    }

    public String nullAttrVal() {
        return "null";
    }

    protected int defaultArrSize() {
        return 16;
    }

    public <T> XmlIO.Output encode(T val) {
        return encode((Class<T>)val.getClass(), val);
    }

    public <T> XmlIO.Output encode(Class<T> type, T val) {
        return super.encode(type, val, null);
    }

    @Override
    public String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "_" + name;
        }
        return name;
    }

    @Override
    public <T> boolean encodeNull(T val, XmlIO.Output out) {
        if (val == null) {
            out.attribute(metaAttrName(), nullAttrVal());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean decodeNull(XmlIO.Input in) {
        return in.attributeMap().nameHasValue(metaAttrName(), nullAttrVal());
    }

    @Override
    public <T> boolean encodeDynamicType(
            Codec<T, XmlIO.Input, XmlIO.Output> codec,
            T val,
            XmlIO.Output out,
            Functions.F<Class<T>, Codec<T, XmlIO.Input, XmlIO.Output>> getDynCodec) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (dynType.equals(codec.type())) {
            return false;
        } else {
            final Codec<T, XmlIO.Input, XmlIO.Output> dynCodec = getDynCodec.apply(dynType);
            out.attribute(typeAttrName(), classToName(dynType));
            dynCodec.encode(val, out);
            return true;
        }
    }

    @Override
    public <T> T decodeDynamicType(XmlIO.Input in, Functions.F<String, T> decoder) {
        if (in.attributeMap().hasName(typeAttrName())) {
            final String typeName = in.attributeMap().getValue(typeAttrName());
            return decoder.apply(typeName);
        } else {
            return null;
        }
    }

    @Override
    public <T> T decodeDynamicType(XmlIO.Input in) {
        return decodeDynamicType(in, name -> getCodec(this.<T>nameToClass(name)).decode(in));
    }

    protected final Codec.BooleanCodec<XmlIO.Input, XmlIO.Output> booleanCodec =
            new Codec.BooleanCodec<XmlIO.Input, XmlIO.Output>() {

        @Override
        public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public XmlIO.Output encodePrim(boolean val, XmlIO.Output out) {
            return out.writeBool(val);
        }

        @Override
        public boolean decodePrim(XmlIO.Input in) {
            return in.readBool();
        }
    };

    @Override
    public Codec.BooleanCodec<XmlIO.Input, XmlIO.Output> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], XmlIO.Input, XmlIO.Output> booleanArrayCodec =
            new Codec.Base<boolean[], XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public XmlIO.Output encode(boolean[] vals, XmlIO.Output out) {
            for (boolean val : vals) {
                booleanCodec().encode(val, out.startElement(entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public boolean[] decode(XmlIO.Input in) {
            boolean[] arr = new boolean[defaultArrSize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(entryElemName());
                arr[i++] = booleanCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<boolean[], XmlIO.Input, XmlIO.Output> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<XmlIO.Input, XmlIO.Output> byteCodec =
            new Codec.ByteCodec<XmlIO.Input, XmlIO.Output>() {

        @Override
        public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public XmlIO.Output encodePrim(byte val, XmlIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public byte decodePrim(XmlIO.Input in) {
            return in.readByte();
        }
    };

    @Override
    public Codec.ByteCodec<XmlIO.Input, XmlIO.Output> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], XmlIO.Input, XmlIO.Output> byteArrayCodec =
            new Codec.Base<byte[], XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public XmlIO.Output encode(byte[] vals, XmlIO.Output out) {
            for (byte val : vals) {
                byteCodec().encode(val, out.startElement(entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public byte[] decode(XmlIO.Input in) {
            byte[] arr = new byte[defaultArrSize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(entryElemName());
                arr[i++] = byteCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<byte[], XmlIO.Input, XmlIO.Output> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<XmlIO.Input, XmlIO.Output> charCodec =
            new Codec.CharCodec<XmlIO.Input, XmlIO.Output>() {

        @Override
        public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public XmlIO.Output encodePrim(char val, XmlIO.Output out) {
            return out.writeChar(val);
        }

        @Override
        public char decodePrim(XmlIO.Input in ) {
            return in.readChar();
        }
    };

    @Override
    public Codec.CharCodec<XmlIO.Input, XmlIO.Output> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], XmlIO.Input, XmlIO.Output> charArrayCodec =
            new Codec.Base<char[], XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public XmlIO.Output encode(char[] vals, XmlIO.Output out) {
            for (char val : vals) {
                charCodec().encode(val, out.startElement(entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public char[] decode(XmlIO.Input in) {
            char[] arr = new char[defaultArrSize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(entryElemName());
                arr[i++] = charCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<char[], XmlIO.Input, XmlIO.Output> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<XmlIO.Input, XmlIO.Output> shortCodec =
            new Codec.ShortCodec<XmlIO.Input, XmlIO.Output>() {

        @Override
        public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public XmlIO.Output encodePrim(short val, XmlIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public short decodePrim(XmlIO.Input in ) {
            return in.readShort();
        }
    };

    @Override
    public Codec.ShortCodec<XmlIO.Input, XmlIO.Output> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], XmlIO.Input, XmlIO.Output> shortArrayCodec =
            new Codec.Base<short[], XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public XmlIO.Output encode(short[] vals, XmlIO.Output out) {
            for (short val : vals) {
                shortCodec().encode(val, out.startElement(entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public short[] decode(XmlIO.Input in) {
            short[] arr = new short[defaultArrSize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(entryElemName());
                arr[i++] = shortCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<short[], XmlIO.Input, XmlIO.Output> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<XmlIO.Input, XmlIO.Output> intCodec =
            new Codec.IntCodec<XmlIO.Input, XmlIO.Output>() {

        @Override
        public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public XmlIO.Output encodePrim(int val, XmlIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public int decodePrim(XmlIO.Input in ) {
            return in.readInt();
        }
    };

    @Override
    public Codec.IntCodec<XmlIO.Input, XmlIO.Output> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], XmlIO.Input, XmlIO.Output> intArrayCodec =
            new Codec.Base<int[], XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public XmlIO.Output encode(int[] vals, XmlIO.Output out) {
            for (int val : vals) {
                intCodec().encode(val, out.startElement(entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public int[] decode(XmlIO.Input in) {
            int[] arr = new int[defaultArrSize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(entryElemName());
                arr[i++] = intCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<int[], XmlIO.Input, XmlIO.Output> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<XmlIO.Input, XmlIO.Output> longCodec =
            new Codec.LongCodec<XmlIO.Input, XmlIO.Output>() {

        @Override
        public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public XmlIO.Output encodePrim(long val, XmlIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public long decodePrim(XmlIO.Input in) {
            return in.readLong();
        }
    };

    @Override
    public Codec.LongCodec<XmlIO.Input, XmlIO.Output> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], XmlIO.Input, XmlIO.Output> longArrayCodec =
            new Codec.Base<long[], XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public XmlIO.Output encode(long[] vals, XmlIO.Output out) {
            for (long val : vals) {
                longCodec().encode(val, out.startElement(entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public long[] decode(XmlIO.Input in) {
            long[] arr = new long[defaultArrSize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(entryElemName());
                arr[i++] = longCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<long[], XmlIO.Input, XmlIO.Output> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<XmlIO.Input, XmlIO.Output> floatCodec =
            new Codec.FloatCodec<XmlIO.Input, XmlIO.Output>() {

        @Override
        public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public XmlIO.Output encodePrim(float val, XmlIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public float decodePrim(XmlIO.Input in ) {
            return in.readFloat();
        }
    };

    @Override
    public Codec.FloatCodec<XmlIO.Input, XmlIO.Output> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], XmlIO.Input, XmlIO.Output> floatArrayCodec =
            new Codec.Base<float[], XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public XmlIO.Output encode(float[] vals, XmlIO.Output out) {
            for (float val : vals) {
                floatCodec().encode(val, out.startElement(entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public float[] decode(XmlIO.Input in) {
            float[] arr = new float[defaultArrSize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(entryElemName());
                arr[i++] = floatCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<float[], XmlIO.Input, XmlIO.Output> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<XmlIO.Input, XmlIO.Output> doubleCodec =
            new Codec.DoubleCodec<XmlIO.Input, XmlIO.Output>() {

        @Override
        public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
            return XmlCodecCoreImpl.this;
        }

        @Override
        public XmlIO.Output encodePrim(double val, XmlIO.Output out) {
            return out.writeNum(val);
        }

        @Override
        public double decodePrim(XmlIO.Input in ) {
            return in.readDbl();
        }
    };

    @Override
    public Codec.DoubleCodec<XmlIO.Input, XmlIO.Output> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], XmlIO.Input, XmlIO.Output> doubleArrayCodec =
            new Codec.Base<double[], XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public XmlIO.Output encode(double[] vals, XmlIO.Output out) {
            for (double val : vals) {
                doubleCodec().encode(val, out.startElement(entryElemName()));
                out.endElement();
            }
            return out;
        }

        @Override
        public double[] decode(XmlIO.Input in) {
            double[] arr = new double[defaultArrSize()];
            int i = 0;
            while (in.hasNext()) {
                if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                    break;
                }

                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, arr.length * 2);
                }

                in.startElement(entryElemName());
                arr[i++] = doubleCodec().decode(in);
                in.endElement();
            }
            return Arrays.copyOf(arr, i);
        }
    };

    @Override
    public Codec<double[], XmlIO.Input, XmlIO.Output> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, XmlIO.Input, XmlIO.Output> stringCodec =
            new Codec.Base<String, XmlIO.Input, XmlIO.Output>(this) {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public XmlIO.Output encode(String val, XmlIO.Output out) {
            return out.writeStr(val);
        }

        @Override
        public String decode(XmlIO.Input in) {
            return in.readStr();
        }
    };

    @Override
    public Codec<String, XmlIO.Input, XmlIO.Output> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, XmlIO.Input, XmlIO.Output> enumCodec(Class<EM> enumType) {
        return new Codec.Base<EM, XmlIO.Input, XmlIO.Output>(this) {
            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public XmlIO.Output encode(EM val, XmlIO.Output out) {
                return out.writeStr(val.name());
            }

            @Override
            public EM decode(XmlIO.Input in) {
                return EM.valueOf(type(), in.readStr());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, XmlIO.Input, XmlIO.Output> mapCodec(
            Class<Map<String, V>> type,
            Codec<V, XmlIO.Input, XmlIO.Output> valueCodec) {
        return new XmlMapCodecs.StringMapCodec<V>(this, type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, XmlIO.Input, XmlIO.Output> mapCodec(
            Class<Map<K, V>> type,
            Codec<K, XmlIO.Input, XmlIO.Output> keyCodec,
            Codec<V, XmlIO.Input, XmlIO.Output> valueCodec) {
        return new XmlMapCodecs.MapCodec<K, V>(this, type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, XmlIO.Input, XmlIO.Output> collCodec(
            Class<Collection<T>> collType,
            Codec<T, XmlIO.Input, XmlIO.Output> elemCodec) {
        return new CollectionCodec<T, XmlIO.Input, XmlIO.Output>(
            XmlCodecCoreImpl.this,
                elemCodec) {
            @Override
            public Class<Collection<T>> type() {
                return collType;
            }

            @Override
            public XmlIO.Output encode(Collection<T> vals, XmlIO.Output out) {
                for (T val : vals) {
                    elemCodec.encodeWithCheck(val, out.startElement(entryElemName()));
                    out.endElement();
                }
                return out;
            }

            @Override
            public Collection<T> decode(XmlIO.Input in) {
                final Collection<T> vals = getTypeConstructor(collType).construct();

                while (in.hasNext()) {
                    if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                        break;
                    }

                    in.startElement(entryElemName());
                    vals.add(elemCodec.decodeWithCheck(in));
                    in.endElement();
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], XmlIO.Input, XmlIO.Output> objectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, XmlIO.Input, XmlIO.Output> elemCodec) {
        return new Codec<T[], XmlIO.Input, XmlIO.Output>() {
            @Override
            public CodecCoreIntl<XmlIO.Input, XmlIO.Output> core() {
                return XmlCodecCoreImpl.this;
            }

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public XmlIO.Output encode(T[] vals, XmlIO.Output out) {
                for (T val : vals) {
                    elemCodec.encodeWithCheck(val, out.startElement(entryElemName()));
                    out.endElement();
                }
                return out;
            }

            @Override
            public T[] decode(XmlIO.Input in) {
                T[] arr = (T[]) Array.newInstance(elemCodec.type(), defaultArrSize());
                int i = 0;
                while (in.hasNext()) {
                    if (!in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                        break;
                    }

                    if (i == arr.length) {
                        arr = Arrays.copyOf(arr, arr.length * 2);
                    }

                    in.startElement(entryElemName());
                    arr[i++] = elemCodec.decodeWithCheck(in);
                    in.endElement();
                }
                return Arrays.copyOf(arr, i);
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.ResultAccumlator<T>> Codec<T, XmlIO.Input, XmlIO.Output> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, XmlIO.Input, XmlIO.Output, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(this, type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(this, type, objMeta);
        }
    }

    private static class ObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends Codec.Base<T, XmlIO.Input, XmlIO.Output> {

        private final Class<T> type;
        private final ObjectMeta<T, XmlIO.Input, XmlIO.Output, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, XmlIO.Input, XmlIO.Output, RA>> fields;

        private ObjectCodec(
                CodecCoreIntl<XmlIO.Input, XmlIO.Output> core,
                Class<T> type,
                ObjectMeta<T, XmlIO.Input, XmlIO.Output, RA> objMeta) {
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
        public XmlIO.Output encode(T val, XmlIO.Output out) {
            fields.forEach((name, field) -> {
                field.encodeField(val, out.startElement(field.name()));
                out.endElement();
            });

            return out;
        }

        @Override
        public T decode(XmlIO.Input in) {
            final Set<String> expKeys = fields.keySet();
            final Set<String> setFields = new HashSet<>();
            final RA ra = objMeta.startDecode();

            while(in.hasNext() && in.type().equals(XmlIO.Input.Type.START_ELEMENT)) {
                final String name = in.startElement();
                if (!expKeys.contains(name)) {
                    throw new CodecException("Field name '" + name + "' unexpected for type " + type +
                                                     " at location " + in.location());
                } else if (setFields.contains(name)) {
                    throw new CodecException("Duplicate field name '" + name + "' for type " + type +
                                                     " at location " + in.location());
                }

                setFields.add(name);
                fields.get(name).decodeField(ra, in);
                in.endElement();
            }

            if (!setFields.equals(expKeys)) {
                // TODO more informative error message.
                throw new CodecException("Encountered fields differs to expected fields for type " + type +
                                                 " at location " + in.location());
            }

            return ra.construct();
        }
    }

    private static class FinalObjectCodec<T, RA extends ObjectMeta.ResultAccumlator<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, XmlIO.Input, XmlIO.Output> {

        private FinalObjectCodec(
                CodecCoreIntl<XmlIO.Input, XmlIO.Output> core,
                Class<T> type,
                ObjectMeta<T, XmlIO.Input, XmlIO.Output, RA> objMeta) {
            super(core, type, objMeta);
        }
    }
}
