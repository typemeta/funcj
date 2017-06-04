package org.funcj.codec;

import org.funcj.codec.TestDataBase.NoPublicEmptyCtor;
import org.funcj.codec.xml.*;
import org.junit.Assert;
import org.w3c.dom.*;

import java.util.Optional;

import static org.funcj.codec.xml.XmlUtils.nodeToString;

public class XmlCodecTest extends TestBase {
    final static XmlCodecCore codec = new XmlCodecCore();

    static {
        codec.registerCodec(NoPublicEmptyCtor.class, new NoPublicEmptyCtorCodec<>(codec));
        codec.registerCodec((Class)Optional.class, new OptionalCodec<>(codec));
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        codec.setNewDocument();
        final Node node = codec.encode(clazz, val, "test");


        final T val2 = codec.decode(clazz, node);

        if (!val.equals(val2)) {
            java.lang.System.out.println(nodeToString(codec.doc, true));
        }

        Assert.assertEquals(val, val2);
    }

    static class OptionalCodec<T> implements Codec<Optional<T>, Node> {

        private final XmlCodecCore core;

        OptionalCodec(XmlCodecCore core) {
            this.core = core;
        }

        @Override
        public Node encode(Optional<T> val, Node out) {
            return val.map(t -> core.dynamicCodec().encode(t, out))
                    .orElseGet(() -> XmlUtils.setAttrValue((Element)out, "empty", "1"));
        }

        @Override
        public Optional<T> decode(Class<Optional<T>> dynType, Node in) {
            final Element elem = (Element)in;
            if (!elem.getAttribute("empty").isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of((T) core.dynamicCodec().decode(in));
            }
        }
    }
}
