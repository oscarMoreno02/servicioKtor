package Modelos

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class RespuestaCasillas(var message:String, @Transient var status:Int=0, var tablero:ArrayList<Casilla>)
