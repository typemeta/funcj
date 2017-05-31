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

    public static class BooleanData extends Base<Boolean> {

        final boolean val;
        final boolean[] valArr;
        final boolean[][] valArrArr;
        final Map<String, Boolean> mapStrVal;

        public BooleanData() {
            this.val = false;
            this.valArr = null;
            this.valArrArr = null;
            this.mapStrVal = null;
        }

        public BooleanData(Init init) {
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
            this.valArrArr = new boolean[][]{{true, false}, {false, true}};
            this.mapStrVal = mapT("f", false, "g", true);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BooleanData rhs = (BooleanData) obj;
            if (val != rhs.val) return false;
            if (!Arrays.equals(valArr, rhs.valArr)) return false;
            if (!Arrays.deepEquals(valArrArr, rhs.valArrArr)) return false;
            if (!Objects.equals(mapStrVal, rhs.mapStrVal)) return false;
            return super.equals(rhs);
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
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }


    public static class IntegerData extends Base<Integer> {

        final int val;
        final int[] valArr;
        final int[][] valArrArr;
        final Map<String, Integer> mapStrVal;

        public IntegerData() {
            this.val = 0;
            this.valArr = null;
            this.valArrArr = null;
            this.mapStrVal = null;
        }

        public IntegerData(Init init) {
            super(
                    10,
                    -1000,
                    new Integer[]{10, 20},
                    new Integer[]{123456},
                    mapT("a", 1, "b", 2),
                    mapH("c", 3, "d", 4),
                    mapH("c", -1, "d", -2),
                    mapT("e", -200, "f", -300)
            );
            this.val = -999;
            this.valArr = new int[]{1234, 5678};
            this.valArrArr = new int[][]{{12, 34}, {56, 78}};
            this.mapStrVal = mapT("f", 321, "g", 654);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            IntegerData rhs = (IntegerData) obj;
            if (val != rhs.val) return false;
            if (!Arrays.equals(valArr, rhs.valArr)) return false;
            if (!Arrays.deepEquals(valArrArr, rhs.valArrArr)) return false;
            if (!Objects.equals(mapStrVal, rhs.mapStrVal)) return false;
            return super.equals(rhs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), val, valArr);
        }

        @Override
        public String toString() {
            return "IntegerData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }
}
