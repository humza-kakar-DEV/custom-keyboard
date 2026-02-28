package com.kakar.customkeyboard.translation

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TranslationScreen(viewModel: TranslationViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var targetDropdownExpanded by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.onPause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Live Translation",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LanguageSelectionRow(
                sourceLanguage = uiState.sourceLanguage,
                targetLanguage = uiState.targetLanguage,
                sourceDropdownExpanded = sourceDropdownExpanded,
                targetDropdownExpanded = targetDropdownExpanded,
                onSourceDropdownToggle = { sourceDropdownExpanded = it },
                onTargetDropdownToggle = { targetDropdownExpanded = it },
                onSourceLanguageSelected = { viewModel.setSourceLanguage(it) },
                onTargetLanguageSelected = { viewModel.setTargetLanguage(it) }
            )

            if (uiState.isModelDownloading) {
                ModelDownloadingIndicator()
            }

            if (uiState.errorMessage.isNotEmpty()) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (!recordAudioPermissionState.status.isGranted) {
                PermissionRequestContent(
                    onRequestPermission = { recordAudioPermissionState.launchPermissionRequest() }
                )
            } else {
                ListeningContent(
                    uiState = uiState,
                    onMicClick = {
                        if (uiState.isListening) viewModel.stopListening()
                        else viewModel.downloadModelAndStartListening()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun LanguageSelectionRow(
    sourceLanguage: Language,
    targetLanguage: Language,
    sourceDropdownExpanded: Boolean,
    targetDropdownExpanded: Boolean,
    onSourceDropdownToggle: (Boolean) -> Unit,
    onTargetDropdownToggle: (Boolean) -> Unit,
    onSourceLanguageSelected: (Language) -> Unit,
    onTargetLanguageSelected: (Language) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            OutlinedButton(
                onClick = { onSourceDropdownToggle(true) },
                modifier = Modifier.width(140.dp)
            ) {
                Text(sourceLanguage.name, maxLines = 1)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = sourceDropdownExpanded,
                onDismissRequest = { onSourceDropdownToggle(false) }
            ) {
                supportedLanguages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.name) },
                        onClick = {
                            onSourceLanguageSelected(language)
                            onSourceDropdownToggle(false)
                        }
                    )
                }
            }
        }

        Text(
            text = "\u2192",
            style = MaterialTheme.typography.headlineMedium
        )

        Box {
            OutlinedButton(
                onClick = { onTargetDropdownToggle(true) },
                modifier = Modifier.width(140.dp)
            ) {
                Text(targetLanguage.name, maxLines = 1)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = targetDropdownExpanded,
                onDismissRequest = { onTargetDropdownToggle(false) }
            ) {
                supportedLanguages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(language.name) },
                        onClick = {
                            onTargetLanguageSelected(language)
                            onTargetDropdownToggle(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelDownloadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            strokeWidth = 2.dp
        )
        Text(
            text = "Downloading translation model...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun PermissionRequestContent(onRequestPermission: () -> Unit) {
    Text(
        text = "Microphone permission required",
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    IconButton(
        onClick = onRequestPermission,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Request Permission",
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ListeningContent(
    uiState: TranslationUiState,
    onMicClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(bottom = 32.dp)
    ) {
        IconButton(
            onClick = onMicClick,
            enabled = !uiState.isModelDownloading,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    when {
                        uiState.isListening -> MaterialTheme.colorScheme.errorContainer
                        uiState.isModelDownloading -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                )
        ) {
            Icon(
                imageVector = if (uiState.isListening) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (uiState.isListening) "Stop" else "Start",
                modifier = Modifier.size(60.dp),
                tint = when {
                    uiState.isListening -> MaterialTheme.colorScheme.error
                    uiState.isModelDownloading -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.primary
                }
            )
        }
    }

    Text(
        text = when {
            uiState.isModelDownloading -> "Model downloading, please wait..."
            uiState.isListening -> "Listening..."
            uiState.isTranslating -> "Translating..."
            else -> "Tap microphone to speak"
        },
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 24.dp)
    )

    if (uiState.recognizedText.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Recognized (${uiState.sourceLanguage.name}):",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = uiState.recognizedText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    if (uiState.translatedText.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Translation (${uiState.targetLanguage.name}):",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = uiState.translatedText,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
