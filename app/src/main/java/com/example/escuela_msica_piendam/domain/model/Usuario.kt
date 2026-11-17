package com.example.escuela_msica_piendam.domain.model
/** Usuario autenticado. `categoria` e `instrumento` solo existen en estudiantes. */
data class Usuario(
    val id: String,
    val nombre: String,
    val numCelular: String,
    val rol: Rol,
    val categoria: Categoria?,
    val instrumento: String?,
) {
    val esDirector: Boolean
        get() = rol == Rol.DIRECTOR
}