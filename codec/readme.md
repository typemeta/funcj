![funcj.codec](https://github.com/typemeta/funcj/blob/master/codec/resources/funcj-codec.png)

# Introduction

**funcj.codec** is a Java framework for round-tripping Java data via a structured data format such as JSON and XML.
It can serialise Java object graphs into serialised form,
and can deserialise the data to reconstruct the original Java values.

## Features

* Supports serialisation via JSON, XML, and b yte streams. Can be extended to support further formats.
* Serialisation is driven by Reflection, consequently the serialised form mirrors the structure of the Java data.
  * Static type information is used where possible to reduce the amount of type metadata present in the serialised data.
* Custom codecs can be registered with the framework, to handle awkward types,
or to simply override the default serialisation provided by the framework.
  * A fluent API is provided to simplify the creation of custom codecs.
* The framework is thread-safe.

## Limitations

* Not optimised for speed.

# Getting Started

## Requirements

funcj.codec requires Java 1.8 (or higher).

## Resources

* **Release builds** are available on the [Releases](https://github.com/typemeta/funcj/releases) page.
* **Maven Artifacts** are available on the [Sonatype Nexus repository](https://repository.sonatype.org/#nexus-search;quick~funcj.codec)
* **Javadocs** are for the latest build are on the [Javadocs](http://typemeta.github.io/funcj/javadocs/) page.

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

First some sample types we want to serialise:

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

// Serialise to JSON.
final JSValue json = codec.encode(person);
System.out.println(json.toString(40));

// Deserialise back to Java.
final Person person2 = codec.decode(Person.class, json);
assert(person.equals(person2));
```

The serialised JSON then looks like this:

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

### XML

If, instead, we want to serialise via XML,
then since it's XML there's a little more ceremony,
but the basics are the same:

```java
final XmlCodecCore codec = Codecs.xmlCodec();

final Document doc =
        DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .newDocument();

// Serialise to XML.
final Element elem = codec.encode(Person.class, person, doc.createElement("person"));
System.out.println(XmlUtils.nodeToString(elem, true));

// Deserialise back to Java.
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

#### Custom Codec Builder

If, for example, we want to override how the `ZonedDateTime` type is serialised,
to serialise it as a `String`,
we can do so like this:

```java
codec.registerCodec(ZonedDateTime.class)
        .field("time", ZonedDateTime::toString, String.class)
        .map(ZonedDateTime::parse);
```

The serialised results are then:

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

Alternatively, we can use `StringProxyCodec` to encode the `ZonedDateTime` as a `String`:

```java
codec.registerStringProxyCodec(
        ZonedDateTime.class,
        ZonedDateTime::toString,
        ZonedDateTime::parse);
```

The serialised results are then:

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
