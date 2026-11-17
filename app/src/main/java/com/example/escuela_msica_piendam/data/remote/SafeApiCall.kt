package com.example.escuela_msica_piendam.data.remote

import com.example.escuela_msica_piendam.data.remote.dto.ErrorDto
import com.example.escuela_msica_piendam.domain.model.ErrorApp
import com.google.gson.Gson
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

private val gson = Gson()

/**
 * Ejecuta una llamada de red y devuelve un [Result], traduciendo cualquier excepción técnica
 * a un [ErrorApp] con mensaje para el usuario. Así los ViewModels nunca necesitan try/catch.
 */
suspend fun <T> safeApiCall(llamada: suspend () -> T): Result<T> =
    try {
        Result.success(llamada())
    } catch (e: CancellationException) {
        // IMPORTANTE: si la corrutina fue cancelada (p. ej. el usuario cambió de filtro) hay que
        // relanzar la excepción. Tragársela rompe la cancelación y puede pintar datos viejos.
        throw e
    } catch (e: HttpException) {
        Result.failure(e.aErrorApp())
    } catch (e: IOException) {
        Result.failure(ErrorApp.SinConexion())
    } catch (e: Exception) {
        Result.failure(ErrorApp.Desconocido())
    }

/** Lee el cuerpo { status, mensaje, detalles } que envía el backend y arma un mensaje claro. */
private fun HttpException.aErrorApp(): ErrorApp {
    val error = runCatching {
        gson.fromJson(response()?.errorBody()?.string(), ErrorDto::class.java)
    }.getOrNull()

    val detalles = error?.detalles.orEmpty()
    val mensaje = buildString {
        append(error?.mensaje ?: "El servidor respondió con un error (${code()})")
        if (detalles.isNotEmpty()) append(": ").append(detalles.joinToString("; "))
    }

    return when (code()) {
        401 -> ErrorApp.NoAutorizado(mensaje)
        else -> ErrorApp.Servidor(code(), mensaje)
    }
}
