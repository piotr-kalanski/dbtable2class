package com.datawizards.dbtable2class.model

case class CustomScalaField (
                            fieldName: String,
                            fieldType: String
                            ) {
  def fieldToString() : String = s"$fieldName : $fieldType"
}
