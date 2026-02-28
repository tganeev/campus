package com.campus.app.presentation.presence

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campus.app.data.api.PresenceApi
import com.campus.app.data.model.Presence
import com.campus.app.data.websocket.WebSocketEvent
import com.campus.app.data.websocket.WebSocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresenceViewModel @Inject constructor(
    private val presenceApi: PresenceApi,
    private val webSocketManager: WebSocketManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PresenceUiState())
    val uiState: StateFlow<PresenceUiState> = _uiState.asStateFlow()

    init {
        loadOnlineUsers()
        observeWebSocket()
    }

    /**
     * Загрузка списка онлайн пользователей через REST API
     */
    fun loadOnlineUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val users = presenceApi.getOnlineUsers()
                val count = presenceApi.getOnlineCount()

                _uiState.update {
                    it.copy(
                        onlineUsers = users,
                        onlineCount = count,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Ошибка загрузки: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Обновление списка (Pull-to-refresh)
     */
    fun refresh() {
        loadOnlineUsers()
    }

    /**
     * Наблюдение за WebSocket событиями для real-time обновлений
     */
    private fun observeWebSocket() {
        viewModelScope.launch {
            webSocketManager.events.collect { event ->
                when (event) {
                    is WebSocketEvent.Connected -> {
                        // Можно показать уведомление о подключении
                    }

                    is WebSocketEvent.PresenceAdd -> {
                        // Добавление нового пользователя в онлайн
                        _uiState.update { state ->
                            val newList = state.onlineUsers.toMutableList()
                            if (newList.none { it.userId == event.presence.userId }) {
                                newList.add(event.presence)
                            }
                            state.copy(
                                onlineUsers = newList,
                                onlineCount = newList.size
                            )
                        }
                    }

                    is WebSocketEvent.PresenceUpdate -> {
                        // Обновление статуса/локации существующего пользователя
                        _uiState.update { state ->
                            val newList = state.onlineUsers.map { user ->
                                if (user.userId == event.presence.userId) event.presence else user
                            }
                            state.copy(onlineUsers = newList)
                        }
                    }

                    is WebSocketEvent.PresenceRemove -> {
                        // Удаление пользователя из онлайн (стал офлайн)
                        _uiState.update { state ->
                            val newList = state.onlineUsers.filter { it.userId != event.userId }
                            state.copy(
                                onlineUsers = newList,
                                onlineCount = newList.size
                            )
                        }
                    }

                    is WebSocketEvent.Error -> {
                        // Обработка ошибки WebSocket
                        _uiState.update { state ->
                            state.copy(
                                error = "WebSocket error: ${event.message}"
                            )
                        }
                    }

                    is WebSocketEvent.Closed -> {
                        // WebSocket закрыт
                    }
                }
            }
        }
    }

    /**
     * Подключение к WebSocket с JWT токеном
     */
    fun connectWebSocket(token: String) {
        webSocketManager.connect(token)
    }

    /**
     * Отключение WebSocket (вызывается при выходе)
     */
    fun disconnectWebSocket() {
        webSocketManager.disconnect()
    }

    /**
     * Автоматическое обновление каждые 30 секунд (как запасной вариант)
     */
    fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(30_000) // 30 секунд
                loadOnlineUsers()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnectWebSocket()
    }
}

/**
 * Состояние UI для экрана присутствия
 */
data class PresenceUiState(
    val onlineUsers: List<Presence> = emptyList(),
    val onlineCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)