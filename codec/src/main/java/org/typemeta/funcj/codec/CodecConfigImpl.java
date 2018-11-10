package org.typemeta.funcj.codec;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Base class for {@link CodecConfig} implementations.
 */
public abstract class CodecConfigImpl implements CodecConfig {

    protected final Set<Package> allowedPackages = new TreeSet<>(Comparator.comparing(Package::getName));

    protected final Set<Class<?>> allowedClasses = new TreeSet<>(Comparator.comparing(Class::getName));

    /**
     * A map that associates a class with its proxy.
     */
    protected final Map<Class<?>, Class<?>> typeProxyRegistry = new TreeMap<>(Comparator.comparing(Class::getName));

    protected CodecConfigImpl() {
    }

    @Override
    public void registerAllowedPackage(Package pkg) {
        allowedPackages.add(pkg);
    }


    @Override
    public void registerAllowedClass(Class<?> clazz) {
        allowedClasses.add(clazz);
    }

    @Override
    public <T> Class<T> checkClassIsAllowed(Class<T> clazz) {
        // Unwrap array types.
        Class<?> cls = clazz;
        while (cls.isArray()) {
            cls = cls.getComponentType();
        }

        if (cls.isPrimitive()) {
            return clazz;
        }

        if (allowedPackages.contains(cls.getPackage())) {
            return clazz;
        } else {
            if (allowedClasses.contains(cls)) {
                return clazz;
            } else {
                throw new RuntimeException(cls + "' is not allowed");
            }
        }
    }

    @Override
    public void registerTypeProxy(Class<?> clazz, Class<?> proxy) {
        typeProxyRegistry.put(clazz, proxy);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> remapType(Class<T> clazz) {
        if (typeProxyRegistry.containsKey(clazz)) {
            return (Class<T>) typeProxyRegistry.get(clazz);
        } else {
            return clazz;
        }
    }

    @Override
    public String classToName(Class<?> clazz) {
        return checkClassIsAllowed(clazz).getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> nameToClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new CodecException("Cannot create class from name '" + name + "'", ex);
        }
    }

    @Override
    public String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "*" + name;
        }
        return name;
    }

    @Override
    public int defaultArraySize() {
        return 16;
    }
}
