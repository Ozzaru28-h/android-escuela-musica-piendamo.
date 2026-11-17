package com.example.escuela_msica_piendam.data.repository

import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.data.mapper.aDominio
import com.example.escuela_msica_piendam.data.mapper.aDto
import com.example.escuela_msica_piendam.data.remote.api.ApiService
import com.example.escuela_msica_piendam.data.remote.safeApiCall
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.domain.model.NuevaActividad
import com.example.escuela_msica_piendam.domain.repository.ActividadRepository

import javax.inject.Inject

class ActividadRepositoryImpl @Inject constructor(
    private val api: ApiService,
) : ActividadRepository {

    override suspend fun obtenerActividades(categoria: Categoria?): Result<List<Actividad>> =
        safeApiCall { api.obtenerActividades(categoria?.valorApi).map { it.aDominio() } }

    override suspend fun obtenerActividad(id: String): Result<Actividad> =
        safeApiCall { api.obtenerActividad(id).aDominio() }

    override suspend fun crearActividad(nueva: NuevaActividad): Result<Actividad> =
        safeApiCall { api.crearActividad(nueva.aDto()).aDominio() }

    override suspend fun obtenerInstrumentosDirigidos(): Result<List<String>> =
        safeApiCall { api.obtenerCatalogos().instrumentosDirigidos }
    override suspend fun obtenerInstrumentos(): Result<List<String>> =
        safeApiCall { api.obtenerCatalogos().instrumentos }
}
