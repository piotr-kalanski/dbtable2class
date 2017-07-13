package com.datawizards.dbtable2class

import java.sql.DriverManager

import com.datawizards.dbtable2class.dialects.H2Dialect
import org.scalatest._

class ClassGeneratorTest extends FunSuite with Matchers {

  private val url = "jdbc:h2:mem:test"
  private val connection = DriverManager.getConnection(url, "", "")

  test("generate people") {
    connection.createStatement().execute("create table PEOPLE(NAME VARCHAR, AGE INT)")
    val classDefinition = ClassGenerator.generateClass("Person", url, null, "PUBLIC", "PEOPLE", H2Dialect)
    classDefinition should equal(
      """
        |case class Person(
        |  NAME: String,
        |  AGE: Int
        |)""".stripMargin
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
    val classDefinition = ClassGenerator.generateClass("AllTypes", url, null, "PUBLIC", "ALL_TYPES", H2Dialect)
    classDefinition should equal(
      """
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
        |)""".stripMargin
    )
  }

}
