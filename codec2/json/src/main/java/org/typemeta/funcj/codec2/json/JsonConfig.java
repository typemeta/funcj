package org.typemeta.funcj.codec2.json;

import org.typemeta.funcj.codec2.core.*;

public class JsonConfig extends CodecConfigImpl {
    public static final Property<JsonConfig, String> TYPE_FIELD_NAME = JsonConfig::typeFieldName;

    public static final Property<JsonConfig, String> KEY_FIELD_NAME = JsonConfig::keyFieldName;

    public static final Property<JsonConfig, String> VALUE_FIELD_NAME = JsonConfig::valueFieldName;

    @Override
    public <CFG extends CodecConfig, T> T get(Property<CFG, T> prop) {
        return prop.value((CFG)this);
    }

    @Override
    public <CFG extends CodecConfig, S, T> T get(Property1<CFG, S, T> prop, S arg) {
        return prop.value((CFG)this, arg);
    }

    public String typeFieldName() {
        return "@type";
    }

    public String keyFieldName() {
        return "@key";
    }

    public String valueFieldName() {
        return "@value";
    }
}
