package org.typemeta.funcj.json;

import javax.json.*;

public class Example {
    public static final JsonValue testValue =
            Json.createObjectBuilder()
                    .add("bools", Json.createArrayBuilder()
                            .add(JsonValue.TRUE)
                            .add(JsonValue.FALSE)
                            .build())
                    .add("null", JsonValue.NULL)
                    .add("numbers", Json.createArrayBuilder()
                            .add(Json.createValue(1.2))
                            .add(Json.createValue(3.4))
                            .add(Json.createValue(4.5))
                            .build())
                    .add("objects", Json.createArrayBuilder()
                            .add(Json.createObjectBuilder()
                                         .add("a", Json.createValue(1))
                                         .add("b", Json.createValue(2))
                                         .build())
                            .add(Json.createObjectBuilder()
                                         .add("c", Json.createValue(3))
                                         .add("d", Json.createValue(4))
                                         .build()))
                    .add("strings", Json.createArrayBuilder()
                            .add(Json.createValue("abcd"))
                            .add(Json.createValue("efgh"))
                            .add(Json.createValue("ijkl"))
                            .build())
                    .build();
}
