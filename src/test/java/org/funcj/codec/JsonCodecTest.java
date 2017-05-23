package org.funcj.codec;

import org.funcj.json.Node;
import org.junit.*;

import java.lang.reflect.*;
import java.util.*;

import static java.lang.System.out;

public class JsonCodecTest {
    final static JsonCodecCore codec = new JsonCodecCore();

    static {
        codec.initialise();
    }

    @Test
    public void roundTrip() {
        final Example.Derived val = Example.Derived.create();
        final Node node = codec.encode(Example.Derived.class, val, null);
        out.println(node.toJson(40));

        final Example.Derived val2 = (Example.Derived)codec.decode(Example.Derived.class, node);

        Assert.assertEquals(val, val2);
    }

    @Test
    public void roundTrip2() {
        final Example.Simple val = Example.Simple.create();
        final Node node = codec.encode(Example.Simple.class, val, null);
        out.println(node.toJson(40));

        final Example.Simple val2 = codec.decode(Example.Simple.class, node);

        Assert.assertEquals(val, val2);
    }

    @Test
    public void roundTrip3() throws NoSuchFieldException {
        final Example.TestObj val = Example.TestObj.create();
        final Node node = codec.encode(Example.TestObj.class, val, null);
        out.println(node.toJson(40));

        final Example.TestObj val2 = codec.decode(Example.TestObj.class, node);

        Assert.assertEquals(val, val2);
    }

    static abstract class MapImpl implements Map<String, Double> {

    }

    static abstract class GenMapImpl<K, V> implements Map<K, V> {

    }

    @Test
    public void stuff() {
        //Field field = MapImpl.class.getField("iface");
        //dump(MapImpl.class);
        dump(HashMap.class);
    }

    static void dump(Class<?> clazz) {
        out.println("******");
        out.println(clazz.getName());

        final Type[] types = clazz.getGenericInterfaces();
        Arrays.stream(types).forEach(t -> out.println("class=" + t.getClass()));
        Arrays.stream(types).forEach(out::println);
        out.println();

        final ParameterizedType pType = (ParameterizedType)types[0];
        out.println("class=" + pType.getClass());
        out.println(pType);
        out.println();

        final Type rawType = pType.getRawType();
        out.println("class=" + rawType);
        out.println(rawType.getTypeName());
        out.println();

        final Type[] typeVars = pType.getActualTypeArguments();
        Arrays.stream(typeVars).forEach(t -> out.println(t.getClass()));
        Arrays.stream(typeVars).forEach(out::println);
        out.println();
    }
}
