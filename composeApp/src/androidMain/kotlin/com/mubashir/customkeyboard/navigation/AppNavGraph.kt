package com.mubashir.customkeyboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mubashir.customkeyboard.ime.KeyboardSetupScreen
import com.mubashir.customkeyboard.ime.OnboardingPreferences
import com.mubashir.customkeyboard.ime.OnboardingScreen
import com.mubashir.customkeyboard.translation.DeepLinkTranslationScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val DEEP_LINK_TRANSLATION = "deep_link_translation"
}

@Composable
fun AppNavGraph() {
    val context = LocalContext.current
    val navController = rememberNavController()

    val startDestination = if (OnboardingPreferences.isOnboardingCompleted(context)) {
        Routes.DEEP_LINK_TRANSLATION
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
                    navController.navigate(Routes.DEEP_LINK_TRANSLATION) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            KeyboardSetupScreen()
        }

        composable(Routes.DEEP_LINK_TRANSLATION) {
            DeepLinkTranslationScreen()
        }
    }
}
