package org.typemeta.funcj.codec.json;

import org.junit.Assert;
import org.junit.Test;
import org.typemeta.funcj.codec.Codecs;
import org.typemeta.funcj.codec.json.io.FileUtils;
import org.typemeta.funcj.json.model.*;
import org.typemeta.funcj.json.parser.JsonParser;

import java.io.StringReader;
import java.io.StringWriter;

public class JsValueCodecTest {
    final static String json =
            FileUtils.openResource("/example.json")
                    .map(FileUtils::read)
                    .getOrThrow();

    final JsArray value0 = (JsArray)JsonParser.parse(json);

    static class Mixed {
        static Mixed createSampleVal() {
            return new Mixed(
                    "TEST",
                    JSAPI.obj(
                            JSAPI.field("2", JSAPI.nul()),
                            JSAPI.field("1", JSAPI.str("xyz")),
                            JSAPI.field("0", JSAPI.arr(JSAPI.num(0), JSAPI.num(1)))
                    ),
                    JSAPI.nul(),
                    JSAPI.bool(true),
                    JSAPI.num(1234.5678),
                    JSAPI.str("X\r\t\n\"X"),
                    JSAPI.arr(JSAPI.nul(), JSAPI.bool(false)),
                    JSAPI.obj(
                            JSAPI.field("\"a", JSAPI.bool(true)),
                            JSAPI.field("\r1\t", JSAPI.num(2))
                    )
            );
        }

        public final String name;
        public final JsValue jsv;
        public final JsNull jsNull;
        public final JsBool jsBool;
        public final JsNumber jsNumber;
        public final JsString jsString;
        public final JsArray jsArray;
        public final JsObject jsObject;

        Mixed(
                String name,
                JsValue jsv,
                JsNull jsNull,
                JsBool jsBool,
                JsNumber jsNumber,
                JsString jsString,
                JsArray jsArray,
                JsObject jsObject) {
            this.name = name;
            this.jsv = jsv;
            this.jsNull = jsNull;
            this.jsBool = jsBool;
            this.jsNumber = jsNumber;
            this.jsString = jsString;
            this.jsArray = jsArray;
            this.jsObject = jsObject;
        }

        private Mixed() {
            this.name = null;
            this.jsv = null;
            this.jsNull = null;
            this.jsBool = null;
            this.jsNumber = null;
            this.jsString = null;
            this.jsArray = null;
            this.jsObject = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o == null || getClass() != o.getClass()) {
                return false;
            } else {
                Mixed mixed = (Mixed) o;
                return name.equals(mixed.name) &&
                        jsv.equals(mixed.jsv) &&
                        jsNull == mixed.jsNull &&
                        jsBool == mixed.jsBool &&
                        jsNumber.equals(mixed.jsNumber) &&
                        jsString.equals(mixed.jsString) &&
                        jsArray.equals(mixed.jsArray) &&
                        jsObject.equals(mixed.jsObject);
            }
        }

        @Override
        public String toString() {
            return JSAPI.obj(
                    JSAPI.field("name", JSAPI.str(name)),
                    JSAPI.field("jsv", jsv),
                    JSAPI.field("jsNull", jsNull),
                    JSAPI.field("jsBool", jsBool),
                    JSAPI.field("jsNumber", jsNumber),
                    JSAPI.field("jsString", jsString),
                    JSAPI.field("jsArray", jsArray),
                    JSAPI.field("jsObject", jsObject)
            ).toString();
        }
    }

    private static final Mixed mixed0 = Mixed.createSampleVal();

    @Test
    public void testPureJsv() {
        final JsonCodecCore codec = Codecs.jsonCodec();

        final StringWriter sw = new StringWriter();
        codec.encode(JsArray.class, value0, sw);

        final String data = sw.toString();

        JsArray value1 = (JsArray)JsonParser.parse(data);
        Assert.assertEquals(value0, value1);

        final StringReader sr = new StringReader(data);

        final JsValue value2 = codec.decode(JsArray.class, sr);

        Assert.assertEquals(value0, value2);
    }

    @Test
    public void testMixedJsv() {
        final JsonCodecCore codec = Codecs.jsonCodec();
        codec.config().registerAllowedClass(Mixed.class);

        final StringWriter sw = new StringWriter();
        codec.encode(Mixed.class, mixed0, sw);

        final String data = sw.toString();

        JsObject jsv1 = (JsObject)JsonParser.parse(data);
        //System.out.println(jsv1);

        final StringReader sr = new StringReader(data);

        final Mixed mixed2 = codec.decode(Mixed.class, sr);

        Assert.assertEquals(mixed0, mixed2);
    }
}
