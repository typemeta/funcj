package org.typemeta.funcj.codec.xmlnode;

public abstract class XmlNodeTypes {

    public static XmlNodeConfigImpl.BuilderImpl configBuilder() {
        return new XmlNodeConfigImpl.BuilderImpl();
    }
}
