# Introduction

**funcj.codec** is a Java framework for serialising Java values
to serialised formats such as JSON and XML, and for deserialising the data back into Java types.

The framework uses Reflection to determine the structure of Java classes,
which is then mirrored in the structure of the serialised data.

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
    <artifactId>parser</artifactId>
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
        final LocalDate birthDate;
        final Set<Colour> favColours;

        Person(String name, double height, LocalDate birthDate, Colour... favColours) {
            this.name = name;
            this.height = height;
            this.birthDate = birthDate;
            this.favColours = new HashSet<>(Arrays.asList(favColours));
        }

        @Deprecated
        Person() {
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

To round trip the data via JSON:

```java
    final JsonCodecCore codec = new JsonCodecCore();

    final Person person = new Person("Jon", 1.86, LocalDate.of(1970, 04, 19), Colour.GREEN, Colour.BLUE);

    // Serialise to JSON.
    final JSValue json = codec.encode(Person.class, person);
    System.out.println(json.toJson(40));
    
    // Deserialise back to Java.
    final Person person2 = codec.decode(Person.class, json);
    assert(person.equals(person2));
```

The serialised JSON looke like this:

```json
{
    "name" : "Jon",
    "height" : 1.86,
    "birthDate" : "1970-04-19",
    "favColours" : {
        "@type" : "java.util.HashSet",
        "@value" : ["GREEN", "BLUE"]
    }
}
```

If we want to instead serialise via XML,
then since it's XML there's a little more ceremony,
the the basics are the same:

```java
    final XmlCodecCore codec = new XmlCodecCore();

    DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    final Document doc = docBuilder.newDocument();
    final Element elem = codec.encode(Person.class, person, doc.createElement("person"));
    System.out.println(XmlUtils.nodeToString(elem,true));
    final Person person2 = codec.decode(Person.class, elem);
    assert(person.equals(person2));
```

and the resultant XML is as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?><person>
    <name>Jon</name>
    <height>1.86</height>
    <birthDate>1970-04-19</birthDate>
    <favColours type="java.util.HashSet">
        <elem>GREEN</elem>
        <elem>BLUE</elem>
    </favColours>
</person>
```