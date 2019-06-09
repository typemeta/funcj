package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Base class for {@link XmlTypes.Config} implementations.
 */
public class XmlConfigImpl extends CodecConfigImpl implements XmlTypes.Config {

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
