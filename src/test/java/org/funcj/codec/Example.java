package org.funcj.codec;

import java.util.*;

public abstract class Example {
    public static class Base {
        boolean b = false;
        boolean b2 = true;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Base base = (Base) o;
            return b == base.b &&
                    b2 == base.b2;
        }
    }

    public static class Derived extends Base {
        final boolean fb = false;

        boolean b = true;
        Boolean bb = Boolean.FALSE;
        boolean[] ba = {true, false};
        Boolean[] bba = {false, true, false};

        Base nul = null;
        Base base = new Base();

        public Derived() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Derived derived = (Derived) o;
            return fb == derived.fb &&
                    b == derived.b &&
                    Objects.equals(bb, derived.bb) &&
                    Arrays.equals(ba, derived.ba) &&
                    Arrays.equals(bba, derived.bba);
        }
    }
}
