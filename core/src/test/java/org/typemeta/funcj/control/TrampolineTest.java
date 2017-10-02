package org.typemeta.funcj.control;

import org.junit.*;

import static org.typemeta.funcj.control.Trampoline.defer;
import static org.typemeta.funcj.control.Trampoline.done;

public class TrampolineTest {
    @Test
    public void testCount() {
        final int N = 65536;
        int i = countT(N).runT();

        Assert.assertEquals("Count", N, i);
    }

    @Test
    public void testFactorial() {
        final int N = 5;
        int i = factT(N).runT();

        Assert.assertEquals("Factorial", 120, i);
    }

    @Test
    public void testFibonacci() {
        final int N = 10;
        int i = fib(N).runT();

        Assert.assertEquals("Factorial", 55, i);
    }

    public static Trampoline<Integer> fib(int n) {
        if (n <= 1) {
            return done(n);
        } else {
            return defer(() -> fib(n-1)).flatMap(x ->
                    defer(() -> fib(n-2)).flatMap(y ->
                            done(x + y)
                    )
            );
        }
    }

    private static Trampoline<Integer> factT(final int n) {
        if (n <= 1) {
            return done(1);
        } else {
            return defer(() -> factT(n - 1)).flatMap(x ->
                    done(n*x)
            );
        }
    }

    private static Trampoline<Integer> countT(final int n) {
        if (n == 0) {
            return done(0);
        } else {
            return defer(() -> countT(n - 1)).flatMap(x ->
                    done(x+1)
            );
        }
    }
}
