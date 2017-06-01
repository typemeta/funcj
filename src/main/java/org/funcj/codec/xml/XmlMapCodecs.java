package org.funcj.codec.xml;

import org.funcj.codec.Codec;
import org.funcj.control.Exceptions;
import org.w3c.dom.*;

import java.util.Map;

import static org.funcj.codec.xml.XmlUtils.*;

public abstract class XmlMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, Node> {
        private final XmlCodecCore core;
        private final Codec<K, Node> keyCodec;
        private final Codec<V, Node> valueCodec;

        public MapCodec(
                XmlCodecCore core,
                Codec<K, Node> keyCodec,
                Codec<V, Node> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Node encode(Map<K, V> map, Node out) {
            int i = 0;
            for (Map.Entry<K, V> entry : map.entrySet()) {
                final Element node = (Element)out.appendChild(core.doc.createElement(core.entryElemName()));

                final Node keyNode = node.appendChild(core.doc.createElement(core.keyElemName()));
                keyCodec.encode(entry.getKey(), keyNode);

                final Node valueNode = node.appendChild(core.doc.createElement(core.valueElemName()));
                valueCodec.encode(entry.getValue(), valueNode);
            }

            return out;
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, Node in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();

            final Map<K, V> map = Exceptions.wrap(() -> dynType.newInstance());

            for (int i = 0; i < l; ++i) {
                final Element childElem = (Element)nodes.item(i);
                final K key = keyCodec.decode(firstChildElement(childElem, core.keyElemName()));
                final V value = valueCodec.decode(firstChildElement(childElem, core.valueElemName()));
                map.put(key, value);
            }

            return map;
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, Node> {
        private final XmlCodecCore core;
        private final Codec<V, Node> valueCodec;

        public StringMapCodec(XmlCodecCore core, Codec<V, Node> valueCodec) {
            this.core = core;
            this.valueCodec = valueCodec;
        }

        @Override
        public Node encode(Map<String, V> map, Node out) {
            int i = 0;
            for (Map.Entry<String, V> entry : map.entrySet()) {
                final Element node = (Element)out.appendChild(core.doc.createElement(core.entryElemName()));
                setAttrValue(node, core.keyAttrName(), entry.getKey());
                valueCodec.encode(entry.getValue(), node);
            }

            return out;
        }

        @Override
        public Map<String, V> decode(Class<Map<String, V>> dynType, Node in) {
            final Element elem = (Element)in;
            final NodeList nodes = elem.getChildNodes();
            final int l = nodes.getLength();

            final Map<String, V> map = Exceptions.wrap(() -> dynType.newInstance(), XmlCodecException::new);

            for (int i = 0; i < l; ++i) {
                final Element childElem = (Element)nodes.item(i);
                final String key = getAttrValue(childElem, core.keyAttrName());
                final V value = valueCodec.decode(childElem);
                map.put(key, value);
            }

            return map;
        }
    }
}
