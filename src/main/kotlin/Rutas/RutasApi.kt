package Rutas

import Controladores.PartidaControlador
import Modelos.Heroe
import Modelos.Respuesta
import Modelos.Usuario
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private val heroes= arrayListOf<Heroe>(
    Heroe("Gandalf", "magia",50),
    Heroe("Thorin", "fuerza",50),
    Heroe("Bilbo", "habilidad",50),
)

fun Route.userRouting(){
    route("/listado") {
        get {
            if (heroes.isNotEmpty()) {
                call.respond(heroes)
            } else {
                call.respondText("No hay usuarios", status = HttpStatusCode.OK)
            }
        }
        get("{dni?}") {
            val dni = call.parameters["dni"] ?: return@get call.respondText(
                "id vacío en la url",
                status = HttpStatusCode.BadRequest
            )

            try {
                val pruebaParam = dni.toString()
            } catch (e: Exception) {
                call.response.status(HttpStatusCode.BadRequest)
                return@get call.respond(Respuesta("Parámetro ${dni} no válido", HttpStatusCode.BadRequest.value))
            }
            val heroe = heroes.find { it.nombre == dni }
            if (heroe == null) {
                call.response.status(HttpStatusCode.NotFound)
                return@get call.respond(Respuesta("Usuario ${dni} no encontrado", HttpStatusCode.NotFound.value))
                //call.respondText("Usuario ${id} no encontrado", status = HttpStatusCode.NotFound)
            }
            call.respond(heroe)
        }
    }
    route("/login") {
        post{
            val us = call.receive<Heroe>()
            val usuario = heroes.find { it.nombre == us.nombre && it.tipo == us.tipo }
            if (usuario == null) {
                call.response.status(HttpStatusCode.NotFound)
                return@post call.respond(Respuesta("Usuario ${us.nombre} login incorrecto", HttpStatusCode.NotFound.value))
                //call.respondText("Usuario ${id} no encontrado", status = HttpStatusCode.NotFound)
            }
            call.respond(usuario)
        }
    }
    route("/registrar") {
        post{
            val us = call.receive<Heroe>()
            heroes.add(us)
            call.respondText("Usuario creado",status = HttpStatusCode.Created)
        }
    }
    route("/borrar") {
        delete("{nombre?}") {
            val dni = call.parameters["dni"] ?: return@delete call.respondText("id vacío en la url", status = HttpStatusCode.BadRequest)
            if (heroes.removeIf { it.nombre == dni }){
                call.respondText("Usuario eliminado",status = HttpStatusCode.Accepted)
            }
            else {
                call.respondText("No encontrado",status = HttpStatusCode.NotFound)
            }
        }
    }
    route("/partida") {
        post("") {
            val usuario = call.receive<Usuario>()
            val respuesta=PartidaControlador.crearPartida(usuario.nombre,usuario.password)
            if (respuesta.status == 200){
                respuesta.status=HttpStatusCode.Created.value
                call.respond(respuesta)
            }
            else {
                call.response.status(HttpStatusCode.Conflict)
                call.respond(respuesta)
            }
        }
        get("") {
            val usuario = call.receive<Usuario>()
            val respuesta=PartidaControlador.obtenerPartidaPendiente(usuario.nombre,usuario.password)
            if (respuesta.status == 200){
                respuesta.status=HttpStatusCode.OK.value
                call.respond(respuesta)
            }
            else {
                respuesta.status=HttpStatusCode.BadRequest.value
                call.respond(respuesta.message)
            }

        }
        route("casillas") {
            get(""){

            val usuario = call.receive<Usuario>()
            val respuesta=PartidaControlador.consultarCasillasAbiertas(usuario.nombre,usuario.password)
            if (respuesta.status == 200){
                respuesta.status=HttpStatusCode.OK.value

                call.response.status(HttpStatusCode.OK)
                call.respond(respuesta)
            }
            else {
                call.response.status(HttpStatusCode.BadRequest)
                respuesta.status=HttpStatusCode.BadRequest.value
                call.respond(respuesta.message)
            }
            }
        put("{numero}") {
            val usuario = call.receive<Usuario>()
            var parametro=call.parameters["numero"] ?: return@put call.respondText("No ha introducido una casilla", status = HttpStatusCode.BadRequest)
            var casilla=0
            try {
                casilla = parametro.toInt()
            } catch (e: NumberFormatException) {
                call.response.status(HttpStatusCode.BadRequest)
                var respuesta=Respuesta("No ha introducido una casilla con un formato correcto")
                return@put call.respond(respuesta)
            }
            val respuesta=PartidaControlador.abrirCasilla(usuario.nombre,usuario.password,casilla)
            if (respuesta.status == 200){
                respuesta.status=HttpStatusCode.OK.value

                call.response.status(HttpStatusCode.OK)
                call.respond(respuesta)
            }
            else {
                call.response.status(HttpStatusCode.BadRequest)
                respuesta.status=HttpStatusCode.BadRequest.value
                var respuestaError=Respuesta(respuesta.mensaje)
                call.respond(respuestaError)
            }
        }
        }


    }

}