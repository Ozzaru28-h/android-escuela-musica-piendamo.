package com.example.escuela_msica_piendam.domain.usecase

import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.NuevaActividad
import com.example.escuela_msica_piendam.domain.repository.ActividadRepository
import javax.inject.Inject

class ObtenerActividadUseCase @Inject constructor(private val repositorio: ActividadRepository) {
    suspend operator fun invoke(id: String): Result<Actividad> = repositorio.obtenerActividad(id)
}

class CrearActividadUseCase @Inject constructor(private val repositorio: ActividadRepository) {
    suspend operator fun invoke(nueva: NuevaActividad): Result<Actividad> = repositorio.crearActividad(nueva)
}

class ObtenerInstrumentosUseCase @Inject constructor(private val repositorio: ActividadRepository) {
    suspend operator fun invoke(): Result<List<String>> = repositorio.obtenerInstrumentosDirigidos()
}
class ObtenerInstrumentosCatalogoUseCase @Inject constructor(private val repositorio: ActividadRepository) {
    suspend operator fun invoke(): Result<List<String>> = repositorio.obtenerInstrumentos()
}