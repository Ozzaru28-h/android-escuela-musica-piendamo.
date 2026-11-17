package com.example.escuela_msica_piendam.domain.repository

import com.example.escuela_msica_piendam.domain.model.NuevoUsuario
import com.example.escuela_msica_piendam.domain.model.Rol
import com.example.escuela_msica_piendam.domain.model.Usuario


interface AuthRepository {
    suspend fun iniciarSesion(numCelular: String, contrasena: String): Result<Usuario>
    /** Crea la cuenta (siempre como estudiante) y deja la sesión iniciada en el dispositivo. */
    suspend fun registrar(nuevo: NuevoUsuario): Result<Usuario>
    suspend fun haySesionGuardada(): Boolean
    suspend fun obtenerRol(): Rol?
    suspend fun cerrarSesion()
}