package com.example.escuela_msica_piendam.domain.model

enum class EstadoAsistencia(val valorApi: String) {
    PENDIENTE("Pendiente"),
    ASISTIO("Asistió"),
    FALTO("Faltó"),
    JUSTIFICADO("Justificado");

    companion object {
        fun desdeApi(valor: String): EstadoAsistencia =
            entries.firstOrNull { it.valorApi == valor }
                ?: throw IllegalArgumentException("Estado de asistencia desconocido: $valor")
    }
}