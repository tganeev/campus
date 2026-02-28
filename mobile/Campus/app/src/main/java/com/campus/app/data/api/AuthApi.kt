package com.campus.app.data.api

import com.campus.app.data.model.AuthResponse
import com.campus.app.data.model.LoginRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}