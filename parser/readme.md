![funcj.parser](https://github.com/typemeta/funcj/blob/master/parser/resources/funcj-parser.png)

# Introduction

**funcj.parser** is a Java parser combinator framework for constructing [LL(1) parsers](http://en.wikipedia.org/wiki/LL_parser).
It's based primarily on the "Deterministic, error-correcting combinator parsers" paper
by S.D. Swierstra & L. Duponcheel, and draws inspiration from various parsers in the Haskell world,
as well as the [ParsecJ](https://github.com/jon-hanson/parsecj) library.

Some notable features include:
* Composable parser combinators, which provide a DSL for constructing parsers from grammars.
* Informative error messages in the event of parse failures.
* Thread-safe due to immutable parsers and inputs.
* Lightweight library with zero dependencies (aside from JUnit for the tests).
* The framework computes first sets and follow sets,
used to select the correct alternative when presented with a choice.

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
that encapsulate the plumbing, typically with an applicative functor or a monad.
The framework provides the basic building blocks -
parsers for constituent language elements such as characters, words and numbers,
as well as combinators that allow more complex parsers to be constructed by composing existing ones.
The framework effectively provides a Domain Specific Language for expressing language grammars,
whereby each grammar instance implements an executable parser.

# Getting Started

## Requirements

funcj-parser requires Java 1.8 (or higher).

## Resources

* **Release builds** are available on the [Releases](https://github.com/typemeta/funcj/releases) page.
* **Maven Artifacts** are available on the [Sonatype Nexus repository](https://repository.sonatype.org/#nexus-search;quick~funcj.parser)
* **Javadocs** are for the latest build are on the [Javadocs](http://typemeta.github.io/funcj/javadocs/) page.

## Maven

Add this dependency to your project pom.xml:

```xml
<dependency>
    <groupId>org.typemeta</groupId>
    <artifactId>funcj-parser</artifactId>
    <version>${funcj.parser.version}</version>
</dependency>
```

## Example

As a quick illustration of implementing a parser using funcj.parser,
consider a simple expression language for expressions of the form *x+y*, where *x* and *y* are integers.

The grammar for the language consists of a single production rule:

```
sum ::= integer '+' integer
```

This can be translated into the following parser:

```java
Parser<Chr, Integer> sum = intr.andL(chr('+')).and(intr).map(Integer::sum);
```

The parser is constructed by taking the `intr` parser for integers,
the `chr` parser for single characters,
and combining them using `and`, `andL` and `map`.

The parser can be used as follows:

```java
int i = sum.parse(Input.of("1+2")).getOrThrow();
assert i == 3;
```

Meanwhile, if we give it invalid input:

```java
int i2 = sum.parse(Input.of("1+z")).getOrThrow();
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
4. Invoke the parser by passing a `Input` object, usually constructed from a `String`, to the `run` method.
5. The `Result` value returned  by `run` holds either the successfully parsed value or an error message.

# Guide

## Types

All parsers implement the `Parser` interface.
The framework internally uses the `Parser.apply` method to invoke the parser,
however this method shouldn't be used directly.
As a user of the library you use the `Parser.parse` method (),
which takes an `Input` and returns a `Result` value.

### Input

`Input` is an abstraction which represents a stream on parseable tokens,
typically (but not limited to) `Chr`s.

To construct an `Input` value, use one of the static constructor methods:

```java
char[] charData = { 'A', 'B', 'C', 'D'};

Input<Chr> chrArrInput = Input.of(charData);

Input<Chr> strInput = Input.of("ABCD");

Inout<Chr> rdrInput = Input.of(new CharArrayReader(charData))
```
### Result

`Result` represents the result of applying a parser.
It's essentially a discriminated union between a `Result.Success`,
indicating a successful parse,
and a `Result.Failure`, indicating a failure.
The result value can be handled in various ways.

```java
Result<Chr, String> res = p.parse(Input.of("ABCD"));

// Applicative-style function application.
Result<Chr, Integer> resLen = res.map(String::length);

// Apply a function to both result types.
int len = res.match(succ -> succ.value.().length, fail -> -1);

// Or just get the result (and throw an exception if it's a failure).
int len = res.getOrThrow();
```

### Parser

As well as the `parse` method,
`Parser` also provides methods for either combining or modifying existing parsers to form new parsers.

#### The `and` Combinator

The `Parser.and` method is used to combine two or more (up to eight) parsers.
The parsers are combined into one that applies each parser sequentially.
If any parser fails then the operation is aborted and the failure is returned.
If all of the parsers are successful then the results are passed to a `map` method.

```java
// A parser for a 3-digit number.
Parser<Chr, Integer> p3d = digit.and(digit).and(digit).map(a -> b -> c -> a*100 + b*10 + c);
```

If the results of some of the parsers aren't required,
then the results can be be ignored in the `map` function:

```java
// d is ignored
Parser<Chr, Double> d = digit.and(chr('.')).and(digit).map(a -> d -> c -> a*10.0 + c/10.0);
```

Alternatively, the `andL` method can be used to indicate the result of the right-hand parser can be ignored:

```java
// Parser for a number of the form "a.b".
Parser<Chr, Double> d = digit.andL(chr('.')).and(digit).map(a -> d -> c -> a*10.0 + c/10.0);
```

and the `andR` method can be used to indicate the result of the left-hand parser can be ignored:

```java
// Parser for a number of the form "0.b".
Parser<Chr, Double> d2 = string("0.").andR(digit).map(a -> a/10.0);
```

### The `or` Combinator

The `Parser.or` method combines two parsers into one that will apply either parser (but not both).
The decision as to which parser gets applied is based on whichever parser can successfully parse the input.
If neither can parse successfully then the result is a failure.

```java
Parser<Chr, Integer> binary = chr('0').or(chr'1');
```
