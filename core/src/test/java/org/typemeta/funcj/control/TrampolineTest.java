package org.typemeta.funcj.control;

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
        return factT(n).runT();
    }

    public static Trampoline<Integer> fib(int n) {
        if (n <= 1) {
            return Trampoline.done(n);
        } else {
            return Trampoline.defer(() -> fib(n-1)).flatMap(x ->
                    Trampoline.defer(() -> fib(n-2)).flatMap(y ->
                            Trampoline.done(x + y)
                    )
            );
        }
    }

    private static Trampoline<Integer> factT(final int n) {
        if (n <= 1) {
            return Trampoline.done(1);
        } else {
            return Trampoline.defer(() -> factT(n - 1)).flatMap(x ->
                    Trampoline.done(n*x)
            );
        }
    }

    private static int count(final int n) {
        return countT(n).runT();
    }

    private static Trampoline<Integer> countT(final int n) {
        if (n == 0) {
            return Trampoline.done(0);
        } else {
            return Trampoline.defer(() -> countT(n - 1)).flatMap(x ->
                    Trampoline.done(x+1)
            );
        }
    }
}
