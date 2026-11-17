package com.example.escuela_msica_piendam.presentation.navegation

object Rutas {
    const val LOGIN = "login"
    const val ACTIVIDADES = "actividades"
    const val CREAR_ACTIVIDAD = "crear_actividad"

    const val ARG_ID_ACTIVIDAD = "idActividad"
    const val DETALLE = "detalle/{$ARG_ID_ACTIVIDAD}"
    const val ASISTENCIA = "asistencia/{$ARG_ID_ACTIVIDAD}"
    const val REGISTRO = "registro"

    fun detalle(id: String) = "detalle/$id"
    fun asistencia(id: String) = "asistencia/$id"
}