package org.example.project

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TranslationApp()
            }
        }
    }
}

data class Language(val code: String, val name: String, val mlKitCode: String)

val supportedLanguages = listOf(
    Language("en", "English", TranslateLanguage.ENGLISH),
    Language("es", "Spanish", TranslateLanguage.SPANISH),
    Language("fr", "French", TranslateLanguage.FRENCH),
    Language("de", "German", TranslateLanguage.GERMAN),
    Language("it", "Italian", TranslateLanguage.ITALIAN),
    Language("pt", "Portuguese", TranslateLanguage.PORTUGUESE),
    Language("ru", "Russian", TranslateLanguage.RUSSIAN),
    Language("zh", "Chinese", TranslateLanguage.CHINESE),
    Language("ja", "Japanese", TranslateLanguage.JAPANESE),
    Language("ko", "Korean", TranslateLanguage.KOREAN),
    Language("ar", "Arabic", TranslateLanguage.ARABIC),
    Language("hi", "Hindi", TranslateLanguage.HINDI),
    Language("tr", "Turkish", TranslateLanguage.TURKISH),
    Language("pl", "Polish", TranslateLanguage.POLISH),
    Language("nl", "Dutch", TranslateLanguage.DUTCH),
    Language("ur", "Urdu", TranslateLanguage.URDU)
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TranslationApp() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val recordAudioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var sourceLanguage by remember { mutableStateOf(supportedLanguages[0]) }
    var targetLanguage by remember { mutableStateOf(supportedLanguages[1]) }

    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var targetDropdownExpanded by remember { mutableStateOf(false) }

    var currentTranslator by remember { mutableStateOf<Translator?>(null) }
    var isModelReady by remember { mutableStateOf(false) }
    var isModelDownloading by remember { mutableStateOf(false) }

    DisposableEffect(sourceLanguage, targetLanguage) {
        currentTranslator?.close()
        isModelReady = false

        val translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage.mlKitCode)
            .setTargetLanguage(targetLanguage.mlKitCode)
            .build()

        val newTranslator = Translation.getClient(translatorOptions)
        currentTranslator = newTranslator

        onDispose { newTranslator.close() }
    }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    val recognitionListener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { isListening = true }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }

            override fun onError(error: Int) {
                isListening = false
                errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error"
                }
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                    currentTranslator?.let { translator ->
                        translateText(matches[0], translator) { translated ->
                            translatedText = translated
                            isTranslating = false
                        }
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    DisposableEffect(speechRecognizer) {
        speechRecognizer.setRecognitionListener(recognitionListener)
        onDispose { speechRecognizer.destroy() }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE && isListening) {
                speechRecognizer.stopListening()
                isListening = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun startSpeechRecognition() {
        isTranslating = true
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, sourceLanguage.code)
        }
        speechRecognizer.startListening(intent)
    }

    fun downloadModelAndStartListening() {
        if (!recordAudioPermissionState.status.isGranted) {
            recordAudioPermissionState.launchPermissionRequest()
            return
        }

        errorMessage = ""

        if (isModelReady) {
            startSpeechRecognition()
            return
        }

        isModelDownloading = true
        currentTranslator?.downloadModelIfNeeded()
            ?.addOnSuccessListener {
                isModelReady = true
                isModelDownloading = false
                startSpeechRecognition()
            }
            ?.addOnFailureListener { e ->
                isModelDownloading = false
                errorMessage = "Failed to download translation model: ${e.message}"
            }
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        isListening = false
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    OutlinedButton(
                        onClick = { sourceDropdownExpanded = true },
                        modifier = Modifier.width(140.dp)
                    ) {
                        Text(sourceLanguage.name, maxLines = 1)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = sourceDropdownExpanded,
                        onDismissRequest = { sourceDropdownExpanded = false }
                    ) {
                        supportedLanguages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language.name) },
                                onClick = {
                                    sourceLanguage = language
                                    isModelReady = false
                                    sourceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "→",
                    style = MaterialTheme.typography.headlineMedium
                )

                Box {
                    OutlinedButton(
                        onClick = { targetDropdownExpanded = true },
                        modifier = Modifier.width(140.dp)
                    ) {
                        Text(targetLanguage.name, maxLines = 1)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = targetDropdownExpanded,
                        onDismissRequest = { targetDropdownExpanded = false }
                    ) {
                        supportedLanguages.forEach { language ->
                            DropdownMenuItem(
                                text = { Text(language.name) },
                                onClick = {
                                    targetLanguage = language
                                    isModelReady = false
                                    targetDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (isModelDownloading) {
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

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (!recordAudioPermissionState.status.isGranted) {
                Text(
                    text = "Microphone permission required",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                IconButton(
                    onClick = { recordAudioPermissionState.launchPermissionRequest() },
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
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    IconButton(
                        onClick = { if (isListening) stopListening() else downloadModelAndStartListening() },
                        enabled = !isModelDownloading,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                if (isListening) MaterialTheme.colorScheme.errorContainer
                                else if (isModelDownloading) MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.primaryContainer
                            )
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isListening) "Stop" else "Start",
                            modifier = Modifier.size(60.dp),
                            tint = if (isListening) MaterialTheme.colorScheme.error
                            else if (isModelDownloading) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = when {
                        isModelDownloading -> "Model downloading, please wait..."
                        isListening -> "Listening..."
                        isTranslating -> "Translating..."
                        else -> "Tap microphone to speak"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (recognizedText.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Recognized (${sourceLanguage.name}):",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = recognizedText,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (translatedText.isNotEmpty()) {
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
                            text = "Translation (${targetLanguage.name}):",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = translatedText,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private fun translateText(
    text: String,
    translator: Translator,
    onResult: (String) -> Unit
) {
    translator.translate(text)
        .addOnSuccessListener { onResult(it) }
        .addOnFailureListener { onResult("Translation error: ${it.message}") }
}
