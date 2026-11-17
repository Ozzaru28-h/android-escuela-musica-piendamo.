package com.example.escuela_msica_piendam.domain.model

/**
 * Categorías de la escuela de música.
 *
 * [valorApi] es el texto EXACTO que el backend espera y devuelve ("Pre-banda", con guion).
 * Centralizarlo aquí evita escribir ese texto suelto por toda la app, donde un error de
 * tipeo ("Pre banda") produciría un 400 difícil de rastrear.
 */
enum class Categoria(val valorApi: String) {
    BANDA("Banda"),
    PRE_BANDA("Pre-banda"),
    SEMILLERO("Semillero");

    companion object {
        /** Convierte el texto del backend en la categoría; falla si llega un valor que la app no conoce. */
        fun desdeApi(valor: String): Categoria =
            entries.firstOrNull { it.valorApi == valor }
                ?: throw IllegalArgumentException("Categoría desconocida: $valor")
    }
}
