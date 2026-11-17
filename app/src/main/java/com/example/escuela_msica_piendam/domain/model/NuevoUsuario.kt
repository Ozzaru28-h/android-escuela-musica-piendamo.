package com.example.escuela_msica_piendam.domain.model

/** Datos que una persona llena para crear su cuenta de estudiante. */
data class NuevoUsuario(
    val nombre: String,
    val numCelular: String,
    val contrasena: String,
    val categoria: Categoria,
    val instrumento: String,
)