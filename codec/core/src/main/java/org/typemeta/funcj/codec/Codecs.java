package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.bytes.*;
import org.typemeta.funcj.codec.utils.*;
import org.typemeta.funcj.functions.Functions.F;

import java.math.*;
import java.time.*;
import java.util.*;

/**
 * Factory methods for creating CodecCore instances.
 */
public abstract class Codecs {

    /**
     * Construct and return a new instance of a {@link ByteCodecCore}.
     * @return the new {@code ByteCodecCore}
     */
    public static ByteCodecCore byteCodec() {
        return byteCodec(new ByteConfig.Builder());
    }

    public static ByteCodecCore byteCodec(CodecConfig.Builder<?, ByteTypes.Config> cfgBldr) {
        return registerAll(cfgBldr, ByteCodecCore::new);
    }

    @SuppressWarnings("unchecked")
    public static <IN, OUT, CFG extends CodecConfig, CORE extends CodecCore<IN, OUT, CFG>>
    CORE registerAll(CodecConfig.Builder<?, CFG> cfgBldr, F<CFG, CORE> coreBldr) {

        // Register allowed packages and classes.

        for (Class<?> clazz : new Class<?>[]{
                java.math.BigInteger.class,
                java.lang.String.class,
                java.time.LocalDate.class,
                java.util.Collection.class

        }) {
            cfgBldr.registerAllowedPackage(clazz.getPackage());
        }

        for (Class<?> clazz : new Class<?>[]{
                Boolean.class,
                Byte.class,
                Character.class,
                Double.class,
                Float.class,
                Integer.class,
                Long.class,
                Short.class,
                boolean.class,
                byte.class,
                char.class,
                double.class,
                float.class,
                int.class,
                long.class,
                short.class,
                String.class
        }) {
            cfgBldr.registerTypeAlias(clazz, clazz.getSimpleName());
        }

        // Register default collection types.
        cfgBldr.registerDefaultCollectionType(List.class, ArrayList.class);
        cfgBldr.registerDefaultCollectionType(Set.class, HashSet.class);
        cfgBldr.registerDefaultCollectionType(Map.class, HashMap.class);

        cfgBldr.registerDefaultCollectionType(
                List.class,
                (Class)ReflectionUtils.classForName("java.util.Arrays$ArrayList")
        );

        // Register a type proxy for ZoneRegion.
        cfgBldr.registerTypeProxy(ReflectionUtils.classForName("java.time.ZoneRegion"), ZoneId.class);

        // Construct the CodecCore implementation.
        final CORE core = coreBldr.apply(cfgBldr.build());

        // Register string proxies for big numbers.

        core.registerStringProxyCodec(
                BigInteger.class,
                BigInteger::toString,
                BigInteger::new
        );

        core.registerStringProxyCodec(
                BigDecimal.class,
                BigDecimal::toString,
                BigDecimal::new
        );

        // Register arg-array constructors for singleton collections..

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Arrays$ArrayList"),
                Arrays::asList
        );

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$SingletonList"),
                args -> Collections.singletonList(args[0]));

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$SingletonSet"),
                args -> Collections.singleton(args[0]));

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$SingletonMap"),
                args -> Collections.singletonMap(args[0], args[1]));

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$UnmodifiableRandomAccessList"),
                args -> Collections.unmodifiableList(Arrays.asList(args)));

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$UnmodifiableRandomAccessList"),
                args -> Collections.unmodifiableList(Arrays.asList(args)));

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$UnmodifiableSet"),
                args -> {
                    if (args.length == 0) {
                        return Collections.unmodifiableSet(Collections.emptySet());
                    } else {
                        final Set<Object> set;

                        if (args[0] instanceof Comparable) {
                            set = new TreeSet<>();
                        } else {
                            set = new HashSet<>();
                        }

                        Collections.addAll(set, args);
                        return Collections.unmodifiableSet(set);
                    }
                });

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$UnmodifiableNavigableSet"),
                args -> {
                    if (args.length == 0) {
                        return Collections.unmodifiableSet(Collections.emptySet());
                    } else {
                        final NavigableSet<Object> set = new TreeSet<>();
                        Collections.addAll(set, args);
                        return Collections.unmodifiableNavigableSet(set);
                    }
                });

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$UnmodifiableSortedSet"),
                args -> {
                    if (args.length == 0) {
                        return Collections.unmodifiableSet(Collections.emptySet());
                    } else {
                        final SortedSet<Object> set = new TreeSet<>();
                        Collections.addAll(set, args);
                        return Collections.unmodifiableSortedSet(set);
                    }
                });

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$UnmodifiableMap"),
                args -> {
                    if (args.length == 0) {
                        return Collections.emptyMap();
                    } else if (args.length % 2 != 0) {
                        throw new CodecException("Argument list length (" + args.length + ") it not a multiple of 2");
                    } else {
                        final Map<Object, Object> map;
                        if (args[0] instanceof Comparable) {
                            map = new TreeMap<>();
                        } else {
                            map = new HashMap<>();
                        }
                        for (int i = 0; i < args.length; i += 2) {
                            map.put(args[i], args[i+1]);
                        }
                        return Collections.unmodifiableMap(map);
                    }
                });

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$UnmodifiableNavigableMap"),
                args -> {
                    if (args.length == 0) {
                        return Collections.emptyMap();
                    } else if (args.length % 2 != 0) {
                        throw new CodecException("Argument list length (" + args.length + ") it not a multiple of 2");
                    } else {
                        final NavigableMap<Object, Object> map = new TreeMap<>();
                        for (int i = 0; i < args.length; i += 2) {
                            map.put(args[i], args[i+1]);
                        }
                        return Collections.unmodifiableNavigableMap(map);
                    }
                });

        core.registerArgArrayCtor(
                ReflectionUtils.classForName("java.util.Collections$UnmodifiableSortedMap"),
                args -> {
                    if (args.length == 0) {
                        return Collections.emptyMap();
                    } else if (args.length % 2 != 0) {
                        throw new CodecException("Argument list length (" + args.length + ") it not a multiple of 2");
                    } else {
                        final NavigableMap<Object, Object> map = new TreeMap<>();
                        for (int i = 0; i < args.length; i += 2) {
                            map.put(args[i], args[i+1]);
                        }
                        return Collections.unmodifiableSortedMap(map);
                    }
                });

        // Register codec for the Class type.
        core.registerStringProxyCodec(
                Class.class,
                core.config()::classToName,
                core.config()::nameToClass
        );

        core.registerCodec(
                (Class)Optional.class,
                new OptionalCodec<IN, OUT, CFG>()
        );

        // Register codecs for Java 8 date/time classes.

        core.registerCodecWithArgArray(LocalDate.class)
                .field("year", LocalDate::getYear, Integer.class)
                .field("month", LocalDate::getMonthValue, Integer.class)
                .field("day", LocalDate::getDayOfMonth, Integer.class)
                .construct(LocalDate::of);

        core.registerCodecWithArgArray(LocalTime.class)
                .field("hours", LocalTime::getHour, Integer.class)
                .field("mins", LocalTime::getMinute, Integer.class)
                .field("secs", LocalTime::getSecond, Integer.class)
                .field("nanos", LocalTime::getNano, Integer.class)
                .construct(LocalTime::of);

        core.registerCodecWithArgArray(LocalDateTime.class)
                .field("date", LocalDateTime::toLocalDate, LocalDate.class)
                .field("time", LocalDateTime::toLocalTime, LocalTime.class)
                .construct(LocalDateTime::of);

        core.registerCodecWithArgArray(ZoneId.class)
                .field("id", ZoneId::getId, String.class)
                .construct(ZoneId::of);

        core.registerCodecWithArgArray(ZoneOffset.class)
                .field("id", ZoneOffset::getId, String.class)
                .construct(ZoneOffset::of);

        core.registerCodecWithArgArray(OffsetTime.class)
                .field("time", OffsetTime::toLocalTime, LocalTime.class)
                .field("offset", OffsetTime::getOffset, ZoneOffset.class)
                .construct(OffsetTime::of);

        core.registerCodecWithArgArray(OffsetDateTime.class)
                .field("dateTime", OffsetDateTime::toLocalDateTime, LocalDateTime.class)
                .field("offset", OffsetDateTime::getOffset, ZoneOffset.class)
                .construct(OffsetDateTime::of);

        core.registerCodecWithArgArray(ZonedDateTime.class)
                .field("dateTime", ZonedDateTime::toLocalDateTime, LocalDateTime.class)
                .field("zone", ZonedDateTime::getZone, ZoneId.class)
                .field("offset", ZonedDateTime::getOffset, ZoneOffset.class)
                .construct(ZonedDateTime::ofLocal);

        return core;
    }

    /**
     * Utility class for creating a {@code Codec} that encodes a type
     * as a {@code String}.
     * @param <T>       the raw type to be encoded/decoded
     * @param <IN>      the encoded input type
     * @param <OUT>     the encoded output type
     */
    public static class StringProxyCodec<T, IN, OUT, CFG extends CodecConfig>
            implements Codec<T, IN, OUT, CFG> {

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
