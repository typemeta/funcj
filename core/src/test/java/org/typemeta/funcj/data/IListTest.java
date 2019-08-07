package org.typemeta.funcj.data;

import org.junit.*;

import static org.junit.Assert.assertEquals;

public class IListTest {

    final IList<Integer> el = IList.of();
    final IList.NonEmpty<Integer> nel = IList.of(1, 2, 3, 4);

    @Test
    public void testEquals() {
        assertEquals("equals for an empty list", IList.of(), el);
        assertEquals("[equals for a non-empty list", IList.of().add(4).add(3).add(2).add(1), nel);
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue("isEmpty for an empty list", el.isEmpty());
        Assert.assertFalse("isEmpty() for a non-empty list", nel.isEmpty());
    }

    @Test
    public void testLength() {
        assertEquals("size for an empty list", 0, el.size());
        assertEquals("[1,2,3,4].size for a non-empty list", 4, nel.size());
    }

    @Test
    public void testToString() {
        assertEquals("toString for an empty list", "[]", el.toString());
        assertEquals("toString for a non-empty list", "[1,2,3,4]", nel.toString());
    }

    @Test
    public void testFoldLeft() {
        assertEquals("Reversal on lists", nel.reverse(), nel.foldLeft(IList::add, IList.empty()));
        assertEquals("foldLeft for an empty list", "X", el.foldLeft((acc, v) -> acc + v, "X"));
        assertEquals("foldLeft for a non-empty list", "X1234", nel.foldLeft((acc, v) -> acc + v, "X"));
        assertEquals(
                "foldLeft for a non-empty list",
                ((((7 * 2 + 1) * 2) + 2) * 2 + 3) * 2 + 4,
                nel.foldLeft((acc, v) -> acc * 2 + v, 7).intValue()
        );
    }

    @Test
    public void testFoldLeft1() {
        assertEquals(
                "foldLeft for a non-empty list",
                ((1 * 2 + 2) * 2 + 3) * 2 + 4,
                 nel.foldLeft1((x, y) -> x * 2 + y).intValue()
        );
        assertEquals("foldLeft for a non-empty list", ((1-2)-3)-4, nel.foldLeft1((x, y) -> x - y).intValue());
    }

    @Test
    public void testFoldRight() {
        assertEquals("Identity function on lists", nel, nel.foldRight((i, acc) -> acc.add(i), IList.empty()));
        assertEquals("foldRight for an empty list", "X", el.foldRight((v, acc) -> v + acc, "X"));
        assertEquals("foldRight for a non-empty list", "1234X", nel.foldRight((v, acc) -> v + acc, "X"));
        assertEquals(
                "foldRight for a non-empty list",
                1 + 2 * (2 + 2 * (3 + 2 * (4 + 2 * 7))),
                nel.foldRight((v, acc) -> v + 2 * acc, 7).intValue()
        );
    }

    @Test
    public void testFoldRight1() {
        assertEquals(
                "foldRight1 for a non-empty list",
                1 + 2 * (2 + 2 * (3 + 2 * 4)),
                nel.foldRight1((x, y) -> x + 2 * y).intValue()
        );
        assertEquals("foldRight for a non-empty list", 1-(2-(3-4)), nel.foldRight1((x, y) -> x - y).intValue());
    }

    @Test
    public void testMap() {
        assertEquals("map for an empty list", IList.of(), el.map(x -> x * 2));
        assertEquals("map for a non-empty list", IList.of(-1, -2, -3, -4), nel.map(i -> -i));
    }

    @Test
    public void testFlatMap() {
        assertEquals("flatmap for an empty list", IList.of(), el.flatMap(x -> IList.of(x, -x)));
        assertEquals("map for a non-empty list", IList.of(1, -1, 2, -2, 3, -3, 4, -4), nel.flatMap(x ->  IList.of(x, -x)));
    }

    @Test
    public void testMatch() {
        Assert.assertTrue("match for an empty list", el.match(n -> false, e -> true));
        Assert.assertTrue("match for a non-empty list", nel.match(n -> true, e -> false));
    }
}
