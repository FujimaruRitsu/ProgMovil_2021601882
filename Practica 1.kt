import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun main() {
    var opcion: Int
    do {
        println("\nSeleccione una opción:")
        println("1. Sumar tres números")
        println("2. Ingresar nombre completo")
        println("3. Calcular tiempo vivido")
        println("4. Salir")
        print("Opción: ")
        opcion = readLine()?.toIntOrNull() ?: 0

        when (opcion) {
            1 -> sumarTresNumeros()
            2 -> ingresarNombreCompleto()
            3 -> calcularTiempoVivido()
            4 -> println("Saliendo del programa... ¡Hasta luego!")
            else -> println("Opción no válida. Intente nuevamente.")
        }
    } while (opcion != 4)
}

// Función para sumar tres números ingresados por el usuario.
fun sumarTresNumeros() {
    println("\nIngrese el primer número:")
    val num1 = readLine()?.toDoubleOrNull() ?: 0.0

    println("Ingrese el segundo número:")
    val num2 = readLine()?.toDoubleOrNull() ?: 0.0

    println("Ingrese el tercer número:")
    val num3 = readLine()?.toDoubleOrNull() ?: 0.0

    val suma = num1 + num2 + num3
    println("La suma de los tres números es: $suma")
}

// Función para ingresar y mostrar el nombre completo del usuario.
fun ingresarNombreCompleto() {
    println("\nIngrese su nombre completo:")
    val nombreCompleto = readLine() ?: ""
    println("Su nombre completo es: $nombreCompleto")
}

// Función para calcular el tiempo vivido en meses, semanas, días, horas, minutos y segundos.
fun calcularTiempoVivido() {
    println("\nIngrese su fecha de nacimiento (formato dd/MM/yyyy):")
    val entradaFecha = readLine() ?: ""
    try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaNacimiento = LocalDate.parse(entradaFecha, formatter)
        val fechaNacimientoInicio = fechaNacimiento.atStartOfDay()
        val ahora = LocalDateTime.now()

        val meses = ChronoUnit.MONTHS.between(fechaNacimientoInicio, ahora)
        val semanas = ChronoUnit.WEEKS.between(fechaNacimientoInicio, ahora)
        val dias = ChronoUnit.DAYS.between(fechaNacimientoInicio, ahora)
        val horas = ChronoUnit.HOURS.between(fechaNacimientoInicio, ahora)
        val minutos = ChronoUnit.MINUTES.between(fechaNacimientoInicio, ahora)
        val segundos = ChronoUnit.SECONDS.between(fechaNacimientoInicio, ahora)

        println("\nUsted ha vivido aproximadamente:")
        println("$meses meses")
        println("$semanas semanas")
        println("$dias días")
        println("$horas horas")
        println("$minutos minutos")
        println("$segundos segundos")
    } catch (e: Exception) {
        println("Formato de fecha inválido. Por favor, use el formato dd/MM/yyyy")
    }
}
