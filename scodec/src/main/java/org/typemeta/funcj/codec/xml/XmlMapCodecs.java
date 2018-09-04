package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.Codec;
import org.w3c.dom.*;

import java.util.Map;

public abstract class XmlMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, Element, Element> {
        private final XmlCodecCoreImpl core;
        private final Codec<K, Element, Element> keyCodec;
        private final Codec<V, Element, Element> valueCodec;

        public MapCodec(
                XmlCodecCoreImpl core,
                Codec<K, Element, Element> keyCodec,
                Codec<V, Element, Element> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Element encode(Map<K, V> map, Element enc) {
            int i = 0;
            for (Map.Entry<K, V> entry : map.entrySet()) {
                final Element elem = core.addEntryElement(enc);

                final Element keyNode = XmlUtils.addElement(elem, core.keyElemName());
                keyCodec.encode(entry.getKey(), keyNode);

                final Element valueNode = XmlUtils.addElement(elem, core.valueElemName());
                valueCodec.encode(entry.getValue(), valueNode);
            }

            return enc;
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, Element enc) {
            final NodeList nodes = enc.getChildNodes();
            final int l = nodes.getLength();

            final Map<K, V> map = core.getTypeConstructor(dynType).construct();

            for (int i = 0; i < l; ++i) {
                final Element elem = (Element)nodes.item(i);
                final K key = keyCodec.decode(XmlUtils.firstChildElement(elem, core.keyElemName()));
                final V value = valueCodec.decode(XmlUtils.firstChildElement(elem, core.valueElemName()));
                map.put(key, value);
            }

            return map;
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, Element, Element> {
        private final XmlCodecCoreImpl core;
        private final Codec<V, Element, Element> valueCodec;

        public StringMapCodec(XmlCodecCoreImpl core, Codec<V, Element, Element> valueCodec) {
            this.core = core;
            this.valueCodec = valueCodec;
        }

        @Override
        public Element encode(Map<String, V> map, Element enc) {
            int i = 0;
            for (Map.Entry<String, V> entry : map.entrySet()) {
                final Element elem = core.addEntryElement(enc);
                XmlUtils.setAttrValue(elem, core.keyAttrName(), entry.getKey());
                valueCodec.encode(entry.getValue(), elem);
            }

            return enc;
        }

        @Override
        public Map<String, V> decode(Class<Map<String, V>> dynType, Element enc) {
            final NodeList nodes = enc.getChildNodes();
            final int l = nodes.getLength();

            final Map<String, V> map = core.getTypeConstructor(dynType).construct();

            for (int i = 0; i < l; ++i) {
                final Element childElem = (Element)nodes.item(i);
                final String key = XmlUtils.getAttrValue(childElem, core.keyAttrName());
                final V value = valueCodec.decode(childElem);
                map.put(key, value);
            }

            return map;
        }
    }
}
