package org.example.project.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ComposeKeyboardView(
    onKeyPress: (KeyAction) -> Unit
) {
    val keyboardState = remember { KeyboardState() }

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