package io.typemeta.funcj.codec.xml;

import io.typemeta.funcj.codec.*;
import io.typemeta.funcj.control.Exceptions;
import io.typemeta.funcj.codec.TestTypes.NoEmptyCtor;
import org.junit.Assert;
import org.w3c.dom.*;

import javax.xml.parsers.*;

import static io.typemeta.funcj.codec.xml.XmlUtils.nodeToString;

public class XmlCodecTest extends TestBase {
    final static XmlCodecCore codec = Codecs.xmlCodec();

    public static final DocumentBuilder docBuilder;

    static {
        docBuilder = Exceptions.wrap(
                () -> DocumentBuilderFactory.newInstance().newDocumentBuilder());
    }
    static {
        codec.registerTypeConstructor(NoEmptyCtor.class, () -> NoEmptyCtor.create(false));
        registerCustomCodec(codec);
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {

        final Document doc = docBuilder.newDocument();

        final Element elem = codec.encode(clazz, val, doc.createElement(clazz.getSimpleName()));

        final T val2 = codec.decode(clazz, elem);

        if (printData || !val.equals(val2)) {
            java.lang.System.out.println(nodeToString(elem, true));
        }

        Assert.assertEquals(val, val2);
    }
}
