package com.example.escuela_msica_piendam.data.remote.dto

import com.google.gson.annotations.SerializedName

data class InscripcionDto(
    @SerializedName("id") val id: String,
    @SerializedName("estado_asistencia") val estadoAsistencia: String,
    @SerializedName("estudiante") val estudiante: EstudianteInscritoDto?,
)

data class EstudianteInscritoDto(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("instrumento") val instrumento: String?,
)

/** Gson omite los null: el estudiante NO envía id_estudiante (el backend usa el del token). */
data class CrearInscripcionRequestDto(
    @SerializedName("id_actividad") val idActividad: String,
    @SerializedName("id_estudiante") val idEstudiante: String? = null,
)

data class AsistenciaRequestDto(
    @SerializedName("estado_asistencia") val estadoAsistencia: String,
)

data class CatalogosDto(
    @SerializedName("instrumentos") val instrumentos: List<String>,
    @SerializedName("instrumentos_dirigidos") val instrumentosDirigidos: List<String>,
)

