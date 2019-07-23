package org.typemeta.funcj.codec.xmlnode;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.impl.CollectionCodec;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.codec.xmlnode.XmlNodeTypes.Config;
import org.typemeta.funcj.functions.Functions;
import org.w3c.dom.*;

import java.lang.reflect.*;
import java.util.*;

import static org.typemeta.funcj.codec.utils.StreamUtils.toLinkedHashMap;

/**
 * Encoding via XML streams.
 */
@SuppressWarnings("unchecked")
public class XmlNodeCodecFormat implements CodecFormat<Element, Element, Config> {

    protected final Config config;

    public XmlNodeCodecFormat(Config config) {
        this.config = config;
    }

    @Override
    public Config config() {
        return config;
    }

    protected Element addEntryElement(Element parent) {
        return XmlUtils.addElement(parent, config().entryElemName());
    }

    @Override
    public <T> WasEncoded<Element> encodeNull(T val, Element out) {
        if (val == null) {
            XmlUtils.setAttrValue(out, config().nullAttrName(), config().nullAttrVal());
            return WasEncoded.of(true, out);
        } else {
            return WasEncoded.of(false, out);
        }
    }

    @Override
    public boolean decodeNull(Element in) {
        return XmlUtils.getAttrValue(in, config().nullAttrName())
                .equals(config().nullAttrVal());
    }

    @Override
    public <T> WasEncoded<Element> encodeDynamicType(
            CodecCoreEx<Element, Element, Config> core,
            Codec<T, Element, Element, Config> codec,
            T val,
            Element out,
            Functions.F<Class<T>, Codec<T, Element, Element, Config>> getDynCodec
    ) {
        final Class<T> dynType = (Class<T>) val.getClass();
        if (config().dynamicTypeMatch(codec.type(), dynType)) {
            return WasEncoded.of(false, out);
        } else if (!config().dynamicTypeTags()) {
            final Codec<T, Element, Element, Config> dynCodec = getDynCodec.apply(dynType);
            dynCodec.encode(core, val, out);
            return WasEncoded.of(true, out);
        } else {
            final Codec<T, Element, Element, Config> dynCodec = getDynCodec.apply(dynType);
            XmlUtils.setAttrValue(out, config.typeAttrName(), config().classToName(dynType));
            dynCodec.encode(core, val, out);
            return WasEncoded.of(true, out);
        }
    }

    @Override
    public <T> T decodeDynamicType(Element in, Functions.F2<String, Element, T> decoder) {
        if (!config().dynamicTypeTags()) {
            return null;
        } else {
            final String typeName = XmlUtils.getAttrValue(in, config().typeAttrName());
            if (!typeName.isEmpty()) {
                return decoder.apply(typeName, in);
            } else {
                return null;
            }
        }
    }

    protected static class BooleanCodec implements Codec.BooleanCodec<Element, Element, Config> {

        @Override
        public Element encodePrim(boolean val, Element out) {
            return XmlUtils.addTextElement(out, String.valueOf(val));
        }

        @Override
        public boolean decodePrim(Element in) {
            return Boolean.parseBoolean(XmlUtils.firstChildText(in).getWholeText());
        }
    }

    protected final Codec.BooleanCodec<Element, Element, Config> booleanCodec = new BooleanCodec();

