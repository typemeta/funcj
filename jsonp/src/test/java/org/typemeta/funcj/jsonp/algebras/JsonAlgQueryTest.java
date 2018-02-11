package org.typemeta.funcj.jsonp.algebras;

import org.junit.Test;
import org.typemeta.funcj.algebra.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.jsonp.Example.testValue;

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
        public Integer num(int value) {
            return 1;
        }

        @Override
        public Integer num(double value) {
            return 1;
        }

        @Override
        public Integer num(BigDecimal value) {
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
        public Integer obj(Map<String, Integer> fields) {
            return super.obj(fields) + 1;
        }
    }

    @Test
    public void computeNodeCount() {
        final int nc = JsonAlgStack.apply(testValue, new NodeCount());
        assertEquals("Count of nodes", 20, nc);
    }
}
