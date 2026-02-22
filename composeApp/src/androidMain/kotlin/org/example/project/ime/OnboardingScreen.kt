package org.example.project.ime

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private data class OnboardingPageData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val backgroundColor: Color,
    val iconTint: Color
)

private val onboardingPages = listOf(
    OnboardingPageData(
        icon = Icons.Filled.Keyboard,
        title = "Welcome to\nCustom Keyboard",
        description = "A powerful, personalized typing experience designed to make communication effortless and enjoyable.",
        backgroundColor = Color(0xFFE3F2FD),
        iconTint = Color(0xFF1A73E8)
    ),
    OnboardingPageData(
        icon = Icons.Filled.AutoAwesome,
        title = "Smart Features",
        description = "Voice input, customizable toolbar, and multiple keyboard layouts including QWERTY and symbol views.",
        backgroundColor = Color(0xFFF3E5F5),
        iconTint = Color(0xFF7B1FA2)
    ),
    OnboardingPageData(
        icon = Icons.Filled.Translate,
        title = "Multi-Language\nSupport",
        description = "Real-time translation across 16+ languages including English, Spanish, French, Arabic, Urdu, and many more.",
        backgroundColor = Color(0xFFE8F5E9),
        iconTint = Color(0xFF388E3C)
    ),
    OnboardingPageData(
        icon = Icons.Filled.RocketLaunch,
        title = "You're All Set!",
        description = "Enable the keyboard in your settings and start typing smarter. Let's get you set up in just a few taps.",
        backgroundColor = Color(0xFFFFF3E0),
        iconTint = Color(0xFFE65100)
    )
)

@Composable
fun OnboardingScreen(onOnboardingComplete: () -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    OnboardingPageContent(
                        pageData = onboardingPages[page],
                        pageIndex = page
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PageIndicator(
                        pageCount = onboardingPages.size,
                        currentPage = pagerState.currentPage
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (isLastPage) {
                                OnboardingPreferences.setOnboardingCompleted(context)
                                onOnboardingComplete()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A73E8)
                        )
                    ) {
                        Text(
                            text = if (isLastPage) "Get Started" else "Next",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            if (!isLastPage) {
                TextButton(
                    onClick = {
                        OnboardingPreferences.setOnboardingCompleted(context)
                        onOnboardingComplete()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 8.dp)
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    pageData: OnboardingPageData,
    pageIndex: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(pageData.backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = pageData.icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = pageData.iconTint
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = pageData.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = pageData.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        if (pageIndex == 1) {
            Spacer(modifier = Modifier.height(32.dp))
            FeatureHighlights()
        }

        if (pageIndex == 2) {
            Spacer(modifier = Modifier.height(32.dp))
            LanguageChips()
        }
    }
}

@Composable
private fun FeatureHighlights() {
    val features = listOf(
        Icons.Filled.Mic to "Voice Input",
        Icons.Filled.Dashboard to "Smart Toolbar",
        Icons.Filled.GridView to "Multiple Layouts"
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        features.forEach { (icon, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF7B1FA2)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun LanguageChips() {
    val languages = listOf(
        "English", "Spanish", "French", "Arabic",
        "Urdu", "Chinese", "Korean", "Hindi"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        languages.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { language ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = language,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF388E3C)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(
                targetValue = if (isSelected) 24.dp else 8.dp,
                label = "indicator_width"
            )
            val color by animateColorAsState(
                targetValue = if (isSelected) Color(0xFF1A73E8)
                else Color(0xFF1A73E8).copy(alpha = 0.3f),
                label = "indicator_color"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
