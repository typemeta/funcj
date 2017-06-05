package org.funcj.codec.xml;

import org.funcj.codec.*;
import org.funcj.codec.utils.ReflectionUtils;
import org.funcj.control.Exceptions;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.lang.reflect.*;
import java.util.*;

import static org.funcj.codec.xml.XmlUtils.*;

public class XmlCodecCore extends CodecCore<Element> {

    public final DocumentBuilder docBuilder;

    public Document doc;

    public XmlCodecCore(DocumentBuilder docBuilder) {
        this.docBuilder = docBuilder;
    }

    public XmlCodecCore() {
        this(Exceptions.wrap(
                () -> DocumentBuilderFactory.newInstance().newDocumentBuilder(),
                XmlCodecException::new));
    }

    public Document setNewDocument() {
        doc = docBuilder.newDocument();
        return doc;
    }

    public String entryElemName() {
        return "elem";
    }

    public String indexAttrName() {
        return "i";
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

    public Element addEntryElement(Element parent) {
        return addElement(doc, parent, entryElemName());
    }

    @Override
    public <T> Element encode(Class<T> type, T val) {
        return encode(type, val, "_");
    }

    public <T> Element encode(Class<T> type, T val, String rootName) {
        return encode(type, val, (Element)doc.appendChild(doc.createElement(rootName)));
    }

    protected final Codec.NullCodec<Element> nullCodec = new Codec.NullCodec<Element>() {
        @Override
        public boolean isNull(Element in) {
            return !in.hasChildNodes() && !in.hasAttributes();
        }

        @Override
        public Element encode(Object val, Element out) {
            return out;
        }

        @Override
        public Object decode(Element in) {
            if (isNull(in)) {
                return null;
            } else {
                throw new IllegalStateException("Expected a null node but got " + in.getNodeName());
            }
        }
    };

    @Override
    public Codec.NullCodec<Element> nullCodec() {
        return nullCodec;
    }

    protected final Codec.BooleanCodec<Element> booleanCodec = new Codec.BooleanCodec<Element>() {

        @Override
        public Element encodePrim(boolean val, Element out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public boolean decodePrim(Element in) {
            return Boolean.parseBoolean(firstChildText(in).getWholeText());
        }
    };

    @Override
    public Codec.BooleanCodec<Element> booleanCodec() {
        return booleanCodec;
    }

    protected final Codec<boolean[], Element> booleanArrayCodec = new Codec<boolean[], Element>() {

        @Override
        public Element encode(boolean[] vals, Element out) {
            for (boolean val : vals) {
                booleanCodec().encode(val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public boolean[] decode(Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final boolean[] vals = new boolean[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(entryElemName())) {
                        vals[i] = booleanCodec().decode(elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<boolean[], Element> booleanArrayCodec() {
        return booleanArrayCodec;
    }

    protected final Codec.ByteCodec<Element> byteCodec = new Codec.ByteCodec<Element>() {

        @Override
        public Element encodePrim(byte val, Element out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public byte decodePrim(Element in) {
            return Byte.parseByte(firstChildText(in).getWholeText());
        }
    };

    @Override
    public Codec.ByteCodec<Element> byteCodec() {
        return byteCodec;
    }

    protected final Codec<byte[], Element> byteArrayCodec = new Codec<byte[], Element>() {

        @Override
        public Element encode(byte[] vals, Element out) {
            for (byte val : vals) {
                byteCodec().encode(val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public byte[] decode(Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final byte[] vals = new byte[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(entryElemName())) {
                        vals[i] = byteCodec().decode(elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<byte[], Element> byteArrayCodec() {
        return byteArrayCodec;
    }

    protected final Codec.CharCodec<Element> charCodec = new Codec.CharCodec<Element>() {

        @Override
        public Element encodePrim(char val, Element out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public char decodePrim(Element in) {
            return firstChildText(in).getWholeText().charAt(0);
        }
    };

    @Override
    public Codec.CharCodec<Element> charCodec() {
        return charCodec;
    }

    protected final Codec<char[], Element> charArrayCodec = new Codec<char[], Element>() {

        @Override
        public Element encode(char[] vals, Element out) {
            for (char val : vals) {
                charCodec().encode(val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public char[] decode(Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final char[] vals = new char[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(entryElemName())) {
                        vals[i] = charCodec().decode(elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<char[], Element> charArrayCodec() {
        return charArrayCodec;
    }

    protected final Codec.ShortCodec<Element> shortCodec = new Codec.ShortCodec<Element>() {

        @Override
        public Element encodePrim(short val, Element out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public short decodePrim(Element in) {
            return Short.parseShort(firstChildText(in).getWholeText());
        }
    };

    @Override
    public Codec.ShortCodec<Element> shortCodec() {
        return shortCodec;
    }

    protected final Codec<short[], Element> shortArrayCodec = new Codec<short[], Element>() {

        @Override
        public Element encode(short[] vals, Element out) {
            for (short val : vals) {
                shortCodec().encode(val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public short[] decode(Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final short[] vals = new short[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(entryElemName())) {
                        vals[i] = shortCodec().decode(elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<short[], Element> shortArrayCodec() {
        return shortArrayCodec;
    }

    protected final Codec.IntCodec<Element> intCodec = new Codec.IntCodec<Element>() {

        @Override
        public Element encodePrim(int val, Element out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public int decodePrim(Element in) {
            return Integer.parseInt(firstChildText(in).getWholeText());
        }
    };

    @Override
    public Codec.IntCodec<Element> intCodec() {
        return intCodec;
    }

    protected final Codec<int[], Element> intArrayCodec = new Codec<int[], Element>() {

        @Override
        public Element encode(int[] vals, Element out) {
            for (int val : vals) {
                intCodec().encode(val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public int[] decode(Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final int[] vals = new int[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(entryElemName())) {
                        vals[i] = intCodec().decode(elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<int[], Element> intArrayCodec() {
        return intArrayCodec;
    }

    protected final Codec.LongCodec<Element> longCodec = new Codec.LongCodec<Element>() {

        @Override
        public Element encodePrim(long val, Element out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public long decodePrim(Element in) {
            return Long.parseLong(firstChildText(in).getWholeText());
        }
    };

    @Override
    public Codec.LongCodec<Element> longCodec() {
        return longCodec;
    }

    protected final Codec<long[], Element> longArrayCodec = new Codec<long[], Element>() {

        @Override
        public Element encode(long[] vals, Element out) {
            for (long val : vals) {
                longCodec().encode(val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public long[] decode(Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final long[] vals = new long[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(entryElemName())) {
                        vals[i] = longCodec().decode(elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<long[], Element> longArrayCodec() {
        return longArrayCodec;
    }

    protected final Codec.FloatCodec<Element> floatCodec = new Codec.FloatCodec<Element>() {

        @Override
        public Element encodePrim(float val, Element out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public float decodePrim(Element in) {
            return Float.parseFloat(firstChildText(in).getWholeText());
        }
    };

    @Override
    public Codec.FloatCodec<Element> floatCodec() {
        return floatCodec;
    }

    protected final Codec<float[], Element> floatArrayCodec = new Codec<float[], Element>() {

        @Override
        public Element encode(float[] vals, Element out) {
            for (float val : vals) {
                floatCodec().encode(val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public float[] decode(Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final float[] vals = new float[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(entryElemName())) {
                        vals[i] = floatCodec().decode(elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<float[], Element> floatArrayCodec() {
        return floatArrayCodec;
    }

    protected final Codec.DoubleCodec<Element> doubleCodec = new Codec.DoubleCodec<Element>() {

        @Override
        public Element encodePrim(double val, Element out) {
            out.appendChild(doc.createTextNode(String.valueOf(val)));
            return out;
        }

        @Override
        public double decodePrim(Element in) {
            return Double.parseDouble(firstChildText(in).getWholeText());
        }
    };

    @Override
    public Codec.DoubleCodec<Element> doubleCodec() {
        return doubleCodec;
    }

    protected final Codec<double[], Element> doubleArrayCodec = new Codec<double[], Element>() {

        @Override
        public Element encode(double[] vals, Element out) {
            for (double val : vals) {
                doubleCodec().encode(val, addEntryElement(out));
            }

            return out;
        }

        @Override
        public double[] decode(Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();
            final double[] vals = new double[l];

            for (int i = 0; i < l; ++i) {
                final Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element elem = (Element) node;
                    if (elem.getNodeName().equals(entryElemName())) {
                        vals[i] = doubleCodec().decode(elem);
                    }
                }
            }

            return vals;
        }
    };

    @Override
    public Codec<double[], Element> doubleArrayCodec() {
        return doubleArrayCodec;
    }

    protected final Codec<String, Element> stringCodec = new Codec<String, Element>() {
        @Override
        public Element encode(String val, Element out) {
            out.appendChild(doc.createTextNode(val));
            return out;
        }

        @Override
        public String decode(Element in) {
            return firstChildText(in).getWholeText();
        }
    };

    @Override
    public Codec<String, Element> stringCodec() {
        return stringCodec;
    }

    @Override
    public <EM extends Enum<EM>> Codec<EM, Element> enumCodec(Class<? super EM> enumType) {
        return new Codec<EM, Element>() {
            @Override
            public Element encode(EM val, Element out) {
                out.appendChild(doc.createTextNode(val.name()));
                return out;
            }

            @Override
            public EM decode(Class<EM> dynType, Element in) {
                return EM.valueOf(dynType, firstChildText(in).getWholeText());
            }
        };
    }

    @Override
    public <V> Codec<Map<String, V>, Element> mapCodec(Codec<V, Element> valueCodec) {
        return new XmlMapCodecs.StringMapCodec<V>(this, valueCodec);
    }

    @Override
    public <K, V> Codec<Map<K, V>, Element> mapCodec(Codec<K, Element> keyCodec, Codec<V, Element> valueCodec) {
        return new XmlMapCodecs.MapCodec<K, V>(this, keyCodec, valueCodec);
    }

    @Override
    public <T> Codec<Collection<T>, Element> collCodec(Class<T> elemType, Codec<T, Element> elemCodec) {
        return new Codec<Collection<T>, Element>() {
            @Override
            public Element encode(Collection<T> vals, Element out) {
                for (T val : vals) {
                    elemCodec.encode(val, addEntryElement(out));
                }

                return out;
            }

            @Override
            public Collection<T> decode(Class<Collection<T>> dynType, Element in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();

                final NodeList nodes = in.getChildNodes();
                final int l = nodes.getLength();

                final Collection<T> vals = Exceptions.wrap(() -> ReflectionUtils.newInstance(dynType));
                if (vals instanceof ArrayList) {
                    ((ArrayList<T>) vals).ensureCapacity(l);
                }

                for (int i = 0; i < l; ++i) {
                    final Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        final Element elem = (Element) node;
                        if (elem.getNodeName().equals(entryElemName())) {
                            vals.add(elemCodec.decode(dynElemType, elem));
                        }
                    }
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T[], Element> objectArrayCodec(Class<T> elemType, Codec<T, Element> elemCodec) {
        return new Codec<T[], Element>() {
            @Override
            public Element encode(T[] vals, Element out) {
                for (int i = 0; i < vals.length; ++i) {
                    elemCodec.encode(vals[i], addEntryElement(out));
                }

                return out;
            }

            @Override
            public T[] decode(Class<T[]> dynType, Element in) {
                final Class<T> dynElemType = (Class<T>)dynType.getComponentType();

                final NodeList nodes = in.getChildNodes();
                final int l = nodes.getLength();

                final T[] vals = (T[]) Array.newInstance(elemType, l);

                for (int i = 0; i < l; ++i) {
                    final Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        final Element elem = (Element) node;
                        if (elem.getNodeName().equals(entryElemName())) {
                            vals[i] = elemCodec.decode(dynElemType, elem);
                        }
                    }
                }

                return vals;
            }
        };
    }

    @Override
    public <T> Codec<T, Element> dynamicCodec(Class<T> stcType) {
        return new Codec<T, Element>() {
            @Override
            public Element encode(T val, Element out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (!dynType.equals(stcType)) {
                    setAttrValue(out, typeAttrName(), classToName(dynType));
                }
                return encode2(XmlCodecCore.this.getNullUnsafeCodec(dynType), val, out);
            }

            protected <S extends T> Element encode2(Codec<S, Element> codec, T val, Element out) {
                return codec.encode((S)val, out);
            }

            @Override
            public T decode(Element in) {
                final String typeName = getAttrValue(in, typeAttrName());
                final Class<T> dynType;
                if (typeName.isEmpty()) {
                    dynType = stcType;
                } else {
                    dynType = nameToClass(typeName);
                }

                final Codec<T, Element> codec = XmlCodecCore.this.getNullUnsafeCodec(dynType);
                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    public <T> Codec<T, Element> dynamicCodec(Codec<T, Element> codec, Class<T> stcType) {
        return new Codec<T, Element>() {
            @Override
            public Element encode(T val, Element out) {
                final Class<? extends T> dynType = (Class<? extends T>)val.getClass();
                if (!dynType.equals(stcType)) {
                    setAttrValue(out, typeAttrName(), classToName(dynType));
                }
                return codec.encode(val, out);
            }

            @Override
            public T decode(Element in) {
                final String typeName = getAttrValue(in, typeAttrName());
                final Class<T> dynType;
                if (typeName.isEmpty()) {
                    dynType = stcType;
                } else {
                    dynType = nameToClass(typeName);
                }

                return codec.decode(dynType, in);
            }
        };
    }

    @Override
    public <T> Codec<T, Element> createObjectCodec(
            Class<T> type,
            Map<String, FieldCodec<Element>> fieldCodecs) {
        return new Codec<T, Element>() {
            @Override
            public Element encode(T val, Element out) {
                fieldCodecs.forEach((name, codec) -> {
                    codec.encodeField(val, addElement(doc, out, name));
                });
                return out;
            }

            @Override
            public T decode(Class<T> dynType, Element in) {
                final T val = Exceptions.wrap(
                        () -> ReflectionUtils.newInstance(dynType),
                        XmlCodecException::new);
                fieldCodecs.forEach((name, codec) -> {
                    codec.decodeField(val, firstChildElement(in, name));
                });
                return val;
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
