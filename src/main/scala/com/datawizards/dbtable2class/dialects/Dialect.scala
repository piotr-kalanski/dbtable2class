package com.datawizards.dbtable2class.dialects

import java.sql.{DriverManager, ResultSet}

import com.datawizards.dbtable2class.model.ColumnMetadata

import scala.collection.mutable.ListBuffer

trait Dialect {
  def mapColumnTypeToScalaType(column: ColumnMetadata): String

  def extractTableColumns(dbUrl: String, connectionProperties: java.util.Properties, schema: String, table: String): Iterable[ColumnMetadata] = {
    Class.forName(driverClassName)
    val connection = DriverManager.getConnection(dbUrl, connectionProperties)
    val query = extractTableColumnsQuery(schema, table)
    val rs = connection.createStatement().executeQuery(query)
    val buffer = new ListBuffer[ColumnMetadata]
    while(rs.next()) {
      buffer += extractColumnMetadata(rs)
    }
    connection.close()
    buffer.toList
  }

  protected def extractColumnMetadata(rs: ResultSet): ColumnMetadata =
    ColumnMetadata(
      columnName = rs.getString(columnWithColumnName),
      typeName = rs.getString(columnWithTypeName)
    )

  protected def driverClassName: String
  protected def extractTableColumnsQuery(schema: String, table: String): String
  protected def columnWithColumnName: String
  protected def columnWithTypeName: String

}
