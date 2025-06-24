package com.example.recetas

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import androidx.navigation.compose.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavegacion(
    viewModel: RecetasViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "inicio") {
        // Pantalla de lista de recetas

        composable("inicio") {


            PantallaInicio(
                viewmodel = viewModel,
                recetas = viewModel.recetas.value, // Observable lista recetas
                onEditar = { receta ->
                    // Guardar receta para editar en el backStackEntry
                    navController.currentBackStackEntry?.savedStateHandle?.set("recetaEditar", receta)
                    navController.navigate("formulario")
                },
                onEliminar = { receta ->
                    viewModel.eliminarReceta(receta)
                },
                onAgregar = {
                    // Limpiar receta editar para modo agregar
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Receta>("recetaEditar")
                    navController.navigate("formulario")
                },
                onSincronizar = {
                    viewModel.sincronizarManual()
                },
                navController = navController // Pasamos NavController aquí
            )
        }

        // Pantalla formulario para agregar o editar receta
        composable("formulario") {
            val recetaEditar = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Receta>("recetaEditar")

            PantallaFormulario(
                recetaEditar = recetaEditar,
                onGuardar = { receta ->
                    viewModel.guardarReceta(receta)
                    // Limpiar y volver atrás
                    navController.previousBackStackEntry?.savedStateHandle?.remove<Receta>("recetaEditar")

                },
                onCancelar = {
                    // Limpiar y volver atrás sin guardar
                    navController.previousBackStackEntry?.savedStateHandle?.remove<Receta>("recetaEditar")
                    navController.popBackStack()
                },
                        viewModel = viewModel
            )
        } // Pantalla de ajustes (Configuración)
        composable("ajustes") {
            PantallaAjustes(
                onCerrar = { navController.popBackStack() },
                viewModel = viewModel
            )
        }//pantalla info
        composable("informacion") {
            PantallaInformacion(navController = navController)
        }

    }
}


