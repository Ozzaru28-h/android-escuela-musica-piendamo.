package com.example.escuela_msica_piendam.data.repository

import com.example.escuela_msica_piendam.domain.model.Rol
import com.example.escuela_msica_piendam.data.local.TokenStorage
import com.example.escuela_msica_piendam.data.mapper.aDominio
import com.example.escuela_msica_piendam.data.remote.api.ApiService
import com.example.escuela_msica_piendam.data.remote.dto.LoginRequestDto
import com.example.escuela_msica_piendam.data.remote.dto.RegistroRequestDto
import com.example.escuela_msica_piendam.data.remote.safeApiCall
import com.example.escuela_msica_piendam.domain.model.NuevoUsuario
import com.example.escuela_msica_piendam.domain.model.Usuario
import com.example.escuela_msica_piendam.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val tokenStorage: TokenStorage,
) : AuthRepository {
    override suspend fun obtenerRol(): Rol? = tokenStorage.obtenerRol()
    override suspend fun iniciarSesion(numCelular: String, contrasena: String): Result<Usuario> =
        safeApiCall {
            val respuesta = api.login(LoginRequestDto(numCelular, contrasena))
            val usuario = respuesta.usuario.aDominio()
            // Se guarda ANTES de devolver éxito: así la siguiente pantalla ya encuentra el token
            // en el AuthInterceptor desde su primera petición.
            tokenStorage.guardarSesion(respuesta.token, usuario)
            usuario
        }

    override suspend fun haySesionGuardada(): Boolean =
        !tokenStorage.obtenerToken().isNullOrBlank()

    override suspend fun cerrarSesion() = tokenStorage.borrarSesion()
    override suspend fun registrar(nuevo: NuevoUsuario): Result<Usuario> =
        safeApiCall {
            val respuesta = api.registrar(
                RegistroRequestDto(
                    nombre = nuevo.nombre.trim(),
                    numCelular = nuevo.numCelular,
                    contrasena = nuevo.contrasena,
                    categoria = nuevo.categoria.valorApi,
                    instrumento = nuevo.instrumento,
                ),
            )
            val usuario = respuesta.usuario.aDominio()
            // Igual que en el login: la sesión queda guardada ANTES de devolver éxito.
            tokenStorage.guardarSesion(respuesta.token, usuario)
            usuario
        }
}