package Modelos

import kotlinx.serialization.Serializable

@Serializable
data class Casilla(val numero:Int, var id_partida:Int, val tipo:String, val esfuerzo:Int, var estado:Int)
