package org.typemeta.funcj.codec.json;

import org.typemeta.funcj.codec.Codecs;
import org.typemeta.funcj.codec.xml.*;
import org.typemeta.funcj.json.model.*;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
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

    public static void main(String[] args) throws Exception {
        jsonTest();
        xmlTest();
    }

    static class ZonedDateTimeJsonCodec extends Codecs.CodecBase<ZonedDateTime, JsValue> {

        public ZonedDateTimeJsonCodec(JsonCodecCore core) {
            super(core);
        }

        @Override
        public JsValue encode(ZonedDateTime val, JsValue enc) throws Exception {
            return JSAPI.obj(
                    JSAPI.field("dateTime", core.encode(val.toLocalDateTime(), enc)),
                    JSAPI.field("zone", core.encode(val.getZone(), enc)),
                    JSAPI.field("offset", core.encode(val.getOffset(), enc))
            );
        }

        @Override
        public ZonedDateTime decode(JsValue enc) throws Exception {
            final JsObject obj = enc.asObject();
            return ZonedDateTime.ofLocal(
                    core.decode(LocalDateTime.class, obj.get("dateTime")),
                    core.decode(ZoneId.class, obj.get("zone")),
                    core.decode(ZoneOffset.class, obj.get("offset"))
            );
        }
    }

    static final Person person = new Person(
            "Jon",
            1.86,
            ZonedDateTime.of(
                    LocalDateTime.of(1970, 04, 19, 17, 05, 41),
                    ZoneId.of("GMT")),
            Colour.GREEN, Colour.BLUE);

    static void jsonTest() throws Exception {
        final JsonCodecCore jsonCodecCore = JsonCodecs.jsonCodec();
//        codec.registerStringProxyCodec(
//                ZonedDateTime.class,
//                ZonedDateTime::toString,
//                ZonedDateTime::parse);

        jsonCodecCore.registerCodec(ZonedDateTime.class, new ZonedDateTimeJsonCodec(jsonCodecCore));

        // Serialise to JSON.
        final JsValue json = jsonCodecCore.encode(person);
        System.out.println(json.toString(40));

        // Deserialise back to Java.
        final Person person2 = jsonCodecCore.decode(Person.class, json);
        assert(person.equals(person2));
    }

    static void xmlTest() throws Exception {
        final XmlCodecCore mmlCodecCore = Codecs.xmlCodec();
        mmlCodecCore.registerStringProxyCodec(
                ZonedDateTime.class,
                ZonedDateTime::toString,
                ZonedDateTime::parse);

        final Document doc =
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .newDocument();

        // Serialise to XML.
        final Element elem = mmlCodecCore.encode(Person.class, person, doc.createElement("person"));
        System.out.println(XmlUtils.nodeToString(elem,true));

        // Deserialise back to Java.
        final Person person2 = mmlCodecCore.decode(Person.class, elem);
        assert(person.equals(person2));
    }
}
