package org.typemeta.funcj.codec.bytes;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;

import javax.xml.bind.DatatypeConverter;
import java.io.*;

public class ByteCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final ByteCodecCore codec = prepareCodecCore(Codecs.byteCodec());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        codec.encode(clazz, val, baos);

        final byte[] ba = baos.toByteArray();
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final T val2 = codec.decode(clazz, bais);

        if (printData || !val.equals(val2)) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        Assert.assertEquals(val, val2);
    }
}
