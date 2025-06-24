package com.example.recetas


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

class MySQLClient(
    private val hostname: String,
    private val username: String,
    private val password: String,
    private val dbname: String
) {

    suspend fun obtenerConexion(): Connection? = withContext(Dispatchers.IO) {
        try {
            Class.forName("com.mysql.jdbc.Driver") // O "com.mysql.cj.jdbc.Driver"
            val url = "jdbc:mysql://bndd8c9fxucrhuvnnpxy-mysql.services.clever-cloud.com:3306/bndd8c9fxucrhuvnnpxy"
            val usuario = "u9ins6pqpla87i9w"
            val password = "an9jWnmzEbucBiymgvUH"

            DriverManager.getConnection(url, usuario, password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun obtenerRecetas(): List<Receta> = withContext(Dispatchers.IO) {
        val recetas = mutableListOf<Receta>()
        val conn = obtenerConexion() ?: return@withContext recetas
        try {
            val stmt = conn.createStatement()
            val rs = stmt.executeQuery("SELECT id, titulo, ingredientes, autor, fecha_mod, eliminado,instrucciones FROM recetas")
            while (rs.next()) {
                val idBytes = rs.getBytes("id")
                val idString = uuidBytesToString(idBytes)
                val titulo = rs.getString("titulo")
                val ingredientes = rs.getString("ingredientes")
                val autor = rs.getString("autor")
                val fechaMod = rs.getTimestamp("fecha_mod").time / 1000
                val eliminado = rs.getBoolean("eliminado")
                val instrucciones = rs.getString("instrucciones")
                recetas.add(Receta(idString, titulo, ingredientes, autor, fechaMod, if (eliminado) 1 else 0,instrucciones))
            }
            rs.close()
            stmt.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }
        recetas
    }

    suspend fun insertarOActualizarReceta(receta: Receta) = withContext(Dispatchers.IO) {
        val conn = obtenerConexion() ?: return@withContext
        try {
            val sql = """
                INSERT INTO recetas (id, titulo, ingredientes, autor, fecha_mod, eliminado,instrucciones)
                VALUES (?, ?, ?, ?, FROM_UNIXTIME(?), ?,?)
                ON DUPLICATE KEY UPDATE
                    titulo = VALUES(titulo),
                    ingredientes = VALUES(ingredientes),
                    autor = VALUES(autor),
                    fecha_mod = VALUES(fecha_mod),
                    eliminado = VALUES(eliminado),
                    instrucciones = VALUES(instrucciones)
            """.trimIndent()

            val pstmt = conn.prepareStatement(sql)
            pstmt.setBytes(1, uuidStringToBytes(receta.id))
            pstmt.setString(2, receta.titulo)
            pstmt.setString(3, receta.ingredientes)
            pstmt.setString(4, receta.autor)
            pstmt.setLong(5, receta.fecha_mod)
            pstmt.setBoolean(6, receta.eliminado == 1)
            pstmt.setString(7, receta.instrucciones)

            pstmt.executeUpdate()
            pstmt.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn.close()
        }
    }

    private fun uuidStringToBytes(uuidStr: String): ByteArray {
        val uuid = UUID.fromString(uuidStr)
        val bb = java.nio.ByteBuffer.allocate(16)
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }

    private fun uuidBytesToString(bytes: ByteArray): String {
        val bb = java.nio.ByteBuffer.wrap(bytes)
        val high = bb.long
        val low = bb.long
        return UUID(high, low).toString()
    }
}
