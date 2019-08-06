package org.typemeta.funcj.codec.avro;

import org.junit.*;
import org.typemeta.funcj.codec.TestBase;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;

public class AvroCodecTest {

    static class AvroTestType {
        static class Prims {
            final boolean bo;
            final byte by;
            final char ch;
            final short sh;
            final int in;
            final long lo;
            final float fl;
            final double du;

            Prims(
                    boolean bo,
                    byte by,
                    char ch,
                    short sh,
                    int in,
                    long lo,
                    float fl,
                    double du
            ) {
                this.bo = bo;
                this.by = by;
                this.ch = ch;
                this.sh = sh;
                this.in = in;
                this.lo = lo;
                this.fl = fl;
                this.du = du;
            }

            Prims() {
                this.bo = false;
                this.by = 0;
                this.ch = 0;
                this.sh = 0;
                this.in = 0;
                this.lo = 0;
                this.fl = 0;
                this.du = 0;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Prims prims = (Prims) o;
                return bo == prims.bo &&
                        by == prims.by &&
                        ch == prims.ch &&
                        sh == prims.sh &&
                        in == prims.in &&
                        lo == prims.lo &&
                        Float.compare(prims.fl, fl) == 0 &&
                        Double.compare(prims.du, du) == 0;
            }
        }

        static class PrimArrs {
            final boolean[] bo;
            final byte[] by;
            final char[] ch;
            final short[] sh;
            final int[] in;
            final long[] lo;
            final float[] fl;
            final double[] du;

            PrimArrs(
                    boolean[] bo,
                    byte[] by,
                    char[] ch,
                    short[] sh,
                    int[] in,
                    long[] lo,
                    float[] fl,
                    double[] du
            ) {
                this.bo = bo;
                this.by = by;
                this.ch = ch;
                this.sh = sh;
                this.in = in;
                this.lo = lo;
                this.fl = fl;
                this.du = du;
            }

            PrimArrs() {
                this.bo = null;
                this.by = null;
                this.ch = null;
                this.sh = null;
                this.in = null;
                this.lo = null;
                this.fl = null;
                this.du = null;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                PrimArrs primArrs = (PrimArrs) o;
                return Arrays.equals(bo, primArrs.bo) &&
                        Arrays.equals(by, primArrs.by) &&
                        Arrays.equals(ch, primArrs.ch) &&
                        Arrays.equals(sh, primArrs.sh) &&
                        Arrays.equals(in, primArrs.in) &&
                        Arrays.equals(lo, primArrs.lo) &&
                        Arrays.equals(fl, primArrs.fl) &&
                        Arrays.equals(du, primArrs.du);
            }
        }

        static class Boxed {
            final Boolean bo;
            final Byte by;
            final Character ch;
            final Short sh;
            final Integer in;
            final Long lo;
            final Float fl;
            final Double du;

            final String st;

            Boxed(
                    Boolean bo,
                    Byte by,
                    Character ch,
                    Short sh,
                    Integer in,
                    Long lo,
                    Float fl,
                    Double du,
                    String st
            ) {
                this.bo = bo;
                this.by = by;
                this.ch = ch;
                this.sh = sh;
                this.in = in;
                this.lo = lo;
                this.fl = fl;
                this.du = du;
                this.st = st;
            }

            Boxed() {
                this.bo = null;
                this.by = null;
                this.ch = null;
                this.sh = null;
                this.in = null;
                this.lo = null;
                this.fl = null;
                this.du = null;
                this.st = null;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                Boxed boxed = (Boxed) o;
                return Objects.equals(bo, boxed.bo) &&
                        Objects.equals(by, boxed.by) &&
                        Objects.equals(ch, boxed.ch) &&
                        Objects.equals(sh, boxed.sh) &&
                        Objects.equals(in, boxed.in) &&
                        Objects.equals(lo, boxed.lo) &&
                        Objects.equals(fl, boxed.fl) &&
                        Objects.equals(du, boxed.du) &&
                        Objects.equals(st, boxed.st);
            }

            @Override
            public int hashCode() {
                return Objects.hash(bo, by, ch, sh, in, lo, fl, du, st);
            }
        }

        static class BoxedArrs {
            final Boolean[] bo;
            final Byte[] by;
            final Character[] ch;
            final Short[] sh;
            final Integer[] in;
            final Long[] lo;
            final Float[] fl;
            final Double[] du;

            final String st;

