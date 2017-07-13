package com.datawizards.dbtable2class.dialects

import com.datawizards.dbtable2class.model.ColumnMetadata

object H2Dialect extends Dialect {

  override def mapColumnTypeToScalaType(column: ColumnMetadata): String =
    typesMapping(column.typeName)

  private val typesMapping = Map(
    "VARCHAR" -> "String",
    "INTEGER" -> "Int",
    "SMALLINT" -> "Short",
    "BIGINT" -> "Long",
    "BYTE" -> "Byte",
    "TINYINT" -> "Byte",
    "BOOLEAN" -> "Boolean",
    "REAL" -> "Float",
    "DOUBLE" -> "Double",
    "DATE" -> "java.sql.Date",
    "TIMESTAMP" -> "java.sql.Timestamp"
  )

  override protected def driverClassName: String =
    "org.h2.Driver"

  override protected def extractTableColumnsQuery(schema: String, table: String): String =
    s"""
       |SELECT COLUMN_NAME, TYPE_NAME
       |FROM INFORMATION_SCHEMA.COLUMNS
       |WHERE TABLE_SCHEMA = '$schema' AND TABLE_NAME = '$table'
      """.stripMargin

  override protected def columnWithColumnName: String = "COLUMN_NAME"

  override protected def columnWithTypeName: String = "TYPE_NAME"
}
