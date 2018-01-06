package org.typemeta.funcj.data;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.typemeta.funcj.functions.Functions;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.util.Exceptions.wrap;

@RunWith(JUnitQuickcheck.class)
public class LazyTest {
    private static class InternalTestException extends RuntimeException {}

    private static int nextVal = 123;

    @Property
    public void testGet(String s) {
        final Lazy<String> l = Lazy.ofTS(() -> s);
        assertEquals(s, l.apply());
    }

    @Test
    public void testLaziness() {
        Lazy.ofTS(() -> {throw new InternalTestException();});
    }

    @Test(expected=InternalTestException.class)
    public void testLaziness2() {
        final Lazy<String> l = Lazy.ofTS(() -> {throw new InternalTestException();});
        l.apply();
    }

    @Test
    public void testGetReturnsSameValue() {
        final Lazy<Integer> l = Lazy.of(() -> nextVal++);
        assertEquals(l.apply(), l.apply());
    }

    @Property
    public void testTSGet(String s) {
        final Lazy<String> l = Lazy.of(() -> s);
        assertEquals(s, l.apply());
    }

    @Test
    public void testTSLaziness() {
        Lazy.of(() -> {throw new InternalTestException();});
    }

    @Test(expected=InternalTestException.class)
    public void testThreadSafeLaziness2() {
        final Lazy<String> l = Lazy.of(() -> {throw new InternalTestException();});
        l.apply();
    }

    @Test
    public void testTSGetReturnsSameValue() {
        final Lazy<Integer> l = Lazy.of(() -> nextVal++);
        assertEquals(l.apply(), l.apply());
    }

    @Test
    public void testTSIsThreadSafe() throws InterruptedException {
        // Changing this to false will result in exceptions.
        final boolean USE_THREADSAFE_LAZY = true;
        final int N_VALUES = 10;
        final int N_THREADS = N_VALUES * N_VALUES;
        final ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

        final List<Lazy<Integer>> lct2 =
            IntStream.range(0, N_VALUES)
                .boxed()
                .map(i -> delayedLazy(USE_THREADSAFE_LAZY, i))
                .collect(toList());

        final List<Callable<Integer>> llzi =
                IntStream.range(0, N_THREADS)
                        .boxed()
                        .map(i -> lct2.get(i % N_VALUES))
                        .map(lz -> callable((lz::apply)))
                        .collect(toList());

        executor.invokeAll(llzi)
                .forEach(flzi -> wrap(() -> flzi.get()));
    }

    private static <T> Lazy<Integer> delayedLazy(boolean threadSafe, int i) {
        final Functions.F0<Integer> res = new Delayed(i);

        if (threadSafe) {
            return Lazy.ofTS(res);
        } else {
            return Lazy.of(res);
        }
    }

    private static <T> Callable<T> callable(Supplier<T> supp) {
        return supp::get;
    }

    static class Delayed implements Functions.F0<Integer> {
        private static final int DELAY = 100;

        final int initValue;
        int value;

        public Delayed(int value) {
            this.initValue = value;
            this.value = value - 1;
        }

        @Override
        public Integer apply() {
            ++value;
            wrap(() -> Thread.sleep(DELAY));
            // Delayed.apply should only get called once for each object.
            // A failure here implied Delayed.apply has been called multiple times.
            assertEquals("value has only been incremented once", initValue, value);
            return value;
        }
    }
}
