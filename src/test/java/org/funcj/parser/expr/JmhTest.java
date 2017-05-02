package org.funcj.parser.expr;

import org.funcj.jmh.FlightRecordingProfiler;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

public class JmhTest {

    public static void main(String args[]) throws RunnerException {
        new JmhTest().runJmh();
    }

    public void runJmh() throws RunnerException {
        final Options opt = new OptionsBuilder()
                .jvmArgs("-XX:+UnlockCommercialFeatures")
                .include(JmhTest.class.getSimpleName())
                .warmupIterations(20)
                .measurementIterations(20)
                .addProfiler(FlightRecordingProfiler.class)
                .forks(1)
                .build();
           new Runner(opt).run();
    }

    static  {
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
