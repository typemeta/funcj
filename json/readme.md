![funcj.json](https://github.com/typemeta/funcj/blob/master/json/resources/funcj-json.png)

# Introduction

**funcj.json** is a Java library for processing JSON data.

Some notable features include:

* An immutable Java data model for JSON values.
* A parser which parses JSON text into the java model.
* A framework for JSON data processing,
using an approach based on [Object Algebras](http://i.cs.hku.hk/~bruno/papers/oopsla2015.pdf).

# Getting Started

## Requirements

funcj.json requires Java 1.8 (or higher).

## Resources

* **Release builds** are available on the [Releases](https://github.com/typemeta/funcj/releases) page.
* **Maven Artifacts** are available on the [Sonatype Nexus repository](https://repository.sonatype.org/#nexus-search;quick~funcj.core)
* **Javadocs** are for the latest build are on the [Javadocs](http://typemeta.github.io/funcj/javadocs/) page.

## Maven

Add this dependency to your project pom.xml:

```xml
<dependency>
    <groupId>org.typemeta</groupId>
    <artifactId>funcj-json</artifactId>
    <version>${funcj.json.version}</version>
</dependency>
```

# Quick Guide

To construct an instance of the Java data model for JSON, use the `JSAPI` class:

```java
import static org.typemeta.funcj.json.model.JSAPI.*;

class Test {
    JSValue jsv =
        obj(
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
            )
        )
    );
}
```

To render the `JSValue` value as JSON text, either use the `JSValue.toString` method:

```java
String text = jsv.toString();
System.out.println(text);
```

to print it as unformatted text:

```json
{"numbers":[1.2,3.4,4.5],"strings":["abcd","efgh","ijkl"],"objects":[{"a":1,"b":2},{"c":3,"d":4}]}
```

or, use `JsonToDoc.toString`:

```java
String text = JsonToDoc.toString(jsv, 40));
System.out.println(text);
```

to add indentation to the JSON text:

```json
{
    "numbers" : [1.2, 3.4, 4.5],
    "strings" : [
        "abcd",
        "efgh",
        "ijkl"
    ],
    "objects" : [
        {"a" : 1, "b" : 2},
        {"c" : 3, "d" : 4}
    ]
}
```

Meanwhile, to parse some JSON text back into the model representation, use `JsonParser.parse`:

```java
JSValue res = JsonParser.parse(text).getOrThrow();
```

To process a JSON value, implement `JsonAlg<T>` (or sub-class one of the base classes that implement it).

The following example counts the number of nodes in the value.

```java
public enum CountNodes implements JsonAlg<Integer> {
    INSTANCE;

    @Override
    public Integer nul() {
        return 1;
    }

    @Override
    public Integer bool(boolean b) {
        return 1;
    }

    @Override
    public Integer num(double value) {
        return 1;
    }

    @Override
    public Integer str(String s) {
        return 1;
    }

    @Override
    public Integer arr(List<Integer> elems) {
        return Folds.foldLeft(Integer::sum, 0, elems) + 1;
    }

    @Override
    public Integer obj(LinkedHashMap<String, Integer> fields) {
        return Folds.foldLeft(Integer::sum, 0, fields.values()) + 1;
    }
}

JSValue jsv = obj(field("ids", arr(num(1.2), str("test"), nul(), bool(true))));
int nodeCount = jsv.apply(CountNodes.INSTANCE);
```

# Guide

The library consists of three parts - 
 
1. A Java model for JSON values, which includes the ability to serialise out as formatted or unformatted JSON.
1. A JSON parser, which parses JSON text into the model.
1. A framework for transforms of JSON model data,
using an approach based on [Object Algebras](http://i.cs.hku.hk/~bruno/papers/oopsla2015.pdf).

# JSON Data Model

The data model consists of an interface, `JsValue`, and six implementing types:

1. `JsNull` - a singleton value corresponding to the JSON `null` value.
1. `JsBool` - an enum of the two values (`TRUE` and `FALSE`) corresponding to the JSON `true` and `false` values.
1. `JsString` - corresponding to the JSON string type.
1. `JsNumber` - corresponding to the JSON number type.
1. `JsArray` - corresponding to the JSON array type.
1. `JsObject` - corresponding to the JSON object type.

JSON values can be created via methods on `JSAPI`, as per the example in the Quick Guide.

```java
// {"ids":[1.2,"test",null,true]}
JSValue jsv = obj(field("ids", arr(num(1.2), str("test"), nul(), bool(true))));
```

# JSON Parser

The `JsonParser` class implements JSON parsing.
The parser can be invoked by calling one of the two `parse` overloads.

```java
String json = "{\"ids\":[1.2,\"test\",null,true]}";

// Call the parse(String) overload.
JSValue jsv = JsonParser.parse(json).getOrThrow();

// Call the parse(Reader) overload.
JSValue jsv2 = JsonParser.parse(new StringReader(json)).getOrThrow();
```

# JSON Object Algebra

The `JsonAlg<T>` interface provides the basic building block for constructing object algebras
for processing JSON values.
The generic type parameter `T` indicates the target type for the processing operation.
In the the case of the `NodeCount` processor above,
which counts the number of nodes in the JSON value,
`T` is `Integer`.


`JsonAlg` contains six methods, which correspond to the six JSON value types.


## `JsonAlg.Query`

Many processing operations map each JSON constituent to a value,
which are then aggregated over to arrive at a final single value.
The `NodesCount` example follows this pattern.

The `JsonAlg.Query` interface specialises `JsonAlg` for this purpose.
The aggregation operation is encapsulated within a `Monoid` instance,
provided by an `m` field of `JsonAlg.Query`.

`JsonAlg.Query.Base` is provided as a convenient base class which implements `JsonAlg.Query`,
and has a field to hold the monoid instance.

The `NodeCount` class can then be rewritten as follows:

```java
class NodeCount extends JsonAlg.Query.Base<Integer> {
    
    public NodeCount() {
        super(MonoidInstances.monoidInteger);
    }
    
    @Override
    public Integer nul() {
        return 1;
    }
    
    @Override
    public Integer bool(boolean b) {
        return 1;
    }
    
    @Override
    public Integer num(double value) {
        return 1;
    }
    
    @Override
    public Integer str(String s) {
        return 1;
    }
    
    @Override
    public Integer arr(List<Integer> elems) {
        return super.arr(elems) + 1;
    }
    
    @Override
    public Integer obj(LinkedHashMap<String, Integer> fields) {
        return super.obj(fields) + 1;
    }
}
```
