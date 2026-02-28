package com.campus.app.domain.usecase

import com.campus.app.data.model.AuthResponse
import com.campus.app.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        // Здесь можно добавить валидацию email/пароля
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email и пароль не могут быть пустыми"))
        }

        return authRepository.login(email, password)
    }
}