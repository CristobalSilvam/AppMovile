package com.example.appmovile.data.remote

import com.example.appmovile.data.remote.external_dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

// 💡 URL Base de ejemplo: https://api.openweathermap.org/data/2.5/
interface WeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String, // Parámetro para la ciudad
        @Query("units") units: String = "metric", // Temperatura en Celsius
        @Query("appid") apiKey: String = "385ded62fb1d532a12602126d0cac5e2" //clave api
    ): WeatherResponse
}