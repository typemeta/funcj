package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecConfigImpl;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Base class for {@link Config} implementations.
 */
public class ConfigImpl extends CodecConfigImpl implements Config {

    @Override
    public String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "_" + name;
        }
        return name;
    }

    @Override
    public String entryElemName() {
        return "_";
    }

    @Override
    public String typeAttrName() {
        return "type";
    }

    @Override
    public String keyElemName() {
        return "key";
    }

    @Override
    public String valueElemName() {
        return "value";
    }

    @Override
    public String nullAttrName() {
        return "null";
    }

    @Override
    public String nullAttrVal() {
        return "true";
    }
}
