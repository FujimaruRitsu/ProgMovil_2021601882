package com.example.recetas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario(
    recetaEditar: Receta?,
    onGuardar: (Receta) -> Unit,
    onCancelar: () -> Unit,
    viewModel: RecetasViewModel // Recibimos el ViewModel para acceder al color de tema
) {
    // Obtenemos el color de tema desde el ViewModel
    val colorTema = viewModel.colorTema.value

    var modoEdicion by rememberSaveable { mutableStateOf(recetaEditar == null) }
    var titulo by rememberSaveable { mutableStateOf(recetaEditar?.titulo ?: "") }
    var ingredientes by rememberSaveable { mutableStateOf(recetaEditar?.ingredientes ?: "") }
    var instrucciones by rememberSaveable { mutableStateOf(recetaEditar?.instrucciones ?: "") }
    var autor by rememberSaveable { mutableStateOf(recetaEditar?.autor ?: "") }

    var errorTitulo by remember { mutableStateOf(false) }
    var errorAutor by remember { mutableStateOf(false) }
    var mostrarAlerta by remember { mutableStateOf(false) }
    var mensajeAlerta by remember { mutableStateOf("") }

    // Función para validar si el texto contiene números
    fun contieneNumeros(texto: String): Boolean {
        return texto.any { it.isDigit() }
    }

    // Validación del formulario
    val formularioValido = titulo.isNotBlank() && ingredientes.isNotBlank() &&
            autor.isNotBlank() && !contieneNumeros(titulo) && !contieneNumeros(autor)

    // Función para mostrar alerta si el formulario no es válido
    fun validarFormulario() {
        when {
            contieneNumeros(titulo) -> {
                mensajeAlerta = "El Título no puede contener números."
                mostrarAlerta = true
            }
            contieneNumeros(autor) -> {
                mensajeAlerta = "El Autor no puede contener números."
                mostrarAlerta = true
            }
            else -> {
                // Si no hay errores, se guarda la receta
                onGuardar(
                    (recetaEditar ?: Receta(
                        titulo = "",
                        ingredientes = "",
                        autor = ""
                    )).copy(
                        titulo = titulo.trim(),
                        ingredientes = ingredientes.trim(),
                        instrucciones = instrucciones.trim(),
                        autor = autor.trim(),
                        fecha_mod = System.currentTimeMillis() / 1000,
                        eliminado = 0
                    )
                )
                modoEdicion = false
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(if (recetaEditar == null) "Nueva Receta" else "Detalle Receta", fontSize = 30.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (modoEdicion == false || recetaEditar == null) {
                            onCancelar()
                        } else {
                            modoEdicion = false
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingValores ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(16.dp)
        ) {
            if (!modoEdicion) {
                // Modo presentación
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = colorTema.copy(alpha = 0.8f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = titulo, style = MaterialTheme.typography.headlineSmall, color = Color.Black, fontSize = 24.sp)
                                Spacer(Modifier.height(8.dp))

                                Text(text = "Ingredientes:", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                                val ingredientesList = ingredientes.split(",").joinToString(", ") { ingrediente ->
                                    val parts = ingrediente.split(".")
                                    val nombre = parts.getOrNull(0)?.trim() ?: ""
                                    val cantidad = parts.getOrNull(1)?.trim() ?: "al gusto"
                                    "$nombre $cantidad"
                                }

                                Text(text = ingredientesList, color = Color.Black, fontSize = 16.sp)
                                Spacer(Modifier.height(8.dp))
                                Text(text = "Instrucciones:", style = MaterialTheme.typography.titleMedium, color = Color.Black)
                                Text(text = instrucciones.ifBlank { "Sin instrucciones" }, color = Color.Black, fontSize = 16.sp)
                                Spacer(Modifier.height(8.dp))

                                Text(text = "Autor: $autor", style = MaterialTheme.typography.bodySmall, color = Color.Black, fontSize = 14.sp)
                                Spacer(Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { modoEdicion = true },
                                        colors = ButtonDefaults.buttonColors(Color(0xFFDEDDEF))
                                    ) {
                                        Text("Editar", color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Modo edición
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = {
                                titulo = it
                                errorTitulo = contieneNumeros(it)
                            },
                            label = { Text("Título") },
                            isError = errorTitulo,
                            supportingText = {
                                if (errorTitulo) Text("No puede contener números", color = MaterialTheme.colorScheme.error)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                        )
                    }

                    item {
                        IngredientesEditor(
                            ingredientesConcatenados = ingredientes,
                            onIngredientesChange = { nuevosIngredientes -> ingredientes = nuevosIngredientes },
                            colorTema
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = instrucciones,
                            onValueChange = { instrucciones = it },
                            label = { Text("Instrucciones") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 7
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = autor,
                            onValueChange = {
                                autor = it
                                errorAutor = contieneNumeros(it)
                            },
                            label = { Text("Autor") },
                            isError = errorAutor,
                            supportingText = {
                                if (errorAutor) Text("No puede contener números", color = MaterialTheme.colorScheme.error)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = {
                                if (recetaEditar == null) {
                                    onCancelar()
                                } else {
                                    modoEdicion = false
                                }
                            },
                                colors = ButtonDefaults.buttonColors(Color(0xFFDEDDEF))) {
                                Text("Cancelar", color = Color.Black)
                            }
                            Button(
                                onClick = { validarFormulario() },
                                enabled = formularioValido,
                                colors = ButtonDefaults.buttonColors(Color(0xFFDEDDEF))
                            ) {
                                Text("Guardar", color = Color.Black)
                            }
                        }
                    }
                }
            }

            // AlertDialog para mostrar el mensaje de error
            if (mostrarAlerta) {
                AlertDialog(
                    onDismissRequest = { mostrarAlerta = false },
                    title = { Text("Error") },
                    text = { Text(mensajeAlerta) },
                    confirmButton = {
                        Button(onClick = { mostrarAlerta = false }) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun IngredientesEditor(
    ingredientesConcatenados: String,
    onIngredientesChange: (String) -> Unit,
    colorTema: Color
) {
    val ingredientesLista = remember(ingredientesConcatenados) {
        ingredientesConcatenados.split(",").map {
            val cleanedIngredient = it.trim()

            val parts = cleanedIngredient.split(".")
            val nombre = parts.getOrNull(0)?.trim() ?: ""
            val cantidad = parts.getOrNull(1)?.trim() ?: ""

            nombre to cantidad
        }.toMutableStateList()
    }

    val ingredientes = remember { ingredientesLista }

    Column {
        ingredientes.forEachIndexed { index, par ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = par.first,
                    onValueChange = {
                        ingredientes[index] = it to ingredientes[index].second
                        onIngredientesChange(concatenarIngredientes(ingredientes))
                    },
                    label = { Text("Ingrediente", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = par.second,
                    onValueChange = {
                        ingredientes[index] = ingredientes[index].first to it
                        onIngredientesChange(concatenarIngredientes(ingredientes))
                    },
                    label = { Text("Cantidad", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp)) },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    ingredientes.removeAt(index)
                    onIngredientesChange(concatenarIngredientes(ingredientes))
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar ingrediente")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                ingredientes.add("" to "")
                onIngredientesChange(concatenarIngredientes(ingredientes))
            }, colors = ButtonDefaults.buttonColors(Color(0xFFDEDDEF))) {
                Text("Agregar ingrediente", color = Color.Black)
            }
        }
    }
}

fun concatenarIngredientes(lista: List<Pair<String, String>>): String {
    return lista.filter { it.first.isNotBlank() || it.second.isNotBlank() }
        .joinToString(", ") { (nombre, cantidad) ->
            val cantidadFinal = if (cantidad.isBlank()) "al gusto" else cantidad
            "$nombre.$cantidadFinal"
        }
}