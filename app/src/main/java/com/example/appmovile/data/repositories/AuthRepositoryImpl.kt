package com.example.appmovile.data.repositories

// 1. Quita la importación del DAO
// import com.example.appmovile.data.local.AuthDao
// 2. Importa el servicio de API y los DTOs
import com.example.appmovile.data.remote.AuthApiService
import com.example.appmovile.data.remote.dto.LoginRequest
import com.example.appmovile.data.remote.dto.RegisterRequest
import com.example.appmovile.domain.models.User
import com.example.appmovile.domain.repositories.AuthRepository

class AuthRepositoryImpl(
    // 3. ⬇️ Cambia la dependencia del constructor
    // private val authDao: AuthDao // <-- SE VA
    private val authApiService: AuthApiService // <-- LLEGA
) : AuthRepository {

    private var currentUserEmail: String? = null // Mantenemos la sesión en memoria

    override suspend fun register(email: String, passwordHash: String) {
        val request = RegisterRequest(email, passwordHash, passwordHash) // Asumimos DTO
        try {
            // 4. Llama a la API (Retrofit) en lugar del DAO
            authApiService.register(request)
            currentUserEmail = email // Auto-login
        } catch (e: Exception) {
            // Manejar error de red o 400 Bad Request
            throw IllegalArgumentException("Error al registrar: ${e.message}")
        }
    }

    override suspend fun login(email: String, passwordHash: String): User? {
        val request = LoginRequest(email, passwordHash)
        return try {
            // 5. Llama a la API
            val response = authApiService.login(request)

            // 6. Si tiene éxito (no hay excepción)
            currentUserEmail = response.email
            // Aquí guardarías el response.token en SharedPreferences/DataStore
            User(email = response.email)
        } catch (e: Exception) {
            // Si Retrofit lanza una excepción (ej. 401 Unauthorized), el login falla
            currentUserEmail = null
            null
        }
    }

    override suspend fun logout() {
        currentUserEmail = null
        // Aquí también borrarías el token guardado
    }

    override suspend fun getCurrentUserEmail(): String? {
        return currentUserEmail
    }
}