package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.bytes.*;
import org.typemeta.funcj.codec.xml.*;
import org.typemeta.funcj.functions.Functions.F;

import java.time.*;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs {

    /**
     * Construct and return a new instance of a {@link org.typemeta.funcj.codec.json.JsonCodecCore}.
     * @return the new {@code JsonCodecCore}
     */
    public static org.typemeta.funcj.codec.json.JsonCodecCore jsonsCodec() {
        return org.typemeta.funcj.codec.json.JsonCodecs.registerAll(new org.typemeta.funcj.codec.json.JsonCodecCoreImpl());
    }

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

    public static <IN, OUT, C extends CodecCoreInternal<IN, OUT>> C registerAll(C core) {

        core.registerStringProxyCodec(
                Class.class,
                core.config()::classToName,
                core.config()::nameToClass
        );

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
     * @param <T>       the raw type to be encoded/decoded
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    public static abstract class CodecBase<T, IN, OUT> implements Codec<T, IN, OUT> {

        protected final CodecCoreInternal<IN, OUT> core;

        protected CodecBase(CodecCoreInternal<IN, OUT> core) {
            this.core = core;
        }

        @Override
        public CodecCoreInternal<IN, OUT> core() {
            return core;
        }
    }

    /**
     * Utility class for creating a {@code Codec} that encodes a type
     * as a {@code String}.
     * @param <T> the raw type to be encoded/decoded
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    public static class StringProxyCodec<T, IN, OUT> extends CodecBase<T, IN, OUT> {

        protected final Class<T> type;
        protected final F<T, String> encode;
        protected final F<String, T> decode;

        public StringProxyCodec(
                CodecCoreInternal<IN, OUT> core,
                Class<T> type,
                F<T, String> encode,
                F<String, T> decode) {
            super(core);
            this.type = type;
            this.encode = encode;
            this.decode = decode;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public OUT encode(T value, OUT out) {
            return core.stringCodec().encode(encode.apply(value), out);
        }

        @Override
        public T decode(IN in) {
            return decode.apply(core.stringCodec().decode(in));
        }
    }
}
