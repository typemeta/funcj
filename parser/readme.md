# Introduction

**funcj.parser** is a Java parser combinator framework for constructing [LL(1) parsers](http://en.wikipedia.org/wiki/LL_parser).
It is largely based on the "Deterministic, error-correcting combinator parsers" paper
by S.D. Swierstra & L. Duponcheel, and draws inspiration by various parsers in the Haskell world,
as well as [ParsecJ](https://github.com/jon-hanson/parsecj).

Some notable features include:
* Composable parser combinators, which provide a DSL for constructing parsers from grammars.
* Informative error messages in the event of parse failures.
* Thread-safe due to immutable parsers and inputs.
* Lightweight library with zero dependencies (aside from JUnit for the tests).

## Parser Combinators

A typical approach to implementing parsers for special-purpose languages
is to use a parser generation tool, such as Yacc/Bison or ANTLR.
With these tools the language is expressed as a series of production rules,
described using a grammar language specific to the tool.
The parsing code for the language is then generated from the grammar definition.

An alternative approach is to implement a
[recursive descent parser](http://en.wikipedia.org/wiki/Recursive_descent_parser),
whereby the production rules comprising the grammar
are translated by hand into parse functions.
The advantage here is that the rules are expressed in the host programming language,
obviating the need for a separate grammar language and the consequent code-generation phase.
A limitation of this approach
is that the extra plumbing required to implement error-handling and backtracking
obscures the relationship between the parsing functions and the language rules

[Parser combinators](http://www.cs.nott.ac.uk/~gmh/bib.html#pearl)
are an extension of recursive descent parsing,
that use an applicative functor (or a monad) to encapsulate the plumbing.
The framework provides the basic building blocks -
parsers for constituent language elements such as characters, words and numbers,
as well as combinators that allow more complex parsers to be constructed by composing existing parsers.
The framework effectively provides a Domain Specific Language for expressing language grammars,
whereby each grammar instance implements an executable parser.

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

As a quick illustration of implementing a parser using funcj.parser,
consider a simple expression language for expressions of the form *x+y*, where *x* and *y* are integers.

The grammar for the language consists of a single production rule:

```
sum ::= integer '+' integer
```

This can be translated into the following ParsecJ parser:

```java
    Parser<Chr, Integer> sum =
            intr.andL(chr('+')).and(intr).map(Integer::sum);
```

The parser is constructed by taking the `intr` parser for integers,
the `chr` parser for single characters,
and combining them using `and`, `andL` and `map`.

The parser can be used as follows:

```java
    int i = sum.run(Input.of("1+2")).getOrThrow();
    assert i == 3;
```

Meanwhile, if we give it invalid input:

```java
    int i2 = sum.run(Input.of("1+z")).getOrThrow();
```

then it throws an exception with an error message that pinpoints the problem:

```java
Failure at position 2, expected=+ - <digit>
```

## General Approach

A typical approach to using the library to implement a parser for a language is as follows:

1. Define a model for language, i.e. a set of classes that represent the language elements.
2. Define a grammar for the language - a set of production rules.
3. Translate the production rules into parsers using the library combinators. The parsers will typically construct values from the model.
4. Book-end the parser for the top-level element with the `eof` combinator.
5. Invoke the parser by passing a `Input` object, usually constructed from a `String`, to the `parse` method.
6. The resultant `Reply` result holds either the successfully parsed value or an error message.
