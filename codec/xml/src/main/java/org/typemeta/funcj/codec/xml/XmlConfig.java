package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Base class for {@link XmlTypes.Config} implementations.
 */
public class XmlConfig extends CodecConfigImpl implements XmlTypes.Config {

    public static class Builder extends AbstractBuilder<Builder, XmlTypes.Config> {
        @Override
        public XmlTypes.Config build() {
            return new XmlConfig(this);
        }
    }

    public static XmlConfig.Builder builder() {
        return new XmlConfig.Builder();
    }

    public XmlConfig() {
    }

    public XmlConfig(Builder builder) {
        super(builder);
    }

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

    @Override
    public String defaultRootElemName(Class<?> type) {
        return type.getSimpleName();
    }

    @Override
    public String defaultRootElemName() {
        return "Root";
    }
}
