package com.mubashir.customkeyboard.translation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mubashir.customkeyboard.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepLinkTranslationScreen(viewModel: TranslationViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Deep Link Translation", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Source Language Selector
            HorizontalLanguageSpinner(
                label = "Source Language",
                selectedLanguage = uiState.sourceLanguage,
                onLanguageSelected = { viewModel.setSourceLanguage(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Source Text Field
            OutlinedTextField(
                value = uiState.recognizedText,
                onValueChange = { viewModel.setRecognizedText(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                label = { Text("Source Text") },
                placeholder = { Text("Type something here...") },
                minLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Language Toggle / Swap Button
            IconButton(
                onClick = { viewModel.swapLanguages() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_swap),
                    contentDescription = "Swap Languages",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target Language Selector
            HorizontalLanguageSpinner(
                label = "Target Language",
                selectedLanguage = uiState.targetLanguage,
                onLanguageSelected = { viewModel.setTargetLanguage(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target Text Field
            OutlinedTextField(
                value = uiState.translatedText,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                label = { Text("Translation") },
                readOnly = true,
                minLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Translate Button
            Button(
                onClick = { viewModel.translateManual() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState.recognizedText.isNotEmpty() && !uiState.isTranslating && !uiState.isModelDownloading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (uiState.isTranslating || uiState.isModelDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (uiState.isModelDownloading) "Downloading Model..." else "Translating...",
                        style = MaterialTheme.typography.labelLarge
                    )
                } else {
                    Text(
                        text = "Translate Now",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply Button
            Button(
                onClick = { viewModel.applyTranslation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState.translatedText.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Apply",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            if (uiState.errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HorizontalLanguageSpinner(
    label: String,
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(supportedLanguages) { language ->
                val isSelected = language.code == selectedLanguage.code
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onLanguageSelected(language) },
                    color = if (isSelected) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = language.name,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
