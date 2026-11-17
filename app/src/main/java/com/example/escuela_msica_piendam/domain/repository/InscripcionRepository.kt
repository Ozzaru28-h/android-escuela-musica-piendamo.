package com.example.escuela_msica_piendam.domain.repository

import com.example.escuela_msica_piendam.domain.model.EstadoAsistencia
import com.example.escuela_msica_piendam.domain.model.Inscripcion

interface InscripcionRepository {
    /** Director: todas las inscripciones de la actividad. Estudiante: solo las suyas. */
    suspend fun obtenerInscripciones(idActividad: String): Result<List<Inscripcion>>

    /** El backend aplica la regla de congruencia (categoría, instrumento, duplicado, cupo) y responde 409 con el motivo. */
    suspend fun inscribirse(idActividad: String): Result<Inscripcion>

    suspend fun cancelar(idInscripcion: String): Result<Unit>

    suspend fun registrarAsistencia(idInscripcion: String, estado: EstadoAsistencia): Result<Inscripcion>
}