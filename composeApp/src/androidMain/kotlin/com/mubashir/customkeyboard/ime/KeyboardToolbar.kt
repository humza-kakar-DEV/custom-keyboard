package com.mubashir.customkeyboard.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mubashir.customkeyboard.R

@Composable
fun KeyboardToolbar(
    toolbarState: ToolbarState,
    showLanguagePanel: Boolean,
    onLanguageToggle: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(KeyboardDimensions.toolbarHeight)
            .background(KeyboardColors.toolbarBackground)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onLanguageToggle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (showLanguagePanel) R.drawable.ic_keyboard else R.drawable.ic_language
                ),
                contentDescription = if (showLanguagePanel) "Show keyboard" else "Language settings",
                modifier = Modifier.size(KeyboardDimensions.toolbarIconSize),
                tint = if (showLanguagePanel)
                    KeyboardColors.toolbarIconActiveColor
                else
                    KeyboardColors.toolbarIconColor
            )
        }

        if (toolbarState.displayText.isNotEmpty()) {
            Text(
                text = toolbarState.displayText,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                fontSize = 14.sp,
                color = if (toolbarState.isListening || toolbarState.isTranslating)
                    KeyboardColors.toolbarIconActiveColor
                else
                    KeyboardColors.keyTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Box(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .then(
                    if (toolbarState.isListening)
                        Modifier.background(KeyboardColors.toolbarIconActiveColor.copy(alpha = 0.1f))
                    else
                        Modifier
                )
                .clickable(onClick = onMicClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (toolbarState.isListening) R.drawable.ic_stop else R.drawable.ic_mic
                ),
                contentDescription = if (toolbarState.isListening) "Stop listening" else "Voice input",
                modifier = Modifier.size(KeyboardDimensions.toolbarIconSize),
                tint = if (toolbarState.isListening)
                    KeyboardColors.toolbarIconActiveColor
                else
                    KeyboardColors.toolbarIconColor
            )
        }
    }
}
