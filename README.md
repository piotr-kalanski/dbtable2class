# dbtable2class
Generate Scala case class based on database table metadata

[![Build Status](https://api.travis-ci.org/piotr-kalanski/dbtable2class.png?branch=development)](https://api.travis-ci.org/piotr-kalanski/dbtable2class.png?branch=development)
[![codecov.io](http://codecov.io/github/piotr-kalanski/dbtable2class/coverage.svg?branch=development)](http://codecov.io/github/piotr-kalanski/dbtable2class/coverage.svg?branch=development)
[<img src="https://img.shields.io/maven-central/v/com.github.piotr-kalanski/dbtable2class_2.11.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22dbtable2class_2.11%22)
[![Stories in Ready](https://badge.waffle.io/piotr-kalanski/dbtable2class.png?label=Ready)](https://waffle.io/piotr-kalanski/dbtable2class)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# Table of contents

- [Goals](#goals)
- [Getting started](#getting-started)
- [Examples](#examples)

# Goals

- Generate Scala case class based on database table metadata

# Getting started

## Include dependencies

```scala
"com.github.piotr-kalanski" % "dbtable2class_2.11" % "0.3.2"
```

or

```xml
<dependency>
    <groupId>com.github.piotr-kalanski</groupId>
    <artifactId>dbtable2class_2.11</artifactId>
    <version>0.3.2</version>
</dependency>
```

# Examples

## Generate from H2

Example table in H2:
```sql
PEOPLE(
   NAME VARCHAR,
   AGE INT
)
```

```scala
val url = "jdbc:h2:mem:test"
ClassGenerator.generateClass(url, null, H2Dialect, TableClassMapping(database="TEST", schema="PUBLIC", table="PEOPLE", packageName="com.datawizards.model", className="Person"))
```

Result:

```scala
package com.datawizards.model

case class Person(
  NAME: String,
  AGE: Int
)
```

## Generate output to directory

```scala
ClassGenerator.generateClassesToDirectory(
  "target", url, null, H2Dialect, Seq(
    TableClassMapping(database="TEST", schema="PUBLIC", table="T11", packageName="com.datawizards.model", className="Person"),
    TableClassMapping(database="TEST", schema="PUBLIC", table="T22", packageName="com.datawizards.model", className="Book")
  )
)
```