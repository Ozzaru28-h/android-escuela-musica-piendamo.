package com.example.escuela_msica_piendam.domain.model

/** Inscripción de un estudiante a una actividad, con los datos del estudiante ya incluidos. */
data class Inscripcion(
    val id: String,
    val estadoAsistencia: EstadoAsistencia,
    val nombreEstudiante: String,
    val categoriaEstudiante: String?,
    val instrumentoEstudiante: String?,
)