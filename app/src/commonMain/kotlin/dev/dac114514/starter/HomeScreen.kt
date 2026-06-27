package dev.dac114514.starter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.ChevronDown
import com.composables.icons.lucide.Eye
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.MoreVertical
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.Trash2
import com.composables.icons.lucide.UserPlus
import com.composables.ui.components.AlertDialog
import com.composables.ui.components.BottomSheet
import com.composables.ui.components.Button
import com.composables.ui.components.ButtonStyle
import com.composables.ui.components.Checkbox
import com.composables.ui.components.Disclosure
import com.composables.ui.components.DisclosureButton
import com.composables.ui.components.DisclosurePanel
import com.composables.ui.components.HorizontalSeparator
import com.composables.ui.components.Icon
import com.composables.ui.components.IconButton
import com.composables.ui.components.IndeterminateProgressIndicator
import com.composables.ui.components.NavigationBar
import com.composables.ui.components.NavigationBarItem
import com.composables.ui.components.ProgressIndicator
import com.composables.ui.components.Radio
import com.composables.ui.components.RadioGroup
import com.composables.ui.components.Slider
import com.composables.ui.components.Switch
import com.composables.ui.components.Text
import com.composables.ui.components.TextField
import com.composables.ui.components.Tooltip
import com.composables.ui.components.TooltipPanel
import com.composables.ui.components.TriStateCheckbox
import com.composables.ui.components.rememberBottomSheetState
import com.composables.ui.theme.colors
import com.composables.ui.theme.controlColor
import com.composables.ui.theme.mutedColor
import com.composables.ui.theme.panelColor
import com.composables.ui.theme.primaryColor
import com.composeunstyled.theme.Theme
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: Long,
    val sender: String,
    val content: String,
    val timestamp: String,
    val isFromMe: Boolean,
)

