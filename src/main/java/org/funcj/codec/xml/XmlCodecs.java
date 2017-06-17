package org.funcj.codec.xml;

import org.funcj.codec.*;
import org.w3c.dom.Element;

import java.util.Optional;

public class XmlCodecs {
    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, Element> {

        private static final String emptyAttrVal = "empty";
        private static final String presAttrVal = "present";

        private final String attrName;

        protected OptionalCodec(XmlCodecCore core) {
            super(core);
            attrName = core.metaAttrName();
        }

        @Override
        public Element encode(Optional<T> val, Element enc) {
            return val.map(t -> {
                XmlUtils.setAttrValue(enc, attrName, presAttrVal);
                return core.dynamicCodec().encode(t, enc);
            }).orElseGet(() -> XmlUtils.setAttrValue(enc, attrName, emptyAttrVal));
        }

        @Override
        public Optional<T> decode(Element enc) {
            if (enc.getAttribute(attrName).equals(emptyAttrVal)) {
                return Optional.empty();
            } else {
                return Optional.of((T)core.dynamicCodec().decode(enc));
            }
        }
    }
}
