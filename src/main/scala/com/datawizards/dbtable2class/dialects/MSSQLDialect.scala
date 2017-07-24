package com.datawizards.dbtable2class.dialects

import com.datawizards.dbtable2class.model.ColumnMetadata

object MSSQLDialect extends Dialect {

  override def mapColumnTypeToScalaType(column: ColumnMetadata): String = {
    if(!typesMapping.contains(column.typeName))
      log.warn("Type not found: " + column.typeName)
    typesMapping.getOrElse(column.typeName, "NOT FOUND")
  }

  private val typesMapping = Map(
    "varchar" -> "String",
    "nvarchar" -> "String",
    "char" -> "String",
    "text" -> "String",
    "nchar" -> "String",
    "int" -> "Int",
    "smallint" -> "Short",
    "tinyint" -> "Byte",
    "bit" -> "Boolean",
    "datetime" -> "java.sql.Timestamp",
    "datetime2" -> "java.sql.Timestamp"
  )

  override protected def driverClassName: String =
    "com.microsoft.sqlserver.jdbc.SQLServerDriver"

  override protected def extractTableColumnsQuery(database: String, schema: String, table: String): String =
    s"""
       |SELECT COLUMN_NAME, DATA_TYPE
       |FROM INFORMATION_SCHEMA.COLUMNS
       |WHERE TABLE_CATALOG = '${database}' AND TABLE_SCHEMA = '${schema}' AND TABLE_NAME = '${table}'
      """.stripMargin

  override protected def columnWithColumnName: String = "COLUMN_NAME"

  override protected def columnWithTypeName: String = "DATA_TYPE"
}
