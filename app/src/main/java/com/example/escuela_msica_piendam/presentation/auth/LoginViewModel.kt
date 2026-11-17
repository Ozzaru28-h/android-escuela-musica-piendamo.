package com.example.escuela_msica_piendam.presentation.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_msica_piendam.domain.usecase.IniciarSesionUseCase
import com.example.escuela_msica_piendam.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del login. Valida el formulario ANTES de llamar al servidor (ahorra una petición
 * inútil), y traduce la respuesta a Loading / Success / Error.
 * La validación definitiva sigue siendo la del backend; esta es solo comodidad para el usuario.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val iniciarSesion: IniciarSesionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onNumCelularChange(valor: String) {
        // Solo dígitos y máximo 10: igual que la regla del backend.
        val limpio = valor.filter { it.isDigit() }.take(LONGITUD_CELULAR)
        _uiState.update { it.copy(numCelular = limpio, errorCelular = null, resultado = null) }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update { it.copy(contrasena = valor, errorContrasena = null, resultado = null) }
    }

    fun onIngresarClick() {
        val estado = _uiState.value
        if (estado.cargando) return // evita doble toque mientras espera la respuesta

        val errorCelular = if (estado.numCelular.length != LONGITUD_CELULAR) {
            "El celular debe tener $LONGITUD_CELULAR dígitos"
        } else {
            null
        }
        val errorContrasena = if (estado.contrasena.isEmpty()) "Ingresa tu contraseña" else null

        if (errorCelular != null || errorContrasena != null) {
            _uiState.update { it.copy(errorCelular = errorCelular, errorContrasena = errorContrasena) }
            return
        }

        _uiState.update { it.copy(resultado = UiState.Loading) }

        viewModelScope.launch {
            iniciarSesion(estado.numCelular, estado.contrasena)
                .onSuccess { usuario ->
                    _uiState.update { it.copy(resultado = UiState.Success(usuario)) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(resultado = UiState.Error(error.message ?: MENSAJE_GENERICO))
                    }
                }
        }
    }

    private companion object {
        const val LONGITUD_CELULAR = 10
        const val MENSAJE_GENERICO = "No se pudo iniciar sesión. Inténtalo de nuevo."
    }
}