            BoxedArrs(
                    Boolean[] bo,
                    Byte[] by,
                    Character[] ch,
                    Short[] sh,
                    Integer[] in,
                    Long[] lo,
                    Float[] fl,
                    Double[] du,
                    String st
            ) {
                this.bo = bo;
                this.by = by;
                this.ch = ch;
                this.sh = sh;
                this.in = in;
                this.lo = lo;
                this.fl = fl;
                this.du = du;
                this.st = st;
            }

            BoxedArrs() {
                this.bo = null;
                this.by = null;
                this.ch = null;
                this.sh = null;
                this.in = null;
                this.lo = null;
                this.fl = null;
                this.du = null;
                this.st = null;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }
                BoxedArrs boxedArrs = (BoxedArrs) o;
                return Arrays.equals(bo, boxedArrs.bo) &&
                        Arrays.equals(by, boxedArrs.by) &&
                        Arrays.equals(ch, boxedArrs.ch) &&
                        Arrays.equals(sh, boxedArrs.sh) &&
                        Arrays.equals(in, boxedArrs.in) &&
                        Arrays.equals(lo, boxedArrs.lo) &&
                        Arrays.equals(fl, boxedArrs.fl) &&
                        Arrays.equals(du, boxedArrs.du) &&
                        Objects.equals(st, boxedArrs.st);
            }
        }

        final Prims prims;
        final PrimArrs primArrs;
        final Boxed boxed;
        final BoxedArrs boxedArrs;

        enum Colour {RED, GREEN, BLUE}
        final Colour en;
        final Colour[] a_en;

        final List<LocalDate> l_d;
        final Set<String> s_s;
        final Map<String, LocalDate> m_s_d;
        final Optional<LocalDate> o_d;

        public AvroTestType(
                Prims prims,
                PrimArrs primArrs,
                Boxed boxed,
                BoxedArrs boxedArrs,
                Colour en,
                Colour[] a_en,
                List<LocalDate> l_d,
                Set<String> s_s,
                Map<String, LocalDate> m_s_d,
                Optional<LocalDate> o_d
        ) {
            this.prims = prims;
            this.primArrs = primArrs;
            this.boxed = boxed;
            this.boxedArrs = boxedArrs;
            this.en = en;
            this.a_en = a_en;
            this.l_d = l_d;
            this.s_s = s_s;
            this.m_s_d = m_s_d;
            this.o_d = o_d;
        }

        public AvroTestType() {
            this.prims = null;
            this.primArrs = null;
            this.boxed = null;
            this.boxedArrs = null;
            this.en = null;
            this.a_en = null;
            this.l_d = null;
            this.s_s = null;
            this.m_s_d = null;
            this.o_d = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AvroTestType that = (AvroTestType) o;
            return Objects.equals(prims, that.prims) &&
                    Objects.equals(primArrs, that.primArrs) &&
                    Objects.equals(boxed, that.boxed) &&
                    Objects.equals(boxedArrs, that.boxedArrs) &&
                    en == that.en &&
                    Arrays.equals(a_en, that.a_en) &&
                    Objects.equals(l_d, that.l_d) &&
                    Objects.equals(s_s, that.s_s) &&
                    Objects.equals(m_s_d, that.m_s_d) &&
                    Objects.equals(o_d, that.o_d);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(prims, primArrs, boxed, boxedArrs, en, l_d, s_s, m_s_d, o_d);
            result = 31 * result + Arrays.hashCode(a_en);
            return result;
        }
    }

    protected boolean printData() {
        return false;
    }

    protected boolean printSizes() {
        return false;
    }

    @Test
    public void testNulls() {
        roundTrip(new AvroTestType(), AvroTestType.class);
    }

    @Test
    public void testNonNull() {
        roundTrip(new AvroTestType(), AvroTestType.class);
    }

    protected <T> void roundTrip(T val, Class<T> clazz) {
        final AvroConfig.Builder cfgBldr = AvroConfig.builder();
        final AvroCodecCore codec = TestBase.prepareCodecCore(cfgBldr, Codecs::avroCodec);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        codec.encode(clazz, val, baos);

        final byte[] ba = baos.toByteArray();

        if (printData()) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        if (printSizes()) {
            System.out.println("Encoded Avro " + clazz.getSimpleName() + " data size = " + ba.length + " bytes");
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        final T val2 = codec.decode(clazz, bais);

        if (!printData() && !val.equals(val2)) {
            System.out.println(DatatypeConverter.printHexBinary(ba));
        }

        Assert.assertEquals(val, val2);
    }
}
