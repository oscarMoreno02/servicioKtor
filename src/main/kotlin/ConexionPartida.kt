import Modelos.Casilla
import java.sql.*
import Modelos.Heroe
import Modelos.HeroePartida
import kotlin.collections.ArrayList

object ConexionPartida {

    var conexion: Connection? = null


    var sentenciaSQL: Statement? = null

    var registros: ResultSet? = null

    // ----------------------------------------------------------
    fun abrirConexion(): Int {
        var cod = 0
        try {

            val controlador = "com.mysql.cj.jdbc.Driver"
            val URL_BD = "jdbc:mysql://" + Constantes.servidor+":"+Constantes.puerto+"/" + Constantes.bbdd

            Class.forName(controlador)

            conexion = DriverManager.getConnection(URL_BD, Constantes.usuario, Constantes.passwd)
            sentenciaSQL = ConexionPartida.conexion!!.createStatement()
            println("Conexion realizada con éxito")
        } catch (e: Exception) {
            System.err.println("Exception: " + e.message)
            cod = -1
        }
        return cod
    }


    fun cerrarConexion(): Int {
        var cod = 0
        try {
            conexion!!.close()
            println("Desconectado de la Base de Datos")
        } catch (ex: SQLException) {
            cod = -1
        }
        return cod
    }



    fun checkPartidaEnCurso(usuario: String):Int{
    var contador=1
    val sentencia = "SELECT count(*) AS contador FROM " + Constantes.TablaPartida + " WHERE usuario = ? AND estado = 0"
    try {
        abrirConexion()
        val pstmt = conexion!!.prepareStatement(sentencia)
        pstmt.setString(1, usuario)
        registros=pstmt.executeQuery()
        while (ConexionPartida.registros!!.next()) {
            contador = ConexionPartida.registros!!.getInt("contador")
        }

    } catch (sq: SQLException) {
        println(sq)

    } finally {
        cerrarConexion()
    }
    return contador
    }
fun crearPartida(nombre: String): Int {
    var cod = 200
    val sentencia = "INSERT INTO " + Constantes.TablaPartida + " (usuario)  VALUES (?)"
    try {
        abrirConexion()
        val pstmt = conexion!!.prepareStatement(sentencia)
        pstmt.setString(1, nombre)
        pstmt.executeUpdate()
    } catch (sq: SQLException) {
        println(sq)
        cod = sq.errorCode
    } finally {
        cerrarConexion()
    }
    return cod
}

    fun insertarCasilla(id_partida : Int,numero:Int,esfuerzo:Int,tipo:String): Int {
        var cod = 200
        val sentencia = "INSERT INTO " + Constantes.TablaCasilla + " (id_partida,numero, esfuerzo, tipo)  VALUES (?, ?, ?,?)"
        try {
            abrirConexion()
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, id_partida)
            pstmt.setInt(2, numero)

            pstmt.setInt(3, esfuerzo)
            pstmt.setString(4, tipo)
            pstmt.executeUpdate()
        } catch (sq: SQLException) {
            println(sq)
            cod = sq.errorCode
        } finally {
            cerrarConexion()
        }
        return cod
    }


    fun obtenerIdPartida(usuario : String): Int {

        var id=0
        val sentencia = "SELECT id FROM " + Constantes.TablaPartida + " WHERE usuario = ? AND estado = 0"
        try {
            abrirConexion()
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setString(1, usuario)
            registros=pstmt.executeQuery()
            while (ConexionPartida.registros!!.next()) {
                id = ConexionPartida.registros!!.getInt("id")
            }

        } catch (sq: SQLException) {
            println(sq)
        } finally {
            cerrarConexion()
        }
        return id
    }
    fun insertarHeroePartida(id:Int, heroe: Heroe):Int{
        var cod = 200

        val sentencia = "INSERT INTO " + Constantes.TablaHeroePartida +" VALUES (?, ?, ?, ? )"
        try {
            abrirConexion()
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, id)
            pstmt.setString(2, heroe.nombre)
            pstmt.setInt(3, heroe.capacidad)
            pstmt.setString(4, heroe.tipo)
            pstmt.executeUpdate()
        } catch (sq: SQLException) {
            cod=500
            println(sq)
        } finally {
            cerrarConexion()


        }
        return cod
    }
    fun obtenerTableroPendiente(usuario: String): ArrayList<Int> {
        val tablero: ArrayList<Int> = ArrayList()
        try {
            abrirConexion()
            val sentencia = "SELECT numero FROM " +Constantes.TablaCasilla + " WHERE estado = 0 AND id_partida IN (SELECT id FROM partida WHERE usuario = ? AND estado = 0 )"
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setString(1, usuario)
            registros = pstmt.executeQuery()
            while (registros!!.next()) {
                tablero.add(registros!!.getInt("numero"))
            }
        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }
        return tablero
    }
    fun obtenerCasillasAbiertas(usuario: String): ArrayList<Casilla> {
        val tablero: ArrayList<Casilla> = ArrayList()
        try {
            abrirConexion()
            val sentencia = "SELECT * FROM " +Constantes.TablaCasilla + " WHERE estado <> 0 AND id_partida IN (SELECT id FROM partida WHERE usuario = ? AND estado = 0 )"
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setString(1, usuario)
            registros = pstmt.executeQuery()
            while (registros!!.next()) {
                tablero.add(Casilla(
                    registros!!.getInt("numero"),
                    registros!!.getInt("id_partida"),
                    registros!!.getString("tipo"),
                    registros!!.getInt("esfuerzo"),
                    registros!!.getInt("estado"),
                )
                )
            }
        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }
        return tablero
    }
    fun obtenerCasilla(usuario: String,numero: Int): Casilla {
        var casilla: Casilla?=null
        try {
            abrirConexion()
            val sentencia = "SELECT * FROM " +Constantes.TablaCasilla + " WHERE numero = ? AND id_partida IN (SELECT id FROM partida WHERE usuario = ? AND estado = 0 )"
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, numero)
            pstmt.setString(2, usuario)
            registros = pstmt.executeQuery()
            while (registros!!.next()) {
                casilla=(Casilla(
                    registros!!.getInt("numero"),
                    registros!!.getInt("id_partida"),
                    registros!!.getString("tipo"),
                    registros!!.getInt("esfuerzo"),
                    registros!!.getInt("estado"),
                )
                )
            }
        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }
        return casilla!!
    }
