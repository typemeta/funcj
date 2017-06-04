package org.funcj.codec;

import org.funcj.json.JSValue;

import java.util.*;

import static org.funcj.codec.TestDataUtils.*;

public class TestDataBase {
    enum Init {INIT}

    static class Base<T> {
        private final T val;
        private final Object obj;
        private final T[] valArr;
        private final Object[] objArr;
        private final List<T> listVal;
        private final Set<T> setVal;
        private final Map<String, T> mapStrVal;
        private final Map<String, Object> mapStrObj;
        private final HashMap<String, Object> hashMapStrObj;
        private final Object objMap;
        private final Optional<T> optValE;
        private final Optional<T> optValF;

        Base() {
            this.val = null;
            this.obj = null;
            this.valArr = null;
            this.objArr = null;
            this.listVal = null;
            this.setVal = null;
            this.mapStrVal = null;
            this.mapStrObj = null;
            this.hashMapStrObj = null;
            this.objMap = null;
            this.optValE = null;
            this.optValF = null;
        }

        Base(   T val,
                Object obj,
                T[] valArr,
                Object[] objArr,
                List<T> listVal,
                Set<T> setVal,
                Map<String, T> mapStrVal,
                Map<String, Object> mapStrObj,
                HashMap<String, Object> hashMapStrObj,
                Map<String, Object> objMap,
                Optional<T> optValE,
                Optional<T> optValF) {
            this.val = val;
            this.obj = obj;
            this.valArr = valArr;
            this.objArr = objArr;
            this.listVal = listVal;
            this.setVal = setVal;
            this.mapStrVal = mapStrVal;
            this.mapStrObj = mapStrObj;
            this.hashMapStrObj = hashMapStrObj;
            this.objMap = objMap;
            this.optValE = optValE;
            this.optValF = optValF;
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
                    Objects.equals(setVal, base.setVal) &&
                    Objects.equals(mapStrVal, base.mapStrVal) &&
                    Objects.equals(mapStrObj, base.mapStrObj) &&
                    Objects.equals(hashMapStrObj, base.hashMapStrObj) &&
                    Objects.equals(objMap, base.objMap) &&
                    Objects.equals(optValE, base.optValE) &&
                    Objects.equals(optValF, base.optValF);
        }

        @Override
        public String toString() {
            return "Base{" +
                    "val=" + val +
                    ", obj=" + obj +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", objArr=" + Arrays.toString(objArr) +
                    ", listVal=" + listVal +
                    ", setVal=" + setVal +
                    ", mapStrVal=" + mapStrVal +
                    ", mapStrObj=" + mapStrObj +
                    ", hashMapStrObj=" + hashMapStrObj +
                    ", objMap=" + objMap +
                    ", optValE=" + optValE +
                    ", optValF=" + optValF +
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
                    treeSet(false, true),
                    treeMap("a", false, "b", true),
                    hashMap("c", true, "d", false),
                    hashMap("c", true, "d", false),
                    treeMap("e", true, "f", true),
                    Optional.empty(),
                    Optional.of(true)
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
                    treeSet((byte)11, (byte)22),
                    treeMap("a", (byte)1, "b", (byte)2),
                    hashMap("c", (byte)3, "d", (byte)4),
                    hashMap("c", (byte)1, "d", (byte)2),
                    treeMap("e", (byte)200, "f", (byte)300),
                    Optional.empty(),
                    Optional.of((byte)99)
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
                    treeSet('z', 'g', 'a'),
                    treeMap("a", 'x', "b", 'y'),
                    hashMap("c", '!', "d", '@'),
                    hashMap("c", '£', "d", '$'),
                    treeMap("e", '%', "f", '\\'),
                    Optional.empty(),
                    Optional.of((char)99)
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
                    treeSet((short)100, (short)10, (short)1),
                    treeMap("a", (short)1, "b", (short)2),
                    hashMap("c", (short)3, "d", (short)4),
                    hashMap("c", (short)-1, "d", (short)-2),
                    treeMap("e", (short)-200, "f", (short)-300),
                    Optional.empty(),
                    Optional.of((short)99)
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
                    treeSet(100, 0, -100),
                    treeMap("a", 1, "b", 2),
                    hashMap("c", 3, "d", 4),
                    hashMap("c", -1, "d", -2),
                    treeMap("e", -200, "f", -300),
                    Optional.empty(),
                    Optional.of((int)99)
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
                    10L,
                    -1000L,
                    new Long[]{10L, 20L},
                    new Long[]{12345L},
                    arrayList(0L, -1L, -2L, -3L, null),
                    treeSet(999L, 9L, -999L),
                    treeMap("a", 1L, "b", 2L),
                    hashMap("c", 3L, "d", 4L),
                    hashMap("c", -1L, "d", -2L),
                    treeMap("e", -200L, "f", -300L),
                    Optional.empty(),
                    Optional.of((long)99)
            );
            this.val = -999L;
            this.valArr = new long[]{1234L, 5678L};
            this.valArrArr = new long[][]{{12, 34}, {56, 78}, null};
            this.listVal = arrayList(0L, 1L, 2L, 3L);
            this.mapStrVal = treeMap("f", 321L, "g", 654L);
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
                    treeSet(-1000.1f, 101f, 1000.1f),
                    treeMap("a", 1.5f, "b", 2.5f),
                    hashMap("c", 3f, "d", 4f),
                    hashMap("c", -1f, "d", -2f),
                    treeMap("e", -200f, "f", -300f),
                    Optional.empty(),
                    Optional.of(99.99f)
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
                    arrayList(0.1d, -1.2d, -2.3d, -3.4d, null),
                    treeSet(-1000.1d, 101d, 1000.1d),
                    treeMap("a", 1.5d, "b", 2.5d),
                    hashMap("c", 3d, "d", 4d),
                    hashMap("c", -1d, "d", -2d),
                    treeMap("e", -200d, "f", -300d),
                    Optional.empty(),
                    Optional.of(99.99d)
            );
            this.val = -999d;
            this.valArr = new double[]{1234d, 5678d};
            this.valArrArr = new double[][]{{12d, 34d}, {56d, 78d}, null};
            this.listVal = arrayList(0.1d, 1.2d, 2.3d, 3.4d);
            this.mapStrVal = treeMap("f", 321.9d, "g", 654.3d);
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

