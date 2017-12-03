![funcj.json](https://github.com/typemeta/funcj/blob/master/json/resources/funcj-json.png)

# Introduction

**funcj.json** is a parser and a data model for JSON.

# Getting Started

## Requirements

funcj-json requires Java 1.8 (or higher).

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

To construct an instance of the Java data mode for JSON, use the `JSAPI` class:

```java
JSValue testNode =
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
                ))
        );
```

To print it as JSON text, either use the `JSValue.toString` method:

```json
{"numbers":[1.2,3.4,4.5],"strings":["abcd","efgh","ijkl"],"objects":[{"a":1,"b":2},{"c":3,"d":4}]}
```

or, use `JsonToDoc`:

```java
String text = JsonToDoc.toString(node, 40));
System.out.println(text);
```

to pretty-print thr JSON:

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

Meanwhile, to parse some JSON text back into the model representation, use `JsonParser`:

```java
JSValue res = JsonParser.parse(text).getOrThrow();
```
