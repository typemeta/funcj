[![Javadocs](http://www.javadoc.io/badge/org.typemeta/funcj-codec.svg)](http://www.javadoc.io/doc/org.typemeta/funcj-codec)

![funcj.codec](https://github.com/typemeta/funcj/blob/master/codec/resources/funcj-codec.png)

- [Introduction](#introduction)
  - [Features](#features)
- [Getting Started](#getting-started)
  - [Requirements](#requirements)
  - [Resources](#resources)
  - [Maven](#maven)
  - [Example](#example)
    - [JSON](#json)
    - [XML](#xml)
    - [Custom Codecs](#custom-codecs)
      - [Custom Codec Builder](#custom-codec-builder)
      - [StringProxyCodec](#stringproxycodec)
      - [Custom Codec Direct Implementation](#custom-codec-direct-implementation)
- [Reference](#reference)
  - [Supported Types](#supported-types)
  - [Object Codecs](#object-codecs)
- [FAQ](#faq)
  - [Why not Jackson?](#why-not-jackson)

# Introduction

**funcj.codec** is a Java framework for round-tripping Java data via structured data formats
such as JSON and XML, as well as byte streams.
It can encode Java object graphs into data streams,
and can then decode the data to reconstruct the original Java values.

## Features

* Supports encoding via JSON, XML, MessagePack, raw byte streams and gzipped byte streams. Can be extended to support further formats.
* Uses streaming to avoid building, for example, large DOM or JSON node graphs in memory.
* Supports primitive types, generics, collections, nulls, as well as any Java class using field-based Reflection.
* Should round-trip data perfectly, meaning that for example,
a `TreeMap` will be reconstructed as the same type (and not as a `HashMap`).
* Is thread-safe.
* The framework is relatively non-invasive - the only requirement imposed on your types are that they contain a default constructor -
one that takes no arguments (the constructor can be private).
  * Note, this requirement can be bypassed by registering a custom constructor for the class, or by providing a custom codec for the class.
* Encoding is driven by reflecting over the field members that comprise each class,
consequently the encoded form mirrors the structure of the original Java data.
  * Static type information is used where possible
to reduce the amount of type metadata present in the encoded data.
* Custom codecs can be registered with the framework, to handle awkward types,
or to simply override the default encoding provided by the framework.
  * A fluent API is provided to simplify the creation of custom codecs.
  * Custom codecs are generally agnostic to a specific encoding, meaning they can be re-used for all encoding types.

## Limitations

* Does not, currently, handle cyclic object graphs (i.e. graphs with loops).

# Getting Started

## Requirements

funcj.codec requires Java 1.8 (or higher).

## Resources

* **Release builds** are available on the [Releases](https://github.com/typemeta/funcj/releases) page.
* **Maven Artifacts** are available on the [Sonatype Nexus repository](https://repository.sonatype.org/#nexus-search;quick~funcj.codec)
* **Javadocs** are for the latest build are on [javadocs.io](http://www.javadoc.io/doc/org.typemeta/funcj-codec) page.

## Maven

Add this dependency to your project pom.xml:

```xml
<dependency>
    <groupId>org.typemeta</groupId>
    <artifactId>funcj-codec-core</artifactId>
    <version>${funcj.codec.version}</version>
</dependency>
```

For MessagePack support use:

```xml
<dependency>
    <groupId>org.typemeta</groupId>
    <artifactId>funcj-codec-mpack</artifactId>
    <version>${funcj.codec.version}</version>
</dependency>
```

## Example

First some sample types we want to encode:

```java
enum Colour {RED, GREEN, BLUE}

static class Person {

    final String name;
    final double height;
    final ZonedDateTime birthDate;
    final Set<Colour> favColours;

    Person(String name, double height, ZonedDateTime birthDate, Colour... favColours) {
        this.name = name;
        this.height = height;
        this.birthDate = birthDate;
        this.favColours = new HashSet<>(Arrays.asList(favColours));
    }

    private Person() {
        this.name = null;
        this.height = 0;
        this.birthDate = null;
        this.favColours = null;
    }

    @Override
    public boolean equals(Object o) {
        // Implementation elided for brevity.
    }
}

final Person person = new Person(
        "Marconi",
        1.86,
        ZonedDateTime.of(
                LocalDateTime.of(1874, 4, 25, 17, 5, 41),
                ZoneId.of("GMT")),
        Colour.GREEN, Colour.BLUE);
```

### JSON

To round trip the data via JSON:

```java
final JsonCodecCore codec = Codecs.jsonCodec();

// Encode to JSON.
jsonCodecCore.encode(Person.class, person, System.out);

System.out.flush();

// Decode back to Java.
final Person person2 = jsonCodecCore.decode(Person.class, System.in);

// Check the object is the same.
assert(person.equals(person2));
```

The encoded JSON then looks like this:

```json
{
    "name" : "Marconi",
    "height" : 1.86,
    "birthDate" : {
        "dateTime" : {
            "date" : {
                "year" : 1874,
                "month" : 4,
                "day" : 25
            },
            "time" : {
                "hours" : 17,
                "mins" : 5,
                "secs" : 41,
                "nanos" : 0
            }
        },
        "zone" : {
            "@type" : "java.time.ZoneRegion",
            "@value" : {"id" : "GMT"}
        },
        "offset" : {"id" : "Z"}
    },
    "favColours" : {
        "@type" : "java.util.HashSet",
        "@value" : ["GREEN", "BLUE"]
    }
}
```

A few things to note:

* The encoded form mirrors the structure of the Java data.
* For most fields type information is not required in the encoded representation,
as the framework will use the static type information when reconstructing the Java data.
  * However a couple of fields do have extra type metadata.
In these cases the dynamic type of the value (e.g. `HashSet`)
is different to the static type (e.g. `Set`),
hence the extra type metadata is included in the encoded form.

### XML

If, instead, we want to encode as XML, then the basics are the same:

```java
final XmlCodecCore codec = Codecs.xmlCodec();

final String root = "person";

// Encode to XML.
xmlCodecCore.encode(Person.class, person, System.out);

System.out.flush();

// Decode back to Java.
final Person person2 = xmlCodecCore.decode(Person.class, System.in);

// Check the object is the same.
assert(person.equals(person2));
```

and the resultant XML is as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<person>
    <name>Marconi</name>
    <height>1.86</height>
    <birthDate>
        <dateTime>
            <date>
                <year>1874</year>
                <month>4</month>
                <day>25</day>
            </date>
            <time>
                <hours>17</hours>
                <mins>5</mins>
                <secs>41</secs>
                <nanos>0</nanos>
            </time>
        </time>
        <zone type="java.time.ZoneRegion">
            <id>GMT</id>
        </zone>
        <offset>
            <id>Z</id>
        </offset>
    </birthDate>
    <favColours type="java.util.HashSet">
        <elem>GREEN</elem>
        <elem>BLUE</elem>
    </favColours>
</person>
```

### Custom Codecs

The framework allows custom codecs to be registered to override the default behaviour.

Custom codecs can be agnostic to the codec implementation encoding,
meaning the same custom codec can be used for JSON, XML, byte streams, etc.
Alternatively a custom codec can target a specific encoding.

#### Custom Codec Builder

The simplest way to define a custom codec is to use either `CodecCore.registerCodecWithArgArray`
or `CodecCore.registerCodecWithArgMap`,
and pass it the class value for the type in question. 
These methods return a class with a fluent API for defining custom codecs.
You first call `field` for each field comprising the class,
supplying the field name, a getter for the field,
and either a codec,
or a class value (which is used to look up the appropriate codec).
Finally call the `construct` method with the function which,
when supplied with the field values, will constuct an instance of the type.

For example, the codec for `ZonedDateTime` type is defined like this

```java
core.registerCodecWithArgArray(ZonedDateTime.class)
        .field("dateTime", ZonedDateTime::toLocalDateTime, LocalDateTime.class)
        .field("zone", ZonedDateTime::getZone, ZoneId.class)
        .field("offset", ZonedDateTime::getOffset, ZoneOffset.class)
        .construct(ZonedDateTime::ofLocal);
```

Note that the same custom codec can be used for any encoding format - XML, JSON etc.

#### StringProxyCodec

If you simply want to encode a type as a string value,
then you can use a `StringProxyCodec`.
To do so, call `CodecCore.registerStringProxyCodec` and provide
the type class, a function which converts the value to a string,
and a function which constructs the value from a string.
 
For example, to encode the `ZonedDateTime` as a `String`:

```java
codec.registerStringProxyCodec(
        ZonedDateTime.class,
        ZonedDateTime::toString,
        ZonedDateTime::parse);
```

The encoded results are then:

```json
{
    "name" : "Marconi",
    "height" : 1.86,
    "birthDate" : "1874-04-25T17:05:41Z[GMT]",
    "favColours" : {
        "@type" : "java.util.HashSet",
        "@value" : ["GREEN", "BLUE"]
    }
}
```

and:

```xml
<?xml version="1.0" encoding="UTF-8"?><person>
    <name>Marconi</name>
    <height>1.86</height>
    <birthDate>1874-04-25T17:05:41Z[GMT]</birthDate>
    <favColours type="java.util.HashSet">
        <elem>GREEN</elem>
        <elem>BLUE</elem>
    </favColours>
</person>
```

#### Custom Codec Direct Implementation

The third way to define a custom codec is to define a class that implements the `Codec` interface.
As the codec is dealing directly with the underlying encoding - e.g. JSON,
it has to be specialised for a specific encoding.

For example, a custom JSON codec for the `ZonedDateTime` class could be written as follows:

```java

static class ZonedDateTimeJsonCodec
        implements Codec.FinalCodec<ZonedDateTime, JsonTypes.InStream, JsonTypes.OutStream, JsonTypes.Config> {

    @Override
    public Class<ZonedDateTime> type() {
        return ZonedDateTime.class;
    }

    @Override
    public JsonTypes.OutStream encode(CodecCoreEx<JsonTypes.InStream, JsonTypes.OutStream, JsonTypes.Config> core, ZonedDateTime value, JsonTypes.OutStream out) {
        out.startObject();

        out.writeField("dateTime");
        core.encode(LocalDateTime.class, value.toLocalDateTime(), out);
        out.writeField("zone");
        core.encode(ZoneId.class, value.getZone(), out);
        out.writeField("offset");
        core.encode(ZoneOffset.class, value.getOffset(), out);

        return out.endObject();
    }

    @Override
    public ZonedDateTime decode(CodecCoreEx<JsonTypes.InStream, JsonTypes.OutStream, JsonTypes.Config> core, JsonTypes.InStream in) {
        in.startObject();

        in.readFieldName("dateTime");
        final LocalDateTime ldt = core.decode(LocalDateTime.class, in);
        in.readFieldName("zone");
        final ZoneId zid = core.decode(ZoneId.class, in);
        in.readFieldName("offset");
        final ZoneOffset zo = core.decode(ZoneOffset.class, in);

        in.endObject();

        return ZonedDateTime.ofLocal(ldt, zid, zo);
    }
}
```

and registered like this:

```java
jsonCodecCore.registerCodec(
        ZonedDateTime.class,
        new ZonedDateTimeJsonCodec(jsonCodecCore));
```

# Reference

## Supported Types

The framework comes with built-in support for the following types:

* Primitive Java types - `boolean`, `byte`, `char`, `short`, `int`, `long`, `float` & `double`.
* Arrays of primitives - `boolean[]`, `byte[]`, `char[]`, `short[]`, `int[]`, `long[]`, `float[]` & `double[]`.
* Object arrays - `T[]`.
* `String`.
* Collections
  * `Map<K, V>` and  `Map<String, V>` are handled as special cases.
  * All other collections types are treated as a generic sequence of values.
  * Note that for all collection types,
    the name of the specific implementation type (`HashSet`, `TreeMap` etc),
    is encoded if necessary, allowing the original collection value to be reconstructed.
* `Enum` types.
* `null` values.

`CodecCore` instances created using the `Codecs` class
will also contain pre-registered codecs for the following types:

* The Java `Class` type.
* Java 8 date/time types - `LocalDate`, `LocalTime`, `LocalDateTime`, `ZoneId`, `ZoneOffset`,
`OffsetTime`, `OffsetDateTime`, & `ZonedDateTime`.

## Object Codecs

For any classes encountered by the framework that it doesn't recognise,
it will introspect the class structure in order to create an object codec for that type.
For each non-static, non-transient field found in the class (including its superclasses),
the codec for that type will be fetched (or created).
The codec for the class is then a composition of the codecs for its constituent fields.

For each field and it's corresonding value,
the static type is compared to the dynamic type.
If they are the same, then no type meta-data need be encoded.
If they are different, then the dynamic type is added to the encoding.

The class must have a default constructor.
The constructor can be private -
the framework will attempt to temporarily disable this by calling `AccessibleObject.setAccessible`.

# FAQ

## Why not Jackson?

I was motivated to write this library after numerous failed attempts
getting Jackson to serialise/deserialise classes based on their fields,
and to round-trip data correctly
without resorting to heavily annotating the classes with Jackson annotations.

For example, if I run the tutorial's `Person` class through the Jackson `ObjectMapper`,
I get the following error:

```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
No serializer found for class org.typemeta.funcj.codec.Example$Person and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)
	at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:77)
```

If, as suggested, I disable the `FAIL_ON_EMPTY_BEANS` feature, then I get an empty JSON object.
