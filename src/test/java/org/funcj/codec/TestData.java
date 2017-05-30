package org.funcj.codec;

import java.util.*;

public class TestData {
    enum Init {INIT}

    static class Base<T> {
        final T val;
        final Object obj;
        final T[] valArr;
        final Object[] objArr;
        final Map<String, T> mapStrVal;
        final Map<String, Object> mapStrObj;
        final HashMap<String, Object> hashMapStrObj;
        final Object objMap;

        Base() {
            this.val = null;
            this.obj = null;
            this.valArr = null;
            this.objArr = null;
            this.mapStrVal = null;
            this.mapStrObj = null;
            this.hashMapStrObj = null;
            this.objMap = null;
        }

        Base(   T val,
                Object obj,
                T[] valArr,
                Object[] objArr,
                Map<String, T> mapStrVal,
                Map<String, Object> mapStrObj,
                HashMap<String, Object> hashMapStrObj,
                Map<String, Object> objMap) {
            this.val = val;
            this.obj = obj;
            this.valArr = valArr;
            this.objArr = objArr;
            this.mapStrVal = mapStrVal;
            this.mapStrObj = mapStrObj;
            this.hashMapStrObj = hashMapStrObj;
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
                    Objects.equals(mapStrVal, base.mapStrVal) &&
                    Objects.equals(mapStrObj, base.mapStrObj) &&
                    Objects.equals(hashMapStrObj, base.hashMapStrObj) &&
                    Objects.equals(objMap, base.objMap);
        }

        @Override
        public String toString() {
            return "Base{" +
                    "val=" + val +
                    ", obj=" + obj +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", objArr=" + Arrays.toString(objArr) +
                    ", mapStrVal=" + mapStrVal +
                    ", mapStrObj=" + mapStrObj +
                    ", hashMapStrObj=" + hashMapStrObj +
                    ", objMap=" + objMap +
                    '}';
        }
    }

    private static <K, V> HashMap<K, V> mapH(K k0, V v0, K k1, V v1) {
        final HashMap<K, V> m = new HashMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    private static <K, V> TreeMap<K, V> mapT(K k0, V v0, K k1, V v1) {
        final TreeMap<K, V> m = new TreeMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    static class BoolData extends Base<Boolean> {

        final boolean val;
        final boolean[] valArr;
        final Map<String, Boolean> mapStrBool;

        BoolData() {
            this.val = false;
            this.valArr = new boolean[]{};
            this.mapStrBool = null;
        }

        BoolData(Init init) {
            super(
                    true,
                    false,
                    new Boolean[]{false, true},
                    new Boolean[]{false},
                    mapT("a", false, "b", true),
                    mapH("c", true, "d", false),
                    mapH("c", true, "d", false),
                    mapT("e", true, "f", true)
            );
            this.val = true;
            this.valArr = new boolean[]{true, false};
            this.mapStrBool = mapT("f", false, "g", true);
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
