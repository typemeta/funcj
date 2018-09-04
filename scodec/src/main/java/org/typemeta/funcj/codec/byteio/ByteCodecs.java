package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.*;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class ByteCodecs {
    public static ByteCodecCoreImpl registerAll(ByteCodecCoreImpl core) {
        core.registerCodec(Optional.class, new OptionalCodec(core));
        return Codecs.registerAll(core);
    }
    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, ByteIO.Input, ByteIO.Output> {

        protected OptionalCodec(CodecCoreIntl<ByteIO.Input, ByteIO.Output> core) {
            super(core);
        }

        @Override
        public ByteIO.Output encode(Optional<T> val, ByteIO.Output out) {
            core.booleanCodec().encode(val.isPresent(), out);
            if (val.isPresent()) {
                return core.dynamicCodec().encode(val.get(), out);
            } else {
                return out;
            }
        }

        @Override
        public Optional<T> decode(ByteIO.Input in) {
            if (core.booleanCodec().decode(in)) {
                return Optional.of((T)core.dynamicCodec().decode(in));
            } else {
                return Optional.empty();
            }
        }
    }
}
