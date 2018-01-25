[![Javadocs](http://www.javadoc.io/badge/org.typemeta/funcj-codec.svg)](http://www.javadoc.io/doc/org.typemeta/funcj-codec)

![funcj.codec](https://github.com/typemeta/funcj/blob/master/codec/resources/funcj-codec.png)

# Introduction

**funcj.codec** is a Java framework for round-tripping Java data via a structured data formats
such as JSON and XML, as well as byte streams.
It can encode Java object graphs into an encoded form,
and can then decode the data to reconstruct the original Java values.

## Features

* Supports encoding via JSON, XML, and byte streams. Can be extended to support further formats.
* Encoding is driven by Reflection,
consequently the encoded form mirrors the structure of the original Java data.
  * Static type information is used where possible to reduce the amount of type metadata present in the encoded data.
* Custom codecs can be registered with the framework, to handle awkward types,
or to simply override the default encoding provided by the framework.
  * A fluent API is provided to simplify the creation of custom codecs.
  * Custom codecs are generally agnostic to a specific encoding, meaning they can be re-used for all encoding types.
* The framework is thread-safe.

## Limitations

* The design favours flexibility over performance.

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
    <artifactId>funcj-codec</artifactId>
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
        // Implementation elided.
    }
}

final Person person = new Person(
        "Marconi",
        1.86,
        ZonedDateTime.of(
                LocalDateTime.of(1874, 04, 25, 17, 05, 41),
                ZoneId.of("GMT")),
        Colour.GREEN, Colour.BLUE);
```

### JSON

To round trip the data via JSON:

```java
final JsonCodecCore codec = Codecs.jsonCodec();

// Encode to JSON.
final JSValue json = codec.encode(person);
System.out.println(json.toString(40));

// Decode back to Java.
final Person person2 = codec.decode(Person.class, json);
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
* For most fields type information is not required,
as the framework will use the static type information.
* A couple of fields have extra type metadata.
In these cases the dynamic type of the value (e.g. `HashSet`)
is different to the static type (e.g. `Set`),
hence the extra type metadata is included in the encoded representation.

### XML

If, instead, we want to encode as XML,
then as it's XML there's a little more ceremony,
but the basics are the same:

```java
final XmlCodecCore codec = Codecs.xmlCodec();

final Document doc =
        DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .newDocument();

// Encode to XML.
final Element elem = codec.encode(Person.class, person, doc.createElement("person"));
System.out.println(XmlUtils.nodeToString(elem, true));

// Decode back to Java.
final Person person2 = codec.decode(Person.class, elem);
assert(person.equals(person2));
```

and the resultant XML is as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?><person>
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

### Custom Codec

The framework allows custom codecs to be registered to override the default behaviour.

Custom codecs can be agnostic to the codec implementation encoding,
meaning the same custom codec can be used for JSON, XML, byte streams, etc.
Alternatively a custom codec can target a specific encoding.

#### Custom Codec Builder

The simplest way to define a custom codec is to call `CodecCore.registerCodec`,
and pass it the class value for the type in question. 
This returns a `ObjectCodecBuilder`,
which has a fluent API for defining custom codecs.
You first call `field` for each field comprising the class,
supplying the field name, a getter for the field,
and either a codec,
or a class value (which is used to look up the appropriate codec).
Finally call `map` with the method which,
given the field values, will constuct an instance of the type.

For example, if we want to override how the `ZonedDateTime` type is encoded,
to encode it as a `String`,
we can do so like this:

```java
codec.registerCodec(ZonedDateTime.class)
        .field("time", ZonedDateTime::toString, String.class)
        .map(ZonedDateTime::parse);
```

The encoded results are then:

```json
{
    "name" : "Marconi",
    "height" : 1.86,
    "birthDate" : {
        "time" : "1874-04-25T17:05:41Z[GMT]"
    },
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
    <birthDate>
        <time>1874-04-25T17:05:41Z[GMT]</time>
    </birthDate>
    <favColours type="java.util.HashSet">
        <elem>GREEN</elem>
        <elem>BLUE</elem>
    </favColours>
</person>
```

For reference, the default codec for `ZonedDateTime` is defined as below:

```java
core.registerCodec(ZonedDateTime.class)
        .field("dateTime", ZonedDateTime::toLocalDateTime, LocalDateTime.class)
        .field("zone", ZonedDateTime::getZone, ZoneId.class)
        .field("offset", ZonedDateTime::getOffset, ZoneOffset.class)
        .map(ZonedDateTime::ofLocal);
```

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

The third way to define a custom codec is to write a class that implements the `Codec` interface.
As the codec is dealing directly with the underlying encoding - e.g. JSON values,
it has to be specialised for a specific encoding.

For example, a custom JSON codec for the `ZonedDateTime` could be written as follows:

```java
class ZonedDateTimeJsonCodec extends Codecs.CodecBase<ZonedDateTime, JsValue> {

    public ZonedDateTimeJsonCodec(JsonCodecCore core) {
        super(core);
    }

    @Override
    public JsValue encode(ZonedDateTime val, JsValue enc) {
        return JSAPI.obj(
                JSAPI.field("dateTime", core.encode(val.toLocalDateTime(), enc)),
                JSAPI.field("zone", core.encode(val.getZone(), enc)),
                JSAPI.field("offset", core.encode(val.getOffset(), enc)));
    }

    @Override
    public ZonedDateTime decode(JsValue enc) {
        final JsObject obj = enc.asObject();
        return ZonedDateTime.ofLocal(
                core.decode(LocalDateTime.class, obj.get("dateTime")),
                core.decode(ZoneId.class, obj.get("zone")),
                core.decode(ZoneOffset.class, obj.get("offset")));
    }
}
```

and registered like this:

```java
jsonCodecCore.registerCodec(
        ZonedDateTime.class,
        new ZonedDateTimeJsonCodec(jsonCodecCore));
```
