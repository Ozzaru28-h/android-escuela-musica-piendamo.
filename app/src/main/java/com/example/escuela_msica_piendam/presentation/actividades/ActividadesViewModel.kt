package com.example.escuela_msica_piendam.presentation.actividades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.domain.model.Rol
import com.example.escuela_msica_piendam.domain.usecase.ObtenerActividadesUseCase
import com.example.escuela_msica_piendam.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.escuela_msica_piendam.domain.repository.AuthRepository


/**
 * ViewModel de la lista de actividades (HU-03).
 *
 * Responsabilidades:
 *  1. Guardar el estado de la pantalla y sobrevivir a rotaciones (la Activity se destruye, el ViewModel no).
 *  2. Pedir los datos al caso de uso y traducir el resultado a Loading / Success / Error.
 *  3. Reaccionar a las acciones del usuario (tocar un chip, reintentar).
 *
 * NO sabe nada de Retrofit, de JSON ni de Compose: solo habla con el dominio.
 */
@HiltViewModel
class ActividadesViewModel @Inject constructor(
    private val obtenerActividades: ObtenerActividadesUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActividadesUiState())
    val uiState: StateFlow<ActividadesUiState> = _uiState.asStateFlow()

    private var cargaEnCurso: Job? = null

    init {
        // El rol decide si se muestra el botón "Nueva actividad". La carga de datos la dispara
        // la pantalla en ON_RESUME (ver recargar()).
        viewModelScope.launch {
            val esDirector = authRepository.obtenerRol() == Rol.DIRECTOR
            _uiState.update { it.copy(esDirector = esDirector) }
        }
    }

    fun seleccionarCategoria(categoria: Categoria?) {
        if (categoria == _uiState.value.categoriaSeleccionada) return
        _uiState.update { it.copy(categoriaSeleccionada = categoria) }
        cargarActividades(mostrarCarga = true)
    }

    fun reintentar() = cargarActividades(mostrarCarga = true)

    /** Se llama cada vez que la pantalla vuelve a primer plano: refresca sin parpadeo si ya había datos. */
    fun recargar() = cargarActividades(mostrarCarga = _uiState.value.actividades !is UiState.Success)

    fun cerrarSesion(alTerminar: () -> Unit) {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            alTerminar()
        }
    }

    private fun cargarActividades(mostrarCarga: Boolean) {
        cargaEnCurso?.cancel() // evita que una respuesta vieja pise la nueva

        cargaEnCurso = viewModelScope.launch {
            if (mostrarCarga) _uiState.update { it.copy(actividades = UiState.Loading) }

            obtenerActividades(_uiState.value.categoriaSeleccionada)
                .onSuccess { lista -> _uiState.update { it.copy(actividades = UiState.Success(lista)) } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(actividades = UiState.Error(error.message ?: MENSAJE_GENERICO))
                    }
                }
        }
    }

    private companion object {
        const val MENSAJE_GENERICO = "No se pudieron cargar las actividades. Inténtalo de nuevo."
    }
}
