package dev.dac114514.starter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import com.composeunstyled.TextInput
import com.composeunstyled.UnstyledButton
import com.composeunstyled.UnstyledTextField
import com.composeunstyled.UnstyledToggleSwitch
import com.composeunstyled.theme.Theme
import dev.dac114514.starter.theme.background
import dev.dac114514.starter.theme.border
import dev.dac114514.starter.theme.colors
import dev.dac114514.starter.theme.muted
import dev.dac114514.starter.theme.onBackground
import dev.dac114514.starter.theme.onPrimary
import dev.dac114514.starter.theme.surface

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
            color = Theme[colors][onBackground],
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Built on compose-unstyled · black & white, no theming",
            color = Theme[colors][muted],
        )
        val fieldShape = RoundedCornerShape(8.dp)
        UnstyledTextField(
            state = nameState,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Theme[colors][border], fieldShape)
                .background(Theme[colors][surface], fieldShape)
                .padding(horizontal = 12.dp, vertical = 14.dp),
        ) {
            TextInput(
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Your name", color = Theme[colors][muted])
                },
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            UnstyledToggleSwitch(
                toggled = notificationsEnabled,
                onToggled = { notificationsEnabled = it },
                modifier = Modifier.size(width = 44.dp, height = 24.dp),
                shape = RoundedCornerShape(999.dp),
                backgroundColor = if (notificationsEnabled) {
                    Theme[colors][onBackground]
                } else {
                    Theme[colors][surface]
                },
                contentPadding = PaddingValues(2.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Theme[colors][background], CircleShape),
                )
            }
            Text("Notifications", color = Theme[colors][onBackground])
        }
        UnstyledButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Theme[colors][onBackground],
            contentColor = Theme[colors][onPrimary],
            contentPadding = PaddingValues(vertical = 14.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("Get started", fontWeight = FontWeight.Medium)
        }
    }
}
