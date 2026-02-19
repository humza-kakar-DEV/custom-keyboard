package org.example.project.ime

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object KeyboardColors {
    val keyboardBackground = Color(0xFFD9DDE0)
    val keyBackground = Color(0xFFFFFFFF)
    val specialKeyBackground = Color(0xFFADB5BD)
    val keyPressedBackground = Color(0xFFBEC3C8)
    val keyTextColor = Color(0xFF1B1B1F)
    val shiftActiveColor = Color(0xFF1A73E8)
    val enterKeyBackground = Color(0xFF1A73E8)
    val enterKeyTextColor = Color.White
}

object KeyboardDimensions {
    val keyHeight = 52.dp
    val keyHorizontalPadding = 3.dp
    val keyVerticalPadding = 4.dp
    val keyCornerRadius = 8.dp
    val keyFontSize = 20.sp
    val specialKeyFontSize = 13.sp
    val iconSize = 22.dp
    val keyboardVerticalPadding = 6.dp
    val keyboardHorizontalPadding = 3.dp
    val keyShadowElevation = 1.dp
}

@Composable
fun KeyboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            surface = KeyboardColors.keyboardBackground,
            onSurface = KeyboardColors.keyTextColor,
        ),
        content = content
    )
}