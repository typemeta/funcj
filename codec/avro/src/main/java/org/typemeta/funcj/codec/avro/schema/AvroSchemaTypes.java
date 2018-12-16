package org.typemeta.funcj.codec.avro.schema;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.schema.io.OutputImpl;

import java.io.OutputStream;

public abstract class AvroSchemaTypes {

    /**
     * Interface for classes which provide configuration information
     * for {@link AvroSchemaCodecCore} implementations.
     */
    public interface Config extends CodecConfig {
    }

    /**
     * Interface for classes which implement an input stream of bytes
     */
    public interface InStream extends CodecFormat.Input<InStream> {
    }

    /**
     * Interface for classes which implement an output stream of bytes
     */
    public interface OutStream extends CodecFormat.Output<OutStream> {
        OutStream startArray(int size);

        OutStream startMap(int size);
    }

    public static OutStream outputOf(OutputStream os) {
        return new OutputImpl(MessagePack.newDefaultPacker(os));
    }
}
