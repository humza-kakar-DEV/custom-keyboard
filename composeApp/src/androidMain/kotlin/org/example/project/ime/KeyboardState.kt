package org.example.project.ime

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ShiftState { OFF, SHIFTED, CAPS_LOCK }

class KeyboardState {
    var shiftState by mutableStateOf(ShiftState.OFF)
        private set
    var currentLayer by mutableStateOf(KeyboardLayer.ALPHA)
        private set

    private var lastShiftTapTime = 0L

    fun onShiftTap() {
        val now = System.currentTimeMillis()
        shiftState = when (shiftState) {
            ShiftState.OFF -> {
                lastShiftTapTime = now
                ShiftState.SHIFTED
            }
            ShiftState.SHIFTED -> {
                if (now - lastShiftTapTime < 300) {
                    ShiftState.CAPS_LOCK
                } else {
                    ShiftState.OFF
                }
            }
            ShiftState.CAPS_LOCK -> ShiftState.OFF
        }
    }

    fun onCharacterTyped() {
        if (shiftState == ShiftState.SHIFTED) {
            shiftState = ShiftState.OFF
        }
    }

    fun toggleSymbols() {
        currentLayer = when (currentLayer) {
            KeyboardLayer.ALPHA -> KeyboardLayer.SYMBOLS
            KeyboardLayer.SYMBOLS -> KeyboardLayer.ALPHA
            KeyboardLayer.SYMBOLS_SHIFTED -> KeyboardLayer.ALPHA
        }
    }

    fun toggleSymbolsShifted() {
        currentLayer = when (currentLayer) {
            KeyboardLayer.SYMBOLS -> KeyboardLayer.SYMBOLS_SHIFTED
            KeyboardLayer.SYMBOLS_SHIFTED -> KeyboardLayer.SYMBOLS
            else -> currentLayer
        }
    }

    val isShifted: Boolean
        get() = shiftState == ShiftState.SHIFTED || shiftState == ShiftState.CAPS_LOCK
}