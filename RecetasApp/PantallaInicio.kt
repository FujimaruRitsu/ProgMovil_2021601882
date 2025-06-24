package com.example.recetas


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable

import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(
    viewmodel: RecetasViewModel,
    recetas: List<Receta>,
    onEditar: (Receta) -> Unit,
    onEliminar: (Receta) -> Unit,
    onAgregar: () -> Unit,
    onSincronizar: () -> Unit,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewmodel.cargarRecetasLocales()  // Cargar las recetas desde el ViewModel
    }
    //var recetas by viewmodel.recetas
    var recetas by remember { viewmodel.recetas }
    val colorActual = viewmodel.colorTema.value // Obtener el color desde el ViewModel
    val ordenActual = viewmodel.ordenProductos.value // Obtener el orden desde el ViewModel
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Mostrar mensajes de sincronización
    LaunchedEffect(viewmodel.mensajeSync.value) {
        viewmodel.mensajeSync.value?.let { mensaje ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(mensaje)
                viewmodel.mensajeSync.value = null
            }
        }
    }

    // Ordenar las recetas según la preferencia
     recetas = when (ordenActual) {
        OrdenReceta.ALFABETICO -> recetas.sortedBy { it.titulo }
        OrdenReceta.FECHA_MODIFICACION -> recetas.sortedByDescending { it.fecha_mod }
    }

    // Scaffold para gestionar la barra superior, botones flotantes y el contenido principal
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Mis Recetas", color = Color.Black,fontSize = 30.sp) },
                actions = {
                    // Botón informacion
                    IconButton(onClick = { navController.navigate("informacion") }) {
                        Icon(Icons.Filled.Info, contentDescription = "Información", tint = Color.Black)
                    }
                    // Botón para sincronizar recetas
                    IconButton(onClick = onSincronizar) {
                        Icon(Icons.Default.Sync, contentDescription = "Sincronizar recetas", tint = Color.Black)
                    }
                    // Botón para navegar a la pantalla de ajustes
                    IconButton(onClick = { navController.navigate("ajustes") }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Ajustes", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorActual)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregar, containerColor = colorActual) {
                Icon(Icons.Default.Add, contentDescription = "Agregar receta")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(colorActual)) {
            if (viewmodel.sincronizando.value) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else {
                if (recetas.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text("No hay recetas disponibles", color = Color.Black)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recetas) { receta ->
                            // Card para cada receta
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 140.dp, max = 300.dp)
                                    .clickable { onEditar(receta) }
                                    .padding(8.dp),
                               // colors = CardDefaults.cardColors(containerColor = colorActual.copy(alpha = 0.1f)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDEDDEF)),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = receta.titulo,style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold,fontSize = 26.sp), color = Color.Black)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Autor: ${receta.autor}", style = MaterialTheme.typography.bodySmall, color = Color.Black,fontSize = 16.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                        Text(text = "Última modificación: ${fecha.format(Date(receta.fecha_mod * 1000))}", style = MaterialTheme.typography.bodySmall, color = Color.Black,fontSize = 16.sp)
                                    }
                                    IconButton(onClick = { onEliminar(receta) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar receta",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecetaCardSimple(receta: Receta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = receta.titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Autor: ${receta.autor}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun RecetaDetalleCard(
    viewmodel: ViewModel,
    receta: Receta,
    modoEdicion: Boolean,
    onEditarClick: () -> Unit,
    onGuardarClick: (Receta) -> Unit,
    onCancelarEdicion: () -> Unit,
    onEliminar: () -> Unit
) {
    var titulo by remember { mutableStateOf(receta.titulo) }
    var ingredientes by remember { mutableStateOf(receta.ingredientes) }
    var instrucciones by remember { mutableStateOf("") } // OJO: si tu modelo aún no tiene instrucciones, agrega ahí el campo
    var autor by remember { mutableStateOf(receta.autor) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (modoEdicion) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = ingredientes,
                    onValueChange = { ingredientes = it },
                    label = { Text("Ingredientes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = instrucciones,
                    onValueChange = { instrucciones = it },
                    label = { Text("Instrucciones") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 6
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = autor,
                    onValueChange = { autor = it },
                    label = { Text("Autor") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onCancelarEdicion) {
                        Text("Cancelar")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        onGuardarClick(
                            receta.copy(
                                titulo = titulo,
                                ingredientes = ingredientes,
                                autor = autor,
                                fecha_mod = System.currentTimeMillis() / 1000,
                                instrucciones = instrucciones,
                            )
                        )
                    }) {
                        Text("Guardar",color = Color.Black )
                    }
                }
            } else {
                Text(text = titulo, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(text = "Ingredientes:", style = MaterialTheme.typography.titleMedium)
                Text(text = ingredientes)
                Spacer(Modifier.height(8.dp))
                Text(text = "Instrucciones:", style = MaterialTheme.typography.titleMedium)
                Text(text = instrucciones.ifBlank { "Sin instrucciones" })
                Spacer(Modifier.height(8.dp))
                Text(text = "Autor: $autor", style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onEditarClick) {
                        Text("Editar")
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = onEliminar) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}
