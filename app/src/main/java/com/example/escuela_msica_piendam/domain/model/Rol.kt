package com.example.escuela_msica_piendam.domain.model
/** Rol del usuario. Decide qué puede hacer en la app (el director puede publicar, el estudiante consulta). */
enum class Rol(val valorApi: String) {
    DIRECTOR("director"),
    ESTUDIANTE("estudiante");

    companion object {
        fun desdeApi(valor: String): Rol =
            entries.firstOrNull { it.valorApi == valor }
                ?: throw IllegalArgumentException("Rol desconocido: $valor")
    }
}