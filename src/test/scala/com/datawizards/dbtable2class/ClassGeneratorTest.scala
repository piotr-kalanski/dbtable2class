package com.datawizards.dbtable2class

import java.io.File
import java.sql.DriverManager

import com.datawizards.dbtable2class.dialects.H2Dialect
import com.datawizards.dbtable2class.model.TableClassMapping
import org.scalatest._

class ClassGeneratorTest extends FunSuite with Matchers {

  private val url = "jdbc:h2:mem:test"
  private val connection = DriverManager.getConnection(url, "", "")

  test("generate people") {
    connection.createStatement().execute("create table PEOPLE(NAME VARCHAR, AGE INT)")
    val classDefinition = ClassGenerator.generateClass(url, null, H2Dialect, TableClassMapping("TEST", "PUBLIC", "PEOPLE", "com.peoplePackage", "Person"))
    classDefinition.replace("\n","").replace("\r","") should equal(
      """
        |package com.peoplePackage
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.PEOPLE}.
        |  * Generated automatically.
        |  */
        |case class Person(
        |  NAME: String,
        |  AGE: Int
        |)""".stripMargin.replace("\n","").replace("\r","")
    )
  }

  test("generate all types") {
    val ddl =
      """create table ALL_TYPES(
        |STRVAL	VARCHAR,
        |INTVAL	INTEGER,
        |LONGVAL	BIGINT,
        |DOUBLEVAL	DOUBLE,
        |FLOATVAL	REAL,
        |SHORTVAL	SMALLINT,
        |BOOLEANVAL	BOOLEAN,
        |BYTEVAL	TINYINT,
        |DATEVAL	DATE,
        |TIMESTAMPVAL	TIMESTAMP
        |)
      """.stripMargin
    connection.createStatement().execute(ddl)
    val classDefinition = ClassGenerator.generateClass(dbUrl=url, connectionProperties = null, dialect = H2Dialect, mapping = TableClassMapping(database = "TEST", schema = "PUBLIC", table = "ALL_TYPES", packageName = "com.pack", className = "AllTypes"))
    classDefinition.replace("\n","").replace("\r","") should equal(
      """
        |package com.pack
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.ALL_TYPES}.
        |  * Generated automatically.
        |  */
        |case class AllTypes(
        |  STRVAL: String,
        |  INTVAL: Int,
        |  LONGVAL: Long,
        |  DOUBLEVAL: Double,
        |  FLOATVAL: Float,
        |  SHORTVAL: Short,
        |  BOOLEANVAL: Boolean,
        |  BYTEVAL: Byte,
        |  DATEVAL: java.sql.Date,
        |  TIMESTAMPVAL: java.sql.Timestamp
        |)""".stripMargin.replace("\n","").replace("\r","")
    )
  }

  test("generate reserved keywords") {
    connection.createStatement().execute(
      """create table TABLE_RESERVED_KEYWORDS(
        |  "case" VARCHAR,
        |  "catch" VARCHAR,
        |  "class" VARCHAR,
        |  "def" VARCHAR,
        |  "do" VARCHAR,
        |  "else" VARCHAR,
        |  "extends" VARCHAR,
        |  "false" VARCHAR,
        |  "final" VARCHAR,
        |  "for" VARCHAR,
        |  "if" VARCHAR,
        |  "match" VARCHAR,
        |  "new" VARCHAR,
        |  "null" VARCHAR,
        |  "package" VARCHAR,
        |  "print" VARCHAR,
        |  "printf" VARCHAR,
        |  "println" VARCHAR,
        |  "throw" VARCHAR,
        |  "to" VARCHAR,
        |  "trait" VARCHAR,
        |  "true" VARCHAR,
        |  "try" VARCHAR,
        |  "type" VARCHAR,
        |  "until" VARCHAR,
        |  "val" VARCHAR,
        |  "var" VARCHAR,
        |  "while" VARCHAR,
        |  "with" VARCHAR
        |)""".stripMargin)
    val classDefinition = ClassGenerator.generateClass(url, null, H2Dialect, TableClassMapping("TEST", "PUBLIC", "TABLE_RESERVED_KEYWORDS", "com.peoplePackage", "Person"))
    classDefinition.replace("\n","").replace("\r","") should equal(
      """
        |package com.peoplePackage
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.TABLE_RESERVED_KEYWORDS}.
        |  * Generated automatically.
        |  */
        |case class Person(
        |  `case`: String,
        |  `catch`: String,
        |  `class`: String,
        |  `def`: String,
        |  `do`: String,
        |  `else`: String,
        |  `extends`: String,
        |  `false`: String,
        |  `final`: String,
        |  `for`: String,
        |  `if`: String,
        |  `match`: String,
        |  `new`: String,
        |  `null`: String,
        |  `package`: String,
        |  `print`: String,
        |  `printf`: String,
        |  `println`: String,
        |  `throw`: String,
        |  `to`: String,
        |  `trait`: String,
        |  `true`: String,
        |  `try`: String,
        |  `type`: String,
        |  `until`: String,
        |  `val`: String,
        |  `var`: String,
        |  `while`: String,
        |  `with`: String
        |)""".stripMargin.replace("\n","").replace("\r","")
    )
  }

