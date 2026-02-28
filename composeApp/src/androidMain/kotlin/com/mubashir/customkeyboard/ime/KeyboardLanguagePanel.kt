package com.mubashir.customkeyboard.ime

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.mubashir.customkeyboard.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mubashir.customkeyboard.translation.Language
import com.mubashir.customkeyboard.translation.supportedLanguages

@Composable
fun LanguageCard(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) KeyboardColors.toolbarIconActiveColor else KeyboardColors.keyBackground
    val textColor = if (isSelected) Color.White else KeyboardColors.keyTextColor

    Box(
        modifier = modifier
            .width(90.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = language.name,
            style = MaterialTheme.typography.bodySmall,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HorizontalLanguageSelector(
    label: String,
    selectedCode: String,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(supportedLanguages) { language ->
                LanguageCard(
                    language = language,
                    isSelected = language.code == selectedCode,
                    onClick = { onLanguageSelected(language.code) }
                )
            }
        }
    }
}

@Composable
fun KeyboardLanguagePanel(
    sourceCode: String,
    targetCode: String,
    onSourceChanged: (String) -> Unit,
    onTargetChanged: (String) -> Unit,
    onSwap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onSwap,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .clip(CircleShape)
                    .background(KeyboardColors.toolbarBackground)
            ) {
                Icon(
                painter = painterResource(id = R.drawable.ic_swap),
                    contentDescription = "Swap languages",
                    modifier = Modifier.size(24.dp),
                    tint = KeyboardColors.toolbarIconActiveColor
                )
            }
        }

        HorizontalLanguageSelector(
            label = "From (Source)",
            selectedCode = sourceCode,
            onLanguageSelected = onSourceChanged,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        HorizontalLanguageSelector(
            label = "To (Target)",
            selectedCode = targetCode,
            onLanguageSelected = onTargetChanged
        )

        Box(modifier = Modifier.weight(1f))
    }
}
