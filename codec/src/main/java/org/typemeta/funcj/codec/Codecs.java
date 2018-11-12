package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.bytes.BytesCodecCore;
import org.typemeta.funcj.codec.json.JsonCodecCore;
import org.typemeta.funcj.codec.utils.ReflectionUtils;
import org.typemeta.funcj.codec.xml.XmlCodecCore;
import org.typemeta.funcj.functions.Functions.F;

import java.time.*;
import java.util.*;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs {

    /**
     * Construct and return a new instance of a {@link JsonCodecCore}.
     * @return the new {@code JsonCodecCore}
     */
    public static JsonCodecCore jsonCodec() {
        return registerAll(new JsonCodecCore());
    }

    /**
     * Construct and return a new instance of a {@link XmlCodecCore}.
     * @return the new {@code XmlCodecCore}
     */
    public static XmlCodecCore xmlCodec() {
        return registerAll(new XmlCodecCore());
    }

    /**
     * Construct and return a new instance of a {@link BytesCodecCore}.
     * @return the new {@code ByteCodecCore}
     */
    public static BytesCodecCore byteCodec() {
        return registerAll(new BytesCodecCore());
    }

    public static <IN, OUT, CFG extends CodecConfig, CORE extends CodecCore<IN, OUT, CFG>> CORE registerAll(CORE core) {

        // Register allowed packages and classes.

        for (Class<?> clazz : new Class<?>[]{
                Boolean.class, Byte.class, Character.class, Double.class, Float.class, Integer.class, Long.class, Short.class,
                boolean.class, byte.class, char.class, double.class, float.class, int.class, long.class, short.class,
                String.class
        }) {
            core.config().registerTypeAlias(clazz, clazz.getSimpleName());
        }

        core.config().registerAllowedPackage(java.lang.String.class.getPackage());
        core.config().registerAllowedPackage(java.util.Collection.class.getPackage());
        core.config().registerAllowedPackage(java.time.LocalDate.class.getPackage());

        // Register default sub-types.
        core.config().registerDefaultSubType(List.class, ArrayList.class);
        core.config().registerDefaultSubType(Set.class, HashSet.class);
        core.config().registerDefaultSubType(Map.class, HashMap.class);

        // Register constructors.

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$SingletonList"),
                args -> Collections.singletonList(args[0]));

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$SingletonSet"),
                args -> Collections.singleton(args[0]));

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$SingletonMap"),
                args -> Collections.singletonMap(args[0], args[1]));

        // Register codec for Class.
        core.registerStringProxyCodec(
                Class.class,
                core.config()::classToName,
                core.config()::nameToClass
        );

        // Register a type proxy for ZoneRegion.
        core.config().registerTypeProxy(ReflectionUtils.classForName("java.time.ZoneRegion"), ZoneId.class);

        // Register codecs for Java 8 date/time classes.

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
     * Utility class for creating a {@code Codec} that encodes a type
     * as a {@code String}.
     * @param <T> the raw type to be encoded/decoded
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    public static class StringProxyCodec<T, IN, OUT, CFG extends CodecConfig> implements Codec<T, IN, OUT, CFG> {

        protected final Class<T> type;
        protected final F<T, String> encode;
        protected final F<String, T> decode;

        public StringProxyCodec(
                Class<T> type,
                F<T, String> encode,
                F<String, T> decode) {
            this.type = type;
            this.encode = encode;
            this.decode = decode;
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public OUT encode(CodecCoreEx<IN, OUT, CFG> core, T value, OUT out) {
            return core.format().stringCodec().encode(core, encode.apply(value), out);
        }

        @Override
        public T decode(CodecCoreEx<IN, OUT, CFG> core, IN in) {
            return decode.apply(core.format().stringCodec().decode(core, in));
        }
    }
}
