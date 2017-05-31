package org.funcj.codec;

import org.funcj.codec.json.JsonCodecCore;
import org.funcj.json.Node;
import org.junit.*;

import static java.lang.System.out;

public class OldJsonCodecTest {
    final static JsonCodecCore codec = new JsonCodecCore();

    @Test
    public void roundTrip() {
        final Example.Derived val = Example.Derived.create();
        final Node node = codec.encode(Example.Derived.class, val, null);
        out.println(node.toJson(40));

        final Example.Derived val2 = codec.decode(Example.Derived.class, node);

        Assert.assertEquals(val, val2);
    }

    @Test
    public void roundTrip2() {
        final Example.Derived2 val = Example.Derived2.create();
        final Node node = codec.encode(Example.Derived2.class, val, null);
        out.println(node.toJson(40));

        final Example.Derived2 val2 = codec.decode(Example.Derived2.class, node);

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
//
//    static abstract class MapImpl implements Map<String, Double> {
//
//    }
//
//    static abstract class GenMapImpl<K, V> implements Map<K, V> {
//        Map<String, Double> d;
//    }
//
//    public void stuff() throws NoSuchFieldException {
//
//        //dump(MapImpl.class);
//        getTypeParams(HashMap.class, Map.class).forEach(out::println);
//        getTypeParams(MapImpl.class, Map.class).forEach(out::println);
//        getTypeParams(GenMapImpl.class.getDeclaredField("d"), Map.class).forEach(out::println);
//        //getTypes(, Map.class);//.forEach(out::println);
//        //dump(HashMap.class);
//    }
//
//    static List<Class<?>> getTypeParams(Field field, Class iface) {
//        out.println("Field: " + field);
//        Type type = field.getGenericType();
//        if (type instanceof ParameterizedType) {
//            final ParameterizedType pt =(ParameterizedType)type;
//            final Type rawType = pt.getRawType();
//            if (rawType.equals(iface)) {
//                return getGenTypeParams(pt);
//            }
//        }
//        return Collections.emptyList();
//    }
//
//    static List<Class<?>> getTypeParams(Class implClass, Class iface) {
//        out.println("Class: " + implClass);
//        final List<ParameterizedType> genIfaces =
//                Arrays.stream(implClass.getGenericInterfaces())
//                        .filter(t -> t instanceof ParameterizedType)
//                        .map(t -> (ParameterizedType) t)
//                        .collect(toList());
//        return genIfaces.stream()
//                .filter(pt -> pt.getRawType().equals(iface))
//                .findFirst()
//                .map(pt -> getGenTypeParams(pt))
//                .orElseThrow(() -> new RuntimeException(""));
//    }
//
//    static List<Class<?>> getGenTypeParams(ParameterizedType type) {
//        return Arrays.stream(type.getActualTypeArguments())
//                .filter(t -> t instanceof Class)
//                .map(t -> (Class<?>)t)
//                .collect(toList());
//    }
//
//    static void dump(Class<?> clazz) {
//        out.println("******");
//        out.println(clazz.getName());
//
//        final Type[] types = clazz.getGenericInterfaces();
//        Arrays.stream(types).forEach(t -> out.println("class=" + t.getClass()));
//        Arrays.stream(types).forEach(out::println);
//        out.println();
//
//        final ParameterizedType pType = (ParameterizedType)types[0];
//        out.println("class=" + pType.getClass());
//        out.println(pType);
//        out.println();
//
//        final Type rawType = pType.getRawType();
//        out.println("class=" + rawType);
//        out.println(rawType.getTypeName());
//        out.println();
//
//        final Type[] typeVars = pType.getActualTypeArguments();
//        Arrays.stream(typeVars).forEach(t -> out.println(t.getClass()));
//        Arrays.stream(typeVars).forEach(out::println);
//        out.println();
//    }
}
