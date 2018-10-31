package org.typemeta.funcj.codec;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Base class for {@link CodecConfig} implementations.
 */
public class CodecConfigImpl implements CodecConfig {

    @Override
    public String classToName(Class<?> clazz) {
        return clazz.getName();
    }

    @Override
    public String classToName(Class<?> clazz, Class<?>... classes) {
        switch (classes.length) {
            case 1:
                return classToName(clazz) + '|' + classToName(classes[0]);
            case 2:
                return classToName(clazz) + '|' + classToName(classes[0])
                        + '|' + classToName(classes[1]);
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append(classToName(clazz)).append('|');
                for (Class<?> cls : classes) {
                    sb.append(classToName(cls)).append('|');
                }
                return sb.toString();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> nameToClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new CodecException("Cannot create class from class name '" + name + "'", ex);
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
    public int defaultArrSize() {
        return 16;
    }
}
