@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.campus.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Presence(
    val userId: Long,
    val userName: String,
    val status: String,
    val location: String?,
    val lastSeen: String
)

@Serializable
data class PresenceUpdateRequest(
    val status: String,
    val location: String?
)