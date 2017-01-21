package org.javafp.parsec4j.text;

import org.javafp.parsec4j.expr.Model;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

import java.io.IOException;

public class JmhTest {

//    public static void main(String[] args) throws IOException, RunnerException {
//        System.in.read();
//        new JmhTest().runJmh();
//    }
    @Test
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

    private static final Parser<Model.Expr> parser = Grammar.parser;

    public static void main(String[] args) throws IOException {
        System.out.println(testGood());
        System.out.println(testBad());
    }

    @Benchmark
    public static String testGood() {
        return org.javafp.parsec4j.expr.Grammar.parse("3*-max(4%+(5bp+-x),-2bp)-1").toString();
    }

    @Benchmark
    public static String testBad() {
        return Grammar.parse("3*-max(4%+(5bp+-x)x,-2bp)-1z").toString();
    }

}
