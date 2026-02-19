package org.example.project.ime

import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay

@Composable
fun KeyboardSetupScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isKeyboardEnabled by remember { mutableStateOf(false) }
    var isKeyboardSelected by remember { mutableStateOf(false) }
    var testText by remember { mutableStateOf("") }

    fun checkKeyboardStatus() {
        val imm = context.getSystemService(InputMethodManager::class.java)
        val enabledMethods = imm.enabledInputMethodList
        val packageName = context.packageName

        isKeyboardEnabled = enabledMethods.any { it.packageName == packageName }

        val defaultIme = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.DEFAULT_INPUT_METHOD
        ) ?: ""
        isKeyboardSelected = defaultIme.startsWith(packageName)
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                checkKeyboardStatus()
                delay(1000)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Keyboard,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Custom Keyboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Setup and test your keyboard",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            SetupStepCard(
                stepNumber = 1,
                title = "Enable Keyboard",
                description = if (isKeyboardEnabled)
                    "Custom Keyboard is enabled in your system settings."
                else
                    "Enable Custom Keyboard in your device's input method settings.",
                isComplete = isKeyboardEnabled,
                buttonText = if (isKeyboardEnabled) "Enabled" else "Open Settings",
                onButtonClick = {
                    context.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                },
                buttonEnabled = !isKeyboardEnabled
            )

            Spacer(modifier = Modifier.height(12.dp))

            SetupStepCard(
                stepNumber = 2,
                title = "Select Keyboard",
                description = if (isKeyboardSelected)
                    "Custom Keyboard is your active input method."
                else if (isKeyboardEnabled)
                    "Switch to Custom Keyboard as your active input method."
                else
                    "First enable the keyboard in Step 1.",
                isComplete = isKeyboardSelected,
                buttonText = if (isKeyboardSelected) "Selected" else "Switch Keyboard",
                onButtonClick = {
                    val imm = context.getSystemService(InputMethodManager::class.java)
                    imm.showInputMethodPicker()
                },
                buttonEnabled = isKeyboardEnabled && !isKeyboardSelected
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Try it out",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            BasicTextField(
                value = testText,
                onValueChange = { testText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .border(
                        width = 1.dp,
                        color = if (isKeyboardSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (testText.isEmpty()) {
                        Text(
                            text = "Tap here to type with your custom keyboard...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )

            if (testText.isNotEmpty()) {
                OutlinedButton(
                    onClick = { testText = "" },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Clear")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SetupStepCard(
    stepNumber: Int,
    title: String,
    description: String,
    isComplete: Boolean,
    buttonText: String,
    onButtonClick: () -> Unit,
    buttonEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isComplete)
                    Icons.Default.CheckCircle
                else
                    Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isComplete)
                    Color(0xFF4CAF50)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Step $stepNumber: $title",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Button(
                onClick = onButtonClick,
                enabled = buttonEnabled,
                colors = if (isComplete)
                    ButtonDefaults.buttonColors(
                        disabledContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        disabledContentColor = Color(0xFF4CAF50)
                    )
                else
                    ButtonDefaults.buttonColors()
            ) {
                if (!isComplete && buttonEnabled) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 2.dp)
                    )
                }
                Text(buttonText, fontSize = 12.sp)
            }
        }
    }
}