package org.typemeta.funcj.codec2.json;

import org.junit.Test;
import org.typemeta.funcj.codec2.core.Context;
import org.typemeta.funcj.codec2.json.io.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonCodecTest {
    public static class BaseTypes {
        public final boolean boolField;
        public final int intField;
        public final String strField;

        public BaseTypes(boolean boolField, int intField, String strField) {
            this.boolField = boolField;
            this.intField = intField;
            this.strField = strField;
        }

        private BaseTypes() {
            this.boolField = false;
            this.intField = 0;
            this.strField = null;
        }

        @Override
        public String toString() {
            return "BaseTypes{" +
                    "boolField=" + boolField +
                    ", intField=" + intField +
                    ", strField='" + strField + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final BaseTypes rhsT = (BaseTypes) rhs;
                return boolField == rhsT.boolField &&
                        intField == rhsT.intField &&
                        Objects.equals(strField, rhsT.strField);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(boolField, intField, strField);
        }
    }

    public static class ArrayTypes {
        public final boolean[] boolField;
        public final int[] intField;
        public final String[] strField;

        public ArrayTypes(boolean[] boolField, int[] intField, String[] strField) {
            this.boolField = boolField;
            this.intField = intField;
            this.strField = strField;
        }

        private ArrayTypes() {
            this.boolField = null;
            this.intField = null;
            this.strField = null;
        }

        @Override
        public String toString() {
            return "ArrayTypes{" +
                    "boolField=" + Arrays.toString(boolField) +
                    ", intField=" + Arrays.toString(intField) +
                    ", strField=" + Arrays.toString(strField) +
                    '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final ArrayTypes rhsT = (ArrayTypes) rhs;
                return Arrays.equals(boolField, rhsT.boolField) &&
                        Arrays.equals(intField, rhsT.intField) &&
                        Arrays.equals(strField, rhsT.strField);
            }
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(boolField);
            result = 31 * result + Arrays.hashCode(intField);
            result = 31 * result + Arrays.hashCode(strField);
            return result;
        }
    }

    public interface SomeInterface {

    }

    public static class SomeImpl implements SomeInterface {
        public final String name;

        public SomeImpl(String name) {
            this.name = name;
        }

        private SomeImpl() {
            this.name = null;
        }

        @Override
        public String toString() {
            return "SomeImpl{" +
                    "name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final SomeImpl rhsT = (SomeImpl) rhs;
                return Objects.equals(name, rhsT.name);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    public static class Recursive {
        final Recursive parent;

        public Recursive(Recursive parent) {
            this.parent = parent;
        }

        private Recursive() {
            this.parent = null;
        }

        @Override
        public String toString() {
            return "Recursive{" +
                    "parent='" + parent + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final Recursive rhsT = (Recursive) rhs;
                return Objects.equals(parent, rhsT.parent);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(parent);
        }
    }

    public static class Container {
        final BaseTypes baseTypes;
        final ArrayTypes arrayTypes;
        final SomeInterface intfType;
        final SomeInterface[] intfTypes;

        public Container(BaseTypes baseTypes, ArrayTypes arrayTypes, SomeInterface intfType, SomeInterface[] intfTypes) {
            this.baseTypes = baseTypes;
            this.arrayTypes = arrayTypes;
            this.intfType = intfType;
            this.intfTypes = intfTypes;
        }

        private Container() {
            this.baseTypes = null;
            this.arrayTypes = null;
            this.intfType = null;
            this.intfTypes = null;
        }

        @Override
        public String toString() {
            return "Container{" +
                    "baseTypes=" + baseTypes +
                    ", arrayTypes=" + arrayTypes +
                    ", intfType=" + intfType +
                    ", intfTypes=" + Arrays.toString(intfTypes) +
                    '}';
        }

        @Override
        public boolean equals(Object rhs) {
            if (this == rhs) {
                return true;
            } else if (rhs == null || getClass() != rhs.getClass()) {
                return false;
            } else {
                final Container rhsT = (Container) rhs;
                return Objects.equals(baseTypes, rhsT.baseTypes) &&
                        Objects.equals(arrayTypes, rhsT.arrayTypes) &&
                        Objects.equals(intfType, rhsT.intfType) &&
                        Arrays.equals(intfTypes, rhsT.intfTypes);
            }
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(baseTypes, arrayTypes, intfType);
            result = 31 * result + Arrays.hashCode(intfTypes);
            return result;
        }
    }

    static class SampleData {
        public static boolean boolVal(AtomicInteger n) {
            return n.getAndIncrement() % 2 == 0;
        }

        public static int intVal(AtomicInteger n) {
            return n.getAndIncrement() * 10;
        }

        public static String strVal(String prefix, AtomicInteger n) {
            return prefix + n.getAndIncrement();
        }

        public static Container container(AtomicInteger n) {
            return new Container(
                    baseType(n),
                    baseTypes(n),
                    intfType(n),
                    intfTypes(n)
            );
        }

        private static BaseTypes baseType(AtomicInteger n) {
            return new BaseTypes(
                    boolVal(n),
                    intVal(n),
                    strVal("str", n)
            );
        }

        private static ArrayTypes baseTypes(AtomicInteger n) {
            return new ArrayTypes(
                    new boolean[] {
                            boolVal(n),
                            boolVal(n),
                            boolVal(n)
                    }, new int[] {
                            intVal(n),
                            intVal(n),
                            intVal(n),
                            intVal(n)
                    }, new String[]{
                            strVal("s", n),
                            strVal("s", n)
                    }
            );
        }

        private static SomeInterface intfType(AtomicInteger n) {
            return new SomeImpl(strVal("name", n));
        }

        private static SomeInterface[] intfTypes(AtomicInteger n) {
            return new SomeInterface[] {
                    intfType(n),
                    intfType(n),
                    intfType(n),
                    intfType(n)
            };
        }
    }

    @Test
    public void testJson() {
        final Container container = SampleData.container(new AtomicInteger(0));

        System.out.println("BEFORE=" + container);

        final JsonCodecCore codecCore = new JsonCodecCore();

        final Context ctx = new Context() {};

        final StringWriter sw = new StringWriter();
        final JsonGenerator jsonGen = new JsonGenerator(sw);

        codecCore.getEncoder(Container.class).encode(codecCore, ctx, container, jsonGen);

        final String json = sw.toString();
        System.out.println("JSON=" + json);

        final StringReader sr = new StringReader(json);
        final JsonStreamParser parser = new JsonStreamParser(sr, 3);
        final Container container2 = codecCore.getDecoder(Container.class).decode(codecCore, ctx, parser);

        System.out.println("AFTER=" + container2);

        System.out.println("MATCH=" + Objects.equals(container,container2));
    }
}
