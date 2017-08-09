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
      dbUrl,
      connectionProperties,
      dialect,
      m
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
        pw.write(definiton)
        pw.close()
    }
  }

  def generateClass(
      dbUrl: String,
      connectionProperties: java.util.Properties,
      dialect: Dialect,
      mapping: TableClassMapping
    ): String = {

    def generateClassFields(): String = {
      def tableColumns = dialect.extractTableColumns(dbUrl, connectionProperties, mapping.database, mapping.schema, mapping.table)
      val buffer = new ListBuffer[String]
      for(c <- tableColumns)
        buffer += generateClassField(c, dialect)
      buffer.mkString(",\n  ")
    }

    def generateClassField(column: ColumnMetadata, dialect: Dialect): String = {
      val caseClassField = columnNameToField(column.columnName)
      s"$caseClassField: ${dialect.mapColumnTypeToScalaType(column)}"
    }

    def reservedKeywords = Seq(
      "case",
      "catch",
      "class",
      "def",
      "do",
      "else",
      "extends",
      "false",
      "final",
      "for",
      "if",
      "match",
      "new",
      "null",
      "package",
      "print",
      "printf",
      "println",
      "throw",
      "to",
      "trait",
      "true",
      "try",
      "type",
      "until",
      "val",
      "var",
      "while",
      "with"
    )

    def columnNameToField(columnName: String) :String ={
      if(reservedKeywords.contains(columnName))
        s"""`$columnName`"""
      else if(columnName.contains("-"))
        s"""`$columnName`"""
      else if(columnName.endsWith("_"))
        s"""`$columnName`"""
      else
        columnName
    }

    val tableLocation = Seq(mapping.database, mapping.schema, mapping.table).filter(n => n != null && n != "").mkString(".")

    s"""package ${mapping.packageName}
      |
      |/**
      |  * Representation of table {@code $tableLocation}.
      |  * Generated automatically.
      |  */
      |case class ${mapping.className}(
      |  ${generateClassFields()}
      |)""".stripMargin
  }

}
