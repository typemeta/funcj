package org.typemeta.funcj.codec;

import org.typemeta.funcj.codec.json.JsonCodecCore;
import org.typemeta.funcj.codec.xml.*;
import org.typemeta.funcj.control.Either;
import org.typemeta.funcj.json.model.JSValue;
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

    public static void main2(String[] args) throws ParserConfigurationException {
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
        codec.registerStringProxyCodec(
                ZonedDateTime.class,
                ZonedDateTime::toString,
                ZonedDateTime::parse);

        // Serialise to JSON.
        final JSValue json = codec.encode(person);
        System.out.println(json.toString(40));

        // Deserialise back to Java.
        final Person person2 = codec.decode(Person.class, json);
        assert(person.equals(person2));
    }

    static void xmlTest() throws ParserConfigurationException {
        final XmlCodecCore codec = Codecs.xmlCodec();
        codec.registerStringProxyCodec(
                ZonedDateTime.class,
                ZonedDateTime::toString,
                ZonedDateTime::parse);
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

    static Either<String, Double> reciprocal(double x) {
        return (x == 0) ? Either.left("Divide by zero") : Either.right(1.0 / x);
    }

    static Either<String, Double> sqrt(double x) {
        return (x < 0) ? Either.left("Square root of -ve number") : Either.right(Math.sqrt(x));
    }

    static Either<String, Double> recipSqrt(double x) {
        return reciprocal(x).flatMap(r -> sqrt(r));
    }

    public static void main(String[] args){
        Either<String, Double> a = recipSqrt(0.25);
        assert(a.equals(Either.right(2.0)));

        Either<String, Double> b = recipSqrt(-1.0);
        assert(b.equals(Either.left("Square root of -ve number")));

        Either<String, Double> c = recipSqrt(0.0);
        assert(c.equals(Either.left("Divide by zero")));

        // map applies a function to the right-value only.
        Either<String, Double> d = a.map(r -> r + 1.0);
        assert(d.equals(Either.right(3.0)));

        // match applies a left function to the left or a right function to the right value
        String s = a.match(left -> left.value, right -> right.value.toString());
        assert(s.equals("2.0"));

        // and/map allow Either values to be collected before being passed to a function.
        Either<String, Double> e = reciprocal(2.0).and(sqrt(4.0)).map(Double::sum);
        assert(e.equals(Either.right(2.5)));
    }
}
