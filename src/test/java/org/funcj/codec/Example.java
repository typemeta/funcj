package org.funcj.codec;

import java.util.*;

public abstract class Example {
    public interface IFace<T> {
        T value();
    }

    public static class Impl<T> implements IFace<T> {

        final T value;

        public Impl() {
            this.value = null;
        }

        public Impl(T value) {
            this.value = value;
        }

        @Override
        public T value() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Impl<?> impl = (Impl<?>) o;
            return Objects.equals(value, impl.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "Impl{" +
                    "value=" + value +
                    '}';
        }
    }

    public static class TestObj {
        static TestObj create() {
            return new TestObj(true, false);
        }

        IFace<Boolean> iface;
        Impl<Boolean> impl;

        public TestObj() {
            iface = null;
            impl = null;
        }

        public TestObj(boolean iface, boolean impl) {
            this.iface = new Impl<>(iface);
            this.impl = new Impl<>(impl);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObj testObj = (TestObj) o;
            return Objects.equals(iface, testObj.iface) &&
                    Objects.equals(impl, testObj.impl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(iface, impl);
        }

        @Override
        public String toString() {
            return "TestObj{" +
                    "iface=" + iface +
                    ", impl=" + impl +
                    '}';
        }
    }

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
            return new Simple(
                    new Simple(
                            Boolean.TRUE,
                            false,
                            new Boolean[]{Boolean.TRUE, Boolean.FALSE},
                            new boolean[]{false, true},
                            new int[]{0, 1},
                            new Integer[]{2, 3, 4},
                            new Number[]{5, 6, 4}
                    ), true,
                    new Boolean[]{Boolean.FALSE, Boolean.TRUE},
                    new boolean[]{true, false},
                    new int[]{10, 11},
                    new Integer[]{12, 13, 14},
                    new Number[]{15, 16, 14}
            );
        }

        Object o;
        Boolean b;
        Boolean[] bba;
        Object oba;
        int[] ia;
        Integer[] bia;
        Number[] bna;

        Simple() {
        }

        Simple(
                Object o,
                Boolean b,
                Boolean[] bba,
                Object oba,
                int[] ia,
                Integer[] bia,
                Number[] bna) {
            this.o = o;
            this.b = b;
            this.bba = bba;
            this.oba = oba;
            this.ia = ia;
            this.bia = bia;
            this.bna = bna;
        }

        @Override
        public boolean equals(Object rhsO) {
            if (this == rhsO) return true;
            if (rhsO == null || getClass() != rhsO.getClass()) return false;
            Simple rhs = (Simple) rhsO;
            return Objects.equals(o, rhs.o) &&
                    Objects.equals(b, rhs.b) &&
                    Arrays.equals(bba, rhs.bba) &&
                    Arrays.equals((boolean[])oba, (boolean[])rhs.oba) &&
                    Arrays.equals(ia, rhs.ia) &&
                    Arrays.equals(bia, rhs.bia) &&
                    Arrays.equals(bna, rhs.bna);
        }
    }

//    static class Simple {
//        static Simple create() {
//            return new Simple(new int[]{5, 6, 4});
//        }
//
//        int[] ia;
//
//        Simple() {
//        }
//
//        Simple(int[] ia) {
//            this.ia = ia;
//        }
//
//        @Override
//        public boolean equals(Object rhsO) {
//            if (this == rhsO) return true;
//            if (rhsO == null || getClass() != rhsO.getClass()) return false;
//            Simple rhs = (Simple) rhsO;
//            return Arrays.equals(ia, rhs.ia);
//        }
//    }
}
