package org.funcj.codec;

import java.util.*;

public class TestData {
    enum Init {INIT}

    static class Base<T> {
        final T val;
        final Object obj;
        final T[] valArr;
        final Object[] objArr;
        final List<T> listVal;
        final Map<String, T> mapStrVal;
        final Map<String, Object> mapStrObj;
        final HashMap<String, Object> hashMapStrObj;
        final Object objMap;

        Base() {
            this.val = null;
            this.obj = null;
            this.valArr = null;
            this.objArr = null;
            this.listVal = null;
            this.mapStrVal = null;
            this.mapStrObj = null;
            this.hashMapStrObj = null;
            this.objMap = null;
        }

        Base(   T val,
                Object obj,
                T[] valArr,
                Object[] objArr,
                List<T> listVal,
                Map<String, T> mapStrVal,
                Map<String, Object> mapStrObj,
                HashMap<String, Object> hashMapStrObj,
                Map<String, Object> objMap) {
            this.val = val;
            this.obj = obj;
            this.valArr = valArr;
            this.objArr = objArr;
            this.listVal = listVal;
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
                    Objects.equals(listVal, base.listVal) &&
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

    private static <T> ArrayList<T> arrayList(T... vals) {
        final ArrayList<T> l = new ArrayList<T>(vals.length);
        for (T val : vals) {
            l.add(val);
        }
        return l;
    }

    private static <K, V> HashMap<K, V> hashMap(K k0, V v0, K k1, V v1) {
        final HashMap<K, V> m = new HashMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    private static <K, V> TreeMap<K, V> treeMap(K k0, V v0, K k1, V v1) {
        final TreeMap<K, V> m = new TreeMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    public static class BooleanData extends Base<Boolean> {

        final boolean val;
        final boolean[] valArr;
        final boolean[][] valArrArr;
        final List<Boolean> listVal;
        final Map<String, Boolean> mapStrVal;

        public BooleanData() {
            this.val = false;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public BooleanData(Init init) {
            super(
                    true,
                    false,
                    new Boolean[]{false, true},
                    new Boolean[]{false},
                    arrayList(true, false, true),
                    treeMap("a", false, "b", true),
                    hashMap("c", true, "d", false),
                    hashMap("c", true, "d", false),
                    treeMap("e", true, "f", true)
            );
            this.val = true;
            this.valArr = new boolean[]{true, false};
            this.valArrArr = new boolean[][]{{true, false}, {false, true}, null};
            this.listVal = arrayList(false, true, false);
            this.mapStrVal = treeMap("f", false, "g", true);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BooleanData rhs = (BooleanData) obj;
            if (val != rhs.val) return false;
            if (!Arrays.equals(valArr, rhs.valArr)) return false;
            if (!Arrays.deepEquals(valArrArr, rhs.valArrArr)) return false;
            if (!Objects.equals(listVal, rhs.listVal)) return false;
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
        final List<Integer> listVal;
        final Map<String, Integer> mapStrVal;

        public IntegerData() {
            this.val = 0;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public IntegerData(Init init) {
            super(
                    10,
                    -1000,
                    new Integer[]{10, 20},
                    new Integer[]{123456},
                    arrayList(0, -1, -2, -3, null),
                    treeMap("a", 1, "b", 2),
                    hashMap("c", 3, "d", 4),
                    hashMap("c", -1, "d", -2),
                    treeMap("e", -200, "f", -300)
            );
            this.val = -999;
            this.valArr = new int[]{1234, 5678};
            this.valArrArr = new int[][]{{12, 34}, {56, 78}, null};
            this.listVal = arrayList(0, 1, 2, 3);
            this.mapStrVal = treeMap("f", 321, "g", 654);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            IntegerData rhs = (IntegerData) obj;
            if (val != rhs.val) return false;
            if (!Arrays.equals(valArr, rhs.valArr)) return false;
            if (!Arrays.deepEquals(valArrArr, rhs.valArrArr)) return false;
            if (!Objects.equals(listVal, rhs.listVal)) return false;
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
