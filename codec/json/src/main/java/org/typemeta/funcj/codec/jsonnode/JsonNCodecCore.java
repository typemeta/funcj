package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.json.model.JSAPI;
import org.typemeta.funcj.json.model.JsValue;

import java.io.Reader;
import java.io.Writer;

/**
 * Interface for classes which implement an encoding via JSON node values.
 */
public class JsonNCodecCore
        extends CodecCoreDelegate<JsValue, JsValue, JsonNTypes.Config>
        implements CodecAPI {

    public JsonNCodecCore(JsonNCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public JsonNCodecCore(JsonNTypes.Config config) {
        this(new JsonNCodecFormat(config));
    }

    public JsonNCodecCore() {
        this(new JsonNConfigImpl());
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
