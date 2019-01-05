package org.typemeta.funcj.control;

import org.junit.Test;
import org.typemeta.funcj.data.IList;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.algebra.MonoidInstances.monoidIList;

public class WriterMTest {

    private static <T> WriterM<IList<String>, T> logger(T t) {
        return WriterM.of(monoidIList(), t, IList.of(t.toString()));
    }

    @Test
    public void monadDemo() {
        final WriterM<IList<String>, Integer> logResult =
                logger(3).flatMap(a ->
                        logger(5).flatMap(b ->
                                WriterM.pure(monoidIList(), a + b)));
        final IList<String> log = logResult.written();
        final int result = logResult.value();

        assertEquals(IList.of("3", "5"), log);
        assertEquals(8, result);
    }
}
