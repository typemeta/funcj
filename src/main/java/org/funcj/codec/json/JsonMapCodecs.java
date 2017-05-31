package org.funcj.codec.json;

import org.funcj.codec.Codec;
import org.funcj.control.Exceptions;
import org.funcj.json.Node;
import org.funcj.util.Functions;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public abstract class JsonMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, Node> {
        private final JsonCodecCore core;
        private final Codec<K, Node> keyCodec;
        private final Codec<V, Node> valueCodec;

        public MapCodec(
                JsonCodecCore core,
                Codec<K, Node> keyCodec,
                Codec<V, Node> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public Node encode(Map<K, V> map, Node out) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();


            final Functions.F<Map.Entry<K, V>, Node> encode =
                    en -> {
                        final K key = en.getKey();
                        final V value = en.getValue();
                        final LinkedHashMap<String, Node> elemFields = new LinkedHashMap<>();
                        elemFields.put(keyFieldName, keyCodec.encode(key, out));
                        elemFields.put(valueFieldName, valueCodec.encode(value, out));
                        return Node.object(elemFields);
                    };

            final List<Node> nodes = map.entrySet().stream()
                    .map(encode::apply)
                    .collect(toList());
            return Node.array(nodes);
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, Node in) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            final Functions.F<Map<K, V>, Consumer<Node>> decodeF = m -> elemNode -> {
                final Node.ObjectNode elemObjNode = elemNode.asObject();
                final K key = keyCodec.decode(elemObjNode.fields.get(keyFieldName));
                final V val = valueCodec.decode(elemObjNode.fields.get(valueFieldName));
                m.put(key, val);
            };

            final Map<K, V> map = Exceptions.wrap(() -> dynType.newInstance());

            final Node.ArrayNode objNode = in.asArray();
            final Consumer<Node> decode = decodeF.apply(map);
            objNode.values.forEach(decode);

            return map;
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, Node> {
        private final JsonCodecCore core;
        private final Codec<V, Node> valueCodec;

        public StringMapCodec(JsonCodecCore core, Codec<V, Node> valueCodec) {
            this.core = core;
            this.valueCodec = valueCodec;
        }

        @Override
        public Node encode(Map<String, V> map, Node out) {
            final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();

            map.forEach((k, v) -> {
                final Node value = valueCodec.encode(v, out);
                fields.put(k, value);
            });

            return Node.object(fields);
        }

        @Override
        public Map<String, V> decode(Class<Map<String, V>> dynType, Node in) {
            final Node.ObjectNode objNode = in.asObject();

            final Map<String, V> map = Exceptions.wrap(() -> dynType.newInstance());

            objNode.fields.forEach((k, v) -> {
                final V value = valueCodec.decode(v);
                map.put(k, value);
            });

            return map;
        }
    }
}
