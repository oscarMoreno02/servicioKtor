package Modelos
import kotlinx.serialization.Serializable

@Serializable
data class Heroe(val nombre:String,val tipo:String,val capacidad:Int)
