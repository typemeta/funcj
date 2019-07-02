package org.typemeta.funcj.codec.xmlnode;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.xml.Codecs;
import org.typemeta.funcj.functions.Functions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

    protected static <IN, OUT, CFG extends CodecConfig, CORE extends CodecCore<IN, OUT, CFG>>
    CORE prepareCodecCore(
            CodecConfig.Builder<?, CFG> cfgBldr,
            Functions.F<CodecConfig.Builder<?, CFG>, CORE> coreBldr
    ) {
        cfgBldr.registerAllowedPackage(TestTypes.class.getPackage());
        return TestBase.prepareCodecCore(cfgBldr, coreBldr);
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final XmlNodeConfigImpl.BuilderImpl cfgBldr =
                XmlNodeTypes.configBuilder()
                        .dynamicTypeTags(false)
                        .failOnNoTypeConstructor(false);

        final XmlNodeCodecCore codec = prepareCodecCore(cfgBldr, Codecs::xmlNodeCodec);

        final Document doc = docBuilder.newDocument();
        final Element out = doc.createElement("Custom");

        codec.encodeImpl(clazz, val, out);

        if (printData()) {
            XmlUtils.write(out, System.out, true);
        }

        final String data = XmlUtils.write(out, new StringWriter(), false).toString();

        if (printSizes()) {
            System.out.println("Encoded XML " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        final XmlNodeConfig config = codec.config();
        Assert.assertFalse(data.contains(config.typeAttrName()));
    }
}
