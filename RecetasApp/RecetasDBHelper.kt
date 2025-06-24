package com.example.recetas

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class RecetasDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "RecetasDbHelper"

        const val DATABASE_NAME = "recetas.db"
        const val DATABASE_VERSION = 1

        const val TABLE_NAME = "recetas"
        const val COL_ID = "id"
        const val COL_TITULO = "titulo"
        const val COL_INGREDIENTES = "ingredientes"
        const val COL_AUTOR = "autor"
        const val COL_FECHA_MOD = "fecha_mod"
        const val COL_ELIMINADO = "eliminado"
        const val COL_INSTRUCCIONES = "instrucciones"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID BLOB PRIMARY KEY,
                $COL_TITULO TEXT NOT NULL,
                $COL_INGREDIENTES TEXT NOT NULL,
                $COL_AUTOR TEXT NOT NULL,
                $COL_FECHA_MOD INTEGER NOT NULL,
                $COL_ELIMINADO INTEGER DEFAULT 0,
                $COL_INSTRUCCIONES TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
        Log.d(TAG, "Base de datos creada con tabla $TABLE_NAME")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
        Log.d(TAG, "Base de datos actualizada de versión $oldVersion a $newVersion")
    }

    fun insertarReceta(receta: Receta): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COL_ID, uuidStringToBytes(receta.id))
                put(COL_TITULO, receta.titulo)
                put(COL_INGREDIENTES, receta.ingredientes)
                put(COL_AUTOR, receta.autor)
                put(COL_FECHA_MOD, receta.fecha_mod)
                put(COL_ELIMINADO, receta.eliminado)
                put(COL_INSTRUCCIONES, receta.instrucciones)
            }
            val result = db.insert(TABLE_NAME, null, values)
            Log.d(TAG, "Insertando receta id=${receta.id}, resultado=$result")
            result != -1L
        } catch (e: Exception) {
            Log.e(TAG, "Error insertando receta id=${receta.id}", e)
            false
        } finally {
            db.close()
        }
    }

    fun actualizarReceta(receta: Receta): Boolean {
        val db = writableDatabase
        return try {
            val sql = """
                UPDATE $TABLE_NAME SET
                $COL_TITULO = ?,
                $COL_INGREDIENTES = ?,
                $COL_AUTOR = ?,
                $COL_FECHA_MOD = ?,
                $COL_ELIMINADO = ?,
                $COL_INSTRUCCIONES = ?
                WHERE $COL_ID = ?
            """.trimIndent()

            val stmt = db.compileStatement(sql)
            stmt.bindString(1, receta.titulo)
            stmt.bindString(2, receta.ingredientes)
            stmt.bindString(3, receta.autor)
            stmt.bindLong(4, receta.fecha_mod)
            stmt.bindLong(5, receta.eliminado.toLong())
            stmt.bindString(6, receta.instrucciones)
            stmt.bindBlob(7, uuidStringToBytes(receta.id))

            val rowsAffected = stmt.executeUpdateDelete()
            Log.d(TAG, "Actualizando receta id=${receta.id}, filas afectadas=$rowsAffected")
            rowsAffected > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error actualizando receta id=${receta.id}", e)
            false
        } finally {
            db.close()
        }
    }
    //llamada para obtener toda la lista de recetas y comparar con la remota
    fun obtenerRecetas(): List<Receta> {
        val recetas = mutableListOf<Receta>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,  // Sin filtro, trae todas las recetas
            null,
            null, null, null
        )
        try {
            with(cursor) {
                while (moveToNext()) {
                    val idBytes = getBlob(getColumnIndexOrThrow(COL_ID))
                    val idString = uuidBytesToString(idBytes)
                    val titulo = getString(getColumnIndexOrThrow(COL_TITULO))
                    val ingredientes = getString(getColumnIndexOrThrow(COL_INGREDIENTES))
                    val autor = getString(getColumnIndexOrThrow(COL_AUTOR))
                    val fechaMod = getLong(getColumnIndexOrThrow(COL_FECHA_MOD))
                    val eliminado = getInt(getColumnIndexOrThrow(COL_ELIMINADO))
                    val instrucciones = getString(getColumnIndexOrThrow(COL_INSTRUCCIONES))
                    recetas.add(Receta(idString, titulo, ingredientes, autor, fechaMod, eliminado,instrucciones))
                }
            }
        } finally {
            cursor.close()
            db.close()
        }
        return recetas
    }

    // La llamamos para mostrar en pantalla
    fun obtenerRecetasFiltradas(): List<Receta> {
        val recetas = mutableListOf<Receta>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COL_ELIMINADO = ?",
            arrayOf("0"),
            null, null, null
        )
        try {
            with(cursor) {
                while (moveToNext()) {
                    val idBytes = getBlob(getColumnIndexOrThrow(COL_ID))
                    val idString = uuidBytesToString(idBytes)
                    val titulo = getString(getColumnIndexOrThrow(COL_TITULO))
                    val ingredientes = getString(getColumnIndexOrThrow(COL_INGREDIENTES))
                    val autor = getString(getColumnIndexOrThrow(COL_AUTOR))
                    val fechaMod = getLong(getColumnIndexOrThrow(COL_FECHA_MOD))
                    val eliminado = getInt(getColumnIndexOrThrow(COL_ELIMINADO))
                    val instrucciones = getString(getColumnIndexOrThrow(COL_INSTRUCCIONES))
                    recetas.add(Receta(idString, titulo, ingredientes, autor, fechaMod, eliminado,instrucciones))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error leyendo recetas", e)
        } finally {
            cursor.close()
            db.close()
        }
        return recetas
    }
    fun eliminarReceta(id: String): Boolean {
        val db = writableDatabase
        return try {
            val sql = "UPDATE $TABLE_NAME SET $COL_ELIMINADO = 1, $COL_FECHA_MOD = ? WHERE $COL_ID = ?"
            val stmt = db.compileStatement(sql)
            stmt.bindLong(1, System.currentTimeMillis() / 1000)
            stmt.bindBlob(2, uuidStringToBytes(id))
            val filasAfectadas = stmt.executeUpdateDelete()
            filasAfectadas > 0
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    // Conversión UUID String <-> ByteArray para almacenar y consultar
    private fun uuidStringToBytes(uuidStr: String): ByteArray {
        val uuid = java.util.UUID.fromString(uuidStr)
        val bb = java.nio.ByteBuffer.allocate(16)
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }

    private fun uuidBytesToString(bytes: ByteArray): String {
        val bb = java.nio.ByteBuffer.wrap(bytes)
        val high = bb.long
        val low = bb.long
        return java.util.UUID(high, low).toString()
    }
}
