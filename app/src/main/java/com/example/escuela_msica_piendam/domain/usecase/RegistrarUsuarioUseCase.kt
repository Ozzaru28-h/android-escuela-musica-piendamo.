package com.example.escuela_msica_piendam.domain.usecase

import com.example.escuela_msica_piendam.domain.model.NuevoUsuario
import com.example.escuela_msica_piendam.domain.model.Usuario
import com.example.escuela_msica_piendam.domain.repository.AuthRepository
import javax.inject.Inject

class RegistrarUsuarioUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(nuevo: NuevoUsuario): Result<Usuario> =
        authRepository.registrar(nuevo)
}