package org.example.project.translation

data class TranslationUiState(
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val translatedText: String = "",
    val isTranslating: Boolean = false,
    val errorMessage: String = "",
    val sourceLanguage: Language = supportedLanguages[0],
    val targetLanguage: Language = supportedLanguages[1],
    val isModelReady: Boolean = false,
    val isModelDownloading: Boolean = false
)
