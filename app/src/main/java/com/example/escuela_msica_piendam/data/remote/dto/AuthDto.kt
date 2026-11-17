package com.example.escuela_msica_piendam.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Cuerpo de POST /api/auth/login. OJO: el backend identifica por celular, no por correo. */
data class LoginRequestDto(
    @SerializedName("numCelular") val numCelular: String,
    @SerializedName("contrasena") val contrasena: String,
)

/** Respuesta del login: el usuario y el token JWT que se debe enviar en cada petición posterior. */
data class LoginResponseDto(
    @SerializedName("usuario") val usuario: UsuarioDto,
    @SerializedName("token") val token: String,
)

/** `rol` es "director" o "estudiante"; categoria e instrumento solo existen en estudiantes. */
data class UsuarioDto(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("numCelular") val numCelular: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("categoria") val categoria: String?,
    @SerializedName("instrumento") val instrumento: String?,
)

/** Cuerpo de POST /api/auth/registro. El rol NO se envía: el backend siempre crea estudiantes. */
data class RegistroRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("numCelular") val numCelular: String,
    @SerializedName("contrasena") val contrasena: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("instrumento") val instrumento: String,
)
