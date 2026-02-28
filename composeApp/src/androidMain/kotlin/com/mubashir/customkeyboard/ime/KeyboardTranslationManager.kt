package com.mubashir.customkeyboard.ime

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.mubashir.customkeyboard.translation.supportedLanguages

data class ToolbarState(
    val isListening: Boolean = false,
    val displayText: String = "",
    val isTranslating: Boolean = false
)

class KeyboardTranslationManager(
    private val context: Context,
    private val onTranslationResult: (String) -> Unit
) {
    private val _toolbarState = MutableStateFlow(ToolbarState())
    val toolbarState: StateFlow<ToolbarState> = _toolbarState.asStateFlow()

    private var speechRecognizer: SpeechRecognizer? = null
    private var translator: Translator? = null
    private var isModelReady = false
    private var currentSourceCode = ""
    private var currentTargetCode = ""

    init {
        loadLanguagesAndInitTranslator()
    }

    private fun loadLanguagesAndInitTranslator() {
        val sourceCode = KeyboardPreferences.getSourceLanguageCode(context)
        val targetCode = KeyboardPreferences.getTargetLanguageCode(context)

        if (sourceCode == currentSourceCode && targetCode == currentTargetCode && translator != null) {
            return
        }

        currentSourceCode = sourceCode
        currentTargetCode = targetCode

        val sourceLang = supportedLanguages.find { it.code == sourceCode } ?: supportedLanguages[0]
        val targetLang = supportedLanguages.find { it.code == targetCode } ?: supportedLanguages.last()

        translator?.close()
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang.mlKitCode)
            .setTargetLanguage(targetLang.mlKitCode)
            .build()
        translator = Translation.getClient(options)
        isModelReady = false
    }

    fun onLanguageChanged() {
        loadLanguagesAndInitTranslator()
    }

    fun onMicClick() {
        if (_toolbarState.value.isListening) {
            stopListening()
            return
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            _toolbarState.update { it.copy(displayText = "Grant mic permission in app") }
            return
        }

        // Reload languages in case user changed them in settings
        loadLanguagesAndInitTranslator()
        downloadModelAndStart()
    }

    private fun downloadModelAndStart() {
        if (isModelReady) {
            startListening()
            return
        }

        _toolbarState.update { it.copy(displayText = "Downloading model...") }
        translator?.downloadModelIfNeeded()
            ?.addOnSuccessListener {
                isModelReady = true
                startListening()
            }
            ?.addOnFailureListener {
                _toolbarState.update { it.copy(displayText = "Model download failed") }
            }
    }

    private fun startListening() {
        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(recognitionListener)

        _toolbarState.update { it.copy(isListening = true, displayText = "Listening...") }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, currentSourceCode)
        }
        speechRecognizer?.startListening(intent)
    }

    private fun stopListening() {
        speechRecognizer?.stopListening()
        _toolbarState.update { it.copy(isListening = false) }
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _toolbarState.update { it.copy(isListening = true, displayText = "Listening...") }
        }

        override fun onEndOfSpeech() {
            _toolbarState.update { it.copy(isListening = false, displayText = "Translating...") }
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val recognized = matches[0]
                _toolbarState.update {
                    it.copy(isListening = false, displayText = recognized, isTranslating = true)
                }
                translateText(recognized)
            } else {
                _toolbarState.update { it.copy(isListening = false, displayText = "") }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                _toolbarState.update { it.copy(displayText = matches[0]) }
            }
        }

        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                SpeechRecognizer.ERROR_CLIENT -> "Client error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "No mic permission"
                SpeechRecognizer.ERROR_NETWORK -> "Network error"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                SpeechRecognizer.ERROR_SERVER -> "Server error"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error ($error)"
            }
            _toolbarState.update { it.copy(isListening = false, displayText = message) }
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun translateText(text: String) {
        translator?.translate(text)
            ?.addOnSuccessListener { translated ->
                _toolbarState.update { it.copy(displayText = translated, isTranslating = false) }
                onTranslationResult(translated)
            }
            ?.addOnFailureListener {
                _toolbarState.update { it.copy(displayText = text, isTranslating = false) }
                onTranslationResult(text)
            }
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
        translator?.close()
        translator = null
    }
}
