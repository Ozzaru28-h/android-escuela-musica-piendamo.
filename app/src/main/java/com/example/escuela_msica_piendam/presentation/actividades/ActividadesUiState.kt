package com.example.escuela_msica_piendam.presentation.actividades

import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.presentation.common.UiState

data class ActividadesUiState(
    val categoriaSeleccionada: Categoria? = null,
    val actividades: UiState<List<Actividad>> = UiState.Loading,
    val esDirector: Boolean = false,
)
