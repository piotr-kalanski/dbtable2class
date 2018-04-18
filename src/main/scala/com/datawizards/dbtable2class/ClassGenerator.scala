package com.datawizards.dbtable2class

import java.io.{File, PrintWriter}

import com.datawizards.dbtable2class.dialects.Dialect
import com.datawizards.dbtable2class.generator.FieldGenerator
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

    val fieldGenerator = new FieldGenerator

    def generateClassFields(): String = {
      def tableColumns = dialect.extractTableColumns(dbUrl, connectionProperties, mapping.database, mapping.schema, mapping.table)
      val buffer = new ListBuffer[String]

      for(c <- tableColumns)
        buffer += fieldGenerator.generateClassField(c, dialect)

      for (f <- mapping.customScalaFields)
        buffer += f.fieldToString()

      buffer.mkString(",\n  ")
    }

    val tableLocation = Seq(mapping.database, mapping.schema, mapping.table).filter(n => n != null && n != "").mkString(".")

    s"""package ${mapping.packageName}
      |${generateImports(mapping)}
      |/**
      |  * Representation of table {@code $tableLocation}.
      |  * Generated automatically.
      |  */
      |${generateAnnotations(mapping)}case class ${mapping.className}(
      |  ${generateClassFields()}
      |)""".stripMargin
  }

  private def generateImports(mapping: TableClassMapping): String =
    "\n" + mapping.imports.map(imp => "import " + imp + "\n").mkString("")

  private def generateAnnotations(mapping: TableClassMapping): String =
    mapping.annotations.map(a => a + "\n").mkString("")

}
