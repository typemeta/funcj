package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.json.model.JSAPI;
import org.typemeta.funcj.json.model.JsValue;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public class JsonCodecCore
        extends CodecCoreDelegate<JsValue, JsValue, JsonTypes.Config>
        implements CodecAPI {

    public JsonCodecCore(JsonCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public JsonCodecCore(JsonTypes.Config config) {
        this(new JsonCodecFormat(config));
    }

    public JsonCodecCore() {
        this(new JsonConfigImpl());
    }

    public <T> JsValue encode(Class<? super T> type, T value) {
        return encode(type, value, JSAPI.nul());
    }

    @Override
    public <T> Writer encode(Class<? super T> type, T value, Writer writer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T decode(Class<? super T> type, Reader reader) {
        throw new UnsupportedOperationException();
    }
}
