package org.funcj.codec;

import org.funcj.codec.old.*;
import org.funcj.control.Exceptions;

import java.lang.reflect.*;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class ClassDef {

    static class Registry {

        private static String classKey(Class<?> clazz) {
            return clazz.getName();
        }

        private final Map<String, ClassDef> classDefs = new HashMap<>();

        public ClassDef get(Class<?> clazz) {
            final String key = classKey(clazz);
            if (classDefs.containsKey(key)) {
                return classDefs.get(key);
            } else {
                final ClassDef classDef = new ClassDef();
                classDefs.put(key, classDef);
                final Map<String, FieldCodec> fieldCodecs = generateFieldCodecs(clazz);
                final Class<?> parentClass = clazz.getSuperclass();
                final Optional<ClassDef> parentClassDef;
                if (!parentClass.equals(Object.class)) {
                    parentClassDef = Optional.of(get(parentClass));
                } else {
                    parentClassDef = Optional.empty();
                }
                return classDef.set(parentClassDef, fieldCodecs);
            }
        }

        private Map<String, FieldCodec> generateFieldCodecs(Class<?> clazz) {
            final Field[] fields = clazz.getDeclaredFields();
            return Arrays.stream(fields)
                .collect(toMap(
                    Field::getName,
                    this::getFieldCodec
                ));
        }

        private FieldCodec getFieldCodec(Field field) {
            final Class<?> type = field.getType();
            if (type.isPrimitive()) {
                if (type.equals(Boolean.class)) {
                    return new BooleanFieldCodec(field);
//            } else if (type.equals(Character.class)) {
//                return new CharacterFieldCodec(field);
                } else {
                    throw new IllegalArgumentException("TODO");
                }
            } else if (Modifier.isFinal(type.getModifiers())) {
                throw new IllegalArgumentException("TODO");
                //return new FinalObjFieldCodec<>(field);
            } else {
                return new ObjFieldCodec<>(field, get(type));
            }
        }
    }

    static abstract class FieldCodec<E> {
        protected final Field field;

        protected FieldCodec(Field field) {
            this.field = field;
        }

        public abstract E encode(CodecRegistry<E> cdReg, Object obj, E out);

        public abstract void decode(CodecRegistry<E> cdReg, Object obj, E in);
    }

    static class BooleanFieldCodec<E> extends FieldCodec<E> {

        BooleanFieldCodec(Field field) {
            super(field);
        }

        @Override
        public E encode(CodecRegistry<E> cdReg, Object obj, E out) {
            final boolean fieldVal = Exceptions.wrap(() -> field.getBoolean(obj));
            return cdReg.booleanCodec().encode(fieldVal, out);
        }

        public void decode(CodecRegistry<E> cdReg, Object obj, E in) {
            final boolean fieldVal = cdReg.booleanCodec().decode(in);
            Exceptions.wrap(() -> field.setBoolean(obj, fieldVal));
        }
    }

    static class ObjFieldCodec<T, E> extends FieldCodec<E> {
        private final ClassDef classDef;

        protected ObjFieldCodec(Field field, ClassDef classDef) {
            super(field);
            this.classDef = classDef;
        }

        public E encode(CodecRegistry<E> cdReg, Object obj, E out) {
            final T fieldVal = Exceptions.wrap(() -> (T)field.get(obj));
            return cdReg.objectCodec(classDef).encode(fieldVal, out);
        }

        public void decode(CodecRegistry<E> cdReg, Object obj, E in) {
            final T fieldVal = cdReg.<T>objectCodec(classDef).decode(in);
            Exceptions.wrap(() -> field.set(obj, fieldVal));
        }
    }

    Optional<ClassDef> parent;

    Map<String, FieldCodec> fields;

    ClassDef set(Optional<ClassDef> parent, Map<String, FieldCodec> fields) {
        this.parent = parent;
        this.fields = fields;
        return this;
    }
}
