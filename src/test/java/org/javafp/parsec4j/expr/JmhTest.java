package org.javafp.parsec4j.expr;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

import java.io.IOException;

public class JmhTest {

    public static void main(String args[]) throws IOException, RunnerException {
        new JmhTest().runJmh();
    }

    public void runJmh() throws RunnerException, IOException {
        final Options opt = new OptionsBuilder()
            .jvmArgs("-XX:+UnlockCommercialFeatures")
                .include(JmhTest.class.getSimpleName())
                .warmupIterations(20)
                .measurementIterations(20)
                .forks(1)
                .build();
           new Runner(opt).run();
    }

    static  {
        System.out.println("Initialising...");
        Grammar.parser.acceptsEmpty();
        Grammar.parser.firstSet();
    }

    @Benchmark
    public static String testGood() {
        return Grammar.parse("3*-max(4%+(5bp+-x),-2bp)-1").toString();
    }

    @Benchmark
    public static String testBad() {
        return Grammar.parse("3*-max(4%+(5bp+-x)x,-2bp)-1z").toString();
    }
}
