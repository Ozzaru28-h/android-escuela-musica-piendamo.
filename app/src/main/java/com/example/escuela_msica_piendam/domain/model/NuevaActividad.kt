package com.example.escuela_msica_piendam.domain.model

/** Datos que el director llena para publicar una actividad. */
data class NuevaActividad(
    val titulo: String,
    val descripcion: String,
    val categoria: Categoria,
    val instrumentoDirigido: String,
    val fecha: String,   // AAAA-MM-DD
    val hora: String,    // HH:MM
    val lugar: String?,
    val indicacionesVestimenta: String?,
    val cupoMaximo: Int?,
)