package com.example.escuela_msica_piendam.domain.repository

import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.domain.model.NuevaActividad


interface ActividadRepository {
    suspend fun obtenerActividades(categoria: Categoria? = null): Result<List<Actividad>>
    suspend fun obtenerActividad(id: String): Result<Actividad>
    suspend fun crearActividad(nueva: NuevaActividad): Result<Actividad>
    /** Instrumentos que puede tocar un estudiante (catálogo del backend, sin "Todos"). */
    suspend fun obtenerInstrumentos(): Result<List<String>>

    /** Opciones del selector de instrumento: "Todos" + los instrumentos del catálogo del backend. */
    suspend fun obtenerInstrumentosDirigidos(): Result<List<String>>
}
