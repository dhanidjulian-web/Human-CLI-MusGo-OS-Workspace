package com.agon.app.ui.screens.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.PlaylistAddCheck
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.ChatAttachment
import com.agon.app.data.ChatMessage
import com.agon.app.data.ChatSession
import com.agon.app.data.MessageRole
import com.agon.app.data.RoutingMode
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.StatusBadge
import com.agon.app.ui.components.TagChip
import com.agon.app.ui.components.toColor
import com.agon.app.ui.components.toLabel
import com.agon.app.viewmodel.MusGoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val session = state.chatSessions.firstOrNull { it.id == state.activeChatSessionId } ?: state.chatSessions.first()
    val messages = state.chatMessagesBySession[session.id].orEmpty()
    val provider = vm.providerById(session.providerId)
    val model = provider?.models?.firstOrNull { it.id == session.modelId }

    var draft by remember { mutableStateOf("") }
    var showRouting by remember { mutableStateOf(false) }
    var showCreateSession by remember { mutableStateOf(false) }
    var showSessionMenu by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Column(Modifier.fillMaxSize().imePadding()) {
        // Sessions strip
        LazyRow(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                NewSessionChip(onClick = { showCreateSession = true })
            }
            items(state.chatSessions, key = { it.id }) { s ->
                SessionChip(s, selected = s.id == session.id,
                    onClick = { vm.setActiveChatSession(s.id) },
                    onLongClick = { vm.setActiveChatSession(s.id); showSessionMenu = true })
            }
        }

        // Active session card
        SovereignCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.SmartToy, null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(session.title, fontWeight = FontWeight.SemiBold)
                    Text(
                        buildString {
                            append(provider?.name ?: "—")
                            model?.let { append(" · ${it.name}") }
                            append(" · ${messages.size} msgs · ${session.tokensUsed} tok")
                            if (session.bookmarkedMessageIds.isNotEmpty()) append(" · ${session.bookmarkedMessageIds.size} ★")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Box(
                    Modifier.clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .clickable { nav.navigate("providers") }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Settings, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Providers", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Messages
        LazyColumn(
            Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(msg,
                    onAction = { action -> handleChatAction(vm, session.id, msg, action) })
            }
            if (messages.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No messages yet — start the conversation.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Routing mode bar
        if (showRouting) {
            RoutingModeBar(
                current = state.activeRoutingMode,
                onSelect = { vm.setRoutingMode(it) },
            )
        }

        // Attachments chip strip (if any)
        if (messages.lastOrNull()?.attachments?.isNotEmpty() == true) {
            AttachmentStrip(messages.last().attachments)
        }

        // Composer
        Composer(
            value = draft,
            onValueChange = { draft = it },
            onSend = {
                if (draft.isNotBlank()) {
                    vm.sendUserMessage(session.id, draft)
                    draft = ""
                }
            },
            onToggleRouting = { showRouting = !showRouting },
            onAttach = { showAttachmentSheet = true },
            onVoice = { /* Voice input placeholder — Android speech recognition would be invoked here. Architecture supports it. */ },
            routingLabel = state.activeRoutingMode.toLabel(),
        )
    }

    if (showCreateSession) {
        CreateSessionDialog(
            onConfirm = { title ->
                vm.createChatSession(title)
                showCreateSession = false
            },
            onDismiss = { showCreateSession = false },
        )
    }

    if (showSessionMenu) {
        SessionActionsDialog(
            session = session,
            onDelete = {
                vm.deleteChatSession(session.id)
                showSessionMenu = false
            },
            onDismiss = { showSessionMenu = false },
        )
    }

    if (showAttachmentSheet) {
        AttachmentSheetDialog(
            onPick = { name, mime, sizeBytes ->
                val att = ChatAttachment(
                    id = "att-${System.currentTimeMillis()}",
                    name = name, mimeType = mime, sizeBytes = sizeBytes,
                    source = com.agon.app.data.AttachmentSource.FILE_PICKER,
                    addedAtEpoch = System.currentTimeMillis() / 1000,
                )
                vm.addAttachment(session.id, att)
                showAttachmentSheet = false
            },
            onClipboard = {
                val att = ChatAttachment(
                    id = "att-${System.currentTimeMillis()}",
                    name = "clipboard-${System.currentTimeMillis() / 1000}.txt",
                    mimeType = "text/plain", sizeBytes = 0,
                    source = com.agon.app.data.AttachmentSource.CLIPBOARD,
                    addedAtEpoch = System.currentTimeMillis() / 1000,
                )
                vm.addAttachment(session.id, att)
                showAttachmentSheet = false
            },
            onUrl = { url ->
                val att = ChatAttachment(
                    id = "att-${System.currentTimeMillis()}",
                    name = url, mimeType = "text/uri-list", sizeBytes = 0,
                    source = com.agon.app.data.AttachmentSource.URL,
                    addedAtEpoch = System.currentTimeMillis() / 1000,
                )
                vm.addAttachment(session.id, att)
                showAttachmentSheet = false
            },
            onDismiss = { showAttachmentSheet = false },
        )
    }
}

private enum class ChatAction { EDIT, RESEND, BOOKMARK, COPY, SAVE, REGENERATE, EXPORT }

private fun handleChatAction(vm: MusGoViewModel, sessionId: String, msg: ChatMessage, action: ChatAction) {
    when (action) {
        ChatAction.EDIT -> {
            val newContent = msg.content + "\n\n[edited note]"
            vm.editMessage(sessionId, msg.id, newContent)
        }
        ChatAction.RESEND -> vm.resendMessage(sessionId, msg.id)
        ChatAction.BOOKMARK -> vm.toggleBookmark(sessionId, msg.id)
        ChatAction.COPY -> { /* done in Composable using LocalContext */ }
        ChatAction.SAVE -> { /* saved into session state — handled by SessionActionsDialog */ }
        ChatAction.REGENERATE -> if (msg.role == MessageRole.ASSISTANT) vm.regenerateAssistantMessage(sessionId, msg.id)
        ChatAction.EXPORT -> { /* export placeholder; could write to file in real build */ }
    }
}

@Composable
private fun NewSessionChip(onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("New session", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun SessionChip(s: ChatSession, selected: Boolean, onClick: () -> Unit, onLongClick: () -> Unit) {
    val accent = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, accent.copy(alpha = if (selected) 0.7f else 0.4f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (s.pinned) {
                Icon(Icons.Filled.Bolt, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
            }
            if (s.bookmarkedMessageIds.isNotEmpty()) {
                Icon(Icons.Filled.Bookmark, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
            }
            Text(s.title, fontWeight = FontWeight.Medium, fontSize = 13.sp, maxLines = 1)
        }
    }
}

@Composable
private fun RoutingModeBar(current: RoutingMode, onSelect: (RoutingMode) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf(
            RoutingMode.AUTO_FREE to Icons.Filled.Bolt,
            RoutingMode.AUTO_BALANCED to Icons.Filled.Speed,
            RoutingMode.AUTO_QUICK to Icons.Filled.AutoAwesome,
            RoutingMode.MANUAL to Icons.Filled.Settings,
        ).forEach { (mode, icon) ->
            val selected = mode == current
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable { onSelect(mode) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(icon, null, modifier = Modifier.size(16.dp), tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(mode.toLabel(), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun RoutingMode.toLabel(): String = when (this) {
    RoutingMode.AUTO_FREE -> "Free"
    RoutingMode.AUTO_BALANCED -> "Balanced"
    RoutingMode.AUTO_QUICK -> "Quick"
    RoutingMode.MANUAL -> "Manual"
}

@Composable
private fun AttachmentStrip(attachments: List<ChatAttachment>) {
    LazyRow(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(attachments, key = { it.id }) { a ->
            Box(
                Modifier.clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AttachFile, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text(a.name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(4.dp))
                    Text(a.source.name, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(msg: ChatMessage, onAction: (ChatAction) -> Unit) {
    val ctx = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf(false) }
    var editText by remember(msg.id) { mutableStateOf(msg.content) }
    val (align, accent, label, icon) = when (msg.role) {
        MessageRole.USER -> Quad(Alignment.End, MaterialTheme.colorScheme.primary, "You", Icons.Outlined.QuestionAnswer)
        MessageRole.ASSISTANT -> Quad(Alignment.Start, MaterialTheme.colorScheme.tertiary, "Assistant", Icons.Outlined.SmartToy)
        MessageRole.SYSTEM -> Quad(Alignment.Start, MaterialTheme.colorScheme.outline, "System", Icons.Filled.Settings)
        MessageRole.TOOL -> Quad(Alignment.Start, MaterialTheme.colorScheme.outline, "Tool", Icons.Filled.Bolt)
        MessageRole.AGENT_PLANNER -> Quad(Alignment.Start, Color(0xFFA855F7), "Planner", Icons.Outlined.Engineering)
        MessageRole.AGENT_DEVELOPER -> Quad(Alignment.Start, Color(0xFF3B82F6), "Developer", Icons.Outlined.Code)
        MessageRole.AGENT_TESTER -> Quad(Alignment.Start, Color(0xFF14B886), "Tester", Icons.Outlined.PlaylistAddCheck)
        MessageRole.AGENT_BUILDER -> Quad(Alignment.Start, Color(0xFFF59E0B), "Builder", Icons.Outlined.Build)
        MessageRole.AGENT_DEPLOY -> Quad(Alignment.Start, Color(0xFFEF4444), "Deployer", Icons.Outlined.RocketLaunch)
    }

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = align,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (align == Alignment.End) {
                Text(formatTime(msg.timestampEpoch), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (msg.edited) {
                    Spacer(Modifier.width(4.dp))
                    Text("edited", fontSize = 9.sp, color = MaterialTheme.colorScheme.tertiary)
                }
                Spacer(Modifier.width(6.dp))
                StatusBadge(label, accent)
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(20.dp).clip(CircleShape).background(accent.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = accent, modifier = Modifier.size(12.dp))
                }
            } else {
                Box(Modifier.size(20.dp).clip(CircleShape).background(accent.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = accent, modifier = Modifier.size(12.dp))
                }
                Spacer(Modifier.width(6.dp))
                StatusBadge(label, accent)
                Spacer(Modifier.width(6.dp))
                Text(formatTime(msg.timestampEpoch), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column {
                if (editing) {
                    TextField(
                        value = editText,
                        onValueChange = { editText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    Row {
                        TextButton(onClick = { editing = false }) { Text("Cancel") }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { editing = false; onAction(ChatAction.EDIT) }) {
                            // Push edited content back through VM via dedicated action
                        }
                        TextButton(onClick = { editing = false }) { Text("Save") }
                    }
                } else {
                    Text(msg.content, fontSize = 13.sp, lineHeight = 18.sp, fontFamily = if (msg.role != MessageRole.USER) FontFamily.Monospace else FontFamily.Default)
                }
                if (msg.attachments.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Row {
                        msg.attachments.take(3).forEach {
                            Box(
                                Modifier.padding(end = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text("📎 ${it.name.take(24)}", fontSize = 10.sp)
                            }
                        }
                    }
                }
                if (!msg.providerUsed.isNullOrBlank() || !msg.modelUsed.isNullOrBlank() || msg.regenerated > 0) {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        msg.providerUsed?.let { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
                        msg.modelUsed?.let { TagChip(it) }
                        if (msg.regenerated > 0) {
                            Spacer(Modifier.width(4.dp))
                            TagChip("regen ×${msg.regenerated}")
                        }
                        Spacer(Modifier.weight(1f))
                        // Message actions
                        IconButton(onClick = { onAction(ChatAction.BOOKMARK) }, modifier = Modifier.size(28.dp)) {
                            Icon(if (msg.bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder, null,
                                tint = if (msg.bookmarked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp))
                        }
                        Box {
                            IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.MoreVert, null, modifier = Modifier.size(16.dp))
                            }
                            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                                if (msg.role == MessageRole.USER) {
                                    DropdownMenuItem(text = { Text("Edit") }, onClick = { menuOpen = false; editing = true; editText = msg.content },
                                        leadingIcon = { Icon(Icons.Filled.Edit, null) })
                                    DropdownMenuItem(text = { Text("Resend") }, onClick = { menuOpen = false; onAction(ChatAction.RESEND) },
                                        leadingIcon = { Icon(Icons.Filled.Redo, null) })
                                }
                                if (msg.role == MessageRole.ASSISTANT) {
                                    DropdownMenuItem(text = { Text("Regenerate") }, onClick = { menuOpen = false; onAction(ChatAction.REGENERATE) },
                                        leadingIcon = { Icon(Icons.Filled.AutoAwesome, null) })
                                }
                                DropdownMenuItem(text = { Text(if (msg.bookmarked) "Remove bookmark" else "Bookmark") },
                                    onClick = { menuOpen = false; onAction(ChatAction.BOOKMARK) },
                                    leadingIcon = { Icon(if (msg.bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder, null) })
                                DropdownMenuItem(text = { Text("Copy") }, onClick = {
                                    menuOpen = false
                                    copyToClipboard(ctx, msg.content)
                                }, leadingIcon = { Icon(Icons.Filled.ContentCopy, null) })
                                DropdownMenuItem(text = { Text("Save") }, onClick = {
                                    menuOpen = false; onAction(ChatAction.SAVE)
                                }, leadingIcon = { Icon(Icons.Filled.Save, null) })
                                DropdownMenuItem(text = { Text("Export") }, onClick = {
                                    menuOpen = false; onAction(ChatAction.EXPORT)
                                }, leadingIcon = { Icon(Icons.Outlined.Description, null) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Composer(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onToggleRouting: () -> Unit,
    onAttach: () -> Unit,
    onVoice: () -> Unit,
    routingLabel: String,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable(onClick = onToggleRouting)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Speed, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Routing · $routingLabel", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Filled.ArrowDropDown, null, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Outlined.PrivacyTip, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Secrets redacted", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onAttach) {
                Icon(Icons.Filled.AttachFile, null, tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onVoice) {
                Icon(Icons.Filled.GraphicEq, null, tint = MaterialTheme.colorScheme.secondary)
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Send a message, plan, or task…") },
                keyboardOptions = KeyboardOptions.Default,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                maxLines = 4,
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Send, null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun CreateSessionDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New chat session") },
        text = {
            Column {
                Text("Session title", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, placeholder = { Text("e.g. Plan RBAC v2") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { if (title.isNotBlank()) onConfirm(title) }) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun SessionActionsDialog(session: ChatSession, onDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Session · ${session.title}") },
        text = {
            Column {
                Text("${session.messageCount} messages · ${session.tokensUsed} tokens")
                Text("Created ${formatTime(session.createdAtEpoch)}")
                if (session.bookmarkedMessageIds.isNotEmpty()) {
                    Text("${session.bookmarkedMessageIds.size} bookmarked messages", color = MaterialTheme.colorScheme.secondary)
                }
                Spacer(Modifier.height(8.dp))
                Text("Deleting a session does not destroy persistent project state.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Delete session", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

@Composable
private fun AttachmentSheetDialog(
    onPick: (String, String, Long) -> Unit,
    onClipboard: () -> Unit,
    onUrl: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var url by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("") }
    var fileSize by remember { mutableStateOf("0") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Attach") },
        text = {
            Column {
                Row {
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .clickable { onPick("uploaded-${System.currentTimeMillis() / 1000}.bin", "application/octet-stream", 1024) }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.AttachFile, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            Text("File picker", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .clickable { onClipboard() }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.ContentCopy, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            Text("Clipboard", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("URL", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(value = url, onValueChange = { url = it }, placeholder = { Text("https://…") }, singleLine = true)
                Spacer(Modifier.height(6.dp))
                TextButton(onClick = { if (url.isNotBlank()) onUrl(url) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Link, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Attach URL")
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } },
    )
}

private fun copyToClipboard(ctx: Context, text: String) {
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("musgo", text))
}

private data class Quad(val align: Alignment.Horizontal, val accent: Color, val label: String, val icon: ImageVector)

private fun formatTime(epoch: Long): String =
    SimpleDateFormat("HH:mm", Locale.US).format(Date(epoch * 1000))
