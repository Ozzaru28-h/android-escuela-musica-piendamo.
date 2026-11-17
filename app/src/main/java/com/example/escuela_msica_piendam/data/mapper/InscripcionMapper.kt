package com.example.escuela_msica_piendam.data.mapper


import com.example.escuela_msica_piendam.data.remote.dto.InscripcionDto
import com.example.escuela_msica_piendam.domain.model.EstadoAsistencia
import com.example.escuela_msica_piendam.domain.model.Inscripcion

fun InscripcionDto.aDominio(): Inscripcion {
    val estadoAsistencia = ""
    return Inscripcion(
        id = id,
        estadoAsistencia = EstadoAsistencia.desdeApi(estadoAsistencia),
        nombreEstudiante = estudiante?.nombre ?: "Estudiante",
        categoriaEstudiante = estudiante?.categoria,
        instrumentoEstudiante = estudiante?.instrumento,
    )
}