    @Override
    public Codec.BooleanCodec<Element, Element, Config> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], Element, Element, Config> booleanArrayCodec =
            new Codec<boolean[], Element, Element, Config>() {

        @Override
        public Class<boolean[]> type() {
            return boolean[].class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, boolean[] value, Element out) {
            for (boolean val : value) {
                booleanCodec().encode(core, val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public boolean[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final String entryName = config.entryElemName();
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getTagName().equals(entryName)) {
                        vals[i] = booleanCodec().decode(core, elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], Element, Element, Config> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected static class ByteCodec implements Codec.ByteCodec<Element, Element, Config> {

        @Override
        public Element encodePrim(byte val, Element out) {
            return XmlUtils.addTextElement(out, String.valueOf(val));
        }

        @Override
        public byte decodePrim(Element in) {
            return Byte.parseByte(XmlUtils.firstChildText(in).getWholeText());
        }
    }

    protected final Codec.ByteCodec<Element, Element, Config> byteCodec = new ByteCodec();

    @Override
    public Codec.ByteCodec<Element, Element, Config> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], Element, Element, Config> byteArrayCodec =
            new Codec<byte[], Element, Element, Config>() {

        @Override
        public Class<byte[]> type() {
            return byte[].class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, byte[] value, Element out) {
            for (byte val : value) {
                byteCodec().encode(core, val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public byte[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final String entryName = config.entryElemName();
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getTagName().equals(entryName)) {
                        vals[i] = byteCodec().decode(core, elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], Element, Element, Config> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected static class CharCodec implements Codec.CharCodec<Element, Element, Config> {

        @Override
        public Element encodePrim(char val, Element out) {
            return XmlUtils.addTextElement(out, String.valueOf(val));
        }

        @Override
        public char decodePrim(Element in ) {
            return XmlUtils.firstChildText(in).getWholeText().charAt(0);
        }
    }

    protected final Codec.CharCodec<Element, Element, Config> charCodec = new CharCodec();

    @Override
    public Codec.CharCodec<Element, Element, Config> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], Element, Element, Config> charArrayCodec =
            new Codec<char[], Element, Element, Config>() {

        @Override
        public Class<char[]> type() {
            return char[].class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, char[] value, Element out) {
            for (char val : value) {
                charCodec().encode(core, val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public char[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final String entryName = config.entryElemName();
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getTagName().equals(entryName)) {
                        vals[i] = charCodec().decode(core, elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], Element, Element, Config> charArrayCodec() {
        return charArrayCodec;
    }

    protected static class ShortCodec implements Codec.ShortCodec<Element, Element, Config> {

        @Override
        public Element encodePrim(short val, Element out) {
            return XmlUtils.addTextElement(out, String.valueOf(val));
        }

        @Override
        public short decodePrim(Element in ) {
            return Short.parseShort(XmlUtils.firstChildText(in).getWholeText());
        }
    }

    protected final Codec.ShortCodec<Element, Element, Config> shortCodec = new ShortCodec();

    @Override
    public Codec.ShortCodec<Element, Element, Config> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], Element, Element, Config> shortArrayCodec =
            new Codec<short[], Element, Element, Config>() {

        @Override
        public Class<short[]> type() {
            return short[].class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, short[] value, Element out) {
            for (short val : value) {
                shortCodec().encode(core, val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public short[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final String entryName = config.entryElemName();
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getTagName().equals(entryName)) {
                        vals[i] = shortCodec().decode(core, elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], Element, Element, Config> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected static class IntCodec implements Codec.IntCodec<Element, Element, Config> {

        @Override
        public Element encodePrim(int val, Element out) {
            return XmlUtils.addTextElement(out, String.valueOf(val));
        }

        @Override
        public int decodePrim(Element in) {
            return Integer.parseInt(XmlUtils.firstChildText(in).getWholeText());
        }
    }

    protected final Codec.IntCodec<Element, Element, Config> intCodec = new IntCodec();

    @Override
    public Codec.IntCodec<Element, Element, Config> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], Element, Element, Config> intArrayCodec =
            new Codec<int[], Element, Element, Config>() {

        @Override
        public Class<int[]> type() {
            return int[].class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, int[] value, Element out) {
            for (int val : value) {
                intCodec().encode(core, val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public int[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final String entryName = config.entryElemName();
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getTagName().equals(entryName)) {
                        vals[i] = intCodec().decode(core, elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], Element, Element, Config> intArrayCodec() {
        return intArrayCodec;
    }

    protected static class LongCodec implements Codec.LongCodec<Element, Element, Config> {

        @Override
        public Element encodePrim(long val, Element out) {
            return XmlUtils.addTextElement(out, String.valueOf(val));
        }

        @Override
        public long decodePrim(Element in) {
            return Long.parseLong(XmlUtils.firstChildText(in).getWholeText());
        }
    }

    protected final Codec.LongCodec<Element, Element, Config> longCodec = new LongCodec();

    @Override
    public Codec.LongCodec<Element, Element, Config> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], Element, Element, Config> longArrayCodec =
            new Codec<long[], Element, Element, Config>() {

        @Override
        public Class<long[]> type() {
            return long[].class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, long[] value, Element out) {
            for (long val : value) {
                longCodec().encode(core, val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public long[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final String entryName = config.entryElemName();
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getTagName().equals(entryName)) {
                        vals[i] = longCodec().decode(core, elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], Element, Element, Config> longArrayCodec() {
        return longArrayCodec;
    }

    protected static class FloatCodec implements Codec.FloatCodec<Element, Element, Config> {

        @Override
        public Element encodePrim(float val, Element out) {
            return XmlUtils.addTextElement(out, String.valueOf(val));
        }

        @Override
        public float decodePrim(Element in ) {
            return Float.parseFloat(XmlUtils.firstChildText(in).getWholeText());
        }
    }

    protected final Codec.FloatCodec<Element, Element, Config> floatCodec = new FloatCodec();

    @Override
    public Codec.FloatCodec<Element, Element, Config> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], Element, Element, Config> floatArrayCodec =
            new Codec<float[], Element, Element, Config>() {

        @Override
        public Class<float[]> type() {
            return float[].class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, float[] value, Element out) {
            for (float val : value) {
                floatCodec().encode(core, val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public float[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final String entryName = config.entryElemName();
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getTagName().equals(entryName)) {
                        vals[i] = floatCodec().decode(core, elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], Element, Element, Config> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected static class DoubleCodec implements Codec.DoubleCodec<Element, Element, Config> {
        @Override
        public Element encodePrim(double val, Element out) {
            return XmlUtils.addTextElement(out, String.valueOf(val));
        }

        @Override
        public double decodePrim(Element in ) {
            return Double.parseDouble(XmlUtils.firstChildText(in).getWholeText());
        }
    }

    protected final Codec.DoubleCodec<Element, Element, Config> doubleCodec = new DoubleCodec();

    @Override
    public Codec.DoubleCodec<Element, Element, Config> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], Element, Element, Config> doubleArrayCodec =
            new Codec<double[], Element, Element, Config>() {

        @Override
        public Class<double[]> type() {
            return double[].class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, double[] value, Element out) {
            for (double val : value) {
                doubleCodec().encode(core, val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public double[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final String entryName = config.entryElemName();
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getTagName().equals(entryName)) {
                        vals[i] = doubleCodec().decode(core, elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], Element, Element, Config> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected static class StringCodec implements Codec<String, Element, Element, Config> {

        @Override
        public Class<String> type() {
            return String.class;
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, Config> core, String val, Element out) {
            return XmlUtils.addTextElement(out, val);
        }

        @Override
        public String decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            return XmlUtils.firstChildText(in).getWholeText();
        }
    }

    protected final Codec<String, Element, Element, Config> stringCodec = new StringCodec();

    @Override
    public Codec<String, Element, Element, Config> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, Element, Element, Config> enumCodec(Class<EM> enumType) {
        return new Codec.FinalCodec<EM, Element, Element, Config>() {

            @Override
            public Class<EM> type() {
                return enumType;
            }

            @Override
            public Element encode(CodecCoreEx<Element, Element, Config> core, EM value, Element out) {
                return core.format().stringCodec().encode(core, value.name(), out);
            }

            @Override
            public EM decode(CodecCoreEx<Element, Element, Config> core, Element in) {
                return EM.valueOf(enumType, core.format().stringCodec().decode(core, in));
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, Element, Element, Config> createMapCodec(
            Class<Map<String, V>> type,
            Codec<V, Element, Element, Config> valueCodec) {
        return new XmlNodeMapCodecs.StringMapCodec<V>(type, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Element, Element, Config> createMapCodec(
            Class<Map<K, V>> type,
            Codec<K, Element, Element, Config> keyCodec,
            Codec<V, Element, Element, Config> valueCodec) {
        return new XmlNodeMapCodecs.MapCodec<K, V>(type, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, Element, Element, Config> createCollCodec(
            Class<Collection<T>> collType,
            Codec<T, Element, Element, Config> elemCodec) {
        return new CollectionCodec<T, Element, Element, Config>(collType, elemCodec) {

            @Override
            public Element encode(CodecCoreEx<Element, Element, Config> core, Collection<T> value, Element out) {
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, addEntryElement(out));
                }

                return out;
            }

            @Override
            public Collection<T> decode(CodecCoreEx<Element, Element, Config> core, Element in) {
                final NodeList nodes = in.getChildNodes();
                final int l = nodes.getLength();

                final CollProxy<T> collProxy = getCollectionProxy(core);

                for (int i = 0; i < l; ++i) {
                    final Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        final Element elem = (Element) node;
                        if (elem.getTagName().equals(config().entryElemName())) {
                            collProxy.add(elemCodec.decodeWithCheck(core, elem));
                        }
                    }
                }

                return collProxy.construct();
            }
        };
    }

    @Override
    public <T> Codec<T[], Element, Element, Config> createObjectArrayCodec(
            Class<T[]> arrType,
            Class<T> elemType,
            Codec<T, Element, Element, Config> elemCodec) {
        return new Codec<T[], Element, Element, Config>() {

            @Override
            public Class<T[]> type() {
                return arrType;
            }

            @Override
            public Element encode(CodecCoreEx<Element, Element, Config> core, T[] value, Element out) {
                for (T val : value) {
                    elemCodec.encodeWithCheck(core, val, addEntryElement(out));
                }

                return out;
            }

            @Override
            public T[] decode(CodecCoreEx<Element, Element, Config> core, Element in) {
                final NodeList nodes = in.getChildNodes();
                final int l = nodes.getLength();
                final T[] arr = (T[]) Array.newInstance(elemCodec.type(), l);

                for (int i = 0; i < l; ++i) {
                    final Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        final Element elem = (Element) node;
                        if (elem.getTagName().equals(config().entryElemName())) {
                            arr[i] = elemCodec.decodeWithCheck(core, elem);
                        }
                    }
                }

                return arr;
            }
        };
    }

    @Override
    public <T, RA extends ObjectMeta.Builder<T>> Codec<T, Element, Element, Config> createObjectCodec(
            Class<T> type,
            ObjectMeta<T, Element, Element, RA> objMeta) {
        if (Modifier.isFinal(type.getModifiers())) {
            return new FinalObjectCodec<T, RA>(type, objMeta);
        } else {
            return new ObjectCodec<T, RA>(type, objMeta);
        }
    }

    protected class ObjectCodec<T, RA extends ObjectMeta.Builder<T>>
            implements Codec<T, Element, Element, Config> {

        private final Class<T> type;
        private final ObjectMeta<T, Element, Element, RA> objMeta;
        private final Map<String, ObjectMeta.Field<T, Element, Element, RA>> fields;

        private ObjectCodec(
                Class<T> type,
                ObjectMeta<T, Element, Element, RA> objMeta) {
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
        public Element encode(CodecCoreEx<Element, Element, Config> core, T value, Element out) {
            fields.forEach((name, field) -> {
                field.encodeField(value, XmlUtils.addElement(out, field.name()));
            });

            return out;
        }

        @Override
        public T decode(CodecCoreEx<Element, Element, Config> core, Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();

            final Set<String> expNames = fields.keySet();
            final Set<String> actNames = new HashSet<>();
            final RA ra = objMeta.startDecode();

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element)node;
                    String name = elem.getTagName();
                    if (!expNames.contains(name)) {
                        if (config().failOnUnrecognisedFields()) {
                            throw new CodecException("Field name '" + name + "' unexpected for type " + type);
                        }
                    } else if (actNames.contains(name)) {
                        throw new CodecException("Duplicate field name '" + name + "' for type " + type);
                    } else {
                        actNames.add(name);
                        fields.get(name).decodeField(ra, elem);
                    }
                }
            }

            checkFields(type, expNames, actNames);

            return ra.construct();
        }
    }

    protected class FinalObjectCodec<T, RA extends ObjectMeta.Builder<T>>
            extends ObjectCodec<T, RA>
            implements Codec.FinalCodec<T, Element, Element, Config> {

        protected FinalObjectCodec(
                Class<T> type,
                ObjectMeta<T, Element, Element, RA> objMeta) {
            super(type, objMeta);
        }
    }
}
