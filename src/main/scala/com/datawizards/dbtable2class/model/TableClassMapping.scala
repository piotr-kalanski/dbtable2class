package com.datawizards.dbtable2class.model

case class TableClassMapping(database: String, schema: String, table: String, packageName: String, className: String)
