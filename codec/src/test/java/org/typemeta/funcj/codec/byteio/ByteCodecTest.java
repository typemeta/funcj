package org.typemeta.funcj.codec.byteio;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.util.Exceptions;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.*;
import java.io.*;

public class ByteCodecTest extends TestBase {
    final static ByteCodecCore codec = Codecs.byteCodec();

    public static final DocumentBuilder docBuilder;

    static {
        docBuilder = Exceptions.wrap(
                () -> DocumentBuilderFactory.newInstance().newDocumentBuilder()
        );
    }
    static {
        codec.registerTypeConstructor(TestTypes.NoEmptyCtor.class, () -> TestTypes.NoEmptyCtor.create(false));
        registerCustomCodec(codec);
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) throws Exception {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ByteIO byteOut = ByteIO.of(new DataOutputStream(baos));

        codec.encode(clazz, val, byteOut);

        final byte[] ba = baos.toByteArray();
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final ByteIO byteIn = ByteIO.of(new DataInputStream(bais));
        final T val2 = codec.decode(clazz, byteIn);

        if (printData || !val.equals(val2)) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        Assert.assertEquals(val, val2);
    }
}
