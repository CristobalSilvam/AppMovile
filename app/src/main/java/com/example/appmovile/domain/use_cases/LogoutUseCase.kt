package com.example.appmovile.domain.use_cases

import com.example.appmovile.domain.repositories.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
