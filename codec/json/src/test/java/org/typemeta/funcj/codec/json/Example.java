package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.*;
import org.typemeta.funcj.codec.jsonnode.JsonNodeConfig;

import java.io.*;
import java.time.*;
import java.util.*;

public class Example {
    enum Colour {RED, GREEN, BLUE}

    static class Person {

        final String name;
        final double height;
        final ZonedDateTime birthDate;
        final Set<Colour> favColours;

        Person(String name, double height, ZonedDateTime birthDate, Colour... favColours) {
            this.name = name;
            this.height = height;
            this.birthDate = birthDate;
            this.favColours = new HashSet<>(Arrays.asList(favColours));
        }

        private Person() {
            this.name = null;
            this.height = 0;
            this.birthDate = null;
            this.favColours = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return Double.compare(person.height, height) == 0 &&
                    Objects.equals(name, person.name) &&
                    Objects.equals(birthDate, person.birthDate) &&
                    Objects.equals(favColours, person.favColours);
        }
    }

    public static void main(String[] args) {
        jsonTest(true);
        jsonTest(false);
    }

    static class ZonedDateTimeJsonCodec
            implements Codec.FinalCodec<ZonedDateTime, JsonTypes.InStream, JsonTypes.OutStream, JsonTypes.Config> {

        @Override
        public Class<ZonedDateTime> type() {
            return ZonedDateTime.class;
        }

        @Override
        public JsonTypes.OutStream encode(CodecCoreEx<JsonTypes.InStream, JsonTypes.OutStream, JsonTypes.Config> core, ZonedDateTime value, JsonTypes.OutStream out) {
            out.startObject();

            out.writeField("dateTime");
            core.encodeImpl(LocalDateTime.class, value.toLocalDateTime(), out);
            out.writeField("zone");
            core.encodeImpl(ZoneId.class, value.getZone(), out);
            out.writeField("offset");
            core.encodeImpl(ZoneOffset.class, value.getOffset(), out);

            return out.endObject();
        }

        @Override
        public ZonedDateTime decode(CodecCoreEx<JsonTypes.InStream, JsonTypes.OutStream, JsonTypes.Config> core, JsonTypes.InStream in) {
            in.startObject();

            in.readFieldName("dateTime");
            final LocalDateTime ldt = core.decodeImpl(LocalDateTime.class, in);
            in.readFieldName("zone");
            final ZoneId zid = core.decodeImpl(ZoneId.class, in);
            in.readFieldName("offset");
            final ZoneOffset zo = core.decodeImpl(ZoneOffset.class, in);

            in.endObject();

            return ZonedDateTime.ofLocal(ldt, zid, zo);
        }
    }

    static final Person person = new Person(
            "Jon",
            1.86,
            ZonedDateTime.of(
                    LocalDateTime.of(1970, 4, 19, 17, 5, 41),
                    ZoneId.of("GMT")),
            Colour.GREEN, Colour.BLUE);

    static void jsonTest(boolean stream) {

        final CodecAPI.RW jsonCodecCore;
        if (stream) {
            System.out.println("JSON Streaming");
            final JsonConfig.Builder cfgBldr = new JsonConfig.Builder();
            cfgBldr.registerAllowedPackage(Example.class.getPackage());
            final JsonCodecCore codec = Codecs.jsonCodec(cfgBldr);
            codec.registerCodec(ZonedDateTime.class, new ZonedDateTimeJsonCodec());
            jsonCodecCore = codec;
        } else {
            System.out.println("JSON node");
            final JsonNodeConfig.Builder cfgBldr = new JsonNodeConfig.Builder();
            cfgBldr.registerAllowedPackage(Example.class.getPackage());
            jsonCodecCore = Codecs.jsonNodeCodec(cfgBldr);
        }

        jsonCodecCore.registerStringProxyCodec(
                ZonedDateTime.class,
                ZonedDateTime::toString,
                ZonedDateTime::parse);


        // Encode to JSON.
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jsonCodecCore.encode(Person.class, person, baos);

        System.out.println(baos);

        // Decode back to Java.
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final Person person2 = jsonCodecCore.decode(Person.class, bais);

        assert(person.equals(person2));
    }
}
