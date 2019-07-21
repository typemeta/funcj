package org.typemeta.funcj.codec.xmlnode;

import org.typemeta.funcj.codec.impl.CodecConfigImpl;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Base class for {@link XmlNodeTypes.Config} implementations.
 */
public class XmlNodeConfig extends CodecConfigImpl implements XmlNodeTypes.Config {

    public static class Builder extends AbstractBuilder<Builder, XmlNodeTypes.Config> {
        @Override
        public XmlNodeTypes.Config build() {
            return new XmlNodeConfig(this);
        }
    }

    public static XmlNodeConfig.Builder builder() {
        return new XmlNodeConfig.Builder();
    }

    @Override
    public String getFieldName(Field field, int depth, Set<String> existingNames) {
        String name = field.getName();
        while (existingNames.contains(name)) {
            name = "_" + name;
        }
        return name;
    }

    public XmlNodeConfig() {
    }

    public XmlNodeConfig(Builder builder) {
        super(builder);
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
