package com.example.escuela_msica_piendam.presentation.auth

import com.example.escuela_msica_piendam.domain.model.Usuario
import com.example.escuela_msica_piendam.presentation.common.UiState


/**
 * Todo lo que la pantalla de login necesita para dibujarse.
 *
 * @property resultado `null` = el usuario todavía no pulsó "Ingresar"; después pasa por
 *   Loading y termina en Success o Error.
 */
data class LoginUiState(
    val numCelular: String = "",
    val contrasena: String = "",
    val errorCelular: String? = null,
    val errorContrasena: String? = null,
    val resultado: UiState<Usuario>? = null,
) {
    val cargando: Boolean
        get() = resultado is UiState.Loading
}