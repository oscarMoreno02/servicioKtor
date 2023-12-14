package Modelos

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class RespuestaTablero(var message:String,   @Transient var status:Int=0,var tablero:ArrayList<Int>){


}
