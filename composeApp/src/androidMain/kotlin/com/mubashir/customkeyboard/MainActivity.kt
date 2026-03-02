package com.mubashir.customkeyboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.mubashir.customkeyboard.navigation.AppNavGraph

import androidx.activity.viewModels
import com.mubashir.customkeyboard.translation.TranslationViewModel
import android.content.Intent

class MainActivity : ComponentActivity() {
    private val translationViewModel: TranslationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        handleIntent(intent)

        setContent {
            MaterialTheme {
                AppNavGraph(translationViewModel)
            }
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_PROCESS_TEXT) {
            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""
            val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            
            translationViewModel.handleProcessTextIntent(text) { translatedText ->
                if (!readonly) {
                    val resultIntent = Intent()
                    resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, translatedText)
                    setResult(RESULT_OK, resultIntent)
                }
                finish()
            }
        }
    }
}
