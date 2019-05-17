package org.typemeta.funcj.algebra;

import org.typemeta.funcj.data.IList;

import java.util.*;

public abstract class MonoidInstances {


    public static final Monoid<Double> monoidDouble = new Monoid<Double>() {
        @Override
        public Double zero() {
            return 0d;
        }

        @Override
        public Double combine(Double x, Double y) {
            return x + y;
        }
    };

    public static final Monoid<Float> monoidFloat = new Monoid<Float>() {
        @Override
        public Float zero() {
            return 0f;
        }

        @Override
        public Float combine(Float x, Float y) {
            return x + y;
        }
    };

    public static final Monoid<Integer> monoidInteger = new Monoid<Integer>() {
        @Override
        public Integer zero() {
            return 0;
        }

        @Override
        public Integer combine(Integer x, Integer y) {
            return x + y;
        }
    };

    public static final Monoid<Long> monoidLong = new Monoid<Long>() {
        @Override
        public Long zero() {
            return 0l;
        }

        @Override
        public Long combine(Long x, Long y) {
            return x + y;
        }
    };

    public static final Monoid<Short> monoidShort = new Monoid<Short>() {
        @Override
        public Short zero() {
            return 0;
        }

        @Override
        public Short combine(Short x, Short y) {
            return (short)(x + y);
        }
    };

    public static final Monoid<String> monoidString = new Monoid<String>() {
        @Override
        public String zero() {
            return "";
        }

        @Override
        public String combine(String x, String y) {
            return (x + y);
        }
    };

    private static final Monoid<IList<Object>> monoidIList = new Monoid<IList<Object>>() {
        @Override
        public IList<Object> zero() {
            return IList.of();
        }

        @Override
        public IList<Object> combine(IList<Object> x, IList<Object> y) {
            return x.appendAll(y);
        }
    };

    private static final Monoid<List<Object>> monoidList = new Monoid<List<Object>>() {
        @Override
        public List<Object> zero() {
            return Collections.emptyList();
        }

        @Override
        public List<Object> combine(List<Object> x, List<Object> y) {
            final List<Object> res = new ArrayList<>(x.size() + y.size());
            res.addAll(x);
            res.addAll(y);
            return res;
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Monoid<IList<T>> monoidIList() {
        return (Monoid)monoidIList;
    }

    @SuppressWarnings("unchecked")
    public static <T> Monoid<List<T>> monoidList() {
        return (Monoid)monoidList;
    }

    @SuppressWarnings("unchecked")
    public static <T> Monoid<T> get(Class<T> clazz) {
        if (clazz.equals(Double.class)) {
            return (Monoid<T>)monoidDouble;
        } else if (clazz.equals(Float.class)) {
            return (Monoid<T>)monoidFloat;
        } else if (clazz.equals(Integer.class)) {
            return (Monoid<T>)monoidInteger;
        } else if (clazz.equals(Long.class)) {
            return (Monoid<T>)monoidLong;
        } else if (clazz.equals(Short.class)) {
            return (Monoid<T>)monoidShort;
        } else if (clazz.equals(String.class)) {
            return (Monoid<T>)monoidString;
        } else if (clazz.equals(IList.class)) {
            return (Monoid<T>)monoidIList;
        } else if (clazz.equals(List.class)) {
            return (Monoid<T>)monoidList;
        } else {
            throw new RuntimeException("No Monoid instance defined for type " + clazz.getName());
        }
    }
}
