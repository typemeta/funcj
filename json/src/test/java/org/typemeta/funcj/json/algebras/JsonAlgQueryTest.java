package org.typemeta.funcj.json.algebras;

import org.junit.Test;
import org.typemeta.funcj.algebra.Monoid;
import org.typemeta.funcj.algebra.MonoidInstances;
import org.typemeta.funcj.json.model.JsonAlg;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.json.TestData.testValue;

public class JsonAlgQueryTest {
    static class NodeCount extends JsonAlg.Query.Base<Integer> {

        public NodeCount() {
            super(MonoidInstances.monoidInteger);
        }

        @Override
        public Integer nul() {
            return 1;
        }

        @Override
        public Integer bool(boolean b) {
            return 1;
        }

        @Override
        public Integer num(double value) {
            return 1;
        }

        @Override
        public Integer str(String s) {
            return 1;
        }

        @Override
        public Integer arr(List<Integer> elems) {
            return super.arr(elems) + 1;
        }

        @Override
        public Integer obj(LinkedHashMap<String, Integer> fields) {
            return super.obj(fields) + 1;
        }
    }

    @Test
    public void computeNodeCount() {
        final int nc = testValue.apply(new NodeCount());
        assertEquals("Count of nodes", 20, nc);
    }

    static class NodeAsPrimes extends JsonAlg.Query.Base<Long> {

        public NodeAsPrimes() {
            super(new Monoid<Long>() {
                @Override
                public Long zero() {
                    return 1l;
                }

                @Override
                public Long combine(Long x, Long y) {
                    return x * y;
                }
            });
        }

        @Override
        public Long nul() {
            return 2l;
        }

        @Override
        public Long bool(boolean b) {
            return 3l;
        }

        @Override
        public Long num(double value) {
            return 5l;
        }

        @Override
        public Long str(String s) {
            return 7l;
        }

        @Override
        public Long arr(List<Long> elems) {
            return m().combine(super.arr(elems), 11l) ;
        }

        @Override
        public Long obj(LinkedHashMap<String, Long> fields) {
            return m().combine(super.obj(fields), 13l);
        }
    }

    @Test
    public void computeNodeAsPrimes() {
        final long nc = testValue.apply(new NodeAsPrimes());
        final long exp = 2l*3*3*5*5*5*5*5*5*5*7*7*7*11*11*11*11*13*13*13;
        assertEquals("Nodes mapped into primes and summed", exp, nc);
    }
}
