package com.example.escuela_msica_piendam.presentation.navegation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.escuela_msica_piendam.presentation.actividades.ActividadesScreen
import com.example.escuela_msica_piendam.presentation.asistencia.AsistenciaScreen
import com.example.escuela_msica_piendam.presentation.auth.LoginScreen
import com.example.escuela_msica_piendam.presentation.crear.CrearActividadScreen
import com.example.escuela_msica_piendam.presentation.detalle.DetalleActividadScreen
import com.example.escuela_msica_piendam.presentation.registro.RegistroScreen

@Composable
fun AppNavHost(destinoInicial: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = destinoInicial) {

        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Rutas.ACTIVIDADES) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                },
                onIrARegistro = { navController.navigate(Rutas.REGISTRO) },
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onVolver = { navController.popBackStack() },
                onRegistroExitoso = {
                    // Saca login y registro de la pila: "Atrás" no debe volver a los formularios.
                    navController.navigate(Rutas.ACTIVIDADES) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                },
            )
        }

        composable(Rutas.ACTIVIDADES) {
            ActividadesScreen(
                onActividadClick = { id -> navController.navigate(Rutas.detalle(id)) },
                onNuevaActividad = { navController.navigate(Rutas.CREAR_ACTIVIDAD) },
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) { popUpTo(Rutas.ACTIVIDADES) { inclusive = true } }
                },
            )
        }

        composable(Rutas.CREAR_ACTIVIDAD) {
            CrearActividadScreen(onVolver = { navController.popBackStack() })
        }

        // El id viaja en la ruta y llega al ViewModel por SavedStateHandle.
        composable(Rutas.DETALLE) {
            DetalleActividadScreen(
                onVolver = { navController.popBackStack() },
                onIrAAsistencia = { id -> navController.navigate(Rutas.asistencia(id)) },
            )
        }

        composable(Rutas.ASISTENCIA) {
            AsistenciaScreen(onVolver = { navController.popBackStack() })
        }
    }
}