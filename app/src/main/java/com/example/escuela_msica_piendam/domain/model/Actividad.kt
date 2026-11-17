package com.example.escuela_msica_piendam.domain.model

/**
 * Una clase, ensayo o taller publicado por el director.
 *
 * Es el modelo de DOMINIO: Kotlin puro, sin Gson ni Retrofit. La capa de datos traduce el JSON
 * del backend a esta clase (ver ActividadMapper); así, si el JSON cambia, la UI no se entera.
 *
 * @property fecha texto ISO "AAAA-MM-DD", tal como lo guarda el backend.
 * @property hora texto "HH:MM" (24 horas).
 * @property cupoMaximo `null` significa que la actividad no tiene límite de cupos.
 * @property inscritos cuántos estudiantes ya están inscritos (lo calcula el backend).
 */
data class Actividad(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val categoria: Categoria,
    val instrumentoDirigido: String,
    val fecha: String,
    val hora: String,
    val lugar: String?,
    val indicacionesVestimenta: String?,
    val cupoMaximo: Int?,
    val inscritos: Int,
) {
    /** Regla de negocio: una actividad sin cupo máximo siempre admite inscripciones. */
    val tieneCupo: Boolean
        get() = cupoMaximo == null || inscritos < cupoMaximo

    /** Cupos que quedan libres, o `null` si no hay límite (la tarjeta muestra "18 de 45" o nada). */
    val cuposDisponibles: Int?
        get() = cupoMaximo?.let { it - inscritos }
}
