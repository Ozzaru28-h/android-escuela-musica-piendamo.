package com.example.escuela_msica_piendam.data.remote.interceptor

import com.example.escuela_msica_piendam.data.local.TokenStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Agrega "Authorization: Bearer <token>" a TODAS las peticiones, así ningún ViewModel
 * ni el ApiService tienen que preocuparse por el token.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val peticionOriginal = chain.request()

        // OkHttp ejecuta los interceptores en un hilo de fondo y su función NO es suspend,
        // por eso se usa runBlocking para leer DataStore. Es seguro aquí; jamás lo hagas en el hilo principal.
        val token = runBlocking { tokenStorage.obtenerToken() }

        val peticion = if (token.isNullOrBlank()) {
            peticionOriginal // sin sesión: login y registro funcionan igual, son rutas públicas
        } else {
            peticionOriginal.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
        return chain.proceed(peticion)
    }
}