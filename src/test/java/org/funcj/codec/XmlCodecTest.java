package org.funcj.codec;

import org.funcj.codec.TestDataBase.NoEmptyCtor;
import org.funcj.codec.xml.XmlCodecCore;
import org.funcj.control.Exceptions;
import org.junit.Assert;
import org.w3c.dom.*;

import javax.xml.parsers.*;

import static org.funcj.codec.xml.XmlUtils.nodeToString;

public class XmlCodecTest extends TestBase {
    final static XmlCodecCore codec = new XmlCodecCore();

    public static final DocumentBuilder docBuilder;

    static {
        docBuilder = (Exceptions.wrap(
                () -> DocumentBuilderFactory.newInstance().newDocumentBuilder()));
        codec.registerTypeConstructor(NoEmptyCtor.class, () -> NoEmptyCtor.create(false));
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {

        final Document doc = docBuilder.newDocument();

        final Element elem = codec.encode(clazz, val, doc.createElement(clazz.getSimpleName()));

        final T val2 = codec.decode(clazz, elem);

        if (!val.equals(val2)) {
            java.lang.System.out.println(nodeToString(doc, true));
        }

        Assert.assertEquals(val, val2);
    }
}
