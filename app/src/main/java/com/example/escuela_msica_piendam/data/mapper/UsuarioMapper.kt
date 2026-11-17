package com.example.escuela_msica_piendam.data.mapper

import com.example.escuela_msica_piendam.data.remote.dto.UsuarioDto
import com.example.escuela_msica_piendam.domain.model.Categoria
import com.example.escuela_msica_piendam.domain.model.Rol
import com.example.escuela_msica_piendam.domain.model.Usuario


/** Traduce el JSON del usuario al modelo de dominio. */
fun UsuarioDto.aDominio(): Usuario = Usuario(
    id = id,
    nombre = nombre,
    numCelular = numCelular,
    rol = Rol.desdeApi(rol),
    categoria = categoria?.let { Categoria.desdeApi(it) },
    instrumento = instrumento,
)