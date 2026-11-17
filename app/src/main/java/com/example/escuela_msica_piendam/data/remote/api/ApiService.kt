package com.example.escuela_msica_piendam.data.remote.api

import com.example.escuela_msica_piendam.data.remote.dto.ActividadDto
import com.example.escuela_msica_piendam.data.remote.dto.AsistenciaRequestDto
import com.example.escuela_msica_piendam.data.remote.dto.CatalogosDto
import com.example.escuela_msica_piendam.data.remote.dto.CrearActividadRequestDto
import com.example.escuela_msica_piendam.data.remote.dto.CrearInscripcionRequestDto
import com.example.escuela_msica_piendam.data.remote.dto.InscripcionDto
import com.example.escuela_msica_piendam.data.remote.dto.LoginRequestDto
import com.example.escuela_msica_piendam.data.remote.dto.LoginResponseDto
import com.example.escuela_msica_piendam.data.remote.dto.RegistroRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /** Responde 201 con la misma forma que el login: { usuario, token }. */
    @POST("api/auth/registro")
    suspend fun registrar(@Body cuerpo: RegistroRequestDto): LoginResponseDto

    // --- Auth (público) y catálogos (público) ---
    @POST("api/auth/login")
    suspend fun login(@Body cuerpo: LoginRequestDto): LoginResponseDto

    @GET("api/catalogos")
    suspend fun obtenerCatalogos(): CatalogosDto

    // --- Actividades ---
    @GET("api/actividades")
    suspend fun obtenerActividades(@Query("categoria") categoria: String? = null): List<ActividadDto>

    @GET("api/actividades/{id}")
    suspend fun obtenerActividad(@Path("id") id: String): ActividadDto

    /** Solo director (con otro rol responde 403). */
    @POST("api/actividades")
    suspend fun crearActividad(@Body cuerpo: CrearActividadRequestDto): ActividadDto

    // --- Inscripciones ---
    @GET("api/inscripciones")
    suspend fun obtenerInscripciones(@Query("id_actividad") idActividad: String): List<InscripcionDto>

    @POST("api/inscripciones")
    suspend fun inscribirse(@Body cuerpo: CrearInscripcionRequestDto): InscripcionDto

    /** Responde 204 sin cuerpo; por eso devuelve Response<Unit> y el repositorio revisa isSuccessful. */
    @DELETE("api/inscripciones/{id}")
    suspend fun cancelarInscripcion(@Path("id") id: String): Response<Unit>

    /** Solo director. */
    @PATCH("api/inscripciones/{id}/asistencia")
    suspend fun registrarAsistencia(
        @Path("id") id: String,
        @Body cuerpo: AsistenciaRequestDto,
    ): InscripcionDto
}