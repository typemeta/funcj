package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.avro.io.*;

import java.io.*;

public abstract class AvroTypes {

    /**
     * Interface for classes which provide configuration information
     * for {@link AvroCodecCore} implementations.
     */
    public interface Config extends CodecConfig {
    }

    public static Schema outputOf(OutputStream os) {
        return new OutputImpl(MessagePack.newDefaultPacker(os));
    }
}
