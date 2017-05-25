package org.funcj.codec;

import org.funcj.control.Exceptions;
import org.funcj.json.Node;
import org.funcj.util.Functions;

import java.util.*;

import static java.util.stream.Collectors.toList;

public abstract class JsonMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, Node> {
        private final JsonCodecCore core;
        private final Class<K> stcKeyClass;
        private final Class<V> stcValClass;

        public MapCodec(
                JsonCodecCore core,
                Class<K> stcKeyClass,
                Class<V> stcValClass) {
            this.core = core;
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

                final Functions.F<Map.Entry<K, V>, Node> encode =
                        en -> {
                            final LinkedHashMap<String, Node> elemFields = new LinkedHashMap<>();
                            elemFields.put(keyFieldName, core.encode(stcKeyClass, en.getKey(), out));
                            elemFields.put(valueFieldName, core.encode(stcValClass, en.getValue(), out));
                            return Node.object(elemFields);
                        };

                if (val instanceof HashMap) {
                    final List<Node> nodes = val.entrySet().stream()
                            .map(encode::apply)
                            .collect(toList());
                    return Node.array(nodes);
                } else {
                    final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                    fields.put(typeFieldName, Node.string(core.classToName(val.getClass())));
                    final List<Node> nodes = val.entrySet().stream()
                            .map(encode::apply)
                            .collect(toList());
                    fields.put(core.valueFieldName(), Node.array(nodes));
                    return Node.object(fields);
                }
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

                final Map<K, V> map;

                if (in.isObject()) {
                    final Node.ObjectNode objNode = in.asObject();
                    final Class<?> mapClass = core.nameToClass(objNode.fields.get(typeFieldName).asString().value);
                    map = (Map<K, V>) Exceptions.wrap(() -> mapClass.newInstance());
                    objNode.fields.get(valueFieldName).asArray().values
                            .forEach(elemNode -> {
                                final Node.ObjectNode elemObjNode = elemNode.asObject();
                                final K key = core.decode(stcKeyClass, elemObjNode.fields.get(keyFieldName));
                                final V val = core.decode(stcValClass, elemObjNode.fields.get(valueFieldName));
                                map.put(key, val);
                            });
                } else {
                    final Node.ArrayNode objNode = in.asArray();
                    map = new HashMap<>();
                    objNode.values
                            .forEach(elemNode -> {
                                final Node.ObjectNode elemObjNode = elemNode.asObject();
                                final K key = core.decode(stcKeyClass, elemObjNode.fields.get(keyFieldName));
                                final V val = core.decode(stcValClass, elemObjNode.fields.get(valueFieldName));
                                map.put(key, val);
                            });
                }

                return map;
            }
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, Node> {
        private final JsonCodecCore core;
        private final Class<V> stcValClass;

        public StringMapCodec(JsonCodecCore core, Class<V> stcValClass) {
            this.core = core;
            this.stcValClass = stcValClass;
        }

        @Override
        public Node encode(Map<String, V> val, Node out) {
            if (val == null) {
                return core.nullCodec().encode(val, out);
            } else {
                final LinkedHashMap<String, Node> fields = new LinkedHashMap<>();
                final Class<Map<String, V>> dynClass = (Class<Map<String, V>>)val.getClass();
                if (!(val instanceof HashMap)) {
                    fields.put(core.typeFieldName(), Node.string(core.classToName(dynClass)));
                }
                val.forEach((k, v) -> {
                    final Node value = core.encode(stcValClass, v, out);
                    fields.put(k, value);
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

                final Class<?> mapClass;
                if (objNode.fields.containsKey(typeFieldName)) {
                    mapClass = core.nameToClass(objNode.fields.get(typeFieldName).asString().value);
                } else {
                    mapClass = HashMap.class;
                }
                final Map<String, V> map = (Map<String, V>) Exceptions.wrap(() -> mapClass.newInstance());

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
