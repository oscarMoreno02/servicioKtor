package com.example.plugins

import Modelos.Respuesta
import Rutas.userRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            //call.respondText("Hello World!")
            call.response.status(HttpStatusCode.OK)
            call.respond(Respuesta("Servidor funcionando", HttpStatusCode.OK.value))
        }

        userRouting()
    }

}
