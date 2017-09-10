package org.funcj.control;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

@RunWith(JUnitQuickcheck.class)
public class EitherPropTest {

    private static Either<String, Integer> decToInt(char c) {
        final int i = Character.digit(c, 10);
        return i != -1 ?
                Either.right(i) :
                Either.left("Not a digit");
    }

    private static Either<String, Integer> hexToInt(char c) {
        final int i = Character.digit(c, 16);
        return i != -1 ?
                Either.right(i) :
                Either.left("Not a hex digit");
    }

    @Property
    public void successIsRight(char c) {

    }
}