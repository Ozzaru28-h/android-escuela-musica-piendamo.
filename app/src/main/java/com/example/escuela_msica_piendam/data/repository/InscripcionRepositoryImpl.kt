package com.example.escuela_msica_piendam.data.repository


import com.example.escuela_msica_piendam.data.mapper.aDominio
import com.example.escuela_msica_piendam.domain.repository.InscripcionRepository
import com.example.escuela_msica_piendam.data.remote.api.ApiService
import com.example.escuela_msica_piendam.data.remote.dto.AsistenciaRequestDto
import com.example.escuela_msica_piendam.data.remote.dto.CrearInscripcionRequestDto
import com.example.escuela_msica_piendam.data.remote.safeApiCall
import com.example.escuela_msica_piendam.domain.model.EstadoAsistencia
import com.example.escuela_msica_piendam.domain.model.Inscripcion
import javax.inject.Inject
import retrofit2.HttpException

class InscripcionRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : InscripcionRepository {

    override suspend fun obtenerInscripciones(idActividad: String): Result<List<Inscripcion>> =
        safeApiCall { api.obtenerInscripciones(idActividad).map { it.aDominio() } }

    override suspend fun inscribirse(idActividad: String): Result<Inscripcion> =
        safeApiCall { api.inscribirse(CrearInscripcionRequestDto(idActividad)).aDominio() }

    override suspend fun cancelar(idInscripcion: String): Result<Unit> =
        safeApiCall {
            val respuesta = api.cancelarInscripcion(idInscripcion)
            // Response<Unit> no lanza excepción en 4xx: se lanza aquí para que safeApiCall la traduzca.
            if (!respuesta.isSuccessful) throw HttpException(respuesta)
        }

    override suspend fun registrarAsistencia(
        idInscripcion: String,
        estado: EstadoAsistencia,
    ): Result<Inscripcion> =
        safeApiCall {
            api.registrarAsistencia(idInscripcion, AsistenciaRequestDto(estado.valorApi)).aDominio()
        }
}