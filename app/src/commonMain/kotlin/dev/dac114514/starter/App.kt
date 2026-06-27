package dev.dac114514.starter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composeunstyled.theme.Theme
import dev.dac114514.starter.theme.AppTheme
import dev.dac114514.starter.theme.background
import dev.dac114514.starter.theme.colors

@Composable
fun App() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme[colors][background]),
        ) {
            HomeScreen()
        }
    }
}
