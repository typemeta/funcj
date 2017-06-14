package org.funcj.codec.xml;

import org.funcj.codec.*;
import org.w3c.dom.Element;

import java.util.Optional;

public class XmlCodecs {
    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, Element> {

        private static final String optAttrName = "optional";
        private static final String emptyAttrVal = "empty";
        private static final String presAttrVal = "present";

        protected OptionalCodec(CodecCore<Element> core) {
            super(core);
        }

        @Override
        public Element encode(Optional<T> val, Element enc) {
            return val.map(t -> {
                XmlUtils.setAttrValue(enc, optAttrName, presAttrVal);
                return core.dynamicCodec().encode(t, enc);
            }).orElseGet(() -> XmlUtils.setAttrValue(enc, optAttrName, emptyAttrVal));
        }

        @Override
        public Optional<T> decode(Element enc) {
            if (enc.getAttribute(optAttrName).equals(emptyAttrVal)) {
                return Optional.empty();
            } else {
                return Optional.of((T)core.dynamicCodec().decode(enc));
            }
        }
    }
}
