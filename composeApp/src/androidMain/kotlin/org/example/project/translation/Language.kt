package org.example.project.translation

import com.google.mlkit.nl.translate.TranslateLanguage

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
