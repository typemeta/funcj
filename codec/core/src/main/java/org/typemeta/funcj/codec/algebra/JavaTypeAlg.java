package org.typemeta.funcj.codec.algebra;

import java.util.Map;

public interface JavaTypeAlg<T> {
    T booleanB();
    T booleanP();
    T booleanArr();

    T byteB();
    T byteP();
    T byteArr();

    T charB();
    T charP();
    T charArr();

    T shortB();
    T shortP();
    T shortArr();

    T integerB();
    T integerP();
    T integerArr();

    T longB();
    T longP();
    T longArr();

    T floatB();
    T floatP();
    T floatArr();

    T doubleB();
    T doubleP();
    T doubleArr();

    T string();

    T object(Map<String, T> fields);
    T objectArr(T elem);

    T enumT(Class<?> enumType);

    T coll(T elem);
    T stringMap(T value);
    T map(T key, T value);

    T interfaceT(Class<?> ifaceType);
}
