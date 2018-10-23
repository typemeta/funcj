package org.typemeta.funcj.codec.xmls;

import org.typemeta.funcj.codec.Codecs;

public class XmlCodecs {
    public static XmlCodecCoreImpl registerAll(XmlCodecCoreImpl core) {
        //core.registerCodec(Optional.class, new JsonCodecs.OptionalCodec(core));
        return Codecs.registerAll(core);
    }
}
