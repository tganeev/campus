package com.campus.app.presentation.presence

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PresenceScreen(
    viewModel: PresenceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Подключение к WebSocket при загрузке экрана
    LaunchedEffect(Unit) {
        // Здесь нужно получить токен из TokenManager
        // пока оставим заглушку
    }

    PresenceContent(
        uiState = uiState,
        onRefresh = viewModel::refresh
    )
}

@Composable
fun PresenceContent(
    uiState: PresenceUiState,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Статистика
        StatisticsCards(
            onlineCount = uiState.onlineCount,
            activeZones = uiState.onlineUsers.map { it.location }.distinct().size
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Заголовок
        Text(
            text = "Кто сейчас в кампусе",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Список онлайн пользователей
        when {
            uiState.isLoading && uiState.onlineUsers.isEmpty() -> {
                LoadingIndicator()
            }
            uiState.error != null -> {
                ErrorMessage(
                    message = uiState.error,
                    onRetry = onRefresh
                )
            }
            uiState.onlineUsers.isEmpty() -> {
                EmptyStateMessage()
            }
            else -> {
                OnlineUsersList(users = uiState.onlineUsers)
            }
        }
    }
}

@Composable
fun StatisticsCards(
    onlineCount: Int,
    activeZones: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            icon = "👥",
            title = "Сейчас в кампусе",
            value = "$onlineCount чел",
            modifier = Modifier.weight(1f)
        )

        StatCard(
            icon = "📍",
            title = "Активные зоны",
            value = "$activeZones мест",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    icon: String,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = icon,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun OnlineUsersList(
    users: List<com.campus.app.data.model.Presence>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            UserCard(user = user)
        }
    }
}

@Composable
fun UserCard(
    user: com.campus.app.data.model.Presence
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Статус индикатор
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(end = 8.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = if (user.status == "ONLINE")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ) {}
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.userName,
                    style = MaterialTheme.typography.titleMedium
                )

                if (!user.location.isNullOrBlank()) {
                    Text(
                        text = "📍 ${user.location}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = formatTimeCompat(user.lastSeen),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(
    message: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message ?: "Произошла ошибка",
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}

@Composable
fun EmptyStateMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Сейчас никого нет в кампусе",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Совместимая с API 24 функция форматирования времени
fun formatTimeCompat(isoString: String): String {
    return try {
        // Для API 24+ используем SimpleDateFormat (совместим с API 24)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(isoString) ?: return ""

        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        outputFormat.format(date)
    } catch (e: Exception) {
        ""
    }
}