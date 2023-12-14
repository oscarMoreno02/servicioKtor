package Modelos

import com.mysql.cj.x.protobuf.MysqlxExpr
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class RespuestaMapa(var mensaje:String="", var ronda: MutableMap<String, String>,   @Transient  var status :Int=0)
