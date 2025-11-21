package com.example.appmovile.data.remote

import com.example.appmovile.data.remote.dto.AuthResponse
import com.example.appmovile.data.remote.dto.LoginRequest
import com.example.appmovile.data.remote.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.ResponseBody // Para respuestas genéricas (como "Usuario registrado")

interface AuthApiService {

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ResponseBody // El back-end devuelve un String ("Usuario registrado...")

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse // El back-end devuelve el DTO AuthResponse
}