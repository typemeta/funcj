package org.typemeta.funcj.codec.jsons;

import org.junit.*;
import org.typemeta.funcj.codec.*;

import java.io.*;
import java.util.*;

public class JsonCodecTest extends TestBase {

    final static JsonCodecCore codec = Codecs.jsonsCodec();

    static {
        codec.registerTypeConstructor(TestTypes.NoEmptyCtor.class, () -> TestTypes.NoEmptyCtor.create(false));
        registerCustomCodec(codec);
    }

    @Override
    protected <T> void roundTrip(T val, Class<T> clazz) {
        final StringWriter sw = new StringWriter();
        codec.encode(clazz, val, sw);

        System.out.println(sw);

        final StringReader sr = new StringReader(sw.toString());
        final T val2 = codec.decode(clazz, sr);

        if (printData || !val.equals(val2)) {
            System.out.println(sw);
        }

        Assert.assertEquals(val, val2);
    }

    @Test
    public void test() {
        Map<Object, Object> map = new HashMap<>();
        map.put(12, "12");
        //map.put(34, "34");

        roundTrip(map, Map.class);
    }
}
