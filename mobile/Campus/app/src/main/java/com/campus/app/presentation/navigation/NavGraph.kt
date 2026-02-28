package com.campus.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Presence : Screen(
        route = "presence",
        title = "В кампусе",
        icon = Icons.Default.Home
    )

    data object Planning : Screen(
        route = "planning",
        title = "Планирование",
        icon = Icons.Default.List
    )

    data object Profile : Screen(
        route = "profile",
        title = "Профиль",
        icon = Icons.Default.Person
    )
}

val screens = listOf(
    Screen.Presence,
    Screen.Planning,
    Screen.Profile
)