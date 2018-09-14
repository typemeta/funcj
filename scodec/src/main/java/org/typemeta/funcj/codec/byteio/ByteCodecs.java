package org.typemeta.funcj.codec.byteio;

import org.typemeta.funcj.codec.CodecCoreIntl;
import org.typemeta.funcj.codec.Codecs;
import org.typemeta.funcj.codec.byteio.ByteIO.Input;
import org.typemeta.funcj.codec.byteio.ByteIO.Output;

import java.util.Optional;

@SuppressWarnings("unchecked")
public class ByteCodecs {
    public static ByteCodecCoreImpl registerAll(ByteCodecCoreImpl core) {
        core.registerCodec(Optional.class, new OptionalCodec(core));
        return Codecs.registerAll(core);
    }
    public static class OptionalCodec<T> extends Codecs.CodecBase<Optional<T>, Input, Output> {

        protected OptionalCodec(CodecCoreIntl<Input, Output> core) {
            super(core);
        }

        @Override
        public Output encode(Optional<T> val, Output out) {
            core.booleanCodec().encode(val.isPresent(), out);
            if (val.isPresent()) {
                return core.dynamicCodec().encode(val.get(), out);
            } else {
                return out;
            }
        }

        @Override
        public Optional<T> decode(Input in) {
            if (core.booleanCodec().decode(in)) {
                return Optional.of((T)core.dynamicCodec().decode(in));
            } else {
                return Optional.empty();
            }
        }
    }
}
