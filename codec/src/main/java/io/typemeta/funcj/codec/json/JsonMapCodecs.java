package io.typemeta.funcj.codec.json;

import io.typemeta.funcj.codec.Codec;
import io.typemeta.funcj.control.Exceptions;
import io.typemeta.funcj.json.*;
import io.typemeta.funcj.util.Functions;

import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

public abstract class JsonMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, JSValue> {
        private final JsonCodecCoreImpl core;
        private final Codec<K, JSValue> keyCodec;
        private final Codec<V, JSValue> valueCodec;

        public MapCodec(
                JsonCodecCoreImpl core,
                Codec<K, JSValue> keyCodec,
                Codec<V, JSValue> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public JSValue encode(Map<K, V> map, JSValue enc) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            final List<JSValue> nodes = map.entrySet().stream()
                    .map(en -> JSObject.of(
                            JSObject.field(keyFieldName, keyCodec.encode(en.getKey(), enc)),
                            JSObject.field(valueFieldName, valueCodec.encode(en.getValue(), enc))
                    )).collect(toList());

            return JSArray.of(nodes);
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, JSValue enc) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            final Functions.F<Map<K, V>, Consumer<JSValue>> decodeF = m -> elemNode -> {
                final JSObject elemObjNode = elemNode.asObject();
                final K key = keyCodec.decode(elemObjNode.get(keyFieldName));
                final V val = valueCodec.decode(elemObjNode.get(valueFieldName));
                m.put(key, val);
            };

            final Map<K, V> map = Exceptions.wrap(
                    () -> core.getTypeConstructor(dynType).construct(),
                    JsonCodecException::new);

            final JSArray objNode = enc.asArray();
            final Consumer<JSValue> decode = decodeF.apply(map);
            objNode.forEach(decode);

            return map;
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, JSValue> {
        private final JsonCodecCoreImpl core;
        private final Codec<V, JSValue> valueCodec;

        public StringMapCodec(JsonCodecCoreImpl core, Codec<V, JSValue> valueCodec) {
            this.core = core;
            this.valueCodec = valueCodec;
        }

        @Override
        public JSValue encode(Map<String, V> map, JSValue enc) {
            final List<JSObject.Field> fields = new ArrayList<>(map.size());

            map.forEach((k, v) -> {
                final JSValue value = valueCodec.encode(v, enc);
                fields.add(JSObject.field(k, value));
            });

            return JSObject.of(fields);
        }

        @Override
        public Map<String, V> decode(Class<Map<String, V>> dynType, JSValue enc) {
            final JSObject objNode = enc.asObject();

            final Map<String, V> map = Exceptions.wrap(
                    () -> core.getTypeConstructor(dynType).construct(),
                    JsonCodecException::new);

            objNode.forEach(field -> {
                final V value = valueCodec.decode(field.getValue());
                map.put(field.getName(), value);
            });

            return map;
        }
    }
}
