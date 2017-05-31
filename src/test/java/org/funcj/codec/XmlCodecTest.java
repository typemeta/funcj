package org.funcj.codec;

import org.funcj.codec.TestData.*;
import org.funcj.codec.xml.XmlCodecCore;
import org.junit.*;
import org.w3c.dom.Node;

import static java.lang.System.out;
import static org.funcj.codec.xml.XmlUtils.nodeToString;

public class XmlCodecTest {
    final static XmlCodecCore codec = new XmlCodecCore();

    @Test
    public void testBooleanNulls() {
        roundTrip(new BooleanData(), BooleanData.class);
    }

    @Test
    public void testBoolean() {
        roundTrip(new BooleanData(Init.INIT), BooleanData.class);
    }

    @Test
    public void testIntegerNulls() {
        roundTrip(new IntegerData(), IntegerData.class);
    }

    @Test
    public void testInteger() {
        roundTrip(new IntegerData(Init.INIT), IntegerData.class);
    }

    private <T> void roundTrip(T val, Class<T> clazz) {
        codec.setNewDocument();
        final Node node = codec.encode(clazz, val);
        out.println(nodeToString(codec.doc, true));

        final T val2 = codec.decode(clazz, node);

        Assert.assertEquals(val, val2);
    }
}
