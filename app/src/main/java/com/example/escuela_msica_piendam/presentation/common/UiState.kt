package com.example.escuela_msica_piendam.presentation.common

/**
 * Los tres estados que debe poder mostrar cualquier pantalla que dependa de la red.
 * Al ser una clase sellada, el `when` en la UI obliga a manejar los tres: no se puede
 * olvidar el estado de error o de carga.
 */
sealed interface UiState<out T> {
    /** Esperando la respuesta: la UI muestra un indicador de progreso. */
    data object Loading : UiState<Nothing>

    /** Llegaron los datos (la lista puede estar vacía; eso lo decide la UI). */
    data class Success<T>(val datos: T) : UiState<T>

    /** Falló algo: [mensaje] ya está redactado para el usuario y la UI ofrece "Reintentar". */
    data class Error(val mensaje: String) : UiState<Nothing>
}
