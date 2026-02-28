// data/repository/AuthRepository.kt
package com.campus.app.data.repository

import com.campus.app.data.api.AuthApi
import com.campus.app.data.local.TokenManager
import com.campus.app.data.model.AuthResponse
import com.campus.app.data.model.LoginRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) {

    val isLoggedIn: Flow<Boolean> = tokenManager.isLoggedIn

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            tokenManager.saveToken(response.token)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }
}