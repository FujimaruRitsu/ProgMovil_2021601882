package com.example.recetas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun PantallaAjustes(
    onCerrar: () -> Unit,
    viewModel: RecetasViewModel
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Ajustes de la aplicación") },
        text = {
            // Agregar un scroll vertical al contenido para permitir el desplazamiento
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Sección para ordenar recetas
                OrdenSection(viewModel = viewModel)

                Spacer(Modifier.height(16.dp)) // Espacio entre las secciones

                // Sección para seleccionar el color del tema
                ColorPickerSection(viewModel = viewModel)
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) {
                Text("Cerrar", color = Color.Black)
            }
        }
    )
}

@Composable
fun OrdenSection(viewModel: RecetasViewModel) {
    // Título de la sección
    Text("Ordenar recetas por:")

    Spacer(Modifier.height(8.dp)) // Espacio entre el título y las opciones

    // Sección de selección del orden
    OrdenSelector(
        ordenActual = viewModel.ordenProductos.value,
        onOrdenSeleccionado = { nuevoOrden ->
            viewModel.cambiarOrdenProductos(nuevoOrden)
        }
    )
}

@Composable
fun OrdenSelector(
    ordenActual: OrdenReceta,
    onOrdenSeleccionado: (OrdenReceta) -> Unit
) {
    Column {
        // Opción para ordenar alfabéticamente
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(
                selected = ordenActual == OrdenReceta.ALFABETICO,
                onClick = { onOrdenSeleccionado(OrdenReceta.ALFABETICO) }
            )
            Spacer(Modifier.width(8.dp))
            Text("Alfabético")
        }

        // Opción para ordenar por fecha de modificación
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            RadioButton(
                selected = ordenActual == OrdenReceta.FECHA_MODIFICACION,
                onClick = { onOrdenSeleccionado(OrdenReceta.FECHA_MODIFICACION) }
            )
            Spacer(Modifier.width(8.dp))
            Text("Fecha de modificación")
        }
    }
}

@Composable
fun ColorPickerSection(viewModel: RecetasViewModel) {
    // Título de la sección
    Text("Selecciona el color del tema:")

    Spacer(Modifier.height(8.dp)) // Espacio entre el título y la paleta de colores

    // Sección de selección del color
    PaletaColores(
        colorActual = viewModel.colorTema.value,
        onColorSelected = { nuevoColor ->
            viewModel.cambiarColorTema(nuevoColor)
        }
    )
}

@Composable
fun PaletaColores(
    colorActual: Color,
    onColorSelected: (Color) -> Unit
) {
    val colores = listOf(
        Color(0xFFA9C8B2), // Pale Spring Bud
        Color(0xFFEBA8A0), // Opal
        Color(0xFF74A7E4), // Alabaster
        Color(0xFFFFDA9E)  // Robin Egg Blue
    )

    // Mostrar colores horizontalmente con LazyRow
    LazyRow(
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth() // Asegura que el LazyRow ocupe todo el ancho disponible
    ) {
        items(colores) { color ->
            Box(
                modifier = Modifier
                    .size(40.dp) // Aumento el tamaño de los círculos para que sean más visibles
                    .background(color, shape = CircleShape)
                    .clickable { onColorSelected(color) }
                    .then(
                        if (color == colorActual) Modifier.border(
                            width = 3.dp,
                            color = Color.Black,
                            shape = CircleShape
                        )
                        else Modifier
                    )
            )
        }
    }
}
