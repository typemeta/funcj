package org.typemeta.funcj.codec.avro;

public class UnitTest {
    static class Rational {
        public static final Rational ZERO = new Rational(0, 1) {
            public Rational mult(Rational rhs) {
                return ZERO;
            }
        };

        public static final Rational ONE = new Rational(1, 1);

        private static int gcd(int a, int b) {
            return b == 0 ? a : gcd(b, a%b);
        }
        
        public static Rational normalise(int numer, int denom) {
            final int gcd = gcd(numer, denom);
            return new Rational(numer / gcd, denom / gcd);
        }

        public final int numer;
        public final int denom;

        private Rational(int numer, int denom) {
            this.numer = numer;
            this.denom = denom;
        }

        public Rational invert() {
            return new Rational(denom, numer);
        }

        public Rational add(Rational rhs) {
            return Rational.normalise(
                    numer * rhs.denom + rhs.numer * denom,
                    denom * rhs.denom
            );
        }

        public Rational mult(Rational rhs) {
            return Rational.normalise(
                    numer * rhs.numer,
                    denom * rhs.denom
            );
        }

        public float asFloat() {
            return ((float)numer) / denom;
        }

        public String toString() {
            return "<" + numer + "/" + denom + ">";
        }
    }

    public static final class Matrix {

        public static Matrix identity(int n) {
            final Rational[][] elems = new Rational[n][n];
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < r; c++) {
                    elems[r][c] = Rational.ZERO;
                    elems[c][r] = Rational.ZERO;
                }
                elems[r][r] = Rational.ONE;
            }
            return new Matrix(elems);
        }

        final Rational elems[][];

        public Matrix(Rational[][] elems) {
            this.elems = elems;
        }

        Matrix mult(Matrix rhs) {
            final Rational[][] elems2 = new Rational[elems.length][elems.length];
            for (int r = 0; r < elems.length; r++) {
                for (int c = 0; c < elems.length; c++) {
                    Rational v = Rational.ZERO;
                    for (int i = 0; i < elems.length; i++) {
                        v = v.add(elems[r][i].mult(elems[i][c]));
                    }
                    elems2[r][c] = v;
                }
            }

            return new Matrix(elems2);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (int r = 0; r < elems.length; ++r) {
                for (int c = 0; c < elems.length; ++c) {
                    sb.append(elems[r][c]).append(" ");
                }
                sb.append("\n");
            }
            return sb.toString();
        }
    }

    enum Units {
        MM, CM, M, KM
    }

    static Matrix metricUnits() {
        final Rational[][] elems = new Rational[4][4];
        for (int r = 0; r < elems.length; r++) {
            for (int c = 0; c < r; c++) {
                elems[r][c] = Rational.ZERO;
                elems[c][r] = Rational.ZERO;
            }
            elems[r][r] = Rational.ONE;
        }

        elems[Units.MM.ordinal()][Units.CM.ordinal()] = Rational.normalise(10, 1);
        elems[Units.CM.ordinal()][Units.MM.ordinal()] = elems[Units.MM.ordinal()][Units.CM.ordinal()].invert();

        elems[Units.CM.ordinal()][Units.M.ordinal()] = Rational.normalise(10, 1);
        elems[Units.M.ordinal()][Units.CM.ordinal()] = elems[Units.MM.ordinal()][Units.CM.ordinal()].invert();

        elems[Units.CM.ordinal()][Units.KM.ordinal()] = Rational.normalise(100, 1);
        elems[Units.KM.ordinal()][Units.CM.ordinal()] = elems[Units.MM.ordinal()][Units.CM.ordinal()].invert();

        return new Matrix(elems);
    }

    static Matrix depth(Matrix m, int n) {
        Matrix m2 = m;
        for (int i = 0; i < n; ++i) {
            m2 = m2.mult(m);
            System.out.println(m2);
        }
        return m2;
    }

    public static void main(String[] args) {
        Matrix m = metricUnits();
        System.out.println(m);

        Matrix m2 = depth(m, 2);
    }
}
