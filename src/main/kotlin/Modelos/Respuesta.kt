package Modelos

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class Respuesta(var message:String, @Transient var status:Int=0)
