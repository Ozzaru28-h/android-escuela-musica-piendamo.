package com.example.escuela_msica_piendam.domain.usecase

import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.domain.repository.ActividadRepository
import javax.inject.Inject

/**
 * Caso de uso: "el estudiante consulta la programación" (HU-02 / HU-03).
 *
 * Hoy solo delega al repositorio, y eso está bien: es el lugar donde irían reglas de aplicación
 * futuras (ocultar actividades pasadas, ordenar por cercanía) sin tocar ni el ViewModel ni Retrofit.
 */
class ObtenerActividadesUseCase @Inject constructor(
    private val repositorio: ActividadRepository,
) {
    suspend operator fun invoke(categoria: Categoria? = null): Result<List<Actividad>> =
        repositorio.obtenerActividades(categoria)
}
