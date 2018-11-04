package org.typemeta.funcj.codec.bytes;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.zip.*;

public class GzipByteCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) throws Exception {
        final ByteCodecCore codec = prepareCodecCore(Codecs.byteCodec());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream gzos = new GZIPOutputStream(baos);

        codec.encode(clazz, val, gzos);
        gzos.close();

        final byte[] ba = baos.toByteArray();
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final GZIPInputStream gzis = new GZIPInputStream(bais);
        final T val2 = codec.decode(clazz, gzis);

        if (printData || !val.equals(val2)) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        Assert.assertEquals(val, val2);
    }
}
