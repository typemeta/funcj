package org.funcj.codec.xml;

import org.funcj.codec.*;
import org.funcj.json.JSValue;
import org.w3c.dom.Element;

import java.util.Optional;

/**
 * Interface for classes which implement an encoding into XML,
 * via the {@link Element} representation for XML values.
 */
public interface XmlCodecCore extends CodecCore<Element> {

    /**
     * Construct and return a new instance of a {@code XmlCodecCore}.
     * @return the new a {@code XmlCodecCore}
     */
    static XmlCodecCore of() {
        final XmlCodecCoreImpl codec = new XmlCodecCoreImpl();
        codec.registerCodec(Optional.class, new XmlCodecs.OptionalCodec(codec));
        return Codecs.registerAll(codec);
    }
}
