package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.local.PersonaRepository
import com.example.service.AiChatService
import com.example.service.SpeechManager
import com.example.service.configureAppCheck
import com.example.ui.navigation.Screen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PersonaGalleryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartMatchScreen
import com.example.ui.screens.VideoCallScreen
import com.example.ui.theme.AiConnectTheme

class MainActivity : ComponentActivity() {

    private lateinit var speechManager: SpeechManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        speechManager = SpeechManager(this)
        val repository = PersonaRepository()
        val aiService = AiChatService()

        setContent {
            AiConnectTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(
                            repository = repository,
                            aiService = aiService,
                            speechManager = speechManager
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechManager.shutdown()
    }
}

@Composable
fun AppNavigation(
    repository: PersonaRepository,
    aiService: AiChatService,
    speechManager: SpeechManager
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                repository = repository,
                onStartCall = { personaId ->
                    navController.navigate(Screen.VideoCall.createRoute(personaId))
                },
                onOpenChat = { personaId ->
                    navController.navigate(Screen.Chat.createRoute(personaId))
                },
                onNavigateToMatch = {
                    navController.navigate(Screen.SmartMatch.route)
                },
                onNavigateToGallery = {
                    navController.navigate(Screen.PersonaGallery.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.PersonaGallery.route) {
            PersonaGalleryScreen(
                repository = repository,
                onBackClick = {
                    navController.popBackStack()
                },
                onStartCall = { personaId ->
                    navController.navigate(Screen.VideoCall.createRoute(personaId))
                },
                onOpenChat = { personaId ->
                    navController.navigate(Screen.Chat.createRoute(personaId))
                }
            )
        }

        composable(
            route = Screen.VideoCall.ROUTE,
            arguments = listOf(navArgument("personaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val personaId = backStackEntry.arguments?.getString("personaId") ?: "nova"
            VideoCallScreen(
                personaId = personaId,
                repository = repository,
                aiService = aiService,
                speechManager = speechManager,
                onEndCall = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Chat.ROUTE,
            arguments = listOf(navArgument("personaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val personaId = backStackEntry.arguments?.getString("personaId") ?: "nova"
            ChatScreen(
                personaId = personaId,
                repository = repository,
                aiService = aiService,
                speechManager = speechManager,
                onBackClick = {
                    navController.popBackStack()
                },
                onStartCall = { id ->
                    navController.navigate(Screen.VideoCall.createRoute(id))
                }
            )
        }

        composable(Screen.SmartMatch.route) {
            SmartMatchScreen(
                repository = repository,
                onBackClick = {
                    navController.popBackStack()
                },
                onStartCall = { personaId ->
                    navController.navigate(Screen.VideoCall.createRoute(personaId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
