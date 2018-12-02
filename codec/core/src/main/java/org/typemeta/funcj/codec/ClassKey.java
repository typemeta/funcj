package org.typemeta.funcj.codec;

/**
 * ClassKey wraps classes in an outer class which supports use as a key in collections.
 * @param <T>       the class type
 */
public interface ClassKey<T> extends Comparable<ClassKey<T>> {
    static <T> ClassKey<T> valueOf(Class<T> clazz) {
        return new Single<T>(clazz);
    }

    static <T> ClassKey<T> valueOf(Class<T> clazz, Class<?> clazzB) {
        return new Double<T>(clazz, clazzB);
    }

    static <T> ClassKey<T> valueOf(Class<T> clazz, Class<?> clazzB, Class<?> clazzC) {
        return new Triple<T>(clazz, clazzB, clazzC);
    }

    class Single<T> implements ClassKey<T> {
        final Class<T> clazz;

        public Single(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final Single<?> rhsSgl = (Single<?>) rhs;
                return clazz == rhsSgl.clazz;
            }
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public int compareTo(ClassKey<T> rhs) {
            if (rhs instanceof Single) {
                final Single<?> rhsSingle = (Single<?>)rhs;
                return clazz.getName().compareTo(rhsSingle.clazz.getName());
            } else if (rhs instanceof Double) {
                return -1;
            } else if (rhs instanceof Triple) {
                return -1;
            } else {
                throw new CodecException("Unrecognised ClassKey type - " + rhs.getClass().getName());
            }
        }
    }

    class Double<T> implements ClassKey<T> {
        final Class<?> clazz;
        final Class<?> clazzA;

        public Double(Class<?> clazz, Class<?> clazzA) {
            this.clazz = clazz;
            this.clazzA = clazzA;
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final Double<?> rhsDbl = (Double<?>) rhs;
                return clazz == rhsDbl.clazz && clazzA == rhsDbl.clazzA;
            }
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public int compareTo(ClassKey<T> rhs) {
            if (rhs instanceof Single) {
                return 1;
            } else if (rhs instanceof Double) {
                final Double<?> rhsDouble = (Double<?>)rhs;
                int cmp = clazz.getName().compareTo(rhsDouble.clazz.getName());
                if (cmp == 0) {
                    cmp = clazzA.getName().compareTo(rhsDouble.clazzA.getName());
                }
                return cmp;
            } else if (rhs instanceof Triple) {
                return -1;
            } else {
                throw new CodecException("Unrecognised ClassKey type - " + rhs.getClass().getName());
            }
        }
    }

    class Triple<T> implements ClassKey<T> {
        final Class<?> clazz;
        final Class<?> clazzA;
        final Class<?> clazzB;

        public Triple(Class<?> clazz, Class<?> clazzA, Class<?> clazzB) {
            this.clazz = clazz;
            this.clazzA = clazzA;
            this.clazzB = clazzB;
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final Triple<?> rhsTpl = (Triple<?>) rhs;
                return clazz == rhsTpl.clazz && clazzA == rhsTpl.clazzA && clazzB == rhsTpl.clazzB;
            }
        }

        @Override
        public int hashCode() {
            return clazz.hashCode();
        }

        @Override
        public int compareTo(ClassKey<T> rhs) {
            if (rhs instanceof Single) {
                return 1;
            } else if (rhs instanceof Double) {
                return 1;
            } else if (rhs instanceof Triple) {
                final Triple<?> rhsTriple = (Triple<?>)rhs;
                int cmp = clazz.getName().compareTo(rhsTriple.clazz.getName());
                if (cmp == 0) {
                    cmp = clazzA.getName().compareTo(rhsTriple.clazzA.getName());
                    if (cmp == 0) {
                        cmp = clazzB.getName().compareTo(rhsTriple.clazzB.getName());
                    }
                }
                return cmp;
            } else {
                throw new CodecException("Unrecognised ClassKey type - " + rhs.getClass().getName());
            }
        }
    }
}
