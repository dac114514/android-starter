package dev.dac114514.starter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.composables.ui.theme.ColorScheme
import com.composables.ui.theme.ComposablesTheme
import com.composables.ui.theme.LocalColorScheme

@Composable
fun App() {
    CompositionLocalProvider(LocalColorScheme provides ColorScheme.Dark) {
        ComposablesTheme {
            HomeScreen()
        }
    }
}
