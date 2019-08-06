package org.typemeta.funcj.codec.algebra;

import org.typemeta.funcj.data.IList;

import java.util.Map;

public interface JavaTypeAlg<T> {
    T booleanB(IList<String> path, String name);
    T booleanP(IList<String> path, String name);
    T booleanArr(IList<String> path, String name);

    T byteB(IList<String> path, String name);
    T byteP(IList<String> path, String name);
    T byteArr(IList<String> path, String name);

    T charB(IList<String> path, String name);
    T charP(IList<String> path, String name);
    T charArr(IList<String> path, String name);

    T shortB(IList<String> path, String name);
    T shortP(IList<String> path, String name);
    T shortArr(IList<String> path, String name);

    T integerB(IList<String> path, String name);
    T integerP(IList<String> path, String name);
    T integerArr(IList<String> path, String name);

    T longB(IList<String> path, String name);
    T longP(IList<String> path, String name);
    T longArr(IList<String> path, String name);

    T floatB(IList<String> path, String name);
    T floatP(IList<String> path, String name);
    T floatArr(IList<String> path, String name);

    T doubleB(IList<String> path, String name);
    T doubleP(IList<String> path, String name);
    T doubleArr(IList<String> path, String name);

    T string(IList<String> path, String name);

    T object(IList<String> path, String name, Map<String, T> fields);
    T objectArr(IList<String> path, String name, T elem);

    T enumT(IList<String> path, String name, Class<?> enumType);

    T coll(IList<String> path, String name, T elem);
    T stringMap(IList<String> path, String name, T value);
    T map(IList<String> path, String name, T key, T value);

    T interfaceT(IList<String> path, String name, Class<?> ifaceType);
}
