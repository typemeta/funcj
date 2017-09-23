package io.typemeta.funcj.control;

import org.junit.*;

public class TrampolineTest {
    @Test
    public void testCount() {
        final int N = 65536;
        int i = count(N);

        Assert.assertEquals("Count", N, i);
    }

    @Test
    public void testFactorial() {
        final int N = 5;
        int i = factorial(5);

        Assert.assertEquals("Factorial", 120, i);
    }

    private static int factorial(final int n) {
        return factorial(n, 1).compute();
    }

    private static Trampoline<Integer> factorial(final int n, final int sum) {
        if (n == 1) {
            return Trampoline.done(sum);
        } else {
            return Trampoline.more(() -> factorial(n - 1, sum * n));
        }
    }

    private static int count(final int n) {
        return count(n, 1).compute();
    }

    private static Trampoline<Integer> count(final int n, final int sum) {
        if (n == 1) {
            return Trampoline.done(sum);
        } else {
            return Trampoline.more(() -> count(n - 1, sum + 1));
        }
    }
}
