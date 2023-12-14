package Modelos

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(val nombre:String,val password:String)
