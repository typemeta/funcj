package org.typemeta.funcj.codec.xml;

import org.junit.*;
import org.typemeta.funcj.codec.*;

import java.io.*;
import java.nio.file.*;

public class XmlCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) throws IOException {
        final XmlCodecCore codec = prepareCodecCore(Codecs.xmlCodec());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        codec.encode(clazz, val, baos);

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
        final XmlCodecCore codec = prepareCodecCore(Codecs.xmlCodec());
        codec.config().failOnUnrecognisedFields(false);
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
