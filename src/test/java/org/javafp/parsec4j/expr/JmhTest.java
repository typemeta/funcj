package org.javafp.parsec4j.expr;

import org.javafp.data.Chr;
import org.javafp.parsec4j.*;
import org.junit.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

import java.io.IOException;

public class JmhTest {

    public void runJmh() throws RunnerException, IOException {
        final Options opt = new OptionsBuilder()
            .jvmArgs("-XX:+UnlockCommercialFeatures")
                .include(JmhTest.class.getSimpleName())
//                .warmupIterations(20)
//                .measurementIterations(10)
                .forks(1)
                .build();
           new Runner(opt).run();
    }

    private static final Parser<Chr, Grammar.Ctx, Model.Expr> parser = Grammar.parser;

    @Benchmark
    public static String testGood() {
        return Grammar.parse("3*-max(4%+(5bp+-x),-2bp)-1").toString();
    }

    @Benchmark
    public static String testBad() {
        return Grammar.parse("3*-max(4%+(5bp+-x)x,-2bp)-1z").toString();
    }
}
