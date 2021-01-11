package org.typemeta.funcj.codec2.json;

import org.typemeta.funcj.codec2.core.*;
import org.typemeta.funcj.codec2.json.JsonTypes.InStream;
import org.typemeta.funcj.codec2.json.JsonTypes.OutStream;

public class JsonCodecCore extends CodecCoreImpl<InStream, OutStream> {

    public JsonCodecCore(JsonCodecFormat format, CodecConfig config) {
        super(format, config);
    }
}
