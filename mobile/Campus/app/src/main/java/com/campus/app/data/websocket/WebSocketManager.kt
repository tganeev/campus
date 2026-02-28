@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.campus.app.data.websocket

import android.util.Log
import com.campus.app.data.model.Presence
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class WebSocketMessage(
    val type: String,
    val data: Presence? = null
)

@Singleton
class WebSocketManager @Inject constructor() {

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private var webSocket: WebSocket? = null
    private val _events = MutableSharedFlow<WebSocketEvent>()
    val events: SharedFlow<WebSocketEvent> = _events.asSharedFlow()

    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun connect(token: String) {
        val request = Request.Builder()
            .url("wss://campus-mydm.onrender.com/ws")
            .addHeader("Authorization", "Bearer $token")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connected")
                scope.launch { _events.emit(WebSocketEvent.Connected) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received: $text")
                try {
                    val message = json.decodeFromString<WebSocketMessage>(text)
                    scope.launch {
                        when (message.type) {
                            "presence_update" -> {
                                message.data?.let {
                                    _events.emit(WebSocketEvent.PresenceUpdate(it))
                                }
                            }
                            "presence_add" -> {
                                message.data?.let {
                                    _events.emit(WebSocketEvent.PresenceAdd(it))
                                }
                            }
                            "presence_remove" -> {
                                message.data?.let {
                                    _events.emit(WebSocketEvent.PresenceRemove(it.userId))
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing message", e)
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closed: $reason")
                scope.launch { _events.emit(WebSocketEvent.Closed) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error", t)
                scope.launch { _events.emit(WebSocketEvent.Error(t.message)) }
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
    }
}

sealed class WebSocketEvent {
    data object Connected : WebSocketEvent()
    data class PresenceUpdate(val presence: Presence) : WebSocketEvent()
    data class PresenceAdd(val presence: Presence) : WebSocketEvent()
    data class PresenceRemove(val userId: Long) : WebSocketEvent()
    data object Closed : WebSocketEvent()
    data class Error(val message: String?) : WebSocketEvent()
}