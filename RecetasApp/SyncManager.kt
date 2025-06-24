package com.example.recetas

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CambiosSync(
    val paraActualizarEnLocal: List<Receta>,
    val paraInsertarEnLocal: List<Receta>,
    val paraActualizarEnRemoto: List<Receta>,
    val paraInsertarEnRemoto: List<Receta>
)

object SyncManager {

    suspend fun sincronizarListas(local: List<Receta>, remoto: List<Receta>): CambiosSync {
        val actualizanLocal = mutableListOf<Receta>()
        val insertanLocal = mutableListOf<Receta>()
        val actualizanRemoto = mutableListOf<Receta>()
        val insertanRemoto = mutableListOf<Receta>()

        val mapLocal = local.associateBy { it.id }
        val mapRemoto = remoto.associateBy { it.id }

        val todosIds = mapLocal.keys.union(mapRemoto.keys)

        for (id in todosIds) {
            val recLocal = mapLocal[id]
            val recRemoto = mapRemoto[id]

            when {
                recLocal != null && recRemoto != null -> {
                    if (recLocal.fecha_mod > recRemoto.fecha_mod) {
                        actualizanRemoto.add(recLocal)
                    } else if (recRemoto.fecha_mod > recLocal.fecha_mod) {
                        actualizanLocal.add(recRemoto)
                    }
                }
                recLocal != null && recRemoto == null -> {
                    if (recLocal.eliminado == 0) {
                        insertanRemoto.add(recLocal)
                    }
                }
                recLocal == null && recRemoto != null -> {
                    if (recRemoto.eliminado == 0) {
                        insertanLocal.add(recRemoto)
                    }
                }
            }
        }

        return CambiosSync(
            paraActualizarEnLocal = actualizanLocal,
            paraInsertarEnLocal = insertanLocal,
            paraActualizarEnRemoto = actualizanRemoto,
            paraInsertarEnRemoto = insertanRemoto
        )
    }

    suspend fun aplicarCambiosEnSQLite(
        dbHelper: RecetasDbHelper,
        actualizaciones: List<Receta>,
        inserciones: List<Receta>
    ) = withContext(Dispatchers.IO) {
        actualizaciones.forEach { dbHelper.actualizarReceta(it) }
        inserciones.forEach { dbHelper.insertarReceta(it) }
    }


    suspend fun sincronizar(
        dbHelper: RecetasDbHelper,
        mysqlClient: MySQLClient
    ) {
        val local = dbHelper.obtenerRecetas()
        val remoto = mysqlClient.obtenerRecetas()

        val cambios = sincronizarListas(local, remoto)

        aplicarCambiosEnSQLite(dbHelper, cambios.paraActualizarEnLocal, cambios.paraInsertarEnLocal)

        for (receta in cambios.paraActualizarEnRemoto) {
            try {
                mysqlClient.insertarOActualizarReceta(receta)
                Log.d("SyncManager", "Actualizada receta remota: ${receta.id}")
            } catch (e: Exception) {
                Log.e("SyncManager", "Error al actualizar receta remota id=${receta.id}", e)
            }
        }
        for (receta in cambios.paraInsertarEnRemoto) {
            try {
                mysqlClient.insertarOActualizarReceta(receta)
                Log.d("SyncManager", "Insertada receta remota: ${receta.id}")
            } catch (e: Exception) {
                Log.e("SyncManager", "Error al insertar receta remota id=${receta.id}", e)
            }
        }
    }

}