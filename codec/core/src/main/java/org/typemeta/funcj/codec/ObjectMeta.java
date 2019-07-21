package org.typemeta.funcj.codec;

import java.util.stream.*;

/**
 * Abstraction for creating an object by accumulating the fields that comprise it.
 * @param <T>       the type of object being created
 * @param <IN>      the encoded input type
 * @param <OUT>     the encoded output type
 * @param <B>       the concrete implementation type for {@code Builder}
 */
public interface ObjectMeta<
        T,
        IN,
        OUT,
        B extends ObjectMeta.Builder<T>
> extends Iterable<ObjectMeta.Field<T, IN, OUT, B>> {
    interface Builder<T> {
        T construct();
    }

    interface Field<T, IN, OUT, RA> {
        String name();
        OUT encodeField(T val, OUT out);
        RA decodeField(RA acc, IN in);
    }

    B startDecode();

    default Stream<Field<T, IN, OUT, B>> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
