package org.typemeta.funcj.codec.byteio;

import org.junit.Assert;
import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.util.Exceptions;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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
    protected <T> void roundTrip(T val, Class<T> clazz) {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ByteIO.Output byteOut = ByteIO.ofOutputStream(baos);

        codec.encode(clazz, val, byteOut);

        final byte[] ba = baos.toByteArray();
        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final ByteIO.Input byteIn = ByteIO.ofInputStream(bais);
        final T val2 = codec.decode(clazz, byteIn);

        if (printData || !val.equals(val2)) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        Assert.assertEquals(val, val2);
    }
}
