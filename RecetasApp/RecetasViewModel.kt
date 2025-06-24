package com.example.recetas


import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class RecetasViewModel(application: Application) : AndroidViewModel(application) {

    var sincronizando = mutableStateOf(false)
        private set

    var mensajeSync = mutableStateOf<String?>(null)
        private set

    private val _configuraciones = mutableStateOf(Configuraciones())
    val configuraciones: State<Configuraciones> = _configuraciones

    private val sharedPreferences = application.getSharedPreferences("configuraciones", Context.MODE_PRIVATE)

    private val dbHelper = RecetasDbHelper(application)
    private val mysqlClient = MySQLClient(
        hostname = "bndd8c9fxucrhuvnnpxy-mysql.services.clever-cloud.com",
        username = "u9ins6pqpla87i9w",
        password = "an9jWnmzEbucBiymgvUH",
        dbname = "bndd8c9fxucrhuvnnpxy"
    )

    var recetas = mutableStateOf<List<Receta>>(emptyList())
        private set

    // Propiedades para preferencias
    var colorTema = mutableStateOf(Color(0xFFF4F1BB)) // Color inicial
        private set

    var ordenProductos = mutableStateOf(OrdenReceta.ALFABETICO) // Orden inicial
        private set

    init {
        cargarRecetasLocales()
        cargarPreferencias()
    }

    // Función para cargar las recetas desde la base de datos local
    fun cargarRecetasLocales() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                recetas.value = dbHelper.obtenerRecetasFiltradas()
            } catch (e: Exception) {
                e.printStackTrace()
                // Aquí podrías usar Log.e para Android o algún manejo de error visual
            }
        }
    }

    private fun tieneConexionInternet(): Boolean {
        val context = getApplication<Application>()
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val nc = cm.getNetworkCapabilities(network) ?: return false
        return nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Función para sincronizar las recetas con la base de datos remota
    fun sincronizarManual() {
        viewModelScope.launch {
            sincronizando.value = true
            mensajeSync.value = null
            try {
                if (tieneConexionInternet()) {
                    SyncManager.sincronizar(dbHelper, mysqlClient)
                    cargarRecetasLocales()
                    mensajeSync.value = "Sincronización exitosa"
                } else {
                    mensajeSync.value = "No hay conexión a internet"
                }
            } catch (e: Exception) {
                mensajeSync.value = "Error al sincronizar: ${e.message}"
            } finally {
                sincronizando.value = false
            }
        }
    }

    // Función para guardar una receta
    fun guardarReceta(receta: Receta) {
        viewModelScope.launch(Dispatchers.IO) {
            val existe = dbHelper.obtenerRecetas().any { it.id == receta.id }
            if (existe) {
                dbHelper.actualizarReceta(receta)
            } else {
                dbHelper.insertarReceta(receta)
            }
            recetas.value = dbHelper.obtenerRecetasFiltradas()
        }
    }

    // Función para eliminar una receta
    fun eliminarReceta(receta: Receta) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.eliminarReceta(receta.id)
            recetas.value = dbHelper.obtenerRecetasFiltradas()
        }
    }

    // Función para cargar las preferencias guardadas en SharedPreferences
    private fun cargarPreferencias() {
        val colorGuardado = sharedPreferences.getInt("colorTema", Color(0xFFF4F1BB).toArgb())
        val ordenGuardado = sharedPreferences.getString("ordenProductos", OrdenReceta.ALFABETICO.name)

        colorTema.value = Color(colorGuardado)
        ordenProductos.value = OrdenReceta.valueOf(ordenGuardado ?: OrdenReceta.ALFABETICO.name)
    }

    // Función para guardar las preferencias en SharedPreferences
    private fun guardarPreferencias() {
        val editor = sharedPreferences.edit()
        editor.putInt("colorTema", colorTema.value.toArgb())
        editor.putString("ordenProductos", ordenProductos.value.name)
        editor.apply()
    }

    // Función para cambiar el color del tema y guardar la preferencia
    fun cambiarColorTema(nuevoColor: Color) {
        colorTema.value = nuevoColor
        guardarPreferencias() // Guardamos la preferencia en SharedPreferences
    }

    // Función para cambiar el orden de las recetas y guardar la preferencia
    fun cambiarOrdenProductos(nuevoOrden: OrdenReceta) {
        ordenProductos.value = nuevoOrden
        guardarPreferencias() // Guardamos la preferencia en SharedPreferences
    }

}
