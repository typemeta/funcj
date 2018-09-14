package org.typemeta.funcj.codec.jsons;

import org.typemeta.funcj.codec.CodecCoreIntl;
import org.typemeta.funcj.codec.jsons.JsonIO.Input;
import org.typemeta.funcj.codec.jsons.JsonIO.Output;

import java.io.Reader;
import java.io.Writer;

/**
 * Interface for classes which implement an encoding into JSON.
 */
public interface JsonCodecCore extends CodecCoreIntl<Input, Output> {
    default <T> void encode(Class<T> type, T value, Writer writer) {
        encode(type, value, new JsonGenerator(writer));
    }

    default <T> T decode(Class<T> type, Reader reader) {
        return decode(type, new JsonParser(reader));
    }
}
