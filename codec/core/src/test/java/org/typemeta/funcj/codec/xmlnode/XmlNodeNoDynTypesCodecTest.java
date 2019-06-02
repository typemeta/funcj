package org.typemeta.funcj.codec.xmlnode;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.StringWriter;

public class XmlNodeNoDynTypesCodecTest extends TestBase {

    public static final DocumentBuilder docBuilder;

    static {
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static <IN, OUT, CFG extends CodecConfig, CC extends CodecCore<IN, OUT, CFG>>
    CC prepareCodecCore(CC core) {
        core.config().registerAllowedPackage(TestTypes.class.getPackage());
        return core;
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final XmlNodeCodecCore codec = prepareCodecCore(Codecs.xmlNodeCodec());

        final Document doc = docBuilder.newDocument();
        final Element out = doc.createElement("Custom");

        final XmlNodeConfig config = codec.config();
        config.dynamicTypeTags(false);
        config.failOnNoTypeConstructor(false);

        codec.encode(clazz, val, out);

        if (printData()) {
            XmlUtils.write(out, System.out, true);
        }

        final String data = XmlUtils.write(out, new StringWriter(), false).toString();

        if (printSizes()) {
            System.out.println("Encoded XML " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        Assert.assertFalse(data.contains(config.typeAttrName()));
    }
}
