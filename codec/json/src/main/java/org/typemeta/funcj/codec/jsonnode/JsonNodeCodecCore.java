package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.impl.*;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.json.parser.JsonParser;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON node values.
 */
public class JsonNodeCodecCore
        extends CodecCoreDelegate<JsValue, JsValue, JsonNodeTypes.Config>
        implements CodecAPI.RW {

    public JsonNodeCodecCore(JsonNodeCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public JsonNodeCodecCore(JsonNodeTypes.Config config) {
        this(new JsonNodeCodecFormat(config));
    }

    public JsonNodeCodecCore() {
        this(new JsonNodeConfig());
    }

    public <T> JsValue encode(Class<? super T> type, T value) {
        return encodeImpl(type, value, JSAPI.nul());
    }

    public <T> T decode(Class<? super T> type, JsValue jsv) {
        return decodeImpl(type, jsv);
    }

    @Override
    public <T> Writer encode(Class<? super T> type, T value, Writer writer) {
        final JsValue jsv = encode(type, value);
        return jsv.write(writer);
    }

    @Override
    public <T> T decode(Class<? super T> type, Reader reader) {
        final JsValue jsv = JsonParser.parse(reader);
        return decodeImpl(type, jsv);
    }
}
