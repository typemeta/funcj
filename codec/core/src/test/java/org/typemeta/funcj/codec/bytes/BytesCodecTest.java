package org.typemeta.funcj.codec.bytes;

import org.junit.Assert;
import org.typemeta.funcj.codec.Codecs;
import org.typemeta.funcj.codec.TestBase;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class BytesCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final ByteCodecCore codec = prepareCodecCore(Codecs.byteCodec());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        codec.encode(clazz, val, baos);

        final byte[] ba = baos.toByteArray();

        if (printData()) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        if (printSizes()) {
            System.out.println("Encoded bytes stream " + clazz.getSimpleName() + " data size = " + ba.length + " bytes");
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final T val2 = codec.decode(clazz, bais);

        if (!printData() && !val.equals(val2)) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        Assert.assertEquals(val, val2);
    }
}
