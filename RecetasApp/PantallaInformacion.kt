package com.example.recetas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInformacion(navController: NavController) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Información de la app") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Instrucciones de uso",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = """
                                    Bienvenido a nuestra aplicación de gestión de recetas. A continuación, te explicamos cómo navegar y utilizar las funciones principales de la app:
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Pantalla Principal
                            Text(
                                text = "1. Pantalla Principal",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Text(
                                text = """
                                    - Ver Recetas: Al abrir la aplicación, se mostrará una lista con todas las recetas guardadas. Si no hay recetas guardadas, aparecerá el mensaje "No hay recetas disponibles".
                                    - Agregar una Receta: Para agregar una nueva receta, toca el botón con el símbolo de "+" ubicado en la parte inferior derecha de la pantalla.
                                    - Editar Receta: Toca cualquier receta de la lista para ver sus detalles. Podrás editar la receta tocando el botón "Editar". Puedes modificar el título, los ingredientes, las instrucciones y el autor. Para guardar los cambios, toca "Guardar"; si deseas cancelar, toca "Cancelar".
                                    - Eliminar Receta: Si deseas eliminar una receta, toca el ícono de la papelera mientras visualizas la receta.
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Pantalla de Formulario
                            Text(
                                text = "2. Nueva Receta",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Text(
                                text = """
                                    Cuando decidas agregar o editar una receta, serás dirigido a un formulario donde podrás ingresar la siguiente información:
                                    - Título: Escribe el nombre de la receta (no puede contener números).
                                    - Ingredientes: Ingresa los ingredientes junto con sus cantidades. Puedes agregar tantos ingredientes como desees presionando el botón "Agregar ingrediente".
                                    - Instrucciones: Detalla el paso a paso para preparar la receta.
                                    - Autor: Indica tu nombre o el del creador de la receta (no puede contener números).
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = """

                                    Una vez ingresada la información, toca "Guardar" para guardarla. Si deseas cancelar, toca "Cancelar" para regresar a la pantalla principal sin guardar los cambios.
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Ajustes
                            Text(
                                text = "3. Ajustes",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Text(
                                text = """
                                    Para personalizar la aplicación, accede a los ajustes tocando el ícono de configuración en la parte superior derecha. Desde ahí podrás:
                                    - Cambiar el tema de la aplicación.
                                    - Ordenar las recetas por nombre o por fecha de modificación.
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sincronización
                            Text(
                                text = "4. Sincronización",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Text(
                                text = """
                                    Si deseas mantener tus recetas sincronizadas entre dispositivos, accede a la pantalla de "Sincronización" tocando el ícono de "Sync" en la parte superior derecha. Esta opción te permite guardar tus recetas en la nube y sincronizarlas con otros dispositivos.
                                """.trimIndent(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Requisitos Mínimos del Dispositivo:",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Text(
                                text = "- Android 11.0 o superior\n- 4 GB de RAM\n- 100 MB de almacenamiento disponible (deseable)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Creadores:",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Text(
                                text = "Desarrollado por el equipo de RecetasApp, compuesto por:\n- Angel David Lozano Rojas y Alan Axel Hernández López",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
