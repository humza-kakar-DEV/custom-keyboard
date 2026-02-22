package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.ime.KeyboardSetupScreen
import org.example.project.ime.OnboardingPreferences
import org.example.project.ime.OnboardingScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
}

@Composable
fun AppNavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val startDestination = if (OnboardingPreferences.isOnboardingCompleted(context)) {
        Routes.HOME
    } else {
        Routes.ONBOARDING
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            KeyboardSetupScreen()
        }
    }
}
