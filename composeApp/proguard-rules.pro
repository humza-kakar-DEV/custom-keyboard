# ==============================================================
# ProGuard / R8 rules for com.mubashir.customkeyboard
# ==============================================================

# ── Kotlin ─────────────────────────────────────────────────────
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Lazy { *; }

# ── Jetpack Compose ────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep Composable functions (needed for reflection-based tools / previews)
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ── Compose Material Icons (vector drawables loaded by name) ───
-keep class androidx.compose.material.icons.** { *; }

# ── AndroidX Lifecycle & ViewModel ─────────────────────────────
-keep class androidx.lifecycle.** { *; }
-keep class androidx.savedstate.** { *; }

# ── InputMethodService (keyboard service must not be stripped) ──
-keep class * extends android.inputmethodservice.InputMethodService { *; }

# ── ML Kit ─────────────────────────────────────────────────────
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**
-keep class com.google.android.gms.internal.mlkit_translate.** { *; }
-keep class com.google.android.gms.internal.mlkit_language_id.** { *; }
-dontwarn com.google.android.gms.**

# ── Navigation Compose ─────────────────────────────────────────
-keepnames class androidx.navigation.** { *; }

# ── Accompanist ────────────────────────────────────────────────
-dontwarn com.google.accompanist.**

# ── Remove verbose logging in release ──────────────────────────
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# ── General Android safety nets ────────────────────────────────
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
