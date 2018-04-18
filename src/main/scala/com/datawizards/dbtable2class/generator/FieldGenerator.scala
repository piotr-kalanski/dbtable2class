package com.datawizards.dbtable2class.generator

import com.datawizards.dbtable2class.dialects.Dialect
import com.datawizards.dbtable2class.model.ColumnMetadata

class FieldGenerator {
  def generateClassField(column: ColumnMetadata, dialect: Dialect): String = {
    val caseClassField = columnNameToField(column.columnName)
    s"$caseClassField: ${dialect.mapColumnTypeToScalaType(column)}"
  }

  private def columnNameToField(columnName: String) :String ={
    if(reservedKeywords.contains(columnName))
      s"""`$columnName`"""
    else if(columnName.contains("-"))
      s"""`$columnName`"""
    else if(columnName.endsWith("_"))
      s"""`$columnName`"""
    else
      columnName
  }

  private def reservedKeywords = Seq(
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
}
