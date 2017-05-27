package org.funcj.codec;

import java.util.*;

public class TestData {
    enum Init {INIT}

    static class Base<T> {
        final T val;
        final Object obj;
        final T[] valArr;
        final Object[] objArr;
        final Map<String, T> mapVal;
        final Map<String, Object> mapObj;
        final Object objMap;

        Base() {
            this.val = null;
            this.obj = null;
            this.valArr = null;
            this.objArr = null;
            this.mapVal = null;
            this.mapObj = null;
            this.objMap = null;
        }

        Base(
                T val,
                Object obj,
                T[] valArr,
                Object[] objArr,
                Map<String, T> mapVal,
                Map<String, Object> mapObj,
                Map<String, Object> objMap) {
            this.val = val;
            this.obj = obj;
            this.valArr = valArr;
            this.objArr = objArr;
            this.mapVal = mapVal;
            this.mapObj = mapObj;
            this.objMap = objMap;
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;
            Base<?> base = (Base<?>) rhs;
            return Objects.equals(val, base.val) &&
                    Objects.equals(obj, base.obj) &&
                    Arrays.equals(valArr, base.valArr) &&
                    Arrays.equals(objArr, base.objArr) &&
                    Objects.equals(mapVal, base.mapVal) &&
                    Objects.equals(mapObj, base.mapObj) &&
                    Objects.equals(objMap, base.objMap);
        }

        @Override
        public int hashCode() {
            return Objects.hash(val, obj, valArr, objArr, mapVal, mapObj, objMap);
        }

        @Override
        public String toString() {
            return "Base{" +
                    "val=" + val +
                    ", obj=" + obj +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", objArr=" + Arrays.toString(objArr) +
                    ", mapVal=" + mapVal +
                    ", mapObj=" + mapObj +
                    ", objMap=" + objMap +
                    '}';
        }
    }

    private static <K, V> Map<K, V> mapH(K k0, V v0, K k1, V v1) {
        final Map<K, V> m = new HashMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    private static <K, V> Map<K, V> mapT(K k0, V v0, K k1, V v1) {
        final Map<K, V> m = new TreeMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    static class BoolData extends Base<Boolean> {

        final boolean val;
        final boolean[] valArr;

        BoolData() {
            this.val = false;
            this.valArr = new boolean[]{};
        }

        BoolData(Init init) {
            super(
                    true,
                    false,
                    new Boolean[]{false, true},
                    new Boolean[]{false},
                    mapT("a", false, "b", true),
                    mapH("c", true, "d", false),
                    mapT("e", true, "f", true)
            );
            this.val = true;
            this.valArr = new boolean[]{true, false};
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) return true;
            if (rhs == null || getClass() != rhs.getClass()) return false;
            if (!super.equals(rhs)) return false;
            BoolData boolData = (BoolData) rhs;
            if (val != boolData.val &&
                    !Arrays.equals(valArr, boolData.valArr))
                return false;
            return super.equals(boolData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), val, valArr);
        }

        @Override
        public String toString() {
            return "BoolData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    "} " + super.toString();
        }
    }
}
