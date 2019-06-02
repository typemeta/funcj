package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.CodecConfig;

/**
 * Interface for classes which provide configuration information
 * for {@link JsonNodeCodecCore} implementations.
 */
public interface JsonNodeConfig extends CodecConfig {

    String typeFieldName();

    String keyFieldName();

    String valueFieldName();
}
