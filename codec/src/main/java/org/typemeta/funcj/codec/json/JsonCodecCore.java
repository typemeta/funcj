package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecCoreInternal;
import org.typemeta.funcj.codec.json.io.JsonIO;
import org.typemeta.funcj.codec.json.io.JsonIO.*;

import java.io.*;

/**
 * Interface for classes which implement an encoding via JSON.
 */
public interface JsonCodecCore extends CodecCoreInternal<Input, Output> {
    default <T> void encode(Class<T> type, T value, Writer writer) {
        encode(type, value, JsonIO.outputOf(writer));
    }

    default <T> T decode(Class<T> type, Reader reader) {
        return decode(type, JsonIO.inputOf(reader));
    }
}
