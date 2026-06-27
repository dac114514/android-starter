package dev.dac114514.starter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.ui.components.Button
import com.composables.ui.components.ButtonStyle
import com.composables.ui.components.Switch
import com.composables.ui.components.Text
import com.composables.ui.components.TextField

@Composable
fun HomeScreen() {
    val nameState = rememberTextFieldState()
    var notificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "Android Starter",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(text = "Built on composables-ui · unstyled, your design")
        TextField(
            state = nameState,
            placeholder = { Text("Your name") },
        )
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it },
        ) {
            Text("Notifications")
        }
        Button(
            onClick = { },
            style = ButtonStyle.Primary,
        ) {
            Text("Get started", fontWeight = FontWeight.Medium)
        }
    }
}
