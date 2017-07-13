package com.datawizards.dbtable2class

import com.datawizards.dbtable2class.dialects.Dialect
import com.datawizards.dbtable2class.model.{ColumnMetadata, TableClassMapping}

import scala.collection.mutable.ListBuffer

object ClassGenerator {

  def generateClasses(
                       dbUrl: String,
                       connectionProperties: java.util.Properties,
                       dialect: Dialect,
                       mappings: Iterable[TableClassMapping]
                     ): Iterable[String] =
    mappings.map(m => generateClass(
      m.className,
      dbUrl,
      connectionProperties,
      m.schema,
      m.table,
      dialect
    ))

  def generateClass(
      className: String,
      dbUrl: String,
      connectionProperties: java.util.Properties,
      schema: String,
      table: String,
      dialect: Dialect
    ): String = {

    def generateClassFields(): String = {
      def tableColumns = dialect.extractTableColumns(dbUrl, connectionProperties, schema, table)
      val buffer = new ListBuffer[String]
      for(c <- tableColumns)
        buffer += generateClassField(c, dialect)
      buffer.mkString(",\n  ")
    }

    def generateClassField(column: ColumnMetadata, dialect: Dialect): String =
      s"${column.columnName}: ${dialect.mapColumnTypeToScalaType(column)}"

    s"""
      |case class $className(
      |  ${generateClassFields()}
      |)""".stripMargin
  }

}