private val sampleConversation: List<ChatMessage> = listOf(
    ChatMessage(1, "Mia", "Hey, did you see the new design tokens?", "10:31", false),
    ChatMessage(2, "You", "Yeah, the corner radius scale is great.", "10:32", true),
    ChatMessage(3, "Mia", "Right? And it composes well with the unstyled primitives.", "10:33", false),
    ChatMessage(
        4,
        "You",
        "Here is the bigger spec I was working on:\nLine 1: spacing\nLine 2: typography\nLine 3: motion",
        "10:35",
        true,
    ),
    ChatMessage(5, "Mia", "Looks solid. Should we ship it after lunch?", "10:37", false),
    ChatMessage(6, "You", "Yep, I'll run the CI one more time.", "10:38", true),
    ChatMessage(7, "Mia", "Thanks. Also, new spec doc:\nhttps://example.com/spec", "10:40", false),
    ChatMessage(8, "You", "Got it. Reviewing now.", "10:41", true),
)

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val messages = remember { sampleConversation.toMutableStateList() }
    val listState = rememberLazyListState()
    var textSize by remember { mutableFloatStateOf(1.0f) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var readReceipts by remember { mutableStateOf(true) }
    var marketing by remember { mutableStateOf(true) }
    var product by remember { mutableStateOf(false) }
    var security by remember { mutableStateOf(true) }
    var themeChoice by remember { mutableStateOf("Dark") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme[colors][panelColor])
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> ChatScreen(
                    messages = messages,
                    listState = listState,
                    textSize = textSize,
                )
                else -> SettingsScreen(
                    textSize = textSize,
                    onTextSizeChange = { textSize = it },
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsEnabledChange = { notificationsEnabled = it },
                    readReceipts = readReceipts,
                    onReadReceiptsChange = { readReceipts = it },
                    marketing = marketing,
                    onMarketingChange = { marketing = it },
                    product = product,
                    onProductChange = { product = it },
                    security = security,
                    onSecurityChange = { security = it },
                    themeChoice = themeChoice,
                    onThemeChoiceChange = { themeChoice = it },
                    onDeleteAll = { showDeleteDialog = true },
                )
            }
        }

        AlertDialog(
            visible = showDeleteDialog,
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete all messages?") },
            text = {
                Text(
                    "This will permanently remove all messages in this conversation. " +
                        "This action cannot be undone.",
                )
            },
            positiveButton = {
                Button(
                    onClick = {
                        messages.clear()
                        showDeleteDialog = false
                    },
                    style = ButtonStyle.Destructive,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Delete")
                }
            },
            negativeButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    style = ButtonStyle.Outlined,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancel")
                }
            },
        )

        NavigationBar(modifier = Modifier.fillMaxWidth()) {
            NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Lucide.MessageCircle,
                    contentDescription = "Chat",
                    modifier = Modifier.size(20.dp),
                )
            }
            NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Lucide.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun ChatScreen(
    messages: SnapshotStateList<ChatMessage>,
    listState: LazyListState,
    textSize: Float,
) {
    val draft = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    val overflowSheet = rememberBottomSheetState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Chat", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Theme[colors][primaryColor]),
                    )
                    Text(
                        text = "Online · 2 members",
                        fontSize = 12.sp,
                        color = Theme[colors][mutedColor],
                    )
                }
            }
            IconButton(
                onClick = { scope.launch { overflowSheet.show() } },
                style = ButtonStyle.Ghost,
            ) {
                Icon(
                    imageVector = Lucide.MoreVertical,
                    contentDescription = "More options",
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        HorizontalSeparator()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(message = message, textSize = textSize)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextField(
                state = draft,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message") },
            )
            IconButton(
                onClick = {
                    val text = draft.text.toString().trim()
                    if (text.isNotEmpty()) {
                        val newId = (messages.maxOfOrNull { it.id } ?: 0L) + 1L
                        messages.add(
                            ChatMessage(
                                id = newId,
                                sender = "You",
                                content = text,
                                timestamp = "now",
                                isFromMe = true,
                            )
                        )
                        draft.edit { replace(0, length, "") }
                    }
                },
                style = ButtonStyle.Primary,
            ) {
                Icon(
                    imageVector = Lucide.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }

    BottomSheet(
        state = overflowSheet,
        onDismissRequest = { scope.launch { overflowSheet.hide() } },
        toolbar = { Text("Conversation", fontWeight = FontWeight.SemiBold) },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            BottomSheetAction(icon = Lucide.Bell, label = "Mute notifications") {
                scope.launch { overflowSheet.hide() }
            }
            BottomSheetAction(icon = Lucide.Eye, label = "Search messages") {
                scope.launch { overflowSheet.hide() }
            }
            BottomSheetAction(icon = Lucide.UserPlus, label = "Add member") {
                scope.launch { overflowSheet.hide() }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, textSize: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 320.dp),
            horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start,
        ) {
            if (!message.isFromMe) {
                Text(
                    text = "${message.sender} · ${message.timestamp}",
                    fontSize = (11f * textSize).sp,
                    color = Theme[colors][mutedColor],
                )
                Spacer(Modifier.height(2.dp))
            }
            val shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (message.isFromMe) 12.dp else 2.dp,
                bottomEnd = if (message.isFromMe) 2.dp else 12.dp,
            )
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(Theme[colors][controlColor])
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = message.content,
                    fontSize = (14f * textSize).sp,
                )
            }
            if (message.isFromMe) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = message.timestamp,
                    fontSize = (11f * textSize).sp,
                    color = Theme[colors][mutedColor],
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    textSize: Float,
    onTextSizeChange: (Float) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    readReceipts: Boolean,
    onReadReceiptsChange: (Boolean) -> Unit,
    marketing: Boolean,
    onMarketingChange: (Boolean) -> Unit,
    product: Boolean,
    onProductChange: (Boolean) -> Unit,
    security: Boolean,
    onSecurityChange: (Boolean) -> Unit,
    themeChoice: String,
    onThemeChoiceChange: (String) -> Unit,
    onDeleteAll: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val licenseSheet = rememberBottomSheetState()
    var aboutExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text("Settings", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(20.dp))

        SectionTitle("Account")
        Spacer(Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Theme[colors][controlColor]),
                contentAlignment = Alignment.Center,
            ) {
                Text("A", fontWeight = FontWeight.SemiBold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Alex Styl", fontWeight = FontWeight.Medium)
                Text(
                    text = "alex@example.com",
                    fontSize = 12.sp,
                    color = Theme[colors][mutedColor],
                )
            }
            Button(onClick = {}, style = ButtonStyle.Destructive) {
                Text("Sign out")
            }
        }
        Spacer(Modifier.height(24.dp))
        HorizontalSeparator()
        Spacer(Modifier.height(24.dp))

        SectionTitle("Preferences")
        Spacer(Modifier.height(10.dp))
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = onNotificationsEnabledChange,
        ) {
            Text("Notifications")
        }
        Spacer(Modifier.height(4.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Text size",
                fontSize = 13.sp,
                color = Theme[colors][mutedColor],
            )
            Slider(
                value = textSize,
                onValueChange = onTextSizeChange,
                valueRange = 0.8f..1.4f,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Spacer(Modifier.height(4.dp))
        Checkbox(
            checked = readReceipts,
            onCheckedChange = onReadReceiptsChange,
        ) {
            Text("Read receipts")
        }
        Spacer(Modifier.height(8.dp))

        val allSelected = marketing && product && security
        val noneSelected = !marketing && !product && !security
        val triState = when {
            allSelected -> ToggleableState.On
            noneSelected -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
        TriStateCheckbox(
            state = triState,
            onStateChange = { nextState ->
                val selected = nextState == ToggleableState.On
                onMarketingChange(selected)
                onProductChange(selected)
                onSecurityChange(selected)
            },
        ) {
            Text("Select all notifications")
        }
        Spacer(Modifier.height(4.dp))
        Column(
            modifier = Modifier.padding(start = 28.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Checkbox(checked = marketing, onCheckedChange = onMarketingChange) {
                Text("Marketing updates", color = Theme[colors][mutedColor])
            }
            Checkbox(checked = product, onCheckedChange = onProductChange) {
                Text("Product announcements", color = Theme[colors][mutedColor])
            }
            Checkbox(checked = security, onCheckedChange = onSecurityChange) {
                Text("Security alerts", color = Theme[colors][mutedColor])
            }
        }
        Spacer(Modifier.height(24.dp))
        HorizontalSeparator()
        Spacer(Modifier.height(24.dp))

        SectionTitle("Appearance")
        Spacer(Modifier.height(10.dp))
        RadioGroup(value = themeChoice, onValueChange = onThemeChoiceChange) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Radio("System") { Text("System") }
                Radio("Light") { Text("Light") }
                Radio("Dark") { Text("Dark") }
            }
        }
        Spacer(Modifier.height(24.dp))
        HorizontalSeparator()
        Spacer(Modifier.height(24.dp))

        SectionTitle("Progress")
        Spacer(Modifier.height(10.dp))
        ProgressIndicator(progress = 0.6f, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        IndeterminateProgressIndicator(modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))
        HorizontalSeparator()
        Spacer(Modifier.height(24.dp))

        SectionTitle("Danger zone")
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = onDeleteAll,
            style = ButtonStyle.Destructive,
        ) {
            Icon(
                imageVector = Lucide.Trash2,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Text("Delete all messages")
        }
        Spacer(Modifier.height(24.dp))
        HorizontalSeparator()
        Spacer(Modifier.height(24.dp))

        Disclosure(
            expanded = aboutExpanded,
            onExpandedChange = { aboutExpanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            DisclosureButton(
                modifier = Modifier.fillMaxWidth(),
                indicator = { _ ->
                    Icon(
                        imageVector = Lucide.ChevronDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            ) {
                Text("About", fontWeight = FontWeight.Medium)
            }
            DisclosurePanel {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("v0.1.0", color = Theme[colors][mutedColor])
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Tap the icon for details")
                        Tooltip(
                            panel = {
                                TooltipPanel {
                                    Text("This is the about panel")
                                }
                            },
                        ) {
                            IconButton(
                                onClick = {},
                                style = ButtonStyle.Ghost,
                            ) {
                                Icon(
                                    imageVector = Lucide.Info,
                                    contentDescription = "Info",
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }
                    }
                    HorizontalSeparator()
                    Button(
                        onClick = { scope.launch { licenseSheet.show() } },
                        style = ButtonStyle.Link,
                    ) {
                        Text("Open source licenses")
                    }
                }
            }
        }

        Spacer(Modifier.height(48.dp))
    }

    BottomSheet(
        state = licenseSheet,
        onDismissRequest = { scope.launch { licenseSheet.hide() } },
        toolbar = { Text("Open source licenses", fontWeight = FontWeight.SemiBold) },
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp),
        ) {
            listOf(
                "Apache License 2.0 — AndroidX, Kotlin",
                "Apache License 2.0 — Compose Multiplatform",
                "MIT — Lucide Icons",
                "Apache License 2.0 — kotlinx.coroutines",
                "BSD 3-Clause — Kotlin standard library",
            ).forEach { license ->
                Text(license)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Theme[colors][mutedColor],
    )
}

@Composable
private fun BottomSheetAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        style = ButtonStyle.Ghost,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Text(label)
    }
}
