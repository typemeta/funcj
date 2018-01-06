package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.*;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class ByteCodecs {
    public static ByteCodecCoreImpl registerAll(ByteCodecCoreImpl core) {
        core.registerCodec(Optional.class, new OptionalCodec(core));
        return Codecs.registerAll(core);
    }
    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, ByteIO> {

        protected OptionalCodec(BaseCodecCore<ByteIO> core) {
            super(core);
        }

        @Override
        public ByteIO encode(Optional<T> val, ByteIO enc) throws Exception {
            core.booleanCodec().encode(val.isPresent(), enc);
            if (val.isPresent()) {
                return core.dynamicCodec().encode(val.get(), enc);
            } else {
                return enc;
            }
        }

        @Override
        public Optional<T> decode(ByteIO enc) throws Exception {
            if (core.booleanCodec().decode(enc)) {
                return Optional.of((T)core.dynamicCodec().decode(enc));
            } else {
                return Optional.empty();
            }
        }
    }
}
