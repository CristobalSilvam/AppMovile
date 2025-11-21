package com.example.appmovile.data.remote.external_dto

import com.google.gson.annotations.SerializedName

// Estructura simplificada que asume que la respuesta tiene un objeto 'main'
data class WeatherResponse(
    // Estructura del clima
    @SerializedName("main") val main: Main,
    @SerializedName("name") val cityName: String,
    @SerializedName("weather") val weather: List<WeatherDescription>
)

data class Main(
    @SerializedName("temp") val temperature: Double
)

data class WeatherDescription(
    @SerializedName("description") val description: String
)