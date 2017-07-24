package com.datawizards.dbtable2class.dialects

import java.sql.{DriverManager, ResultSet}
import org.apache.log4j.Logger
import com.datawizards.dbtable2class.model.ColumnMetadata
import scala.collection.mutable.ListBuffer

trait Dialect {
  protected val log: Logger = Logger.getLogger(getClass.getName)

  def mapColumnTypeToScalaType(column: ColumnMetadata): String

  def extractTableColumns(dbUrl: String, connectionProperties: java.util.Properties, database: String, schema: String, table: String): Iterable[ColumnMetadata] = {
    Class.forName(driverClassName)
    val connection = DriverManager.getConnection(dbUrl, connectionProperties)
    val query = extractTableColumnsQuery(database, schema, table)
    val rs = connection.createStatement().executeQuery(query)
    val buffer = new ListBuffer[ColumnMetadata]
    while(rs.next()) {
      buffer += extractColumnMetadata(rs)
    }
    connection.close()

    val extractedColumns = buffer.toList
    if(extractedColumns.size == 0){
      throw new RuntimeException(s"No columns found for specified table ${database}.${schema}.${table}. Please check if table is correct.")
    }
    extractedColumns
  }

  protected def extractColumnMetadata(rs: ResultSet): ColumnMetadata =
    ColumnMetadata(
      columnName = rs.getString(columnWithColumnName),
      typeName = rs.getString(columnWithTypeName)
    )

  protected def driverClassName: String
  protected def extractTableColumnsQuery(database: String, schema: String, table: String): String
  protected def columnWithColumnName: String
  protected def columnWithTypeName: String

}
