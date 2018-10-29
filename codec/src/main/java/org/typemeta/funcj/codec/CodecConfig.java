package org.typemeta.funcj.codec;

public class CodecConfig {

    @SuppressWarnings("unchecked")
    public <T> Class<T> nameToClass(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException ex) {
            throw new CodecException("Cannot create class from class name '" + name + "'", ex);
        }
    }

    /**
     * Map a class to a name.
     * @param clazz     the class
     * @return          the name
     */
    public String classToName(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * Map one or  more classes to a name.
     * @param clazz     the class
     * @param classes   the classes
     * @return          the name
     */
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

    public int defaultArrSize() {
        return 16;
    }

}
