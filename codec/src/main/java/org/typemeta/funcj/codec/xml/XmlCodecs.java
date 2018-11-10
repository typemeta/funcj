package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.Codecs;

public class XmlCodecs {
    public static XmlCodecCore registerAll(XmlCodecCore core) {
        return Codecs.registerAll(core);
    }
}
