package com.example.escuela_msica_piendam.data.mapper

import com.example.escuela_msica_piendam.data.remote.dto.ActividadDto
import com.example.escuela_msica_piendam.data.remote.dto.CrearActividadRequestDto
import com.example.escuela_msica_piendam.domain.model.Actividad
import com.example.escuela_msica_piendam.domain.model.NuevaActividad
import com.example.escuela_msica_piendam.domain.model.Categoria


fun ActividadDto.aDominio(): Actividad = Actividad(
    id = id,
    titulo = titulo,
    descripcion = descripcion,
    categoria = Categoria.desdeApi(categoriaDirigida),
    instrumentoDirigido = instrumentoDirigido,
    fecha = fecha,
    hora = hora,
    lugar = lugar,
    indicacionesVestimenta = indicacionesVestimenta,
    cupoMaximo = cupoMaximo,
    inscritos = inscritos,
)

/** Los opcionales vacíos viajan como null (Gson los omite) para que el backend no guarde textos en blanco. */
fun NuevaActividad.aDto(): CrearActividadRequestDto = CrearActividadRequestDto(
    titulo = titulo.trim(),
    descripcion = descripcion.trim(),
    categoriaDirigida = categoria.valorApi,
    instrumentoDirigido = instrumentoDirigido,
    fecha = fecha,
    hora = hora,
    lugar = lugar?.trim()?.takeIf { it.isNotEmpty() },
    indicacionesVestimenta = indicacionesVestimenta?.trim()?.takeIf { it.isNotEmpty() },
    cupoMaximo = cupoMaximo,
)