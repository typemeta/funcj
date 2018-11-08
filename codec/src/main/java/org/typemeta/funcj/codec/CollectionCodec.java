package org.typemeta.funcj.codec;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Base class for {@link Codec}s for {@link Collection}s.
 * @param <T>       the element type of the collection
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 */
public abstract class CollectionCodec<T, IN, OUT> extends Codec.Base<Collection<T>, IN, OUT> {

    protected final Codec<T, IN, OUT> elemCodec;

    protected CollectionCodec(CodecCoreInternal<IN, OUT> core, Codec<T, IN, OUT> elemCodec) {
        super(core);
        this.elemCodec = elemCodec;
    }

    private Codec<Collection<T>, IN, OUT> getCodec(Class<Collection<T>> type) {
        return  core().getCollCodec(type, elemCodec);
    }

    @Override
    public OUT encodeWithCheck(Collection<T> value, OUT out) {
        if (core().encodeNull(value, out)) {
            return out;
        } else {
            if (!core().encodeDynamicType(this, value, out, this::getCodec)) {
                return encode(value, out);
            } else {
                return out;
            }
        }
    }

    @Override
    public Collection<T> decodeWithCheck(IN in) {
        if (core().decodeNull(in)) {
            return null;
        } else {
            final Collection<T> val = core().decodeDynamicType(
                    in,
                    type -> getCodec(core().config().nameToClass(type)).decode(in)
            );
            if (val != null) {
                return val;
            } else {
                return decode(in);
            }
        }
    }

    protected interface CollProxy<T> {
        void add(T elem);
        Collection<T> construct();
    }

    protected static class CollProxy1<T> implements CollProxy<T> {
        protected final Collection<T> coll;

        public CollProxy1(Collection<T> coll) {
            this.coll = coll;
        }

        @Override
        public void add(T elem) {
            coll.add(elem);
        }

        @Override
        public Collection<T> construct() {
            return coll;
        }
    }

    protected static class CollProxy2<T> implements CollProxy<T> {
        final Class<T> elemType;
        final List<T> args = new ArrayList<>();
        final ArgArrayTypeCtor<Collection<T>> argArrCtor;

        public CollProxy2(
                Class<T> elemType,
                ArgArrayTypeCtor<Collection<T>> argArrCtor) {
            this.elemType = elemType;
            this.argArrCtor = argArrCtor;
        }

        @Override
        public void add(T elem) {
            args.add(elem);
        }

        @Override
        public Collection<T> construct() {
            final T[] arr = (T[]) Array.newInstance(elemType, args.size());
            args.toArray(arr);
            return argArrCtor.construct(arr);
        }
    }

    protected CollProxy<T> getCollectionProxy(Class<Collection<T>> collType) {
        final Optional<NoArgsTypeCtor<Collection<T>>> noaCtorOpt = core().getNoArgsCtorOpt(collType);
        if (noaCtorOpt.isPresent()) {
            return new CollProxy1<T>(noaCtorOpt.get().construct());
        } else {
            final ArgArrayTypeCtor<Collection<T>> argArrCtor =
                    core().getArgArrayCtorOpt(collType)
                            .orElseThrow(() -> new CodecException(
                                    "Could not find suitable constructor for " + collType));

            return new CollProxy2<T>(elemCodec.type(), argArrCtor);
        }
    }
}
