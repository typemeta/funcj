# Introduction

**funcj.codec** is a Java framework for serialising Java values
to serialised structured data formats such as JSON and XML,
and for deserialising the same data back into Java values.

The framework uses Reflection to determine the structure of Java classes,
which is then mirrored in the structure of the serialised data.
Custom codecs can be registered with the framework, to handle awkward types,
or to simply override the default serialisation provided by the framework.
A DSL is provided to simplify the creation of custom codecs.    

# Getting Started

## Requirements

funcj.parser requires Java 1.8 (or higher).

## Resources

* **Release builds** are available on the [Releases](https://github.com/jon-hanson/funcj/releases) page.
* **Maven Artifacts** are available on the [Sonatype Nexus repository](https://repository.sonatype.org/#nexus-search;quick~funcj.parser)
* **Javadocs** are for the latest build are on the [Javadocs](http://jon-hanson.github.io/funcj/javadocs/) page.

## Maven

Add this dependency to your project pom.xml:

```xml
<dependency>
    <groupId>org.funcj</groupId>
    <artifactId>codec</artifactId>
    <version>0.1-SNAPSHOT</version>
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
```

Then, to round trip the data via JSON:

```java
final JsonCodecCore codec = Codecs.jsonCodec();

final Person person = new Person(
        "Jon",
        1.86,
        ZonedDateTime.of(
                LocalDateTime.of(1970, 04, 19, 17, 05, 41),
                ZoneId.of("GMT")),
        Colour.GREEN, Colour.BLUE);

// Serialise to JSON.
final JSValue json = codec.encode(person);
System.out.println(json.toJson(40));

// Deserialise back to Java.
final Person person2 = codec.decode(Person.class, json);
assert(person.equals(person2));
```

The serialised JSON looks like this:

```json
{
    "name" : "Jon",
    "height" : 1.86,
    "birthDate" : {
        "dateTime" : {
            "date" : {
                "year" : 1970,
                "month" : 4,
                "day" : 19
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

If, instead, we want to serialise via XML,
then since it's XML there's a little more ceremony,
but the basics are the same:

```java
final XmlCodecCore codec = Codecs.xmlCodec();

final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
final Document doc = docBuilder.newDocument();

// Serialise to XML.
final Element elem = codec.encode(Person.class, person, doc.createElement("person"));
System.out.println(XmlUtils.nodeToString(elem,true));

// Deserialise back to Java.
final Person person2 = codec.decode(Person.class, elem);
assert(person.equals(person2));
```

and the resultant XML is as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?><person>
    <name>Jon</name>
    <height>1.86</height>
    <birthDate>
        <dateTime>
            <date>
                <year>1970</year>
                <month>4</month>
                <day>19</day>
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
