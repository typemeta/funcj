package org.funcj.control;

import org.junit.Test;

public class TrampolineTest {
    @Test
    public void test() {
        int i = count(65536);

        System.out.println(i);
    }

    public static int factorial(final int n) {
        return factorial(n, 1).compute();
    }

    public static Trampoline<Integer> factorial(final int n, final int sum) {
        if (n == 1) {
            return Trampoline.done(sum);
        } else {
            return Trampoline.more(() -> factorial(n - 1, sum * n));
        }
    }

    public static int count(final int n) {
        return count(n, 1).compute();
    }

    public static Trampoline<Integer> count(final int n, final int sum) {
        if (n == 1) {
            return Trampoline.done(sum);
        } else {
            return Trampoline.more(() -> count(n - 1, sum + 1));
        }
    }
}