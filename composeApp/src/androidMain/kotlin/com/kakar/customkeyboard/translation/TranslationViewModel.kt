package com.kakar.customkeyboard.translation

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.AndroidViewModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TranslationViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()

    private var translator: Translator? = null
    private var speechRecognizer: SpeechRecognizer? = null

    init {
        initSpeechRecognizer()
        initTranslator()
    }

    private fun initSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _uiState.update { it.copy(isListening = true) }
            }

            override fun onEndOfSpeech() {
                _uiState.update { it.copy(isListening = false) }
            }

            override fun onError(error: Int) {
                val message = when (error) {
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
                _uiState.update { it.copy(isListening = false, errorMessage = message) }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognized = matches[0]
                    _uiState.update { it.copy(isListening = false, recognizedText = recognized) }
                    translateText(recognized)
                } else {
                    _uiState.update { it.copy(isListening = false) }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _uiState.update { it.copy(recognizedText = matches[0]) }
                }
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun initTranslator() {
        val state = _uiState.value
        translator?.close()

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(state.sourceLanguage.mlKitCode)
            .setTargetLanguage(state.targetLanguage.mlKitCode)
            .build()

        translator = Translation.getClient(options)
        _uiState.update { it.copy(isModelReady = false) }
    }

    fun setSourceLanguage(language: Language) {
        _uiState.update { it.copy(sourceLanguage = language, isModelReady = false) }
        initTranslator()
    }

    fun setTargetLanguage(language: Language) {
        _uiState.update { it.copy(targetLanguage = language, isModelReady = false) }
        initTranslator()
    }

    fun downloadModelAndStartListening() {
        _uiState.update { it.copy(errorMessage = "") }

        if (_uiState.value.isModelReady) {
            startListening()
            return
        }

        _uiState.update { it.copy(isModelDownloading = true) }
        translator?.downloadModelIfNeeded()
            ?.addOnSuccessListener {
                _uiState.update { it.copy(isModelReady = true, isModelDownloading = false) }
                startListening()
            }
            ?.addOnFailureListener { e ->
                _uiState.update {
                    it.copy(
                        isModelDownloading = false,
                        errorMessage = "Failed to download translation model: ${e.message}"
                    )
                }
            }
    }

    private fun startListening() {
        _uiState.update { it.copy(isTranslating = true) }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, _uiState.value.sourceLanguage.code)
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _uiState.update { it.copy(isListening = false) }
    }

    fun onPause() {
        if (_uiState.value.isListening) {
            stopListening()
        }
    }

    private fun translateText(text: String) {
        translator?.translate(text)
            ?.addOnSuccessListener { translated ->
                _uiState.update { it.copy(translatedText = translated, isTranslating = false) }
            }
            ?.addOnFailureListener { e ->
                _uiState.update {
                    it.copy(
                        translatedText = "Translation error: ${e.message}",
                        isTranslating = false
                    )
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        speechRecognizer = null
        translator?.close()
        translator = null
    }
}
