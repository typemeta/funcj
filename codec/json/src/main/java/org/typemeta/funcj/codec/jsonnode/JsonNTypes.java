package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.CodecConfig;

public class JsonNTypes {

    /**
     * Interface for classes which provide configuration information
     * for {@link JsonNCodecCore} implementations.
     */
    public interface Config extends CodecConfig {

        String typeFieldName();

        String keyFieldName();

        String valueFieldName();
    }
}
