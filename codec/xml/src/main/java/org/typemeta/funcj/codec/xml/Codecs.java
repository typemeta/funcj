package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.xmlnode.*;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs extends org.typemeta.funcj.codec.Codecs {

    /**
     * Construct and return a new instance of a {@link XmlCodecCore}.
     * @return the new {@code XmlCodecCore}
     */
    public static XmlCodecCore xmlCodec() {
        return xmlCodec(new XmlConfigImpl.BuilderImpl());
    }

    public static XmlCodecCore xmlCodec(CodecConfig.Builder<?, XmlTypes.Config> cfgBldr) {
        return registerAll(cfgBldr, XmlCodecCore::new);
    }

    /**
     * Construct and return a new instance of a {@link XmlCodecCore}.
     * @return the new {@code XmlCodecCore}
     */
    public static XmlNodeCodecCore xmlNodeCodec() {
        return xmlNodeCodec(new XmlNodeConfigImpl.BuilderImpl());
    }

    public static XmlNodeCodecCore xmlNodeCodec(CodecConfig.Builder<?, XmlNodeConfig> cfgBldr) {
        return registerAll(cfgBldr, XmlNodeCodecCore::new);
    }
}
