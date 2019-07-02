package org.typemeta.funcj.codec.jsonnode;

public abstract class JsonNodeTypes {
    public static JsonNodeConfigImpl.BuilderImpl configBuilder() {
        return new JsonNodeConfigImpl.BuilderImpl();
    }
}