  test("character -") {
    connection.createStatement().execute(
      """create table TABLE_WITH_DASH(
        |  "two-words" VARCHAR
        |)""".stripMargin)
    val classDefinition = ClassGenerator.generateClass(url, null, H2Dialect, TableClassMapping("TEST", "PUBLIC", "TABLE_WITH_DASH", "com.peoplePackage", "Person"))
    classDefinition.replace("\n","").replace("\r","") should equal(
      """
        |package com.peoplePackage
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.TABLE_WITH_DASH}.
        |  * Generated automatically.
        |  */
        |case class Person(
        |  `two-words`: String
        |)""".stripMargin.replace("\n","").replace("\r","")
    )
  }

  test("ends with _") {
    connection.createStatement().execute(
      """create table TABLE_ENDING_WITH_(
        |  "column_" VARCHAR
        |)""".stripMargin)
    val classDefinition = ClassGenerator.generateClass(url, null, H2Dialect, TableClassMapping("TEST", "PUBLIC", "TABLE_ENDING_WITH_", "com.peoplePackage", "Person"))
    classDefinition.replace("\n","").replace("\r","") should equal(
      """
        |package com.peoplePackage
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.TABLE_ENDING_WITH_}.
        |  * Generated automatically.
        |  */
        |case class Person(
        |  `column_`: String
        |)""".stripMargin.replace("\n","").replace("\r","")
    )
  }

  test("Generate multiple classes") {
    connection.createStatement().execute("create table T1(NAME VARCHAR, AGE INT)")
    connection.createStatement().execute("create table T2(TITLE VARCHAR, AUTHOR VARCHAR)")
    val classDefinitions = ClassGenerator.generateClasses(
      url, null, H2Dialect, Seq(
        TableClassMapping(database = "TEST", schema = "PUBLIC", table = "T1", packageName = "pp", className = "Person"),
        TableClassMapping(database = "TEST", schema = "PUBLIC", table = "T2", packageName = "pp", className = "Book")
      )
    )
    classDefinitions.map(_.replace("\n","").replace("\r","")) should equal(Seq(
      """
        |package pp
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.T1}.
        |  * Generated automatically.
        |  */
        |case class Person(
        |  NAME: String,
        |  AGE: Int
        |)""".stripMargin.replace("\n","").replace("\r",""),
      """
        |package pp
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.T2}.
        |  * Generated automatically.
        |  */
        |case class Book(
        |  TITLE: String,
        |  AUTHOR: String
        |)""".stripMargin.replace("\n","").replace("\r","")
      )
    )
  }

  test("Generate to directory") {
    connection.createStatement().execute("create table T11(NAME VARCHAR, AGE INT)")
    connection.createStatement().execute("create table T22(TITLE VARCHAR, AUTHOR VARCHAR)")
    ClassGenerator.generateClassesToDirectory(
      "target", url, null, H2Dialect, Seq(
        TableClassMapping(database = "TEST", schema = "PUBLIC", table = "T11", packageName = "com.datawizards.model", className = "Person"),
        TableClassMapping(database = "TEST", schema = "PUBLIC", table = "T22", packageName = "com.datawizards.model", className = "Book")
      )
    )

    deleteDirectory("target/com")

    readFileContent("target/com/datawizards/model/Person.scala").replace("\n","").replace("\r","") should equal(
      """package com.datawizards.model
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.T11}.
        |  * Generated automatically.
        |  */
        |case class Person(
        |  NAME: String,
        |  AGE: Int
        |)""".stripMargin.replace("\n","").replace("\r","")
    )
    readFileContent("target/com/datawizards/model/Book.scala").replace("\n","").replace("\r","") should equal(
      """package com.datawizards.model
        |
        |/**
        |  * Representation of table {@code TEST.PUBLIC.T22}.
        |  * Generated automatically.
        |  */
        |case class Book(
        |  TITLE: String,
        |  AUTHOR: String
        |)""".stripMargin.replace("\n","").replace("\r","")
    )
  }

  private def readFileContent(file: String): String =
    scala.io.Source.fromFile(file).getLines().mkString("\n")

  private def deleteDirectory(dir: String): Unit =
    new File(dir).delete()
}
