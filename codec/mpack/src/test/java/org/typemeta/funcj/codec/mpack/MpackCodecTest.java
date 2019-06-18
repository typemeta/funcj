package org.typemeta.funcj.codec.mpack;

import org.junit.Assert;
import org.typemeta.funcj.codec.TestBase;

import javax.xml.bind.DatatypeConverter;
import java.io.*;

public class MpackCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final MpackConfigImpl.BuilderImpl cfgBldr = new MpackConfigImpl.BuilderImpl();
        final MpackCodecCore codec = prepareCodecCore(cfgBldr, Codecs::mpackCodec);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        codec.encode(clazz, val, baos);

        final byte[] ba = baos.toByteArray();

        if (printData()) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        if (printSizes()) {
            System.out.println("Encoded MessagePack " + clazz.getSimpleName() + " data size = " + ba.length + " bytes");
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final T val2 = codec.decode(clazz, bais);

        if (!printData() && !val.equals(val2)) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        Assert.assertEquals(val, val2);
    }
}
