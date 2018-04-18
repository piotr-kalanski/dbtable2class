package com.datawizards.dbtable2class.model

case class CustomScalaField (
                            fieldName: String,
                            fieldType: String,
                            fieldAnnotations: Seq[String] = Seq.empty
                            ) {
  def fieldToString(indentWidth: Int = 2) : String = fieldAnnotations.map(_ + "\n" + " "*indentWidth).mkString + s"$fieldName : $fieldType"
}
