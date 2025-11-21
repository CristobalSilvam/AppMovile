package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.models.Weather
import com.example.appmovile.domain.repositories.WeatherRepository

class GetWeatherUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String): Weather {
        return repository.getCurrentWeather(city)
    }
}