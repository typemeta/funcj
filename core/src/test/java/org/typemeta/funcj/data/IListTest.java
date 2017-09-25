package org.typemeta.funcj.data;

import org.junit.*;

public class IListTest {

    final IList<Integer> empty = IList.of();
    final IList<Integer> l = IList.of(1, 2, 3, 4);

    @Test
    public void testEquals() {
        Assert.assertEquals("equals for an empty list", IList.of(), empty);
        Assert.assertEquals("[equals for a non-empty list", IList.of().add(4).add(3).add(2).add(1), l);
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue("isEmpty for an empty list", empty.isEmpty());
        Assert.assertFalse("isEmpty() for a non-empty list", l.isEmpty());
    }

    @Test
    public void testLength() {
        Assert.assertEquals("size for an empty list", 0, empty.size());
        Assert.assertEquals("[1,2,3,4].size for a non-empty list", 4, l.size());
    }

    @Test
    public void testToString() {
        Assert.assertEquals("toString for an empty list", "[]", empty.toString());
        Assert.assertEquals("toString for a non-empty list", "[1,2,3,4]", l.toString());
    }

    @Test
    public void testFoldl() {
        Assert.assertEquals("foldLeft for an empty list", 0, empty.foldLeft((x, y) -> x - y, 0).intValue());
        Assert.assertEquals("foldLeft for a non-empty list", (((10-1)-2)-3)-4, l.foldLeft((x, y) -> x - y, 10).intValue());
    }

    @Test
    public void testFoldr() {
        Assert.assertEquals("foldRight for an empty list", 0, empty.foldRight((x, y) -> x - y, 0).intValue());
        Assert.assertEquals("foldRight for a non-empty list", 1-(2-(3-(4-10))), l.foldRight((x, y) -> x - y, 10).intValue());
    }

    @Test
    public void testMap() {
        Assert.assertEquals("map for an empty list", IList.of(), empty.map(x -> null));
        Assert.assertEquals("map for a non-empty list", IList.of(-1, -2, -3, -4), l.map(i -> -i));
    }

    @Test
    public void testMatch() {
        Assert.assertTrue("match for an empty list", empty.match(n -> false, e -> true));
        Assert.assertTrue("match for a non-empty list", l.match(n -> true, e -> false));
    }
}
