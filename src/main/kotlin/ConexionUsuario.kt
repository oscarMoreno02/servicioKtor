import java.sql.*

object ConexionUsuario {
    var conexion: Connection? = null


    var sentenciaSQL: Statement? = null

    var registros: ResultSet? = null

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

    fun checkUsuarioRegistrado(usuario: String):Int{
        var contador=1
        val sentencia = "SELECT count(*) AS contador FROM " + Constantes.TablaUsuario+ " WHERE nombre = ? "
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
    fun registro(usuario: String,password: String):Boolean{
        var registro=true
        val sentencia = "INSERT INTO  "+Constantes.TablaUsuario+" (?. ?)"
        try {
            abrirConexion()
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setString(1, usuario)
           pstmt.executeUpdate()


        } catch (sq: SQLException) {
            println(sq)
            registro=false
        } finally {
            cerrarConexion()
        }
        return registro
    }

    fun checkPassword(usuario: String,password:String):Boolean{
        var contador=1
        val sentencia = "SELECT count(*) AS contador FROM " + Constantes.TablaUsuario+ " WHERE nombre = ? and password = ? "
        try {
            abrirConexion()
            val pstmt = conexion!!.prepareStatement(sentencia)
            pstmt.setString(1, usuario)
            pstmt.setString(2, password)
            registros=pstmt.executeQuery()
            while (ConexionPartida.registros!!.next()) {
                contador = ConexionPartida.registros!!.getInt("contador")
            }

        } catch (sq: SQLException) {
            println(sq)

        } finally {
            cerrarConexion()
        }
        return contador==1
    }
}