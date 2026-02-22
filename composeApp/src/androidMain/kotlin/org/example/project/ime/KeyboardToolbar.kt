package org.example.project.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
        // Left icon: Translate / Keyboard toggle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onLanguageToggle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (showLanguagePanel)
                    Icons.Filled.Keyboard
                else
                    Icons.Filled.Translate,
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
                imageVector = if (toolbarState.isListening) Icons.Filled.Stop else Icons.Filled.Mic,
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
