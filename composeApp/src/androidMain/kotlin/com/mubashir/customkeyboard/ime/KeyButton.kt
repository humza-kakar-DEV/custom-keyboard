package com.mubashir.customkeyboard.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mubashir.customkeyboard.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun KeyButton(
    keyData: KeyData,
    modifier: Modifier = Modifier,
    shiftState: ShiftState = ShiftState.OFF,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scope = rememberCoroutineScope()

    val isDelete = keyData.action is KeyAction.Delete
    val isEnter = keyData.action is KeyAction.Enter
    val isShift = keyData.action is KeyAction.Shift
    val isShiftActive = isShift && shiftState != ShiftState.OFF

    val backgroundColor = when {
        isPressed -> KeyboardColors.keyPressedBackground
        isEnter -> KeyboardColors.enterKeyBackground
        isShiftActive -> KeyboardColors.shiftActiveColor
        keyData.isSpecial -> KeyboardColors.specialKeyBackground
        else -> KeyboardColors.keyBackground
    }

    val textColor = when {
        isEnter -> KeyboardColors.enterKeyTextColor
        isShiftActive -> KeyboardColors.enterKeyTextColor
        else -> KeyboardColors.keyTextColor
    }

    val shape = RoundedCornerShape(KeyboardDimensions.keyCornerRadius)

    val baseModifier = modifier
        .padding(
            horizontal = KeyboardDimensions.keyHorizontalPadding,
            vertical = KeyboardDimensions.keyVerticalPadding
        )
        .height(KeyboardDimensions.keyHeight)
        .shadow(KeyboardDimensions.keyShadowElevation, shape)
        .clip(shape)
        .background(backgroundColor)

    val finalModifier = if (isDelete) {
        baseModifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    onClick()
                    val job = scope.launch {
                        delay(400)
                        while (true) {
                            onClick()
                            delay(50)
                        }
                    }
                    tryAwaitRelease()
                    job.cancel()
                }
            )
        }
    } else {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    }

    Box(
        modifier = finalModifier,
        contentAlignment = Alignment.Center
    ) {
        when {
            isShift -> {
                val iconRes = if (shiftState == ShiftState.CAPS_LOCK)
                    R.drawable.ic_capslock
                else
                    R.drawable.ic_shift
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Shift",
                    modifier = Modifier.size(KeyboardDimensions.iconSize),
                    tint = textColor
                )
            }
            isDelete -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete",
                    modifier = Modifier.size(KeyboardDimensions.iconSize),
                    tint = textColor
                )
            }
            isEnter -> {
                Icon(
                    painter = painterResource(id = R.drawable.ic_enter),
                    contentDescription = "Enter",
                    modifier = Modifier.size(KeyboardDimensions.iconSize),
                    tint = textColor
                )
            }
            keyData.action is KeyAction.Space -> {

            }
            else -> {
                Text(
                    text = keyData.label,
                    fontSize = if (keyData.isSpecial)
                        KeyboardDimensions.specialKeyFontSize
                    else
                        KeyboardDimensions.keyFontSize,
                    color = textColor,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}