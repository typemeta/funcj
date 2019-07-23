package org.typemeta.funcj.codec.impl;

import org.typemeta.funcj.codec.*;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Base class for {@link Codec}s for {@link Collection}s.
 * @param <T>       the element type of the collection
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 * @param <CFG>     the config type
 */
public abstract class CollectionCodec<T, IN, OUT, CFG extends CodecConfig>
        implements Codec<Collection<T>, IN, OUT, CFG> {

    protected final Class<Collection<T>> collType;
    protected final Codec<T, IN, OUT, CFG> elemCodec;

    protected CollectionCodec(
            Class<Collection<T>> collType,
            Codec<T, IN, OUT, CFG> elemCodec) {
        this.collType = collType;
        this.elemCodec = elemCodec;
    }

    @Override
    public Class<Collection<T>> type() {
        return collType;
    }

    protected Codec<Collection<T>, IN, OUT, CFG> getCodec(
            CodecCoreEx<IN, OUT, CFG> core,
            Class<Collection<T>> type) {
        return core.getCollCodec(type, elemCodec);
    }

    @Override
    public OUT encodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, Collection<T> value, OUT out) {
        final CodecFormat.WasEncoded<OUT> nullRes = core.format().encodeNull(value, out);
        if (nullRes.wasEncoded) {
            return nullRes.out;
        } else if (core.config().isDefaultCollectionType(type(), value.getClass())) {
            final Class<Collection<T>> implCollType = core.config().getDefaultCollectionType(type());
            return getCodec(core, implCollType).encode(core, value, out);
        } else {
            final CodecFormat.WasEncoded<OUT> dynRes = core.format().encodeDynamicType(
                    core,
                    this,
                    value,
                    out,
                    type -> getCodec(core, type));
            if (dynRes.wasEncoded) {
                return dynRes.out;
            } else {
                return encode(core, value, out);
            }
        }
    }

    @Override
    public Collection<T> decodeWithCheck(CodecCoreEx<IN, OUT, CFG> core, IN in) {
        if (core.format().decodeNull(in)) {
            return null;
        } else {
            final Collection<T> val = core.format().decodeDynamicType(
                    in,
                    (type, in2) -> getCodec(core, core.config().nameToClass(type)).decode(core, in2)
            );

            if (val != null) {
                return val;
            } else {
                final Class<Collection<T>> dynClass = core.config().getDefaultCollectionType(type());
                if (dynClass != null) {
                    final Codec<Collection<T>, IN, OUT, CFG> codec = getCodec(core, dynClass);
                    return codec.decode(core, in);
                } else {
                    return decode(core, in);
                }
            }
        }
    }

    protected CollProxy<T> getCollectionProxy(CodecCoreEx<IN, OUT, CFG> core) {
        final ArgArrayTypeCtor<Collection<T>> argArrCtor = core.getArgArrayCtor(collType);
        if (argArrCtor != null) {
            return new CollProxy2<T>(elemCodec.type(), argArrCtor);
        } else {
            final NoArgsTypeCtor<Collection<T>> noaCtor = core.getNoArgsCtor(collType);
            return new CollProxy1<T>(noaCtor.construct());
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
                ArgArrayTypeCtor<Collection<T>> argArrCtor
        ) {
            this.elemType = elemType;
            this.argArrCtor = argArrCtor;
        }

        @Override
        public void add(T elem) {
            args.add(elem);
        }

        @Override
        public Collection<T> construct() {
            @SuppressWarnings("unchecked")
            final T[] arr = (T[]) Array.newInstance(elemType, args.size());
            args.toArray(arr);
            return argArrCtor.construct(arr);
        }
    }
}
