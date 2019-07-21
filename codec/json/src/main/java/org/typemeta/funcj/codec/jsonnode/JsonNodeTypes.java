package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.CodecConfig;

public abstract class JsonNodeTypes {
    /**
     * Interface for classes which provide configuration information
     * for {@link JsonNodeCodecCore} implementations.
     */
    public interface Config extends CodecConfig {

        String typeFieldName();

        String keyFieldName();

        String valueFieldName();
    }
}
