package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.functions.*;
import org.typemeta.funcj.json.model.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.typemeta.funcj.util.Exceptions.*;

public abstract class JsonMapCodecs {

    public static class MapCodec<K, V> implements Codec<Map<K, V>, JsValue, JsValue> {
        private final JsonCodecCoreImpl core;
        private final Codec<K, JsValue, JsValue> keyCodec;
        private final Codec<V, JsValue, JsValue> valueCodec;

        public MapCodec(
                JsonCodecCoreImpl core,
                Codec<K, JsValue, JsValue> keyCodec,
                Codec<V, JsValue, JsValue> valueCodec) {
            this.core = core;
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        @Override
        public JsValue encode(Map<K, V> map, JsValue enc) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            final List<JsValue> nodes =
                    map.entrySet().stream()
                            .map(en -> CodecRuntimeException.wrap(() ->
                                JSAPI.obj(
                                        JSAPI.field(keyFieldName, keyCodec.encode(en.getKey(), enc)),
                                        JSAPI.field(valueFieldName, valueCodec.encode(en.getValue(), enc))
                                )
                            )).collect(toList());

            return JSAPI.arr(nodes);
        }

        @Override
        public Map<K, V> decode(Class<Map<K, V>> dynType, JsValue enc) {
            final String keyFieldName = core.keyFieldName();
            final String valueFieldName = core.valueFieldName();

            final Functions.F<Map<K, V>, SideEffect.F<JsValue>> decodeF = m -> elemNode -> {
                final JsObject elemObj = elemNode.asObject();
                final K key = keyCodec.decode(elemObj.get(keyFieldName));
                final V val = valueCodec.decode(elemObj.get(valueFieldName));
                m.put(key, val);
            };

            final Map<K, V> map = core.getTypeConstructor(dynType).construct();

            final JsArray jsArr = enc.asArray();
            final SideEffect.F<JsValue> decode = decodeF.apply(map);
            jsArr.forEach(decode::apply);

            return map;
        }
    }

    public static class StringMapCodec<V> implements Codec<Map<String, V>, JsValue, JsValue> {
        private final JsonCodecCoreImpl core;
        private final Codec<V, JsValue, JsValue> valueCodec;

        public StringMapCodec(JsonCodecCoreImpl core, Codec<V, JsValue, JsValue> valueCodec) {
            this.core = core;
            this.valueCodec = valueCodec;
        }

        @Override
        public JsValue encode(Map<String, V> map, JsValue enc) {
            final List<JsObject.Field> fields = new ArrayList<>(map.size());

            map.forEach((k, v) -> {
                    final JsValue value = CodecRuntimeException.wrap(() -> valueCodec.encode(v, enc));
                    fields.add(JSAPI.field(k, value));
            });

            return JSAPI.obj(fields);
        }

        @Override
        public Map<String, V> decode(Class<Map<String, V>> dynType, JsValue enc) {
            final JsObject objNode = enc.asObject();

            final Map<String, V> map = core.getTypeConstructor(dynType).construct();

            objNode.forEach(field -> {
                    final V value = CodecRuntimeException.wrap(() -> valueCodec.decode(field.getValue()));
                    map.put(field.getName(), value);
            });

            return map;
        }
    }
}
