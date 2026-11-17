package com.example.escuela_msica_piendam.presentation.registro

import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.domain.model.Usuario
import com.example.escuela_msica_piendam.presentation.common.UiState

data class ErroresRegistro(
    val nombre: String? = null,
    val numCelular: String? = null,
    val contrasena: String? = null,
    val confirmacion: String? = null,
    val categoria: String? = null,
    val instrumento: String? = null,
) {
    val hayErrores: Boolean
        get() = listOfNotNull(nombre, numCelular, contrasena, confirmacion, categoria, instrumento).isNotEmpty()
}

/**
 * @property instrumentos opciones del selector (vienen del backend).
 * @property resultado null = aún no se pulsó "Registrarme"; luego Loading / Success / Error.
 */
data class RegistroUiState(
    val nombre: String = "",
    val numCelular: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val categoria: Categoria? = null,
    val instrumento: String? = null,
    val errores: ErroresRegistro = ErroresRegistro(),
    val instrumentos: UiState<List<String>> = UiState.Loading,
    val resultado: UiState<Usuario>? = null,
) {
    val cargando: Boolean
        get() = resultado is UiState.Loading
}