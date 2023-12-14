package Controladores

import ConexionPartida
import ConexionUsuario
import Modelos.*

object PartidaControlador {

    fun obtenerPartidaPendiente(usuario: String,password: String):RespuestaTablero{

        var respuesta=RespuestaTablero("",200,ArrayList<Int>())
        var codigo=0
        var usuarioExiste=ConexionUsuario.checkUsuarioRegistrado(usuario)
        if(usuarioExiste==1){
            if(ConexionUsuario.checkPassword(usuario,password)){


                var contador=ConexionPartida.checkPartidaEnCurso(usuario)
                if(contador==1){

                respuesta.tablero=ConexionPartida.obtenerTableroPendiente(usuario)
                respuesta.message="Tablero pendiente encontrado"

                }else{
                    respuesta=RespuestaTablero("No existe una partida en curso en curso",404,ArrayList<Int>())

                }
            }else{
                respuesta.message="Contraseña incorrecta"
                respuesta.status=404
            }

        }else{
            codigo =404
            respuesta.message="Usuario no registrado"
            respuesta.status=404
        }
        return respuesta

    }

    fun crearPartida(usuario:String,password:String): Respuesta {
        var respuesta=Respuesta("Partida creada exitosamente",200)
        var codigo=0
        var usuarioExiste=ConexionUsuario.checkUsuarioRegistrado(usuario)
        if(usuarioExiste==1){
            if(ConexionUsuario.checkPassword(usuario,password)){


            var contador=ConexionPartida.checkPartidaEnCurso(usuario)
            if(contador==0){

                val heroes= arrayListOf<Heroe>(
                    Heroe("Gandalf", "magia",50),
                    Heroe("Thorin", "fuerza",50),
                    Heroe("Bilbo", "habilidad",50),
                )

                codigo = ConexionPartida.crearPartida(usuario)
                if(codigo==200){
                    var arrayEsfuerzo= arrayOf(5,10,15,20,25,30,35,40,45,50)
                    var tipos= arrayOf("magia","fuerza","habilidad")
                    var id=ConexionPartida.obtenerIdPartida(usuario)
                    if(id!=0){
                        for (i in 1..21) {
                            var indice = (0 .. arrayEsfuerzo.size-1).random()
                            var esfuerzo=arrayEsfuerzo[indice]
                            indice=(0..tipos.size-1).random()
                            var tipo= tipos[indice]
                            codigo=ConexionPartida.insertarCasilla(id,i, esfuerzo  ,tipo)
                        }
                        if(codigo==200){

                            for (heroe in heroes){

                                codigo=ConexionPartida.insertarHeroePartida(id,heroe)
                            }
                        }else{
                            respuesta.message="Error al crear las casillas"
                            respuesta.status=codigo
                        }

                    }else{
                        codigo =404
                        respuesta.message="Error al crear la partida"
                        respuesta.status=codigo
                    }


                }else{
                    codigo =404
                    respuesta.message="Error al crear la partida"
                    respuesta.status=codigo
                }

            }else{
                respuesta=Respuesta("Ya existe una partida en curso",404)

            }
            }else{
                respuesta.message="Contraseña incorrecta"
                respuesta.status=404
            }

        }else{
            codigo =404
            respuesta.message="Usuario no registrado"
            respuesta.status=404
        }
        return respuesta
    }
    fun consultarCasillasAbiertas(usuario: String,password: String):RespuestaCasillas{

        var respuesta=RespuestaCasillas("",200,ArrayList<Casilla>())
        var codigo=0
        var usuarioExiste=ConexionUsuario.checkUsuarioRegistrado(usuario)
        if(usuarioExiste==1){
            if(ConexionUsuario.checkPassword(usuario,password)){


                var contador=ConexionPartida.checkPartidaEnCurso(usuario)
                if(contador==1){

                    respuesta.tablero=ConexionPartida.obtenerCasillasAbiertas(usuario)
                    respuesta.message="Casillas abiertas encontradas"

                }else{
                    respuesta=RespuestaCasillas("No existe una partida en curso en curso",404,ArrayList<Casilla>())

                }
            }else{
                respuesta.message="Contraseña incorrecta"
                respuesta.status=404
            }

        }else{
            codigo =404
            respuesta.message="Usuario no registrado"
            respuesta.status=404
        }
        return respuesta

    }


