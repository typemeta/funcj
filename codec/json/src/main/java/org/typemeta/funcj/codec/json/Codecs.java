package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.CodecConfig;
import org.typemeta.funcj.codec.jsonnode.*;
import org.typemeta.funcj.json.model.*;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs extends org.typemeta.funcj.codec.Codecs {

    /**
     * Construct and return a new instance of a {@link JsonCodecCore}.
     * @return      the JSON codec
     */
    public static JsonCodecCore jsonCodec() {
        return jsonCodec(new JsonConfigImpl.BuilderImpl());
    }

    public static JsonCodecCore jsonCodec(CodecConfig.Builder<JsonTypes.Config> cfgBldr) {
        cfgBldr.registerAllowedPackage(JsArray.class.getPackage());
        final JsonCodecCore core = registerAll(cfgBldr, JsonCodecCore::new);
        return JsValueCodec.registerAll(core);
    }

    /**
     * Construct and return a new instance of a {@link JsonNodeCodecCore}.
     * @return      the JSON codec
     */
    public static JsonNodeCodecCore jsonNodeCodec() {
        return jsonNodeCodec(new JsonNodeConfigImpl.BuilderImpl());
    }

    public static JsonNodeCodecCore jsonNodeCodec(CodecConfig.Builder<JsonNodeConfig> cfgBldr) {
        return registerAll(cfgBldr, JsonNodeCodecCore::new);
    }
}
