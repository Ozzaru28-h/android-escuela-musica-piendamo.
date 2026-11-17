package com.example.escuela_msica_piendam.domain.usecase
import com.example.escuela_msica_piendam.domain.model.Usuario

import com.example.escuela_msica_piendam.domain.repository.AuthRepository
import javax.inject.Inject

/** Caso de uso: "una persona inicia sesión con su celular y contraseña" (HU-00). */
class IniciarSesionUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(numCelular: String, contrasena: String): Result<Usuario> =
        authRepository.iniciarSesion(numCelular, contrasena)
}