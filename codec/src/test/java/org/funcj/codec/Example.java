package org.funcj.codec;

import org.funcj.codec.json.*;
import org.funcj.codec.xml.*;
import org.funcj.json.JSValue;
import org.w3c.dom.*;

import javax.xml.parsers.*;
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

    public static void main(String[] args) throws ParserConfigurationException {
        jsonTest();
        xmlTest();
    }

    static final Person person = new Person(
            "Jon",
            1.86,
            ZonedDateTime.of(
                    LocalDateTime.of(1970, 04, 19, 17, 05, 41),
                    ZoneId.of("GMT")),
            Colour.GREEN, Colour.BLUE);

    static void jsonTest() {
        final JsonCodecCore codec = Codecs.jsonCodec();

        // Serialise to JSON.
        final JSValue json = codec.encode(person);
        System.out.println(json.toJson(40));

        // Deserialise back to Java.
        final Person person2 = codec.decode(Person.class, json);
        assert(person.equals(person2));
    }

    static void xmlTest() throws ParserConfigurationException {
        final XmlCodecCore codec = Codecs.xmlCodec();

        final Document doc =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        // Serialise to XML.
        final Element elem = codec.encode(Person.class, person, doc.createElement("person"));
        System.out.println(XmlUtils.nodeToString(elem,true));

        // Deserialise back to Java.
        final Person person2 = codec.decode(Person.class, elem);
        assert(person.equals(person2));
    }
}
