[![Javadocs](http://www.javadoc.io/badge/org.typemeta/funcj-core.svg)](http://www.javadoc.io/doc/org.typemeta/funcj-core)

![funcj.core](https://github.com/typemeta/funcj/blob/master/core/resources/funcj-core.png)

# Introduction

**funcj.core** is a set of data structures and algorithms.

# Getting Started

## Requirements

funcj.core requires Java 1.8 (or higher).

## Resources

* **Release builds** are available on the [Releases](https://github.com/typemeta/funcj/releases) page.
* **Maven Artifacts** are available on the [Sonatype Nexus repository](https://repository.sonatype.org/#nexus-search;quick~funcj.core)
* **Javadocs** are for the latest build are on [javadocs.io](http://www.javadoc.io/doc/org.typemeta/funcj-core) page.

## Maven

Add this dependency to your project pom.xml:

```xml
<dependency>
    <groupId>org.typemeta</groupId>
    <artifactId>funcj-core</artifactId>
    <version>${funcj.core.version}</version>
</dependency>
```

# Guide

## Tuples

func.core provide tuple types with lengths from 2 to 8.
The first, `Tuple2`, is described below - the remaining tuple types follow the same pattern.

### Tuple2

`Tuple2` provides a static constructor:

```Java
Tuple2<Double, Boolean> p = Tuple2.of(1.2, false);
```

Each of the elements can be accessed via a field and a method:

```Java
assert(p._1 == p.get1());
assert(p._2 == p.get2());
```

Either of the elements can be overriden to form a new tuple value:

```Java
Tuple2<String, Boolean> p2 = p.with1("test");
Tuple2<Double, Character> p3 = p.with2('x');
```

Either element can also be overriden by applying a function to it:

```Java
Tuple2<String, Boolean> p4 = p.map1(Object::toString);
Tuple2<Double, Character> p5 = p.map2(b -> b.toString().charAt(0));
```

## Control

The `org.typemeta.funcj.control` package contains data types relating to control of flow.

### Either

`Either<A, B>` is a right-biased union type,
and can be an `Either.Left<A, B>` that holds a value of type `A`,
or a `Either.Right<A, B>` that holds a value of type 'B'.
It is right-biased in the sense that `Either.Left<A, B>` values short-circuit any further computations,
whereas `Either.Right<A, B>` allows it to continue.
Under this usage, the type parameter `A` typically represents an error,
conversely `B` is a successful result type.

#### Example

Both of these functions can either fail by returning a `String` error message wrapped in an `Either.Left`,
or by returning a successful `Double` wrapped in an `Either.Right`.

```Java
static Either<String, Double> reciprocal(double x) {
    return (x == 0) ? Either.left("Divide by zero") : Either.right(1.0 / x);
}

static Either<String, Double> sqrt(double x) {
    return (x < 0) ? Either.left("Square root of -ve number") : Either.right(Math.sqrt(x));
}
```

We can combine these to form a new function using `flatMap`:

```Java
static Either<String, Double> recipSqrt(double x) {
    return reciprocal(x).flatMap(r -> sqrt(r));
}
```

The `sqrt` function is only invoked if `reciprocal` succeeds:

```Java
Either<String, Double> a = recipSqrt(0.25);
assert(a.equals(Either.right(2.0)));

Either<String, Double> b = recipSqrt(-1.0);
assert(b.equals(Either.left("Square root of -ve number")));

Either<String, Double> c = recipSqrt(0.0);
assert(c.equals(Either.left("Divide by zero")));
```

#### Operations

In addition to `flatMap` described above,
`Either` supports other operations common to control data types.

```Java
// map applies a function to the right-value only.
Either<String, Double> d = a.map(r -> r + 1.0);
assert(d.equals(Either.right(3.0)));

// match applies a left function to the left or a right function to the right value
String s = a.match(left -> left.value, right -> right.value.toString());
assert(s.equals("2.0"));

// and/map allow Either values to be collected before being passed to a function.
Either<String, Double> e = reciprocal(2.0).and(sqrt(4.0)).map(Double::sum);
assert(e.equals(Either.right(2.5)));
```

### Try

The `Try<T>` data type is equivalent to `Either<Exception, T>`.
I.e. it is a union type between a `Failure` which contains an `Exception`,
and a `Success` which contains a value of type `T`.

### Validated

The `Validated<E, T>` data type is equivalent to `Either<List<E>, T>`.
I.e. it is a union type between a `Failure` which contains an `List<E>`,
and a `Success` which contains a value of type `T`.
