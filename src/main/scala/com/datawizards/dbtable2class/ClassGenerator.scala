package com.datawizards.dbtable2class

import java.io.{File, PrintWriter}

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

  def generateClassesToDirectory(
                                  outputPath: String,
                                  dbUrl: String,
                                  connectionProperties: java.util.Properties,
                                  dialect: Dialect,
                                  mappings: Iterable[TableClassMapping]
                                ): Unit = {
    val definitions = generateClasses(dbUrl, connectionProperties, dialect, mappings)

    (mappings zip definitions)
      .foreach{case (mapping, definiton) =>
        val directory = outputPath + "/" + mapping.packageName.replaceAll("\\.", "/") + "/"
        new File(directory).mkdirs()
        val file = directory + mapping.className + ".scala"
        val pw = new PrintWriter(file)
        pw.write("package " + mapping.packageName + "\n")
        pw.write(definiton)
        pw.close()
    }
  }

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
