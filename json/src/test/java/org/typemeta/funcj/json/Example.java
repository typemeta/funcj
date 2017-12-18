package org.typemeta.funcj.json;

import org.typemeta.funcj.json.model.JsValue;

import static org.typemeta.funcj.json.model.JSAPI.*;
import static org.typemeta.funcj.json.model.JSAPI.num;

public class Example {
    public static final JsValue testValue =
            obj(
                    field("null", nul()),
                    field("bools", arr(bool(true), bool(false))),
                    field("numbers", arr(num(1.2), num(3.4), num(4.5))),
                    field("strings", arr(str("abcd"), str("efgh"), str("ijkl"))),
                    field("objects", arr(
                            obj(
                                    field("a", num(1)),
                                    field("b", num(2))
                            ),
                            obj(
                                    field("c", num(3)),
                                    field("d", num(4))
                            )
                    ))
            );
}
