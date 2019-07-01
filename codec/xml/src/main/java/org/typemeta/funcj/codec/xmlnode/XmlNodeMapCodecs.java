package org.typemeta.funcj.codec.xmlnode;

import org.typemeta.funcj.codec.Codec;
import org.typemeta.funcj.codec.CodecCoreEx;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Map;

public abstract class XmlNodeMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, Element, Element, XmlNodeConfig> {

        public MapCodec(
                Class<Map<K, V>> mapType,
                Codec<K, Element, Element, XmlNodeConfig> keyCodec,
                Codec<V, Element, Element, XmlNodeConfig> valueCodec) {
            super(mapType, keyCodec, valueCodec);
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, XmlNodeConfig> core, Map<K, V> value, Element out) {
            final String entryName = core.config().entryElemName();
            final String keyName = core.config().keyElemName();
            final String valueName = core.config().valueElemName();

            value.forEach((key, val) -> {
                final Element elem = XmlUtils.addElement(out, entryName);

                final Element keyNode = XmlUtils.addElement(elem, keyName);
                keyCodec.encodeWithCheck(core, key, keyNode);

                final Element valueNode = XmlUtils.addElement(elem, valueName);
                valueCodec.encodeWithCheck(core, val, valueNode);
            });

            return out;
        }

        @Override
        public Map<K, V> decode(CodecCoreEx<Element, Element, XmlNodeConfig> core, Element in) {
            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();

            final String keyName = core.config().keyElemName();
            final String valueName = core.config().valueElemName();

            final MapProxy<K, V> mapProxy = getMapProxy(core);

            for (int i = 0; i < l; ++i) {
                final Element elem = (Element)nodes.item(i);
                final K key = keyCodec.decodeWithCheck(core, XmlUtils.firstChildElement(elem, keyName));
                final V value = valueCodec.decodeWithCheck(core, XmlUtils.firstChildElement(elem, valueName));
                mapProxy.put(key, value);
            }

            return mapProxy.construct();
        }
    }

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, Element, Element, XmlNodeConfig> {

        public StringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, Element, Element, XmlNodeConfig> valueCodec) {
            super(type, valueCodec);
        }

        @Override
        public Element encode(CodecCoreEx<Element, Element, XmlNodeConfig> core, Map<String, V> value, Element out) {

            value.forEach((key, val) ->
                valueCodec.encodeWithCheck(core, val, XmlUtils.addElement(out, key))
            );

            return out;
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<Element, Element, XmlNodeConfig> core, Element in) {
            final MapProxy<String, V> mapProxy = getMapProxy(core);

            final NodeList nodes = in.getChildNodes();
            final int l = nodes.getLength();

            for (int i = 0; i < l; ++i) {
                final Element childElem = (Element) nodes.item(i);
                final String key = childElem.getNodeName();
                final V val = valueCodec.decodeWithCheck(core, childElem);
                mapProxy.put(key, val);
            }

            return mapProxy.construct();
        }
    }
}
