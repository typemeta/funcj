package org.typemeta.funcj.codec.xml;

import org.junit.*;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.xml.XmlTypes.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class XmlCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) throws IOException {
        final XmlConfigImpl.BuilderImpl cfgBldr = new XmlConfigImpl.BuilderImpl();
        final XmlCodecCore codec = prepareCodecCore(cfgBldr, Codecs::xmlCodec);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        codec.encode(clazz, val, new OutputStreamWriter(baos, StandardCharsets.UTF_8));

        final String data = baos.toString();

        if (printData()) {
            System.out.println(data);
        }

        if (printSizes()) {
            System.out.println("Encoded XML " + clazz.getSimpleName() + " data size = " + data.length() + " chars");
        }

        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

            final T val2 = codec.decode(clazz, bais);

            if (!printData() && !val.equals(val2)) {
                System.out.println(data);
            }

            Assert.assertEquals(val, val2);
        } catch (Exception ex) {
            final Path path  = FileSystems.getDefault().getPath("out.xml");
            System.out.println("Saving file to " + path);
            Files.write(path, baos.toByteArray());
            if (!printData()) {
                System.out.println(data);
            }
            throw ex;
        }
    }

    @Test
    public void testDontFailOnUnrecognisedFields() {
        final CodecConfig.Builder<Config> cfgBldr = new XmlConfigImpl.BuilderImpl();
        cfgBldr.failOnUnrecognisedFields(false);
        final XmlCodecCore codec = prepareCodecCore(cfgBldr, Codecs::xmlCodec);
        final TestTypes.Custom val = new TestTypes.Custom(TestTypes.Init.INIT);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        codec.encode(TestTypes.Custom.class, val, baos);

        final String data = baos.toString();

        final String data2 = data.replace(
                "<flag>true</flag>",
                "<flag>true</flag><test a=\"1\"><value>1.234</value></test>");

        final ByteArrayInputStream bais = new ByteArrayInputStream(data2.getBytes());

        final TestTypes.Custom val2 = codec.decode(TestTypes.Custom.class, bais);

        Assert.assertEquals(val, val2);
    }
}
