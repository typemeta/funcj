package org.typemeta.funcj.codec;

//import org.typemeta.funcj.codec.xml.XmlCodecCore;
//import org.typemeta.funcj.codec.xml.XmlUtils;

import org.typemeta.funcj.codec.json.JsonCodecCore;
import org.typemeta.funcj.codec.json.io.JsonIO;
import org.typemeta.funcj.codec.xml.XmlCodecCore;

import java.io.StringReader;
import java.io.StringWriter;
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
        jsonTest();
        xmlTest();
    }

    static class ZonedDateTimeJsonCodec
            extends Codecs.CodecBase<ZonedDateTime, JsonIO.Input, JsonIO.Output>
            implements Codec.FinalCodec<ZonedDateTime, JsonIO.Input, JsonIO.Output> {

        public ZonedDateTimeJsonCodec(JsonCodecCore core) {
            super(core);
        }

        @Override
        public Class<ZonedDateTime> type() {
            return ZonedDateTime.class;
        }

        @Override
        public JsonIO.Output encode(ZonedDateTime value, JsonIO.Output out) {
            out.startObject();

            out.writeField("dateTime");
            core.encode(LocalDateTime.class, value.toLocalDateTime(), out);
            out.writeField("zone");
            core.encode(ZoneId.class, value.getZone(), out);
            out.writeField("offset");
            core.encode(ZoneOffset.class, value.getOffset(), out);

            return out.endObject();
        }

        @Override
        public ZonedDateTime decode(JsonIO.Input in) {
            in.startObject();

            in.readFieldName("dateTime");
            final LocalDateTime ldt = core.decode(LocalDateTime.class, in);
            in.readFieldName("zone");
            final ZoneId zid = core.decode(ZoneId.class, in);
            in.readFieldName("offset");
            final ZoneOffset zo = core.decode(ZoneOffset.class, in);

            in.endObject();

            return ZonedDateTime.ofLocal(ldt, zid, zo);
        }
    }

    static final Person person = new Person(
            "Jon",
            1.86,
            ZonedDateTime.of(
                    LocalDateTime.of(1970, 04, 19, 17, 05, 41),
                    ZoneId.of("GMT")),
            Colour.GREEN, Colour.BLUE);

    static void jsonTest() {
        final JsonCodecCore jsonCodecCore = Codecs.jsonCodec();
        jsonCodecCore.registerStringProxyCodec(
                ZonedDateTime.class,
                ZonedDateTime::toString,
                ZonedDateTime::parse);

        jsonCodecCore.registerCodec(ZonedDateTime.class, new ZonedDateTimeJsonCodec(jsonCodecCore));

        // Encode to JSON.
        final StringWriter wtr = new StringWriter();
        jsonCodecCore.encode(Person.class, person, wtr);
        System.out.println(wtr.toString());

        // Decode back to Java.
        final StringReader rdr = new StringReader(wtr.toString());
        final Person person2 = jsonCodecCore.decode(Person.class, rdr);
        assert(person.equals(person2));
    }

    static void xmlTest() {
        final String root = "root";

        final XmlCodecCore xmlCodecCore = Codecs.xmlCodec();
        xmlCodecCore.registerStringProxyCodec(
                ZonedDateTime.class,
                ZonedDateTime::toString,
                ZonedDateTime::parse);


        // Encode to XML.
        final StringWriter wtr = new StringWriter();
        xmlCodecCore.encode(Person.class, person, wtr, root);
        System.out.println(wtr.toString());

        // Decode back to Java.
        final StringReader rdr = new StringReader(wtr.toString());
        final Person person2 = xmlCodecCore.decode(Person.class, rdr, root);
        assert(person.equals(person2));
    }
}
