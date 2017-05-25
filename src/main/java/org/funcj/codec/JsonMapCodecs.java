package org.funcj.codec;

import org.funcj.control.Exceptions;
import org.funcj.json.Node;

import java.util.*;

import static java.util.stream.Collectors.toList;

public abstract class JsonMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, Node> {
        private final JsonCodecCore core;
        private final Class<Map<K, V>> stcClass;
        private final Class<K> stcKeyClass;
        private final Class<V> stcValClass;

        public MapCodec(
                JsonCodecCore core,
                Class<Map<K, V>> stcClass,
                Class<K> stcKeyClass,
                Class<V> stcValClass) {
            this.core = core;
            this.stcClass = stcClass;
            this.stcKeyClass = stcKeyClass;
            this.stcValClass = stcValClass;
        }

        @Override
        public Node encode(Map<K, V> val, Node out) {
            if (val == null) {
                return core.nullCodec().encode(val, out);
            } else {
                final String typeFieldName = core.typeFieldName();
                final String keyFieldName = core.keyFieldName();
                final String valueFieldName = core.valueFieldName();
                final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                fields.put(typeFieldName, Node.string(core.classToName(val.getClass())));

                final List<Node> nodes = val.entrySet().stream()
                        .map(en -> {
                            final LinkedHashMap<String, Node> elemFields = new LinkedHashMap<>();
                            elemFields.put(keyFieldName, core.encode(stcKeyClass, en.getKey(), out));
                            elemFields.put(valueFieldName, core.encode(stcValClass, en.getValue(), out));
                            return Node.object(elemFields);
                        }).collect(toList());
                fields.put(core.valueFieldName(), Node.array(nodes));
                return Node.object(fields);
            }
        }

        @Override
        public Map<K, V> decode(Node in) {
            if (in.isNull()) {
                return (Map<K, V>) core.nullCodec().decode(in);
            } else {
                final String typeFieldName = core.typeFieldName();
                final String keyFieldName = core.keyFieldName();
                final String valueFieldName = core.valueFieldName();
                final Node.ObjectNode objNode = in.asObject();
                final Class<?> clazz = core.nameToClass(objNode.fields.get(typeFieldName).asString().value);
                final Map<K, V> map = (Map<K, V>) Exceptions.wrap(() -> clazz.newInstance());
                objNode.fields.get(valueFieldName).asArray().values.forEach(elemNode -> {
                    final Node.ObjectNode elemObjNode = elemNode.asObject();
                    final K key = core.decode(stcKeyClass, elemObjNode.fields.get(keyFieldName));
                    final V val = core.decode(stcValClass, elemObjNode.fields.get(valueFieldName));
                    map.put(key, val);
                });
                return map;
            }
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, Node> {
        private final JsonCodecCore core;
        private final Class<Map<String, V>> stcClass;
        private final Class<V> stcValClass;

        public StringMapCodec(JsonCodecCore core, Class<Map<String, V>> stcClass, Class<V> stcValClass) {
            this.core = core;
            this.stcClass = stcClass;
            this.stcValClass = stcValClass;
        }

        @Override
        public Node encode(Map<String, V> val, Node out) {
            if (val == null) {
                return core.nullCodec().encode(val, out);
            } else {
                final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                fields.put(core.typeFieldName(), Node.string(core.classToName(val.getClass())));

                val.forEach((k, v) -> {
                    final String key = (String)k;
                    final Node value = core.encode(stcValClass, v, out);
                    fields.put(key, value);
                });

                return Node.object(fields);
            }
        }

        @Override
        public Map<String, V> decode(Node in) {
            if (in.isNull()) {
                return (Map<String, V>)core.nullCodec().decode(in);
            } else {
                final String typeFieldName = core.typeFieldName();
                final Node.ObjectNode objNode = in.asObject();
                final Class<?> clazz = core.nameToClass(objNode.fields.get(typeFieldName).asString().value);
                final Map<String, V> map = (Map<String, V>) Exceptions.wrap(() -> clazz.newInstance());

                objNode.fields.forEach((k, v) -> {
                    if (!typeFieldName.equals(k)) {
                        final V value = core.decode(stcValClass, v);
                        map.put(k, value);
                    }
                });

                return map;
            }
        }
    }
}
