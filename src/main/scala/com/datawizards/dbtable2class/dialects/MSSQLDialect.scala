package com.datawizards.dbtable2class.dialects

import com.datawizards.dbtable2class.model.ColumnMetadata

object MSSQLDialect extends Dialect {

  override def mapColumnTypeToScalaType(column: ColumnMetadata): String =
    typesMapping(column.typeName)

  private val typesMapping = Map(
    "varchar" -> "String",
    "nvarchar" -> "String",
    "char" -> "String",
    "text" -> "String",
    "int" -> "Int",
    "smallint" -> "Short",
    "tinyint" -> "Byte",
    "bit" -> "Boolean",
    "datetime" -> "java.sql.Timestamp"
  )

  override protected def driverClassName: String =
    "com.microsoft.sqlserver.jdbc.SQLServerDriver"

  override protected def extractTableColumnsQuery(schema: String, table: String): String =
    s"""
       |SELECT COLUMN_NAME, DATA_TYPE
       |FROM INFORMATION_SCHEMA.COLUMNS
       |WHERE TABLE_SCHEMA = '$schema' AND TABLE_NAME = '$table'
      """.stripMargin

  override protected def columnWithColumnName: String = "COLUMN_NAME"

  override protected def columnWithTypeName: String = "DATA_TYPE"
}
