package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.byteio.*;
import org.typemeta.funcj.codec.xml.*;
import org.typemeta.funcj.functions.Functions.F;

import java.time.*;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs {

    /**
     * Construct and return a new instance of a {@link XmlCodecCore}.
     * @return the new {@code XmlCodecCore}
     */
    public static XmlCodecCore xmlCodec() {
        final XmlCodecCoreImpl codec = new XmlCodecCoreImpl();
        return XmlCodecs.registerAll(codec);
    }

    /**
     * Construct and return a new instance of a {@link ByteCodecCore}.
     * @return the new {@code ByteCodecCore}
     */
    public static ByteCodecCore byteCodec() {
        final ByteCodecCoreImpl codec = new ByteCodecCoreImpl();
        return ByteCodecs.registerAll(codec);
    }

    public static <E, C extends CodecCoreIntl<E>> C registerAll(C core) {

        core.registerCodec(Class.class, new ClassCodec<E>(core));

        core.registerTypeProxy("java.time.ZoneRegion", ZoneId.class);

        core.registerCodec(LocalDate.class)
                .field("year", LocalDate::getYear, Integer.class)
                .field("month", LocalDate::getMonthValue, Integer.class)
                .field("day", LocalDate::getDayOfMonth, Integer.class)
                .map(LocalDate::of);

        core.registerCodec(LocalTime.class)
                .field("hours", LocalTime::getHour, Integer.class)
                .field("mins", LocalTime::getMinute, Integer.class)
                .field("secs", LocalTime::getSecond, Integer.class)
                .field("nanos", LocalTime::getNano, Integer.class)
                .map(LocalTime::of);

        core.registerCodec(LocalDateTime.class)
                .field("date", LocalDateTime::toLocalDate, LocalDate.class)
                .field("time", LocalDateTime::toLocalTime, LocalTime.class)
                .map(LocalDateTime::of);

        core.registerCodec(ZoneId.class)
                .field("id", ZoneId::getId, String.class)
                .map(ZoneId::of);

        core.registerCodec(ZoneOffset.class)
                .field("id", ZoneOffset::getId, String.class)
                .map(ZoneOffset::of);

        core.registerCodec(OffsetTime.class)
                .field("time", OffsetTime::toLocalTime, LocalTime.class)
                .field("offset", OffsetTime::getOffset, ZoneOffset.class)
                .map(OffsetTime::of);

        core.registerCodec(OffsetDateTime.class)
                .field("dateTime", OffsetDateTime::toLocalDateTime, LocalDateTime.class)
                .field("offset", OffsetDateTime::getOffset, ZoneOffset.class)
                .map(OffsetDateTime::of);

        core.registerCodec(ZonedDateTime.class)
                .field("dateTime", ZonedDateTime::toLocalDateTime, LocalDateTime.class)
                .field("zone", ZonedDateTime::getZone, ZoneId.class)
                .field("offset", ZonedDateTime::getOffset, ZoneOffset.class)
                .map(ZonedDateTime::ofLocal);

        return core;
    }

    /**
     * Base class for {@code Codec}s.
     * @param <T> the raw type to be encoded/decoded
     * @param <E> the encoded type
     */
    public static abstract class CodecBase<T, E> implements Codec<T, E> {

        protected final CodecCoreIntl<E> core;

        protected CodecBase(CodecCoreIntl<E> core) {
            this.core = core;
        }
    }

    /**
     * A {@code Codec} for the {@link Class} type.
     * @param <E> the encoded type
     */
    public static class ClassCodec<E> extends CodecBase<Class, E> {

        protected ClassCodec(CodecCoreIntl<E> core) {
            super(core);
        }

        @Override
        public E encode(Class val, E enc) throws Exception {
            return core.stringCodec().encode(core.classToName(val), enc);
        }

        @Override
        public Class decode(E enc) throws Exception {
            return core.nameToClass(core.stringCodec().decode(enc));
        }
    }

    /**
     * Utility class for creating a {@code Codec} that encodes a type
     * as a {@code String}.
     * @param <T> the raw type to be encoded/decoded
     * @param <E> the encoded type
     */
    public static class StringProxyCodec<T, E> extends CodecBase<T, E> {

        protected final F<T, String> encode;
        protected final F<String, T> decode;

        public StringProxyCodec(
                CodecCoreIntl<E> core,
                F<T, String> encode,
                F<String, T> decode) {
            super(core);
            this.encode = encode;
            this.decode = decode;
        }

        @Override
        public E encode(T val, E enc) throws Exception {
            return core.stringCodec().encode(encode.apply(val), enc);
        }

        @Override
        public T decode(E enc) throws Exception {
            return decode.apply(core.stringCodec().decode(enc));
        }
    }
}
