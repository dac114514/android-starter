package dev.dac114514.starter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.composables.ui.theme.ColorScheme
import com.composables.ui.theme.LocalColorScheme
import com.composables.ui.theme.backgroundColor
import com.composables.ui.theme.borderColor
import com.composables.ui.theme.colors
import com.composables.ui.theme.controlColor
import com.composables.ui.theme.destructiveColor
import com.composables.ui.theme.fieldColor
import com.composables.ui.theme.mutedColor
import com.composables.ui.theme.onBackgroundColor
import com.composables.ui.theme.onControlColor
import com.composables.ui.theme.onDestructiveColor
import com.composables.ui.theme.onFieldColor
import com.composables.ui.theme.onPanelColor
import com.composables.ui.theme.onPrimaryColor
import com.composables.ui.theme.onSecondaryColor
import com.composables.ui.theme.onSelectedControlColor
import com.composables.ui.theme.panelColor
import com.composables.ui.theme.primaryColor
import com.composables.ui.theme.ringColor
import com.composables.ui.theme.scrimColor
import com.composables.ui.theme.secondaryColor
import com.composables.ui.theme.selectedControlColor
import com.composables.ui.theme.switchSelectedTrackColor
import com.composables.ui.theme.switchThumbColor
import com.composables.ui.theme.switchTrackColor
import com.composables.ui.theme.thumbColor
import com.composeunstyled.theme.ThemeComposable
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme

private val PaperLightColors: Map<ThemeToken<Color>, Color> = mapOf(
    backgroundColor to Color(0xFFFAFAF7),
    onBackgroundColor to Color(0xFF1A1A1A),
    panelColor to Color(0xFFFFFFFF),
    onPanelColor to Color(0xFF1A1A1A),
    mutedColor to Color(0xFF737373),
    primaryColor to Color(0xFF1A1A1A),
    onPrimaryColor to Color(0xFFFAFAF7),
    secondaryColor to Color(0xFFF1F1EE),
    onSecondaryColor to Color(0xFF1A1A1A),
    controlColor to Color(0xFFE5E5E0),
    onControlColor to Color(0xFF737373),
    thumbColor to Color(0xFFFAFAF7),
    switchTrackColor to Color(0xFFD4D4D0),
    switchSelectedTrackColor to Color(0xFF1A1A1A),
    switchThumbColor to Color(0xFFFAFAF7),
    selectedControlColor to Color(0xFFE0E0DC),
    onSelectedControlColor to Color(0xFF1A1A1A),
    destructiveColor to Color(0xFFB91C1C),
    onDestructiveColor to Color(0xFFFAFAF7),
    borderColor to Color(0xFFE5E5E0),
    fieldColor to Color(0xFFF5F5F2),
    onFieldColor to Color(0xFF1A1A1A),
    scrimColor to Color(0x66000000),
    ringColor to Color(0x3D000000),
)

private val PaperDarkColors: Map<ThemeToken<Color>, Color> = mapOf(
    backgroundColor to Color(0xFF0F0F10),
    onBackgroundColor to Color(0xFFE5E5E0),
    panelColor to Color(0xFF1A1A1D),
    onPanelColor to Color(0xFFE5E5E0),
    mutedColor to Color(0xFF888888),
    primaryColor to Color(0xFFE5E5E0),
    onPrimaryColor to Color(0xFF0F0F10),
    secondaryColor to Color(0xFF26262A),
    onSecondaryColor to Color(0xFFE5E5E0),
    controlColor to Color(0xFF33333A),
    onControlColor to Color(0xFF888888),
    thumbColor to Color(0xFFE5E5E0),
    switchTrackColor to Color(0xFF33333A),
    switchSelectedTrackColor to Color(0xFFE5E5E0),
    switchThumbColor to Color(0xFFE5E5E0),
    selectedControlColor to Color(0xFF3F3F46),
    onSelectedControlColor to Color(0xFFE5E5E0),
    destructiveColor to Color(0xFFF87171),
    onDestructiveColor to Color(0xFF0F0F10),
    borderColor to Color(0xFF33333A),
    fieldColor to Color(0xFF26262A),
    onFieldColor to Color(0xFFE5E5E0),
    scrimColor to Color(0x7A000000),
    ringColor to Color(0x47000000),
)

private fun paperTheme(dark: Boolean): ThemeComposable = buildTheme {
    properties[colors] = if (dark) PaperDarkColors else PaperLightColors
}

var appColorScheme: ColorScheme by mutableStateOf(ColorScheme.Dark)
    private set

fun setColorScheme(scheme: ColorScheme) {
    appColorScheme = scheme
}

@Composable
fun App() {
    CompositionLocalProvider(LocalColorScheme provides appColorScheme) {
        paperTheme(dark = appColorScheme == ColorScheme.Dark) {
            HomeScreen(
                colorScheme = appColorScheme,
                onColorSchemeChange = ::setColorScheme,
            )
        }
    }
}
