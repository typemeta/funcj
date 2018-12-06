package org.typemeta.funcj.codec;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Base class for {@link CodecConfig} implementations.
 */
public class CodecConfigImpl implements CodecConfig {


    protected final Set<Package> allowedPackages = new TreeSet<>(Comparator.comparing(Package::getName));

    protected final Set<Class<?>> allowedClasses = new TreeSet<>(Comparator.comparing(Class::getName));

    protected final Map<Class<?>, String> classToNameMap = new TreeMap<>(Comparator.comparing(Class::getName));

    protected final Map<String, Class<?>> nameToClassMap = new HashMap<>();

    protected final Map<Class<?>, Class<?>> defaultDynamicTypeMap = new TreeMap<>(Comparator.comparing(Class::getName));

    /**
     * A map that associates a class with its proxy.
     * Where a class has a proxy, the codec for the proxy will be used for the class.
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
    public <T> Class<T> mapToProxy(Class<T> clazz) {
        if (typeProxyRegistry.containsKey(clazz)) {
            return (Class<T>) typeProxyRegistry.get(clazz);
        } else {
            return clazz;
        }
    }

    @Override
    public void registerTypeAlias(Class<?> clazz, String name) {
        classToNameMap.put(clazz, name);
        nameToClassMap.put(name, clazz);
    }

    @Override
    public String classToName(Class<?> clazz) {
        final String name = classToNameMap.get(clazz);
        return name == null ? clazz.getName() : name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> nameToClass(String name) {
        Class<T> clazz = (Class<T>) nameToClassMap.get(name);
        if (clazz != null) {
            return checkClassIsAllowed(clazz);
        } else {
            try {
                clazz = (Class<T>) Class.forName(name);
                return checkClassIsAllowed(clazz);
            } catch (ClassNotFoundException ex) {
                throw new CodecException("Cannot find class from name '" + name + "'", ex);
            }
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

    @Override
    public int resizeArray(int size) {
        return size + (size >> 1);
    }

    @Override
    public <T> void registerDefaultSubType(Class<T> stcClass, Class<? extends T> dynClass) {
        defaultDynamicTypeMap.put(stcClass, dynClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, U extends T> Class<U> getDefaultSubType(Class<T> stcClass) {
        return (Class<U>)defaultDynamicTypeMap.get(stcClass);
    }

    @Override
    public <T> boolean dynamicTypeMatch(Class<T> stcClass, Class<? extends T> dynClass) {
        if (dynClass == stcClass) {
            return true;
        } else if (stcClass.isEnum()) {
            return dynClass.getSuperclass() == stcClass;
        } else {
            return false;
        }
    }
}