    public static class HasOptional<T> {
        public final Optional<T> optVal;
        public final Optional<String> optStr;

        public HasOptional() {
            this.optVal = null;
            this.optStr = null;
        }

        public HasOptional(T val, String str) {
            this.optVal = Optional.of(val);
            this.optStr = Optional.of(str);
        }

        public HasOptional(Optional<T> optVal, Optional<String> optStr) {
            this.optVal = optVal;
            this.optStr = optStr;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HasOptional<?> that = (HasOptional<?>) o;
            return Objects.equals(optVal, that.optVal) &&
                    Objects.equals(optStr, that.optStr);
        }

        @Override
        public String toString() {
            return "HasOptional{" +
                    "optVal=" + optVal +
                    ", optStr=" + optStr +
                    '}';
        }
    }

    public static final class NoPublicEmptyCtor {
        public static NoPublicEmptyCtor create(boolean flag) {
            return new NoPublicEmptyCtor(flag);
        }

        public boolean flag;

        private NoPublicEmptyCtor() {
        }

        private NoPublicEmptyCtor(boolean flag) {
            this.flag = flag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NoPublicEmptyCtor that = (NoPublicEmptyCtor) o;
            return flag == that.flag;
        }

        @Override
        public String toString() {
            return "NoPublicEmptyCtor{" +
                    "flag=" + flag +
                    '}';
        }
    }

    public static class Recursive {
        private final Recursive next;
        private final int id;

        public Recursive() {
            this.next = null;
            this.id = -1;
        }

        public Recursive(Recursive next, int id) {
            this.next = next;
            this.id = id;
        }

        @Override
        public String toString() {
            return "Recursive{" +
                    "next=" + next +
                    ", id=" + id +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Recursive recursive = (Recursive) o;
            return id == recursive.id &&
                    Objects.equals(next, recursive.next);
        }
    }
}

abstract class TestDataUtils {

    static <T> ArrayList<T> arrayList(T... vals) {
        final ArrayList<T> l = new ArrayList<T>(vals.length);
        l.addAll(Arrays.asList(vals));
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

    static <T> TreeSet<T> treeSet(T... vals) {
        final TreeSet<T> l = new TreeSet<T>();
        l.addAll(Arrays.asList(vals));
        return l;
    }
}