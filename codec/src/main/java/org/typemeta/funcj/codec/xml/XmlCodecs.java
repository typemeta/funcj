package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.Codecs;

public class XmlCodecs {
    public static XmlCodecCore registerAll(XmlCodecCore core) {
        //core.registerCodec(Optional.class, new JsonCodecs.OptionalCodec(core));
        return Codecs.registerAll(core);
    }
}
