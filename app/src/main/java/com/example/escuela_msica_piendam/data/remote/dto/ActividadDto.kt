package com.example.escuela_msica_piendam.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Espejo EXACTO del JSON que devuelve el backend (nombres en snake_case incluidos).
 * @SerializedName conecta el nombre del JSON con el nombre Kotlin (camelCase).
 *
 * Los campos opcionales del backend son nulables aquí. Atención: Gson ignora la nulabilidad de
 * Kotlin, así que un campo no nulable que no llegue en el JSON quedaría en null sin avisar.
 */
data class ActividadDto(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("categoria_dirigida") val categoriaDirigida: String,
    @SerializedName("instrumento_dirigido") val instrumentoDirigido: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("hora") val hora: String,
    @SerializedName("lugar") val lugar: String?,
    @SerializedName("indicaciones_vestimenta") val indicacionesVestimenta: String?,
    @SerializedName("cupo_maximo") val cupoMaximo: Int?,
    @SerializedName("inscritos") val inscritos: Int,
)

/**
 * Cuerpo de POST /api/actividades (HU-01, rol director). Gson omite los campos null, así que
 * lugar, indicaciones y cupo simplemente no viajan si el formulario los deja vacíos.
 */
data class CrearActividadRequestDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("categoria_dirigida") val categoriaDirigida: String,
    @SerializedName("instrumento_dirigido") val instrumentoDirigido: String,
    @SerializedName("fecha") val fecha: String,
    @SerializedName("hora") val hora: String,
    @SerializedName("lugar") val lugar: String? = null,
    @SerializedName("indicaciones_vestimenta") val indicacionesVestimenta: String? = null,
    @SerializedName("cupo_maximo") val cupoMaximo: Int? = null,
)
