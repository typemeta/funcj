package org.funcj.codec;

import org.funcj.util.Functions.F;

import java.time.*;

public class Codecs {
    public static <E, C extends CodecCore<E>> C registerAll(C core) {
        core.registerTypeRemap("java.time.ZoneRegion", ZoneId.class);

        core.registerCodec(Class.class, new ClassCodec<E>(core));
        core.registerCodec(OffsetDateTime.class, new OffsetDateTimeCodec<E>(core));

        core.codecBuilder(LocalDate.class)
                .field("year", LocalDate::getYear, Integer.class)
                .field("month", LocalDate::getMonthValue, Integer.class)
                .field("day", LocalDate::getDayOfMonth, Integer.class)
                .map(LocalDate::of);

        core.codecBuilder(LocalTime.class)
                .field("hours", LocalTime::getHour, Integer.class)
                .field("mins", LocalTime::getMinute, Integer.class)
                .field("secs", LocalTime::getSecond, Integer.class)
                .field("nanos", LocalTime::getNano, Integer.class)
                .map(LocalTime::of);

        core.codecBuilder(LocalDateTime.class)
                .field("date", LocalDateTime::toLocalDate, LocalDate.class)
                .field("time", LocalDateTime::toLocalTime, LocalTime.class)
                .map(LocalDateTime::of);

        core.codecBuilder(ZoneId.class)
                .field("id", ZoneId::getId, String.class)
                .map(ZoneId::of);

        core.codecBuilder(ZoneOffset.class)
                .field("id", ZoneOffset::getId, String.class)
                .map(ZoneOffset::of);

        core.codecBuilder(OffsetTime.class)
                .field("time", OffsetTime::toLocalTime, LocalTime.class)
                .field("offset", OffsetTime::getOffset, ZoneOffset.class)
                .map(OffsetTime::of);

        core.codecBuilder(OffsetDateTime.class)
                .field("dateTime", OffsetDateTime::toLocalDateTime, LocalDateTime.class)
                .field("offset", OffsetDateTime::getOffset, ZoneOffset.class)
                .map(OffsetDateTime::of);

        core.codecBuilder(ZonedDateTime.class)
                .field("dateTime", ZonedDateTime::toLocalDateTime, LocalDateTime.class)
                .field("zone", ZonedDateTime::getZone, ZoneId.class)
                .field("offset", ZonedDateTime::getOffset, ZoneOffset.class)
                .map(ZonedDateTime::ofLocal);

        return core;
    }

    private static Integer zeroToNull(int i, int j) {
        return i == 0 && j == 0 ? null : i;
    }

    private static Integer zeroToNull(int i) {
        return i == 0 ? null : i;
    }

    private static LocalTime toLocalTime(int h, int m, Integer s, Integer ns) {
        if (s == null) {
            return LocalTime.of(h, m);
        } else if(ns == null) {
            return LocalTime.of(h, m, s);
        } else {
            return LocalTime.of(h, m, s, ns);
        }
    }

    public static abstract class CodecBase<T, E> implements Codec<T, E> {

        protected final CodecCore<E> core;

        protected CodecBase(CodecCore<E> core) {
            this.core = core;
        }
    }

    public static class ClassCodec<E> extends CodecBase<Class, E> {

        protected ClassCodec(CodecCore<E> core) {
            super(core);
        }

        @Override
        public E encode(Class val, E enc) {
            return core.stringCodec().encode(core.classToName(val), enc);
        }

        @Override
        public Class decode(E enc) {
            return core.nameToClass(core.stringCodec().decode(enc));
        }
    }

    public static class StringProxyCodec<T, E> extends CodecBase<T, E> {

        protected final F<String, T> parser;

        public StringProxyCodec(CodecCore<E> core, F<String, T> parser) {
            super(core);
            this.parser = parser;
        }

        @Override
        public E encode(T val, E enc) {
            return core.stringCodec().encode(val.toString(), enc);
        }

        @Override
        public T decode(E enc) {
            return parser.apply(core.stringCodec().decode(enc));
        }
    }

    public static class LocalDateCodec<E> extends StringProxyCodec<LocalDate, E> {
        public LocalDateCodec(CodecCore<E> core) {
            super(core, LocalDate::parse);
        }
    }

    public static class OffsetDateTimeCodec<E> extends StringProxyCodec<OffsetDateTime, E> {
        public OffsetDateTimeCodec(CodecCore<E> core) {
            super(core, OffsetDateTime::parse);
        }
    }
}
