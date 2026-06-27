package dev.dac114514.starter.theme

import androidx.compose.ui.graphics.Color
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme

val colors = ThemeProperty<Color>("colors")
val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("onBackground")
val surface = ThemeToken<Color>("surface")
val onSurface = ThemeToken<Color>("onSurface")
val primary = ThemeToken<Color>("primary")
val onPrimary = ThemeToken<Color>("onPrimary")
val border = ThemeToken<Color>("border")
val muted = ThemeToken<Color>("muted")

val AppTheme = buildTheme {
    properties[colors] = mapOf(
        background to Color.Black,
        onBackground to Color.White,
        surface to Color(0xFF111111),
        onSurface to Color.White,
        primary to Color.White,
        onPrimary to Color.Black,
        border to Color(0xFF333333),
        muted to Color(0xFF888888),
    )
}
