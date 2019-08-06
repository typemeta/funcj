package org.typemeta.funcj.codec.avro;

import org.junit.Assert;
import org.typemeta.funcj.codec.TestBase;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AvroCodecTest extends TestBase {

    static class AvroTestType {
        final boolean p_bo;
        final byte p_by;
        final char p_ch;
        final short p_sh;
        final int p_in;
        final long p_lo;
        final float p_fl;
        final double p_du;

        final Boolean b_bo;
        final Byte b_by;
        final Character b_ch;
        final Short b_sh;
        final Integer b_in;
        final Long b_lo;
        final Float b_fl;
        final Double b_du;

        final boolean p_bo;
        final byte p_by;
        final char p_ch;
        final short p_sh;
        final int p_in;
        final long p_lo;
        final float p_fl;
        final double p_du;

        final Boolean b_bo;
        final Byte b_by;
        final Character b_ch;
        final Short b_sh;
        final Integer b_in;
        final Long b_lo;
        final Float b_fl;
        final Double b_du;
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final AvroConfig.Builder cfgBldr = AvroConfig.builder();
        final AvroCodecCore codec = prepareCodecCore(cfgBldr, Codecs::avroCodec);

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
