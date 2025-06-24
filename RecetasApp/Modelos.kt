package com.example.recetas



import java.util.UUID
import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize

@Parcelize
data class Receta(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String = "",
    val ingredientes: String = "",
    val autor: String = "",
    val fecha_mod: Long = (System.currentTimeMillis() / 1000),
    val eliminado: Int = 0,
    val instrucciones: String = ""
): Parcelable


data class Configuraciones(
    val colorBotones: Color = Color(0xFF6200EE),  // Color predeterminado (usando Color)
    val colorCard: Color = Color(0xFFCDCED0),     // Color predeterminado (usando Color)
    val colorFondo: Color = Color(0xFFEBA8A0),    // Color predeterminado (usando Color)
    val tamanoFuente: Int = 16                    // Tamaño de fuente predeterminado
)




