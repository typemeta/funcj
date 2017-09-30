package org.typemeta.funcj.data;

import org.junit.*;
import org.typemeta.funcj.control.Exceptions;
import org.typemeta.funcj.tuples.Tuple2;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.*;

public class LazyTest {
    private static class InternalTestException extends RuntimeException {}

    private static int nextVal = 123;

    private static <T> Lazy<T> delayedLazy(boolean threadSafe, T r, long delay) {
        if (threadSafe) {
            return Lazy.of(() -> {
                Exceptions.wrap(() -> Thread.sleep(delay));
                return r;
            });
        } else {
            return Lazy.ofTS(() -> {
                Exceptions.wrap(() -> Thread.sleep(delay));
                return r;
            });
        }
    }

    @Test
    public void testGet() {
        final String VALUE = "s9rjIOmyuo3r0-";
        final Lazy<String> l = Lazy.ofTS(() -> VALUE);
        assertEquals(VALUE, l.apply());
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

    @Test
    public void testTSGet() {
        final String VALUE = "s9rjIOmyuo3r0-";
        final Lazy<String> l = Lazy.of(() -> VALUE);
        assertEquals(VALUE, l.apply());
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

    private static <T> Callable<T> callable(Supplier<T> supp) {
        return supp::get;
    }

    @Test
    public void testTSIsThreadSafe() throws InterruptedException {
        final int N_THREADS = 100;
        final int N_VALUES = 10;
        final int DELAY = 1000;
        final Random rng = new Random();
        final ExecutorService executor = Executors.newFixedThreadPool(N_THREADS * N_VALUES / 2);

        final Map<Integer, Lazy<Integer>> lm =
            IntStream.range(0, N_VALUES)
                .mapToObj(Integer::new)
                .collect(toMap(
                    i -> i,
                    i -> delayedLazy(true, i, rng.nextInt(DELAY))
                ));

        final List<Callable<Tuple2<Integer, Integer>>> lct2 =
            IntStream.range(0, N_THREADS)
                .mapToObj(Integer::new)
                .flatMap(i -> IntStream.range(1, N_VALUES)
                    .mapToObj(j -> callable(() -> Tuple2.of(j, lm.get(j).apply())))
                ).collect(toList());

        executor
            .invokeAll(lct2)
            .stream()
            .map(ft2 -> Exceptions.wrap(() -> ft2.get()))
            .forEach(t2 -> Assert.assertEquals(t2._1.toString(), t2._1, t2._2));
    }
}
