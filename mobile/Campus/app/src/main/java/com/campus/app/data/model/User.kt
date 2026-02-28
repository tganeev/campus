@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.campus.app.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@Serializable
data class User(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    val telegramNick: String? = null,
    val avatarUrl: String? = null,
    val role: String = "USER",
    val clubs: List<String> = emptyList(),
    val presenceStatus: String = "OFFLINE"
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)