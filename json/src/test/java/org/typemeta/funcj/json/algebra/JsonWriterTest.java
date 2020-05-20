package org.typemeta.funcj.json.algebra;

import org.junit.Test;
import org.typemeta.funcj.json.model.*;

import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.typemeta.funcj.json.model.JSAPI.*;

public class JsonWriterTest {

    private static final String EOL = System.lineSeparator();

    private static final JsObject TEST_JSON =
            obj(
                    field("null", nul()),
                    field("bool", bool(true)),
                    field("num", num(123.456)),
                    field("str", str("abc def")),
                    field("arr", arr(
                        nul(), bool(false), num(1e8), str("def"))
                    ),
                    field("obj", obj(
                        field("null2", nul()),
                        field("bool2", bool(false)),
                        field("num2", num(456.789)),
                        field("str2", str("def123"))
                    ))
            );

    private static final String nonIndent = "{\"null\":null,\"bool\":true,\"num\":123.456,\"str\":\"abc def\"," +
            "\"arr\":[null,false,1.0E8,\"def\"],\"obj\":{\"null2\":null,\"bool2\":false,\"num2\":456.789," +
            "\"str2\":\"def123\"}}";

    private static final String indent =
            "{" + EOL +
            "    \"null\": null," + EOL +
            "    \"bool\": true," + EOL +
            "    \"num\": 123.456," + EOL +
            "    \"str\": \"abc def\"," + EOL +
            "    \"arr\": [" + EOL +
            "        null," + EOL +
            "        false," + EOL +
            "        1.0E8," + EOL +
            "        \"def\"" + EOL +
            "    ]," + EOL +
            "    \"obj\": {" + EOL +
            "        \"null2\": null," + EOL +
            "        \"bool2\": false," + EOL +
            "        \"num2\": 456.789," + EOL +
            "        \"str2\": \"def123\"" + EOL +
            "    }" + EOL +
            "}";

    @Test
    public void testNonIndent() {
        final String actual = JsonWriter.toString(TEST_JSON, new StringWriter()).toString();
        assertEquals(nonIndent, actual);
    }

    @Test
    public void testIndent() {
        final String actual = JsonIndentWriter.toString(TEST_JSON, new StringWriter(), 4).toString();
        //System.out.println(actual);
        assertEquals(indent, actual);
    }
}
