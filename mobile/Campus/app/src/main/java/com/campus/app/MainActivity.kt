package com.campus.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.campus.app.data.repository.AuthRepository
import com.campus.app.presentation.auth.LoginScreen
import com.campus.app.presentation.navigation.BottomNavigationBar
import com.campus.app.presentation.navigation.Screen
import com.campus.app.presentation.presence.PresenceScreen
import com.campus.app.presentation.planning.PlanningScreen  // Добавьте импорт
import com.campus.app.presentation.profile.ProfileScreen    // Добавьте импорт
import com.campus.app.presentation.theme.CampusTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CampusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CampusApp(authRepository)
                }
            }
        }
    }
}

@Composable
fun CampusApp(
    authRepository: AuthRepository
) {
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
    val navController = rememberNavController()

    if (isLoggedIn) {
        MainScreenWithNav(navController)
    } else {
        LoginScreen(
            onLoginSuccess = { /* Уже обрабатывается через isLoggedIn */ }
        )
    }
}

@Composable
fun MainScreenWithNav(
    navController: NavHostController
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route ?: Screen.Presence.route

    Column {
        NavHost(
            navController = navController,
            startDestination = Screen.Presence.route,
            modifier = Modifier.weight(1f)
        ) {
            composable(Screen.Presence.route) {
                PresenceScreen()
            }

            composable(Screen.Planning.route) {
                // Временно показываем заглушку
                PlanningScreen()
            }

            composable(Screen.Profile.route) {
                // Временно показываем заглушку
                ProfileScreen()
            }
        }

        BottomNavigationBar(
            currentRoute = currentRoute,
            onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
    }
}