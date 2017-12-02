package org.typemeta.funcj.algebra;

import org.typemeta.funcj.data.IList;

public abstract class MonoidInstances {
    public static final Monoid<Double> monoidDouble = new  Monoid<Double>() {
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

    private static final Monoid<IList<Object>> monoidList = new Monoid<IList<Object>>() {
        @Override
        public IList<Object> zero() {
            return IList.of();
        }

        @Override
        public IList<Object> combine(IList<Object> x, IList<Object> y) {
            return x.appendAll(y);
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Monoid<IList<T>> monoidList() {
        return (Monoid)monoidList;
    }
}
