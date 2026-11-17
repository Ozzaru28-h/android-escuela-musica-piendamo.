package com.example.escuela_msica_piendam.domain.usecase

import com.example.escuela_msica_piendam.domain.model.EstadoAsistencia
import com.example.escuela_msica_piendam.domain.model.Inscripcion
import com.example.escuela_msica_piendam.domain.repository.InscripcionRepository
import javax.inject.Inject

class ObtenerInscripcionesUseCase @Inject constructor(private val repositorio: InscripcionRepository) {
    suspend operator fun invoke(idActividad: String): Result<List<Inscripcion>> =
        repositorio.obtenerInscripciones(idActividad)
}

class InscribirseUseCase @Inject constructor(private val repositorio: InscripcionRepository) {
    suspend operator fun invoke(idActividad: String): Result<Inscripcion> =
        repositorio.inscribirse(idActividad)
}

class CancelarInscripcionUseCase @Inject constructor(private val repositorio: InscripcionRepository) {
    suspend operator fun invoke(idInscripcion: String): Result<Unit> =
        repositorio.cancelar(idInscripcion)
}

class RegistrarAsistenciaUseCase @Inject constructor(private val repositorio: InscripcionRepository) {
    suspend operator fun invoke(idInscripcion: String, estado: EstadoAsistencia): Result<Inscripcion> =
        repositorio.registrarAsistencia(idInscripcion, estado)
}