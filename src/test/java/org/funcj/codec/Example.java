package org.funcj.codec;

import java.util.*;

public abstract class Example {
    public static class ZBase {
        boolean b = false;
        boolean b2 = true;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ZBase base = (ZBase) o;
            return b == base.b &&
                    b2 == base.b2;
        }

        @Override
        public String toString() {
            return "ZBase{" +
                    "b=" + b +
                    ", b2=" + b2 +
                    '}';
        }
    }

    public static class Derived extends ZBase {
        final boolean fb = false;

        boolean b = true;
        Boolean bb = Boolean.FALSE;
        boolean[] ba = {true, false};
        Boolean[] bba = {false, true, false};
        ZBase[] za = {new ZBase()};
        Object[] oa = {new ZBase()};

        ZBase nul = null;
        ZBase z = new ZBase();
        Object o = new ZBase();

        public Derived() {
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;
            if (!super.equals(rhs)) return false;
            Derived derived = (Derived) rhs;
            return fb == derived.fb &&
                    b == derived.b &&
                    Objects.equals(bb, derived.bb) &&
                    Arrays.equals(ba, derived.ba) &&
                    Arrays.equals(bba, derived.bba) &&
                    Arrays.equals(za, derived.za) &&
                    Arrays.equals(oa, derived.oa) &&
                    Objects.equals(nul, derived.nul) &&
                    Objects.equals(z, derived.z) &&
                    Objects.equals(o, derived.o);
        }

        @Override
        public String toString() {
            return "Derived{" +
                    "\n\tb=" + b +
                    ", \n\tb2=" + b2 +
                    ", \n\tfb=" + fb +
                    ", \n\tb=" + b +
                    ", \n\tbb=" + bb +
                    ", \n\tba=" + Arrays.toString(ba) +
                    ", \n\tbba=" + Arrays.toString(bba) +
                    ", \n\tza=" + Arrays.toString(za) +
                    ", \n\toa=" + Arrays.toString(oa) +
                    ", \n\tnul=" + nul +
                    ", \n\tz=" + z +
                    ", \n\to=" + o +
                    "\n}";
        }
    }

    static class Simple {
        static Simple create() {
            return new Simple(new Simple(Boolean.TRUE, false, new boolean[]{false, true}), true, new boolean[]{true, false});
        }

        Object o;
        Boolean b;
        Object ba;

        Simple() {
        }

        Simple(Object o, Boolean b, Object ba) {
            this.o = o;
            this.b = b;
            this.ba = ba;
        }

        @Override
        public boolean equals(Object o1) {
            if (this == o1) return true;
            if (o1 == null || getClass() != o1.getClass()) return false;
            Simple simple = (Simple) o1;
            return Objects.equals(o, simple.o) &&
                    Objects.equals(b, simple.b) &&
                    Arrays.equals((boolean[])ba, (boolean[])simple.ba);
        }
    }
}
