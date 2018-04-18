package com.datawizards.dbtable2class

import com.datawizards.dbtable2class.dialects.MSSQLDialect
import com.datawizards.dbtable2class.generator.FieldGenerator
import com.datawizards.dbtable2class.model.ColumnMetadata
import org.scalatest.{FunSuite, Matchers}

class MSSQLDialectTest extends FunSuite with Matchers {
  test("Data types") {
    val fieldGenerator = new FieldGenerator
    fieldGenerator.generateClassField(ColumnMetadata("colName", "numeric"), MSSQLDialect) should equal("colName: BigDecimal")
    fieldGenerator.generateClassField(ColumnMetadata("colName2", "decimal"), MSSQLDialect) should equal("colName2: BigDecimal")
    fieldGenerator.generateClassField(ColumnMetadata("colName3", "smallmoney"), MSSQLDialect) should equal("colName3: BigDecimal")
  }
}
