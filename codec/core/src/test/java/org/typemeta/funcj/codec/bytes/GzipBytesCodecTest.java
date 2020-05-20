package org.typemeta.funcj.codec.bytes;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;

import java.io.*;
import java.util.zip.*;

public class GzipBytesCodecTest extends TestBase {

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) throws Exception {
        final ByteConfig.Builder cfgBldr = ByteConfig.builder();
        final ByteCodecCore codec = prepareCodecCore(cfgBldr, Codecs::byteCodec);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final GZIPOutputStream gzos = new GZIPOutputStream(baos);

        codec.encode(clazz, val, gzos);
        gzos.close();

        final byte[] ba = baos.toByteArray();

        if (printData()) {
            System.out.println(TestDataUtils.printHexBinary(ba));
        }

        if (printSizes()) {
            System.out.println("Encoded gzipped byte stream " + clazz.getSimpleName() + " data size = " + ba.length + " bytes");
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final GZIPInputStream gzis = new GZIPInputStream(bais);
        final T val2 = codec.decode(clazz, gzis);

        if (!printData() && !val.equals(val2)) {
            System.out.println(TestDataUtils.printHexBinary(ba));
        }

        Assert.assertEquals(val, val2);
    }
}