    fun abrirCasilla(usuario:String,password:String,casilla:Int):RespuestaMapa{

        var respuesta = RespuestaMapa("", mutableMapOf("resultado" to ""))
        respuesta.status=200
        var usuarioExiste=ConexionUsuario.checkUsuarioRegistrado(usuario)
        if(usuarioExiste==1){
            if(ConexionUsuario.checkPassword(usuario,password)){

                var contador=ConexionPartida.checkPartidaEnCurso(usuario)
                if(contador==1){
                    if(ConexionPartida.comprobarNumeroCasilla(usuario,casilla)==1){
                        var casilla=ConexionPartida.obtenerCasilla(usuario,casilla)
                        if(casilla.estado==0){

                        var heroe=ConexionPartida.obtenerHeroePorTipo(usuario,casilla.tipo)
                            respuesta.mensaje="Casilla abierta"
                            respuesta.ronda["tipo_prueba"]=casilla.tipo
                            respuesta.ronda["luchador"]=heroe.nombre
                            respuesta.ronda["esfuerzo_requerido"]=casilla.esfuerzo.toString()
                            respuesta.ronda["capacidad_heroe"]=heroe.capacidad.toString()
                        if(heroe.capacidad>0){
                            var resultado=this.comprobarVictoriaCasilla(heroe,casilla)
                            if(resultado){

                                casilla.estado=1
                                heroe.capacidad=heroe.capacidad-casilla.esfuerzo
                                if(heroe.capacidad<0){
                                    heroe.capacidad=0
                                }
                                respuesta.ronda["resultado"]="Ganada"

                            }else {
                                casilla.estado = -1
                                heroe.capacidad = 0
                                respuesta.ronda["resultado"]="Perdida"

                            }

                            ConexionPartida.actualizarCasilla(casilla)
                            ConexionPartida.actualizarHeroePartida(heroe)
                            ConexionPartida.actualizarRacha(casilla.id_partida,resultado)
                            respuesta.ronda["capacidad_restante"]=heroe.capacidad.toString()
                        }else{
                            casilla.estado = -1
                            heroe.capacidad = 0

                            respuesta.mensaje=="Casilla abierta"
                            ConexionPartida.actualizarCasilla(casilla)
                            ConexionPartida.actualizarHeroePartida(heroe)
                            ConexionPartida.actualizarRacha(casilla.id_partida,false)
                            respuesta.ronda["resultado"]="Perdida"
                        }

                        respuesta.ronda["capacidad_restante"]=heroe.capacidad.toString()
                        var resultado=this.comprobarFinPartida(casilla.id_partida)
                        if(resultado["finalizada"]==true){
                            when{
                                resultado["resultado"]==true->respuesta.ronda["partida"]="victoria"
                                resultado["resultado"]==false->respuesta.ronda["partida"]="derrota"
                            }
                        }else{
                            respuesta.ronda["partida"]="continua"
                        }
                        }else{
                            respuesta.status=400
                            respuesta.mensaje="Numero de casilla introducido anteriormente"
                        }
                    }else{
                        respuesta.status=400
                        respuesta.mensaje="Numero de casilla introducido incorrecto"
                    }

                }else{
                    respuesta.mensaje="No existe una partida en curso"
                    respuesta.status=400
                }
            }else{
                respuesta.mensaje="Contraseña incorrecta"
                respuesta.status=400
            }

        }else{

            respuesta.mensaje="Usuario no registrado"
            respuesta.status=400
        }
        return respuesta
    }

fun comprobarVictoriaCasilla(heroe:HeroePartida,casilla: Casilla):Boolean{
    var resultado=true
    var probabilidad=0

    when{
        heroe.capacidad>casilla.esfuerzo->probabilidad=90
        heroe.capacidad==casilla.esfuerzo->probabilidad=70
        heroe.capacidad<casilla.esfuerzo->probabilidad=50

    }
    var aleatorio=(1..100).random()

    if(aleatorio>probabilidad){
        resultado=false
    }


    return resultado

}
    fun comprobarFinPartida(id:Int):MutableMap<String,Boolean>{
        var partida= mutableMapOf("finalizada" to false)

        if(ConexionPartida.consultarHeroesVivos(id)>0){
            if(ConexionPartida.consultarRacha(id)>=5){
                partida["finalizada"]=true
                partida["resultado"]=false
            }else{
                if(ConexionPartida.consultarRondasGanadas(id)>=10){
                    partida["finalizada"]=true
                    partida["resultado"]=true
                }
            }
        }else{
            partida["finalizada"]=true
            partida["resultado"]=false
        }
        if(partida["finalizada"]==true){
            ConexionPartida.actualizarEstadoPartida(id,partida["resultado"]!!)
        }

        return partida
    }

}