fun comprobarNumeroCasilla(usuario:String,numero: Int):Int{
    var contador=0
    try {
        abrirConexion()
        val sentencia = "SELECT COUNT(*) AS CONTADOR FROM " +Constantes.TablaCasilla + " WHERE numero = ? AND id_partida IN (SELECT id FROM partida WHERE usuario = ? AND estado = 0 )"
        val pstmt = conexion!!.prepareStatement(sentencia)
        pstmt.setInt(1, numero)
        pstmt.setString(2, usuario)
        registros = pstmt.executeQuery()
        while (registros!!.next()) {
            contador= registros!!.getInt("contador")

        }
    } catch (ex: SQLException) {
        println(ex)
    } finally {
        cerrarConexion()
    }
    return contador
}
    fun actualizarCasilla(casilla: Casilla) {
        var correcto=true
        try {
            abrirConexion()
            val sentencia = "UPDATE " +Constantes.TablaCasilla + " SET estado = ? WHERE numero = ? AND id_partida = ? "
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, casilla.estado)
            pstmt.setInt(2, casilla.numero)
            pstmt.setInt(3, casilla.id_partida)
            pstmt.executeUpdate()

        } catch (ex: SQLException) {
            println(ex)
            correcto=false
        } finally {
            cerrarConexion()
        }
    }
    fun obtenerHeroePorTipo(usuario: String,tipo:String):HeroePartida{
        var heroe: HeroePartida?=null
        try {
            abrirConexion()
            val sentencia = "SELECT * FROM " +Constantes.TablaHeroePartida + " WHERE tipo = ? AND id_partida IN (SELECT id FROM partida WHERE usuario = ? AND estado = 0 )"
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setString(1, tipo)
            pstmt.setString(2, usuario)
            registros = pstmt.executeQuery()
            while (registros!!.next()) {
                heroe=(HeroePartida(
                    registros!!.getString("nombre"),
                    registros!!.getInt("id_partida"),
                    registros!!.getInt("capacidad"),
                    registros!!.getString("tipo")
                ))
            }
        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }
        return heroe!!
    }
    fun actualizarHeroePartida(heroe:HeroePartida){
        try {
            abrirConexion()
            val sentencia = "UPDATE " +Constantes.TablaHeroePartida + " SET capacidad = ? WHERE nombre = ? AND id_partida = ? "
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, heroe.capacidad)
            pstmt.setString(2, heroe.nombre)
            pstmt.setInt(3, heroe.id_partida)
            pstmt.executeUpdate()

        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }

    }
    fun consultarRondasGanadas(id:Int):Int{
            var contador=0
        try {
            abrirConexion()
            val sentencia = "SELECT COUNT(*) AS contador FROM "+Constantes.TablaCasilla+" WHERE id_partida = ? AND estado = 1"
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, id)
            registros = pstmt.executeQuery()
            while (registros!!.next()) {
                contador= registros!!.getInt("contador")

            }
        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }
        return contador
    }
    fun consultarRacha(id:Int):Int{
        var contador=0
        try {
            abrirConexion()
            val sentencia = "SELECT racha_derrotas FROM "+Constantes.TablaPartida+" WHERE id = ? "
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, id)
            registros = pstmt.executeQuery()
            while (registros!!.next()) {
                contador= registros!!.getInt("racha_derrotas")
            }
        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }
        return contador
    }
    fun actualizarRacha(id:Int,resultado:Boolean){
        var sentencia = "UPDATE "+Constantes.TablaPartida+" SET racha_derrotas = 0 where id = ? "
       if(resultado==false){
           sentencia = "UPDATE "+Constantes.TablaPartida+" SET racha_derrotas = racha_derrotas + 1 where id = ? "
       }
        try {
            abrirConexion()
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, id)
            pstmt.executeUpdate()


        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }

    }
    fun actualizarEstadoPartida(id:Int,resultado: Boolean) {
        var res = 1
        if (resultado == false) {
            res = -1
        }
        try {
            var sentencia = "UPDATE " + Constantes.TablaPartida + " SET estado = ? where id = ? "
            abrirConexion()
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, res)
            pstmt.setInt(2, id)
            pstmt.executeUpdate()


        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }
    }
    fun consultarHeroesVivos(id:Int):Int{
        var contador=0
        try {
            abrirConexion()
            val sentencia = "SELECT COUNT(*) AS contador FROM "+Constantes.TablaHeroePartida+" WHERE id_partida = ? AND capacidad > 0 "
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setInt(1, id)
            registros = pstmt.executeQuery()
            while (registros!!.next()) {
                contador= registros!!.getInt("contador")

            }
        } catch (ex: SQLException) {
            println(ex)
        } finally {
            cerrarConexion()
        }
        return contador
    }

}