package com.example.escuela_msica_piendam.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escuela_msica_piendam.domain.repository.AuthRepository
import com.example.escuela_msica_piendam.presentation.navegation.Rutas
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Si ya hay un token guardado, la app abre en la lista; si no, en el login. */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _destinoInicial = MutableStateFlow<String?>(null) // null = todavía decidiendo
    val destinoInicial: StateFlow<String?> = _destinoInicial.asStateFlow()

    init {
        viewModelScope.launch {
            _destinoInicial.value =
                if (authRepository.haySesionGuardada()) Rutas.ACTIVIDADES else Rutas.LOGIN
        }
    }
}