package com.example.escuela_msica_piendam.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Formato único de error del backend: { "status": 409, "mensaje": "...", "detalles": [] } */
data class ErrorDto(
    @SerializedName("status") val status: Int?,
    @SerializedName("mensaje") val mensaje: String?,
    @SerializedName("detalles") val detalles: List<String>?,
)
