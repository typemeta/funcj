package org.typemeta.funcj.codec.avro;

import org.apache.avro.Schema;
import org.typemeta.funcj.codec.utils.CodecException;
import org.typemeta.funcj.data.IList;

import static java.util.stream.Collectors.toList;

public abstract class GenerateSchema {

    public static Schema apply(Class<?> clazz) {
        return apply(new Alg(), clazz);
    }

    public static Schema apply(JavaTypeAlg<Schema> alg, Class<?> clazz) {
        return apply(new JavaTypeTraversal<Schema>(alg), clazz);
    }

    public static Schema apply(JavaTypeTraversal<Schema> jtt, Class<?> clazz) {
        return jtt.apply(clazz);
    }

    public static class Alg implements JavaTypeAlg<Schema> {

        private static String toName(IList<String> path, String name) {
            return path.match(
                    nel -> nel.reverse().foldLeft1((ls, rs) -> ls + "." + rs),
                    el -> ""
            );
        }

        @Override
        public Schema booleanB(IList<String> path, String name) {
            return Schema.createUnion(Schema.create(Schema.Type.BOOLEAN), Schema.create(Schema.Type.NULL));
        }

        @Override
        public Schema booleanP(IList<String> path, String name) {
            return Schema.create(Schema.Type.BOOLEAN);
        }

        @Override
        public Schema booleanArr(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.BOOLEAN)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema byteB(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.createFixed(toName(path, name), null, null, 1),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema byteP(IList<String> path, String name) {
            return Schema.createFixed(toName(path, name), null, null, 1);
        }

        @Override
        public Schema byteArr(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.BYTES),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema charB(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.STRING),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema charP(IList<String> path, String name) {
            return Schema.create(Schema.Type.STRING);
        }

        @Override
        public Schema charArr(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.BOOLEAN)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema shortB(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.INT),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema shortP(IList<String> path, String name) {
            return Schema.create(Schema.Type.INT);
        }

        @Override
        public Schema shortArr(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.INT)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema integerB(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.INT),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema integerP(IList<String> path, String name) {
            return Schema.create(Schema.Type.INT);
        }

        @Override
        public Schema integerArr(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.INT)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema longB(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.LONG),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema longP(IList<String> path, String name) {
            return Schema.create(Schema.Type.LONG);
        }

        @Override
        public Schema longArr(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.LONG)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema floatB(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.FLOAT),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema floatP(IList<String> path, String name) {
            return Schema.create(Schema.Type.FLOAT);
        }

        @Override
        public Schema floatArr(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.FLOAT)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema doubleB(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.DOUBLE),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema doubleP(IList<String> path, String name) {
            return Schema.create(Schema.Type.DOUBLE);
        }

        @Override
        public Schema doubleArr(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.createArray(Schema.create(Schema.Type.DOUBLE)),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema string(IList<String> path, String name) {
            return Schema.createUnion(
                    Schema.create(Schema.Type.STRING),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema object(IList<String> path, String name, Map<String, Schema> fields) {
            final List<Schema.Field> fieldList =
                    fields.entrySet()
                            .stream()
                            .map(en -> new Schema.Field(en.getKey(), en.getValue()))
                            .collect(toList());
            return Schema.createUnion(
                    Schema.createRecord(toName(path, name), null, null, false, fieldList),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema objectArr(IList<String> path, String name, Schema elem) {
            return Schema.createUnion(
                    Schema.createArray(elem),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema enumT(IList<String> path, String name, Class<?> enumType) {
            final List<String> enumValues = Arrays.stream(enumType.getEnumConstants()).map(Object::toString).collect(toList());
            return Schema.createUnion(
                    Schema.createEnum(toName(path, name), null, null, enumValues),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema coll(IList<String> path, String name, Schema elem) {
            return Schema.createUnion(
                    Schema.createArray(elem),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema stringMap(IList<String> path, String name, Schema value) {
            return Schema.createUnion(
                    Schema.createMap(value),
                    Schema.create(Schema.Type.NULL)
            );
        }

        @Override
        public Schema map(
                IList<String> path, String name,
                Schema key,
                Schema value
        ) {
            throw new CodecException("Maps with a non-string key type are not supported.");
        }

        @Override
        public Schema interfaceT(IList<String> path, String name, Class<?> ifaceType) {
            throw new CodecException("Non-collection interface types are not supported.");
        }
    }
}
