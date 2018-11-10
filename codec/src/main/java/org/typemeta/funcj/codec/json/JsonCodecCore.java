package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.json.io.JsonIO;
import org.typemeta.funcj.codec.json.io.JsonIO.Input;
import org.typemeta.funcj.codec.json.io.JsonIO.Output;

import java.io.Reader;
import java.io.Writer;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public class JsonCodecCore extends CodecCoreDelegate<Input, Output, Config> {

    public JsonCodecCore(JsonCodecFormat format) {
        super(new CodecCoreImpl<>(format));
    }

    public JsonCodecCore(Config config) {
        this(new JsonCodecFormat(config));
    }

    public JsonCodecCore() {
        this(new ConfigImpl());
    }

    /**
     * Encode the supplied value into JSON and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param type      the static type of the value
     * @param value     the value to be encoded
     * @param writer    the output stream to which the JSON is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(Class<? super T> type, T value, Writer writer) {
        delegate.encode(type, value, JsonIO.outputOf(writer));
    }

    /**
     * Encode the supplied value into JSON and write the results to the {@link Writer} object.
     * The static type determines whether type information is written to recover the value's
     * dynamic type.
     * @param value     the value to be encoded
     * @param writer    the output stream to which the JSON is written
     * @param <T>       the static type of the value
     */
    public <T> void encode(T value, Writer writer) {
        encode(Object.class, value, writer);
    }

    /**
     * Decode a value by reading JSON from the supplied {@link Reader} object.
     * @param type      the static type of the value to be decoded.
     * @param reader    the input stream from which JSON is read
     * @param <T>       the static type of the value
     * @return          the decoded value
     */
    public <T> T decode(Class<T> type, Reader reader) {
        return delegate.decode(type, JsonIO.inputOf(reader));
    }
}
