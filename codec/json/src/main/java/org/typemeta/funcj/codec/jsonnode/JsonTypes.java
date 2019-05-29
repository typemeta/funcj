package org.typemeta.funcj.codec.jsonnode;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.jsonnode.JsonCodecCore;


import java.io.*;
import java.math.BigDecimal;

public class JsonTypes {

    /**
     * Interface for classes which provide configuration information
     * for {@link JsonCodecCore} implementations.
     */
    public interface Config extends CodecConfig {

        String typeFieldName();

        String keyFieldName();

        String valueFieldName();
    }
}
