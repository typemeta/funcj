package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.json.JsonTypes.*;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.json.parser.JsonEvent;

import java.util.*;

public class JsValueCodec implements Codec<JsValue, InStream, OutStream, Config> {
    public static final JsNullCodec jsNullCodec = new JsNullCodec();
    public static final JsBoolCodec jsBoolCodec = new JsBoolCodec();
    public static final JsStringCodec jsStringCodec = new JsStringCodec();
    public static final JsNumberCodec jsNumberCodec = new JsNumberCodec();
    public static final JsArrayCodec jsArrayCodec = new JsArrayCodec();
    public static final JsObjectCodec jsObjectCodec = new JsObjectCodec();
    public static final JsValueCodec jsValueCodec = new JsValueCodec();

    public static <CORE extends CodecCore<InStream, OutStream, Config>> CORE registerAll(CORE core) {
        core.registerCodec(JsNull.class, jsNullCodec);
        core.registerCodec(JsBool.class, jsBoolCodec);
        core.registerCodec(JsString.class, jsStringCodec);
        core.registerCodec(JsNumber.class, jsNumberCodec);
        core.registerCodec(JsArray.class, jsArrayCodec);
        core.registerCodec(JsObject.class, jsObjectCodec);
        core.registerCodec(JsValue.class, jsValueCodec);
        return core;
    }

    public static class JsNullCodec implements Codec<JsNull, InStream, OutStream, Config> {
        @Override
        public Class<JsNull> type() {
            return JsNull.class;
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                JsNull value,
                OutStream out) {
            return out.writeNull();
        }

        @Override
        public JsNull decode(
                CodecCoreEx<InStream, OutStream, Config> core,
                InStream in) {
            in.readNull();
            return JSAPI.nul();
        }

        @Override
        public JsNull decodeWithCheck(
                CodecCoreEx<InStream, OutStream, Config> core,
                InStream in) {
            return decode(core, in);
        }
    }

    public static class JsBoolCodec implements Codec<JsBool, InStream, OutStream, Config> {
        @Override
        public Class<JsBool> type() {
            return JsBool.class;
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                JsBool value,
                OutStream out) {
            return out.writeBoolean(value.value());
        }

        @Override
        public JsBool decode(
                CodecCoreEx<InStream, OutStream, Config> core,
                InStream in) {
            return JSAPI.bool(in.readBoolean());
        }
    }

    public static class JsStringCodec implements Codec<JsString, InStream, OutStream, Config> {
        @Override
        public Class<JsString> type() {
            return JsString.class;
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                JsString value,
                OutStream out) {
            return out.writeString(value.value());
        }

        @Override
        public JsString decode(
                CodecCoreEx<InStream, OutStream, Config> core,
                InStream in) {
            return JSAPI.str(in.readString());
        }
    }

    public static class JsNumberCodec implements Codec<JsNumber, InStream, OutStream, Config> {
        @Override
        public Class<JsNumber> type() {
            return JsNumber.class;
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                JsNumber value,
                OutStream out) {
            return out.writeNumber(value.value());
        }

        @Override
        public JsNumber decode(
                CodecCoreEx<InStream, OutStream, Config> core,
                InStream in) {
            return JSAPI.num(in.readNumber());
        }
    }

    public static class JsArrayCodec implements Codec<JsArray, InStream, OutStream, Config> {
        @Override
        public Class<JsArray> type() {
            return JsArray.class;
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                JsArray value,
                OutStream out) {
            out.startArray();
            value.forEach(jsv -> jsValueCodec.encode(core, jsv, out));
            return out.endArray();
        }

        @Override
        public JsArray decode(
                CodecCoreEx<InStream, OutStream, Config> core,
                InStream in) {
            JsValue[] arr = new JsValue[core.config().defaultArraySize()];

            in.startArray();
            int i = 0;
            while (in.notEOF() && in.currentEventType() != JsonEvent.Type.ARRAY_END) {
                if (i == arr.length) {
                    arr = Arrays.copyOf(arr, core.config().resizeArray(arr.length));
                }
                arr[i++] = jsValueCodec.decode(core, in);
            }
            in.endArray();

            return JSAPI.arr(Arrays.copyOf(arr, i));
        }
    }

    public static class JsObjectCodec implements Codec<JsObject, InStream, OutStream, Config> {
        @Override
        public Class<JsObject> type() {
            return JsObject.class;
        }

        @Override
        public OutStream encode(
                CodecCoreEx<InStream, OutStream, Config> core,
                JsObject value,
                OutStream out) {
            out.startObject();

            value.forEach(field -> {
                out.writeField(field.name());
                jsValueCodec.encode(core, field.value(), out);
            });

            return out.endObject();
        }

        @Override
        public JsObject decode(
                CodecCoreEx<InStream, OutStream, Config> core,
                InStream in) {
            in.startObject();

            final LinkedHashMap<String, JsValue> map = new LinkedHashMap<>();

            while(in.notEOF() && in.currentEventType() == JsonEvent.Type.FIELD_NAME) {
                final String key = in.readFieldName();
                final JsValue val = jsValueCodec.decode(core, in);
                map.put(key, val);
            }

            in.endObject();

            return JSAPI.obj(map);
        }
    }

    @Override
    public Class<JsValue> type() {
        return JsValue.class;
    }

    @Override
    public OutStream encodeWithCheck(
            CodecCoreEx<InStream, OutStream, Config> core,
            JsValue value,
            OutStream out) {
        return encode(core, value, out);
    }

    public OutStream encode(CodecCoreEx<InStream, OutStream, Config> core, JsValue jsv, OutStream out) {
        switch (jsv.type()) {
            case ARRAY:
                return jsArrayCodec.encode(core, (JsArray)jsv, out);
            case BOOL:
                return jsBoolCodec.encode(core, (JsBool)jsv, out);
            case OBJECT:
                return jsObjectCodec.encode(core, (JsObject) jsv, out);
            case NUMBER:
                return jsNumberCodec.encode(core, (JsNumber) jsv, out);
            case NULL:
                return jsNullCodec.encode(core, (JsNull) jsv, out);
            case STRING:
                return jsStringCodec.encode(core, (JsString) jsv, out);
            default:
                throw new IllegalArgumentException("Unrecognised JsValue type - " + jsv.type());
        }
    }

    @Override
    public JsValue decodeWithCheck(
            CodecCoreEx<InStream, OutStream, Config> core,
            InStream in) {
        if (core.format().decodeNull(in)) {
            return JSAPI.nul();
        } else {
            return decode(core, in);
        }
    }

    public JsValue decode(CodecCoreEx<InStream, OutStream, Config> core, InStream in) {
        switch (in.currentEventType()) {
            case ARRAY_START:
                return jsArrayCodec.decode(core, in);
            case FALSE:
            case TRUE:
                return jsBoolCodec.decode(core, in);
            case NULL:
                return jsNullCodec.decode(core, in);
            case NUMBER:
                return jsNumberCodec.decode(core, in);
            case OBJECT_START:
                return jsObjectCodec.decode(core, in);
            case STRING:
                return jsStringCodec.decode(core, in);
            default:
                throw new IllegalArgumentException("Unexpected event type - " + in.currentEventType());
        }
    }
}
