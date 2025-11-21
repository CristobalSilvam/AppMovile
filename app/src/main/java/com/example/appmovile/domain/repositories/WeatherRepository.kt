package com.example.appmovile.domain.repositories

import com.example.appmovile.data.remote.WeatherApiService
import com.example.appmovile.domain.models.Weather // Modelo limpio

// Interfaz para la capa Domain
interface WeatherRepository {
    suspend fun getCurrentWeather(city: String): Weather
}

// Implementación (Puede ir en el mismo archivo o en data/repositories)
class WeatherRepositoryImpl(
    private val apiService: WeatherApiService
) : WeatherRepository {

    override suspend fun getCurrentWeather(city: String): Weather {
        // Llama a la API
        val response = apiService.getCurrentWeather(city = city)

        // Mapeo simple de la respuesta a un modelo limpio de dominio
        return Weather(
            city = response.cityName,
            temperature = response.main.temperature,
            description = response.weather.firstOrNull()?.description ?: "Clima desconocido"
        )
    }
}