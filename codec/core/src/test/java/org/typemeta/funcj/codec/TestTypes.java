package org.typemeta.funcj.codec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

import static org.typemeta.funcj.codec.TestDataUtils.*;

public abstract class TestTypes {

    public static class CommonData {
        public enum Side {
            LEFT, RIGHT
        }

        public static class Empty {
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                return o != null && getClass() == o.getClass();
            }

            @Override
            public String toString() {
                return "Empty{}";
            }
        }

        private final Empty empty;
        private final Optional<Empty> optEmptyE;
        private final Optional<Empty> optEmptyF;
        private final Optional<Side> side;

        public CommonData() {
            this.empty = null;
            this.optEmptyE = null;
            this.optEmptyF = null;
            this.side = null;
        }

        public CommonData(Init init) {
            this.empty = new Empty();
            this.optEmptyE = Optional.empty();
            this.optEmptyF = Optional.of(new Empty());
            this.side =  Optional.of(Side.LEFT);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CommonData commonData = (CommonData) o;
            return Objects.equals(empty, commonData.empty) &&
                    Objects.equals(optEmptyE, commonData.optEmptyE) &&
                    Objects.equals(optEmptyF, commonData.optEmptyF) &&
                    Objects.equals(side, commonData.side);
        }

        @Override
        public String toString() {
            return "Common{" +
                    "empty=" + empty +
                    ", optEmptyE=" + optEmptyE +
                    ", optEmptyF=" + optEmptyF +
                    ", side=" + side +
                    '}';
        }
    }

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
            this.val = '0';
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

    public static class NumberData<T extends Number> extends Base<T> {

        final T valT;
        final T[] valArrT;
        final T[][] valArrArrT;
        final List<T> listValT;
        final Map<String, T> mapStrValT;

        public NumberData() {
            this.valT = null;
            this.valArrT = null;
            this.valArrArrT = null;
            this.listValT = null;
            this.mapStrValT = null;
        }

        public NumberData(
                T val,
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
                Optional<T> optValF,
                T valT,
                T[] valArrT,
                T[][] valArrArrT,
                List<T> listValT,
                Map<String, T> mapStrValT
        ) {
            super(
                    val,
                    obj,
                    valArr,
                    objArr,
                    listVal,
                    setVal,
                    mapStrVal,
                    mapStrObj,
                    hashMapStrObj,
                    objMap,
                    optValE,
                    optValF);
            this.valT = valT;
            this.valArrT = valArrT;
            this.valArrArrT = valArrArrT;
            this.listValT = listValT;
            this.mapStrValT = mapStrValT;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            @SuppressWarnings("unchecked")
            NumberData<T> rhs = (NumberData<T>) obj;
            if (!Objects.equals(valT, rhs.valT)) return false;
            if (!Arrays.equals(valArrT, rhs.valArrT)) return false;
            if (!Arrays.deepEquals(valArrArrT, rhs.valArrArrT)) return false;
            if (!Objects.equals(listValT, rhs.listValT)) return false;
            if (!Objects.equals(mapStrValT, rhs.mapStrValT)) return false;
            return super.equals(rhs);
        }

        @Override
        public String toString() {
            return "NumberData{" +
                    "val=" + valT +
                    ", valArr=" + Arrays.toString(valArrT) +
                    ", valArrArr=" + Arrays.deepToString(valArrArrT) +
                    ", listVal=" + listValT +
                    ", mapStrVal=" + mapStrValT +
                    "} " + super.toString();
        }
    }

    public static class BigIntegerData extends NumberData<BigInteger> {

        final BigInteger val;
        final BigInteger[] valArr;
        final BigInteger[][] valArrArr;
        final List<BigInteger> listVal;
        final Map<String, BigInteger> mapStrVal;

        public BigIntegerData() {
            this.val = BigInteger.ZERO;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public BigIntegerData(Init init) {
            super(
                    BigInteger.valueOf(10),
                    BigInteger.valueOf(-1000),
                    new BigInteger[]{BigInteger.valueOf(10), BigInteger.valueOf(20)},
                    new BigInteger[]{BigInteger.valueOf(123456)},
                    arrayList(
                            BigInteger.valueOf(0),
                            BigInteger.valueOf(-1),
                            BigInteger.valueOf(-2),
                            BigInteger.valueOf(-3),
                            null),
                    treeSet(
                            BigInteger.valueOf(-1000),
                            BigInteger.valueOf(101),
                            BigInteger.valueOf(1000)),
                    treeMap("a", BigInteger.valueOf(1), "b", BigInteger.valueOf(2)),
                    hashMap("c", BigInteger.valueOf(3), "d", BigInteger.valueOf(4)),
                    hashMap("c", BigInteger.valueOf(-1), "d", BigInteger.valueOf(-2)),
                    treeMap("e", BigInteger.valueOf(-200), "f", BigInteger.valueOf(-300)),
                    Optional.empty(),
                    Optional.of(BigInteger.valueOf(99)),
                    BigInteger.valueOf(-999),
                    new BigInteger[]{BigInteger.valueOf(1234), BigInteger.valueOf(5678)},
                    new BigInteger[][]{
                            {BigInteger.valueOf(12), BigInteger.valueOf(34)},
                            {BigInteger.valueOf(56), BigInteger.valueOf(78)}, null},
                    arrayList(BigInteger.valueOf(0), BigInteger.valueOf(1), BigInteger.valueOf(2),
                              BigInteger.valueOf(3)),
                    treeMap(
                            "f", BigInteger.valueOf(321),
                            "g", BigInteger.valueOf(654))
            );
            this.val = BigInteger.valueOf(-999);
            this.valArr = new BigInteger[]{BigInteger.valueOf(1234), BigInteger.valueOf(5678)};
            this.valArrArr = new BigInteger[][]{
                    {BigInteger.valueOf(12), BigInteger.valueOf(34)},
                    {BigInteger.valueOf(56), BigInteger.valueOf(78)}, null};
            this.listVal = arrayList(BigInteger.valueOf(0), BigInteger.valueOf(1), BigInteger.valueOf(2),
                                     BigInteger.valueOf(3));
            this.mapStrVal = treeMap(
                    "f", BigInteger.valueOf(321),
                    "g", BigInteger.valueOf(654));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BigIntegerData rhs = (BigIntegerData) obj;
            if (!Objects.equals(val, rhs.val)) return false;
            if (!Arrays.equals(valArr, rhs.valArr)) return false;
            if (!Arrays.deepEquals(valArrArr, rhs.valArrArr)) return false;
            if (!Objects.equals(listVal, rhs.listVal)) return false;
            if (!Objects.equals(mapStrVal, rhs.mapStrVal)) return false;
            return super.equals(rhs);
        }

        @Override
        public String toString() {
            return "BigIntegerData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", listVal=" + listVal +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }

    public static class BigDecimalData extends NumberData<BigDecimal> {

        final BigDecimal val;
        final BigDecimal[] valArr;
        final BigDecimal[][] valArrArr;
        final List<BigDecimal> listVal;
        final Map<String, BigDecimal> mapStrVal;

        public BigDecimalData() {
            this.val = BigDecimal.ZERO;
            this.valArr = null;
            this.valArrArr = null;
            this.listVal = null;
            this.mapStrVal = null;
        }

        public BigDecimalData(Init init) {
            super(
                    new BigDecimal(10.45),
                    new BigDecimal(-1000),
                    new BigDecimal[]{new BigDecimal(10.1), new BigDecimal(20.2)},
                    new BigDecimal[]{new BigDecimal(123456.5678)},
                    arrayList(
                            new BigDecimal(0.1d),
                            new BigDecimal(-1.2d),
                            new BigDecimal(-2.3d),
                            new BigDecimal(-3.4d),
                            null),
                    treeSet(
                            new BigDecimal(-1000.1d),
                            new BigDecimal(101d),
                            new BigDecimal(1000.1d)),
                    treeMap("a", new BigDecimal(1.5d), "b", new BigDecimal(2.5d)),
                    hashMap("c", new BigDecimal(3d), "d", new BigDecimal(4d)),
                    hashMap("c", new BigDecimal(-1d), "d", new BigDecimal(-2d)),
                    treeMap("e", new BigDecimal(-200d), "f", new BigDecimal(-300d)),
                    Optional.empty(),
                    Optional.of(new BigDecimal(99.99d)),
                    new BigDecimal(-999d),
                    new BigDecimal[]{new BigDecimal(1234d), new BigDecimal(5678d)},
                    new BigDecimal[][]{
                            {new BigDecimal(12d), new BigDecimal(34d)},
                            {new BigDecimal(56d), new BigDecimal(78d)}, null},
                    arrayList(new BigDecimal(0.1d), new BigDecimal(1.2d), new BigDecimal(2.3d),
                              new BigDecimal(3.4d)),
                    treeMap(
                            "f", new BigDecimal(321.9d),
                            "g", new BigDecimal(654.3d))
            );
            this.val = new BigDecimal(-999d);
            this.valArr = new BigDecimal[]{new BigDecimal(1234d), new BigDecimal(5678d)};
            this.valArrArr = new BigDecimal[][]{
                    {new BigDecimal(12d), new BigDecimal(34d)},
                    {new BigDecimal(56d), new BigDecimal(78d)}, null};
            this.listVal = arrayList(new BigDecimal(0.1d), new BigDecimal(1.2d), new BigDecimal(2.3d),
                                     new BigDecimal(3.4d));
            this.mapStrVal = treeMap(
                    "f", new BigDecimal(321.9d),
                    "g", new BigDecimal(654.3d));
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BigDecimalData rhs = (BigDecimalData) obj;
            if (!Objects.equals(val, rhs.val)) return false;
            if (!Arrays.equals(valArr, rhs.valArr)) return false;
            if (!Arrays.deepEquals(valArrArr, rhs.valArrArr)) return false;
            if (!Objects.equals(listVal, rhs.listVal)) return false;
            if (!Objects.equals(mapStrVal, rhs.mapStrVal)) return false;
            return super.equals(rhs);
        }

        @Override
        public String toString() {
            return "BigDecimalData{" +
                    "val=" + val +
                    ", valArr=" + Arrays.toString(valArr) +
                    ", valArrArr=" + Arrays.deepToString(valArrArr) +
                    ", listVal=" + listVal +
                    ", mapStrVal=" + mapStrVal +
                    "} " + super.toString();
        }
    }

    public static class HasOptional<T> {
        public static <T> HasOptional<T> create() {
            return new HasOptional<>();
        }

        public final Optional<T> optVal;
        public final Optional<String> optStr;

        private HasOptional() {
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

    public static final class NoEmptyCtor {
        public static NoEmptyCtor create(boolean flag) {
            return new NoEmptyCtor(flag);
        }

        public boolean flag;

        private NoEmptyCtor(boolean flag) {
            this.flag = flag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NoEmptyCtor that = (NoEmptyCtor) o;
            return flag == that.flag;
        }

        @Override
        public String toString() {
            return "NoEmptyCtor{" +
                    "flag=" + flag +
                    '}';
        }
    }

    public static final class StaticCtor {
        public static StaticCtor create(boolean flag) {
            return new StaticCtor(flag);
        }

        public boolean flag;

        private StaticCtor(boolean flag) {
            this.flag = flag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StaticCtor that = (StaticCtor) o;
            return flag == that.flag;
        }

        @Override
        public String toString() {
            return "StaticCtor{" +
                    "flag=" + flag +
                    '}';
        }
    }

    public static final class Recursive {
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

    static class Custom {

        enum Colour {RED, GREEN, BLUE};

        final Colour colour;
        final LocalDate date;
        final boolean flag;
        final String name;
        final double age;

        Custom(Colour colour, LocalDate date, boolean flag, String name, double age) {
            this.colour = colour;
            this.date = date;
            this.flag = flag;
            this.name = name;
            this.age = age;
        }

        Custom() {
            this.colour = null;
            this.date = null;
            this.flag = false;
            this.name = null;
            this.age = 0.0;
        }

        Custom(Init init) {
            this.colour = Colour.GREEN;
            this.date = LocalDate.now();
            this.flag = true;
            this.name = "zap";
            this.age = 3.141592;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Custom custom = (Custom) o;
            return colour == custom.colour &&
                    Objects.equals(date, custom.date) &&
                    flag == custom.flag &&
                    Objects.equals(name, custom.name);
        }

        @Override
        public String toString() {
            return "Custom{" +
                    "colour=" + colour +
                    ", date=" + date +
                    ", flag=" + flag +
                    ", name=" + name +
                    '}';
        }
    }

    enum EnumType {
        VALUE1 {
            @Override
            public String code() {
                return "1";
            }
        }, VALUE2 {
            @Override
            public String code() {
                return "2";
            }
        };

        public abstract String code();
    }

    static class TypeWithEnum {
        final EnumType enumType;

        public TypeWithEnum(EnumType enumType) {
            this.enumType = enumType;
        }

        TypeWithEnum() {
            this.enumType = null;
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                TypeWithEnum that = (TypeWithEnum) rhs;
                return enumType == that.enumType;
            }
        }
    }

    static class CollTypesz {

        final Set<Integer> unmodSet = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(1, 2, 3, 4)));
        final Set<Integer> unmodSet2 = Collections.unmodifiableSortedSet(new TreeSet<>(Arrays.asList(1, 2, 3, 4)));

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                CollTypesz that = (CollTypesz) rhs;
                return Objects.equals(unmodSet, that.unmodSet) &&
                        Objects.equals(unmodSet2, that.unmodSet2);
            }
        }
    }

    static class CollTypes {
        final static TreeMap<String, Integer> m = new TreeMap<>();
        static {
            m.put("alpha", 1);
            m.put("beta", 2);
        }

        final List<String> emptyList = Collections.emptyList();
        final Set<String> emptySet = Collections.emptySet();
        final Map<String, Integer> emptyMap = Collections.emptyMap();

        final List<String> singletonList = Collections.singletonList("abcd");
        final Set<String> singletonSet = Collections.singleton("abcd");
        final Map<String, Integer> singletonMap = Collections.singletonMap("ghij", 1234);

        final List<Integer> unmodList = Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4));
        final Set<Integer> unmodSet = Collections.unmodifiableSet(new HashSet<>(unmodList));
        final Set<Integer> unmodSet2 = Collections.unmodifiableSortedSet(new TreeSet<>(unmodList));

        final Map<String, Integer> unmodMap = Collections.unmodifiableMap(m);
        final Map<String, Integer> unmodMap2 = Collections.unmodifiableNavigableMap(m);
        final Map<String, Integer> unmodMap3 = Collections.unmodifiableSortedMap(m);

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                CollTypes that = (CollTypes) rhs;
                return Objects.equals(emptyList, that.emptyList) &&
                        Objects.equals(emptySet, that.emptySet) &&
                        Objects.equals(emptyMap, that.emptyMap) &&
                        Objects.equals(singletonList, that.singletonList) &&
                        Objects.equals(singletonSet, that.singletonSet) &&
                        Objects.equals(singletonMap, that.singletonMap) &&
                        Objects.equals(unmodSet, that.unmodSet) &&
                        Objects.equals(unmodSet2, that.unmodSet2) &&
                        Objects.equals(unmodMap, that.unmodMap) &&
                        Objects.equals(unmodMap2, that.unmodMap2) &&
                        Objects.equals(unmodMap3, that.unmodMap3);
            }
        }
    }
}

