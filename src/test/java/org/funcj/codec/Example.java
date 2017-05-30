package org.funcj.codec;

import java.util.*;

public abstract class Example {
    enum Colour {
        RED, GREEN, BLUE
    }

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
        public static Derived create() {
            final Map<Boolean, Integer> m = new HashMap<>();
            m.put(true, 123);
            m.put(false, 456);
            final Map<String, Integer> ms = new HashMap<String, Integer>();
            ms.put("abc", 123);
            ms.put("def", 456);
            return new Derived(
                    true,
                    false,
                    Boolean.TRUE,
                    new boolean[]{true, false},
                    new Boolean[]{false, true},
                    new ZBase[]{new ZBase()},
                    new Object[]{2, 3},
                    null,
                    new ZBase(),
                    "str",
                    "objStr",
                    m,
                    ms,
                    Colour.BLUE,
                    Colour.RED
            );
        }

        final boolean fb;

        final boolean b;
        final Boolean bb;
        final boolean[] ba;
        final Boolean[] bba;
        final ZBase[] za;
        final Object[] oa;

        final ZBase nul;
        final ZBase z;

        final String s;
        final Object os;

        final Colour en;
        final Object eno;

        final Map<Boolean, Integer> m;
        final Map<String, Integer> ms;

        public Derived() {
            this.fb = true;
            this.b = false;
            this.bb = null;
            this.ba = null;
            this.bba = null;
            this.za = null;
            this.oa = null;
            this.nul = null;
            this.z = null;
            this.s = null;
            this.os = null;
            this.m = null;
            this.ms = null;
            this.en = null;
            this.eno = null;
        }

        public Derived(
                boolean fb,
                boolean b,
                Boolean bb,
                boolean[] ba,
                Boolean[] bba,
                ZBase[] za,
                Object[] oa,
                ZBase nul,
                ZBase z,
                String s,
                String os,
                Map<Boolean, Integer> m,
                Map<String, Integer> ms,
                Colour en,
                Colour eno) {
            this.fb = fb;
            this.b = b;
            this.bb = bb;
            this.ba = ba;
            this.bba = bba;
            this.za = za;
            this.oa = oa;
            this.nul = nul;
            this.z = z;
            this.s = s;
            this.os = os;
            this.m = m;
            this.ms = ms;
            this.en = en;
            this.eno = eno;
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
                    Objects.equals(s, derived.s) &&
                    Objects.equals(os, derived.os) &&
                    m.equals(derived.m) &&
                    m.equals(derived.m);
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
                    ", \n\ts=" + s +
                    ", \n\tos=" + os +
                    ", \n\tm=" + m +
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

    static class Simple2<T> {
        static Simple2 create() {
            final HashMap<String, Object> m = new HashMap<>();
            m.put("abc", "123");
            return new Simple2(m);
        }

        final boolean[] value;

        Simple2() {
            value = null;
        }

        Simple2(HashMap<String, T> value) {
            this.value = null;
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;
            Simple2 simple2 = (Simple2) rhs;
            return Objects.equals(value, simple2.value);
        }

        @Override
        public String toString() {
            return "Simple2{" +
                    "value=" + value +
                    '}';
        }
    }

    static class Derived2 extends Simple2<Integer> {
        static Derived2 create() {
            final HashMap<String, Integer> m = new HashMap<>();
            m.put("abc", 123);
            return new Derived2(m);
        }

        public Derived2() {

        }

        public Derived2(HashMap<String, Integer> value) {
            super(value);
        }
    }
}
