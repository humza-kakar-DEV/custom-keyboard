package com.mubashir.customkeyboard.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun ComposeKeyboardView(
    toolbarState: ToolbarState,
    onKeyPress: (KeyAction) -> Unit,
    onMicClick: () -> Unit,
    onLanguageChanged: () -> Unit
) {
    val context = LocalContext.current
    val keyboardState = remember { KeyboardState() }
    var showLanguagePanel by remember { mutableStateOf(false) }

    var sourceCode by remember { mutableStateOf(KeyboardPreferences.getSourceLanguageCode(context)) }
    var targetCode by remember { mutableStateOf(KeyboardPreferences.getTargetLanguageCode(context)) }

    val currentRows = when (keyboardState.currentLayer) {
        KeyboardLayer.ALPHA -> qwertyAlphaRows
        KeyboardLayer.SYMBOLS -> symbolRows
        KeyboardLayer.SYMBOLS_SHIFTED -> symbolShiftedRows
    }

    KeyboardTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(KeyboardColors.keyboardBackground)
        ) {
            KeyboardToolbar(
                toolbarState = toolbarState,
                showLanguagePanel = showLanguagePanel,
                onLanguageToggle = { showLanguagePanel = !showLanguagePanel },
                onMicClick = onMicClick
            )

            // 4 rows × (52dp key + 8dp padding) + 12dp outer padding = 252dp
            val contentHeight = (KeyboardDimensions.keyHeight + KeyboardDimensions.keyVerticalPadding * 2) * 4 +
                    KeyboardDimensions.keyboardVerticalPadding * 2

            if (showLanguagePanel) {
                KeyboardLanguagePanel(
                    sourceCode = sourceCode,
                    targetCode = targetCode,
                    onSourceChanged = { code ->
                        sourceCode = code
                        KeyboardPreferences.setSourceLanguageCode(context, code)
                        onLanguageChanged()
                    },
                    onTargetChanged = { code ->
                        targetCode = code
                        KeyboardPreferences.setTargetLanguageCode(context, code)
                        onLanguageChanged()
                    },
                    onSwap = {
                        val oldSource = sourceCode
                        val oldTarget = targetCode
                        sourceCode = oldTarget
                        targetCode = oldSource
                        KeyboardPreferences.setSourceLanguageCode(context, sourceCode)
                        KeyboardPreferences.setTargetLanguageCode(context, targetCode)
                        onLanguageChanged()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(contentHeight)
                        .padding(
                            vertical = KeyboardDimensions.keyboardVerticalPadding,
                            horizontal = KeyboardDimensions.keyboardHorizontalPadding
                        )
                )
            } else {
                Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentHeight)
                    .padding(
                        vertical = KeyboardDimensions.keyboardVerticalPadding,
                        horizontal = KeyboardDimensions.keyboardHorizontalPadding
                    )
            ) {
                currentRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val totalWeight = row.sumOf { it.weight.toDouble() }.toFloat()
                        row.forEach { keyData ->
                            val displayLabel = when {
                                keyData.action is KeyAction.Character &&
                                        keyboardState.currentLayer == KeyboardLayer.ALPHA &&
                                        !keyboardState.isShifted ->
                                    keyData.label.lowercase()
                                else -> keyData.label
                            }

                            val displayKeyData = keyData.copy(label = displayLabel)

                            KeyButton(
                                keyData = displayKeyData,
                                modifier = Modifier.weight(keyData.weight / totalWeight),
                                shiftState = keyboardState.shiftState,
                                onClick = {
                                    when (keyData.action) {
                                        is KeyAction.Shift -> {
                                            keyboardState.onShiftTap()
                                        }
                                        is KeyAction.SymbolToggle -> {
                                            keyboardState.toggleSymbols()
                                        }
                                        is KeyAction.SymbolShiftToggle -> {
                                            keyboardState.toggleSymbolsShifted()
                                        }
                                        is KeyAction.Character -> {
                                            val char = if (keyboardState.isShifted &&
                                                keyboardState.currentLayer == KeyboardLayer.ALPHA
                                            ) {
                                                keyData.action.char.uppercaseChar()
                                            } else {
                                                keyData.action.char.lowercaseChar()
                                            }
                                            onKeyPress(KeyAction.Character(char))
                                            keyboardState.onCharacterTyped()
                                        }
                                        else -> onKeyPress(keyData.action)
                                    }
                                }
                            )
                        }
                    }
                }
                }
            }
        }
    }
}
