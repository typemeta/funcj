package org.typemeta.funcj.codec.xmlnode;

import org.junit.*;
import org.typemeta.funcj.codec.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;

public class XmlNodeCodecTest extends TestBase {

    public static final DocumentBuilder docBuilder;

    static {
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final XmlNodeCodecCore codec = prepareCodecCore(Codecs.xmlNodeCodec());

        final Document doc = docBuilder.newDocument();
        final Element out = doc.createElement("Custom");

        codec.encode(clazz, val, out);

        if (printData()) {
            XmlUtils.write(out, System.out, true);
        }

        if (printSizes()) {
            final String data = XmlUtils.write(out, new StringWriter(), false).toString();
            System.out.println("Encoded XML " + clazz.getSimpleName() + " data size = " + data  .length() + " chars");
        }

        final T val2 = codec.decode(clazz, out);

        if (!printData() && !val.equals(val2)) {
            XmlUtils.write(out, System.out, true);
        }

        Assert.assertEquals(val, val2);
    }

    @Test
    public void testDontFailOnUnrecognisedFields() {
        final XmlNodeCodecCore codec = prepareCodecCore(Codecs.xmlNodeCodec());

        codec.config().failOnUnrecognisedFields(false);
        final TestTypes.Custom val = new TestTypes.Custom(TestTypes.Init.INIT);

        final Document doc = docBuilder.newDocument();
        final Element out = doc.createElement("Custom");

        codec.encode(TestTypes.Custom.class, val, out);

        final TestTypes.Custom val2 = codec.decode(TestTypes.Custom.class, out);

        Assert.assertEquals(val, val2);
    }
}
