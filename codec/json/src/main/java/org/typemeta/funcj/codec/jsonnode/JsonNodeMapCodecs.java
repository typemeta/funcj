package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.Codec;
import org.typemeta.funcj.codec.CodecCoreEx;
import org.typemeta.funcj.codec.MapCodecs.*;
import org.typemeta.funcj.json.model.*;

import java.util.*;

public abstract class JsonNodeMapCodecs {

    public static class MapCodec<K, V> extends AbstractMapCodec<K, V, JsValue, JsValue, JsonNodeConfig> {

        public MapCodec(
                Class<Map<K, V>> mapType,
                Codec<K, JsValue, JsValue, JsonNodeConfig> keyCodec,
                Codec<V, JsValue, JsValue, JsonNodeConfig> valueCodec) {
            super(mapType, keyCodec, valueCodec);
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, JsonNodeConfig> core, Map<K, V> value, JsValue out) {
            final String keyFieldName = core.config().keyFieldName();
            final String valueFieldName = core.config().valueFieldName();

            final List<JsValue> jsvs = new ArrayList<>(value.size());

            value.forEach((k, v) ->
                    jsvs.add(JSAPI.obj(
                            JSAPI.field(keyFieldName, keyCodec.encodeWithCheck(core, k, out)),
                            JSAPI.field(valueFieldName, valueCodec.encodeWithCheck(core, v, out))
                    ))
            );

            return JSAPI.arr(jsvs);
        }

        @Override
        public Map<K, V> decode(CodecCoreEx<JsValue, JsValue, JsonNodeConfig> core, JsValue in) {
            final JsArray jsa = in.asArray();

            final String keyFieldName = core.config().keyFieldName();
            final String valueFieldName = core.config().valueFieldName();

            final MapProxy<K, V> mapProxy = getMapProxy(core);

            jsa.forEach(jsElem -> {
                final JsObject jso = jsElem.asObject();
                final K k = keyCodec.decodeWithCheck(core, jso.get(keyFieldName));
                final V v = valueCodec.decodeWithCheck(core, jso.get(valueFieldName));
                mapProxy.put(k, v);
            });

            return mapProxy.construct();
        }
    }

    public static class StringMapCodec<V> extends AbstractStringMapCodec<V, JsValue, JsValue, JsonNodeConfig> {

        public StringMapCodec(
                Class<Map<String, V>> type,
                Codec<V, JsValue, JsValue, JsonNodeConfig> valueCodec) {
            super(type, valueCodec);
        }

        @Override
        public JsValue encode(CodecCoreEx<JsValue, JsValue, JsonNodeConfig> core, Map<String, V> value, JsValue out) {
            final List<JsObject.Field> fields = new ArrayList<>(value.size());

            value.forEach((key, val) -> {
                fields.add(JSAPI.field(key, valueCodec.encodeWithCheck(core, val, out)));
            });

            return JSAPI.obj(fields);
        }

        @Override
        public Map<String, V> decode(CodecCoreEx<JsValue, JsValue, JsonNodeConfig> core, JsValue in) {
            final JsObject jso = in.asObject();

            final MapProxy<String, V> mapProxy = getMapProxy(core);

            jso.forEach(field -> {
                mapProxy.put(field.name(), valueCodec.decodeWithCheck(core, field.value()));
            });

            return mapProxy.construct();
        }
    }
}
