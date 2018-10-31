package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecConfig;

/**
 * Interface for classes which provide configuration information
 * for {@link JsonCodecCore} implementations.
 */
public interface JsonCodecConfig extends CodecConfig {

    String typeFieldName();

    String keyFieldName();

    String valueFieldName();
}
