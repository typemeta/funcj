package org.typemeta.funcj.codec.xml;

import org.typemeta.funcj.codec.CodecStrAPI;
import org.typemeta.funcj.codec.xmlnode.XmlNodeConfig;

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
        xmlTest(true);
        xmlTest(false);
    }

    static final Person person = new Person(
            "Jon",
            1.86,
            ZonedDateTime.of(
                    LocalDateTime.of(1970, 4, 19, 17, 5, 41),
                    ZoneId.of("GMT")),
            Colour.GREEN, Colour.BLUE);

    static void xmlTest(boolean stream) {
        final CodecStrAPI.IO xmlCodecCore;
        if (stream) {
            System.out.println("XML Streaming");
            final XmlConfig.Builder cfgBldr = new XmlConfig.Builder();
            cfgBldr.registerAllowedPackage(Example.class.getPackage());
            xmlCodecCore = Codecs.xmlCodec(cfgBldr);
        } else {
            System.out.println("XML node");
            final XmlNodeConfig.Builder cfgBldr = new XmlNodeConfig.Builder();
            cfgBldr.registerAllowedPackage(Example.class.getPackage());
            xmlCodecCore = Codecs.xmlNodeCodec(cfgBldr);
        }

        xmlCodecCore.registerStringProxyCodec(
                ZonedDateTime.class,
                ZonedDateTime::toString,
                ZonedDateTime::parse);

        // Encode to XML.
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        xmlCodecCore.encode(Person.class, person, baos);

        System.out.println(baos);

        // Decode back to Java.
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final Person person2 = xmlCodecCore.decode(Person.class, bais);

        assert(person.equals(person2));
    }
}
