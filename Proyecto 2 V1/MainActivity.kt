package com.example.tmoproductos

import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UIPrincipal()
        }
    }
}

@Composable
fun UIPrincipal() {
    val dbManager = DBHelper(LocalContext.current) // Instanciamos la base
    val productos = remember { mutableStateOf(dbManager.obtenerProductos()) }//lista de los productos obtenida

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)  // Padding general al contenido
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Espacio entre la barra principal
                horizontalArrangement = Arrangement.SpaceBetween //separamos los elementos de la barra
            ) {
                Text(text = "Productos Disponibles", style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { /* TODO:Construir la logica para funcionalidad AÑADIR */ }) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir Producto")
                }
            }

            // Itera en la lista de productos
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(productos.value) { producto ->
                    ProductoItem(producto)//enviamos cada producto
                }
            }
        }
    }
}
// Función para convertir una cadena Base64 a Bitmap
fun convertirBase64AImagen(base64: String): Bitmap? {
    return try {
        val decodedString = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ProductoItem(producto: Producto) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Convertir la imagen Base64 a Bitmap para mostrarla
        producto.imagenBase64?.let { base64 ->
            val bitmap = convertirBase64AImagen(base64)
            bitmap?.let {
                // Aquí mostramos la imagen convertida desde Base64
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Imagen del producto",
                    modifier = Modifier.size(128.dp),
                    contentScale = ContentScale.Crop
                )
            }
        } ?: run {
            // Si la imagen Base64 no está disponible, mostrar un un texto
            Text("Imagen no disponible por el momento...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        //datos del producto
        Text(text = producto.nombre, style = MaterialTheme.typography.bodyLarge)
        Text(text = "$${producto.precio}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        producto.descripcion?.let {
            Text(text = it, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }

        // Botones sin acción por ahora
        Row {
            Button(onClick = { /* TODO:Acción pendiente */ }, modifier = Modifier.padding(8.dp)) {
                Text("Editar")
            }
            Button(onClick = { /* TODO: Acción pendiente */ }, modifier = Modifier.padding(8.dp)) {
                Text("Eliminar")
            }
        }
    }
}

data class Producto(
    val nombre: String,
    val precio: Double,
    val descripcion: String?,
    val imagenBase64: String?  // En base64,sin convertir aun
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UIPrincipal()  // Muestra la interfaz  previsualizar
}