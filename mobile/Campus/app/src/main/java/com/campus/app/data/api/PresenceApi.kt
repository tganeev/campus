package com.campus.app.data.api

import com.campus.app.data.model.Presence
import retrofit2.http.GET
import retrofit2.http.Header

interface PresenceApi {
    @GET("api/v1/presence/online")
    suspend fun getOnlineUsers(): List<Presence>

    @GET("api/v1/presence/online/count")
    suspend fun getOnlineCount(): Int
}