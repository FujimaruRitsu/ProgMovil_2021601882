package com.example.picominecraft

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lastUpdate: Long = 0
    private val swingThreshold = 10f // Umbral de aceleración para detectar la accion de minar

    // Variable para el estado del pico (si está en estado "minando")
    private val minandoState = mutableStateOf(false)

    // MediaPlayer para reproducir el sonido cuando se hace un golpe
    private var mediaPlayer: MediaPlayer? = null

    // Handler para restablecer el estado a "no minando" después de un tiempo
    private val handler = Handler(Looper.getMainLooper())

    // Al crear la actividad, inicializamos el SensorManager y la UI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager // Inicializamos el SensorManager

        enableEdgeToEdge()  // Configuración opcional para pantalla completa sin bordes

        setContent {
            // Llamamos a la función Composable para mostrar la UI, pasándole el estado de si el pico está minando
            PrincipalUI(minando = minandoState.value)
        }
    }

    // Metodo que se llama cada vez que cambia el valor del acelerómetro
    override fun onSensorChanged(event: SensorEvent?) {
        // Solo se ejecuta si el pico está "minando"
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            // Evitamos procesar demasiados eventos, limitamos a 100 ms entre cada lectura
            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                // Calculamos la aceleración total y le restamos la gravedad
                val acceleracion = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

                // Si la aceleración excede el umbral, se activa la acción de "golpear"
                if (acceleracion > swingThreshold && !minandoState.value) {
                    minar()
                }
            }
        }
    }

    // Función que activa el estado de "minar", reproduce el sonido y luego restablece el estado
    private fun minar() {
        minandoState.value = true  // Cambiamos el estado a "minando"

        // Reproducimos el sonido de minar
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.pickaxe_sound_1)
        }
        mediaPlayer?.start()  // Iniciamos la reproducción del sonido

        // Después de 50 ms, restablecemos el estado a "no minando"
        handler.postDelayed({
            minandoState.value = false
        }, 50)
    }

    // Metodo necesario, pero no utilizado en este actividad
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se necesita realizar ninguna acción en este caso
    }

    // Cuando la actividad se reanuda, registramos el acelerómetro para seguir detectando el movimiento
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,  // 'this' es el listener que escucha los cambios del sensor
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  // Usamos el acelerómetro
            SensorManager.SENSOR_DELAY_NORMAL  // La frecuencia de actualización del sensor
        )
    }

    // Cuando la actividad se pausa, desregistramos el acelerómetro y liberamos los recursos del MediaPlayer
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)  // Desregistramos el listener del acelerómetro
        mediaPlayer?.release()  // Liberamos el MediaPlayer
        mediaPlayer = null
    }
}

@Composable
fun PrincipalUI(minando: Boolean) {
    // Usamos un Box para centrar el contenido en la pantalla
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Mostramos la imagen del pico (dependiendo si está en estado "minando" o no)
        Image(
            painter = painterResource(
                id = if (minando) R.drawable.pickaxe else R.drawable.pickaxe // Posteriormente queremos agregar una animacion al estado de "minando"
            ),
            contentDescription = if (minando) "Pico minando" else "Pico estatico",
            modifier = Modifier.size(400.dp)  // Tamaño de la imagen
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PickaxePreview() {
    PrincipalUI(minando = false)
}