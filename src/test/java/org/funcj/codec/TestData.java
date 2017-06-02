package org.funcj.codec;

import java.util.*;

import static org.funcj.codec.TestDataUtils.*;

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
                    ", listVal=" + listVal +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }

    public static class ByteData extends Base<Byte> {

        final byte val;
        final byte[] valArr;
        final byte[][] valArrArr;
        final List<Byte> listVal;
        final Map<String, Byte> mapStrVal;

        public ByteData() {
            this.val = 0;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public ByteData(Init init) {
            super(
                    (byte)10,
                    (byte)100,
                    new Byte[]{10, 20},
                    new Byte[]{123},
                    arrayList((byte)0, (byte)1, (byte)2, (byte)3, null),
                    treeMap("a", (byte)1, "b", (byte)2),
                    hashMap("c", (byte)3, "d", (byte)4),
                    hashMap("c", (byte)1, "d", (byte)2),
                    treeMap("e", (byte)200, "f", (byte)300)
            );
            this.val = 99;
            this.valArr = new byte[]{123, 56};
            this.valArrArr = new byte[][]{{12, 34}, {56, 78}, null};
            this.listVal = arrayList((byte)0, (byte)1, (byte)2, (byte)3);
            this.mapStrVal = treeMap("f", (byte)21, "g", (byte)54);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ByteData rhs = (ByteData) obj;
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
            return "ByteData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", listVal=" + listVal +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }

    public static class CharData extends Base<Character> {

        final char val;
        final char[] valArr;
        final char[][] valArrArr;
        final List<Character> listVal;
        final Map<String, Character> mapStrVal;

        public CharData() {
            this.val = 0;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public CharData(Init init) {
            super(
                    'A',
                    'B',
                    new Character[]{'a', 'b'},
                    new Character[]{'c'},
                    arrayList('0', '1', '2', '3', null),
                    treeMap("a", 'x', "b", 'y'),
                    hashMap("c", '!', "d", '@'),
                    hashMap("c", '£', "d", '$'),
                    treeMap("e", '%', "f", '\\')
            );
            this.val = 99;
            this.valArr = new char[]{'q', 'w'};
            this.valArrArr = new char[][]{{'a', 's'}, {'d', 'f'}, null};
            this.listVal = arrayList('z', 'x', 'c', 'v');
            this.mapStrVal = treeMap("f", '1', "g", '2');
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CharData rhs = (CharData) obj;
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
            return "CharData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", listVal=" + listVal +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }

    public static class ShortData extends Base<Short> {

        final short val;
        final short[] valArr;
        final short[][] valArrArr;
        final List<Short> listVal;
        final Map<String, Short> mapStrVal;

        public ShortData() {
            this.val = 0;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public ShortData(Init init) {
            super(
                    (short)10,
                    -1000,
                    new Short[]{10, 20},
                    new Short[]{12345},
                    arrayList((short)0, (short)-1, (short)-2, (short)-3, null),
                    treeMap("a", (short)1, "b", (short)2),
                    hashMap("c", (short)3, "d", (short)4),
                    hashMap("c", (short)-1, "d", (short)-2),
                    treeMap("e", (short)-200, "f", (short)-300)
            );
            this.val = -999;
            this.valArr = new short[]{1234, 5678};
            this.valArrArr = new short[][]{{12, 34}, {56, 78}, null};
            this.listVal = arrayList((short)0, (short)1, (short)2, (short)3);
            this.mapStrVal = treeMap("f", (short)321, "g", (short)654);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ShortData rhs = (ShortData) obj;
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
            return "ShortData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", listVal=" + listVal +
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
                    ", listVal=" + listVal +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }
    
    public static class LongData extends Base<Long> {

        final long val;
        final long[] valArr;
        final long[][] valArrArr;
        final List<Long> listVal;
        final Map<String, Long> mapStrVal;

        public LongData() {
            this.val = 0;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public LongData(Init init) {
            super(
                    (long)10,
                    -1000,
                    new Long[]{10l, 20l},
                    new Long[]{12345l},
                    arrayList((long)0, (long)-1, (long)-2, (long)-3, null),
                    treeMap("a", (long)1, "b", (long)2),
                    hashMap("c", (long)3, "d", (long)4),
                    hashMap("c", (long)-1, "d", (long)-2),
                    treeMap("e", (long)-200, "f", (long)-300)
            );
            this.val = -999;
            this.valArr = new long[]{1234, 5678};
            this.valArrArr = new long[][]{{12, 34}, {56, 78}, null};
            this.listVal = arrayList((long)0, (long)1, (long)2, (long)3);
            this.mapStrVal = treeMap("f", (long)321, "g", (long)654);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            LongData rhs = (LongData) obj;
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
            return "LongData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", listVal=" + listVal +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }

    public static class FloatData extends Base<Float> {

        final float val;
        final float[] valArr;
        final float[][] valArrArr;
        final List<Float> listVal;
        final Map<String, Float> mapStrVal;

        public FloatData() {
            this.val = 0;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public FloatData(Init init) {
            super(
                    10.45f,
                    -1000,
                    new Float[]{10.1f, 20.2f},
                    new Float[]{123456.5678f},
                    arrayList(0.1f, -1.2f, -2.3f, -3.4f, null),
                    treeMap("a", 1.5f, "b", 2.5f),
                    hashMap("c", 3f, "d", 4f),
                    hashMap("c", -1f, "d", -2f),
                    treeMap("e", -200f, "f", -300f)
            );
            this.val = -999;
            this.valArr = new float[]{1234, 5678};
            this.valArrArr = new float[][]{{12, 34}, {56, 78}, null};
            this.listVal = arrayList(0f, 1f, 2f, 3f);
            this.mapStrVal = treeMap("f", 321f, "g", 654f);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            FloatData rhs = (FloatData) obj;
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
            return "FloatData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", listVal=" + listVal +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }

    public static class DoubleData extends Base<Double> {

        final double val;
        final double[] valArr;
        final double[][] valArrArr;
        final List<Double> listVal;
        final Map<String, Double> mapStrVal;

        public DoubleData() {
            this.val = 0;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public DoubleData(Init init) {
            super(
                    10.45,
                    -1000,
                    new Double[]{10.1, 20.2},
                    new Double[]{123456.5678},
                    arrayList(0.1, -1.2, -2.3, -3.4, null),
                    treeMap("a", 1.5, "b", 2.5),
                    hashMap("c", 3, "d", 4),
                    hashMap("c", -1, "d", -2),
                    treeMap("e", -200, "f", -300)
            );
            this.val = -999;
            this.valArr = new double[]{1234, 5678};
            this.valArrArr = new double[][]{{12, 34}, {56, 78}, null};
            this.listVal = arrayList(0.1, 1.2, 2.3, 3.4);
            this.mapStrVal = treeMap("f", 321.9, "g", 654.3);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            DoubleData rhs = (DoubleData) obj;
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
            return "DoubleData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", listVal=" + listVal + 
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }
}

abstract class TestDataUtils {

    static <T> ArrayList<T> arrayList(T... vals) {
        final ArrayList<T> l = new ArrayList<T>(vals.length);
        for (T val : vals) {
            l.add(val);
        }
        return l;
    }

    static <K, V> HashMap<K, V> hashMap(K k0, V v0, K k1, V v1) {
        final HashMap<K, V> m = new HashMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

    static <K, V> TreeMap<K, V> treeMap(K k0, V v0, K k1, V v1) {
        final TreeMap<K, V> m = new TreeMap<>();
        m.put(k0, v0);
        m.put(k1, v1);
        return m;
    }

}