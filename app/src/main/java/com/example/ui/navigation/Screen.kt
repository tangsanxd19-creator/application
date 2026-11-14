package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object SmartMatch : Screen("smart_match")
    data object PersonaGallery : Screen("persona_gallery")
    data object Settings : Screen("settings")
    data class VideoCall(val personaId: String) : Screen("video_call/$personaId") {
        companion object {
            const val ROUTE = "video_call/{personaId}"
            fun createRoute(personaId: String) = "video_call/$personaId"
        }
    }
    data class Chat(val personaId: String) : Screen("chat/$personaId") {
        companion object {
            const val ROUTE = "chat/{personaId}"
            fun createRoute(personaId: String) = "chat/$personaId"
        }
    }
}
