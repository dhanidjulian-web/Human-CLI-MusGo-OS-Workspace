package com.agon.app.ui.screens.chat

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Token
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.AIModel
import com.agon.app.data.AIProvider
import com.agon.app.data.ModelCapability
import com.agon.app.data.ProviderApiKey
import com.agon.app.data.ProviderType
import com.agon.app.ui.components.ProgressLine
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.StatusBadge
import com.agon.app.ui.components.toColor
import com.agon.app.ui.components.toLabel
import com.agon.app.viewmodel.MusGoViewModel

@Composable
fun ProvidersScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showCreate by remember { mutableStateOf(false) }
    var addKeyFor by remember { mutableStateOf<String?>(null) }
    var deleteKeyFor by remember { mutableStateOf<Pair<String, String>?>(null) }
    var providerMenuFor by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.clip(RoundedCornerShape(10.dp)).clickable { nav.popBackStack() }.padding(8.dp),
                ) { Icon(Icons.Filled.ArrowBack, null) }
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("AI Providers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("One provider · many keys · priority · rotation · fallback · cooldown", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { showCreate = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("New provider", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Router", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("Routing mode · ${state.activeRoutingMode.toLabel()}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Capability matching · availability · API key rotation · fallback chain · priority sort",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        items(state.providers, key = { it.id }) { p ->
            ProviderCard(
                p,
                onAddKey = { addKeyFor = p.id },
                onDeleteKey = { keyId -> deleteKeyFor = p.id to keyId },
                onToggleKey = { keyId, enabled -> vm.toggleProviderKey(p.id, keyId, enabled) },
                onTestKey = { keyId -> vm.testProviderKey(p.id, keyId) },
                onSetActiveKey = { keyId -> vm.setActiveProviderKey(p.id, keyId) },
                onChangeKeyPriority = { keyId, prio -> vm.setKeyPriority(p.id, keyId, prio) },
                onChangeProviderPriority = { prio -> vm.setProviderPriority(p.id, prio) },
                onToggleModel = { modelId -> vm.toggleModel(p.id, modelId) },
                onProviderMenu = { providerMenuFor = p.id },
                menuOpen = providerMenuFor == p.id,
                onMenuDismiss = { providerMenuFor = null },
                onDeleteProvider = { vm.deleteProvider(p.id) },
            )
        }
    }

    if (showCreate) {
        CreateProviderDialog(
            onConfirm = { name, type, baseUrl, region ->
                vm.createProvider(name, type, baseUrl, region)
                showCreate = false
            },
            onDismiss = { showCreate = false },
        )
    }

    addKeyFor?.let { pid ->
        AddKeyDialog(
            onConfirm = { label, rawToken, prio ->
                vm.addProviderKey(pid, label, rawToken, prio)
                addKeyFor = null
            },
            onDismiss = { addKeyFor = null },
        )
    }

    deleteKeyFor?.let { (pid, kid) ->
        AlertDialog(
            onDismissRequest = { deleteKeyFor = null },
            title = { Text("Remove API key?") },
            text = { Text("The key will be revoked immediately. Active sessions will fall back to the next available key.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.removeProviderKey(pid, kid)
                    deleteKeyFor = null
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = { TextButton(onClick = { deleteKeyFor = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun ProviderCard(
    p: AIProvider,
    onAddKey: () -> Unit,
    onDeleteKey: (String) -> Unit,
    onToggleKey: (String, Boolean) -> Unit,
    onTestKey: (String) -> Unit,
    onSetActiveKey: (String) -> Unit,
    onChangeKeyPriority: (String, Int) -> Unit,
    onChangeProviderPriority: (Int) -> Unit,
    onToggleModel: (String) -> Unit,
    onProviderMenu: () -> Unit,
    menuOpen: Boolean,
    onMenuDismiss: () -> Unit,
    onDeleteProvider: () -> Unit,
) {
    val color = p.status.toColor()
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.Hub, null, tint = color) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(p.name, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(6.dp))
                        StatusBadge(p.status.toLabel(), color, pulse = p.status.toLabel() == "Online")
                    }
                    Text("${p.region} · ${p.type.toLabel()}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(p.baseUrl, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${p.latencyMs}ms", fontWeight = FontWeight.Bold)
                    Text("latency", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box {
                    IconButton(onClick = onProviderMenu) { Icon(Icons.Filled.MoreVert, null) }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = onMenuDismiss) {
                        DropdownMenuItem(text = { Text("Delete provider") }, onClick = { onMenuDismiss(); onDeleteProvider() },
                            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) })
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row {
                StatPill("${p.apiKeys.size} keys", Icons.Filled.Token, Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                StatPill(if (p.supportsVision) "Vision" else "No-vision", Icons.Outlined.Visibility, Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                StatPill(if (p.supportsTools) "Tools" else "No-tools", Icons.Filled.Build, Modifier.weight(1f))
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Usage", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                ProgressLine(p.monthlyUsagePct, color = color, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(6.dp))
                Text("${p.monthlyUsagePct}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Provider priority", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Slider(
                    value = p.priority.toFloat(),
                    onValueChange = { onChangeProviderPriority(it.toInt()) },
                    valueRange = 0f..150f,
                    steps = 0,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Text("${p.priority}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(28.dp))
            }

            // API KEYS section
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Key, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("API keys", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                        .clickable(onClick = onAddKey)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add key", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            if (p.apiKeys.isEmpty()) {
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(10.dp),
                ) {
                    Text("No keys configured · local provider (e.g. Ollama) does not require keys.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                p.apiKeys.forEach { k ->
                    ApiKeyRow(k, isActive = k.id == p.activeKeyId,
                        onToggle = { enabled -> onToggleKey(k.id, enabled) },
                        onTest = { onTestKey(k.id) },
                        onSetActive = { onSetActiveKey(k.id) },
                        onPriority = { prio -> onChangeKeyPriority(k.id, prio) },
                        onDelete = { onDeleteKey(k.id) })
                }
            }

            Spacer(Modifier.height(10.dp))
            Text("Models", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            p.models.forEach { m -> ModelRow(m, onToggle = { onToggleModel(m.id) }) }
        }
    }
}

@Composable
private fun ApiKeyRow(
    k: ProviderApiKey,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
    onTest: () -> Unit,
    onSetActive: () -> Unit,
    onPriority: (Int) -> Unit,
    onDelete: () -> Unit,
) {
    val color = if (k.enabled) k.status.toColor() else MaterialTheme.colorScheme.outline
    var menuOpen by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(10.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isActive) {
                    StatusBadge("Active", MaterialTheme.colorScheme.primary, pulse = true)
                    Spacer(Modifier.width(6.dp))
                } else {
                    Box(
                        Modifier.size(8.dp).clip(CircleShape).background(if (k.enabled) color else MaterialTheme.colorScheme.outline)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(k.label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text(k.maskedToken, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = k.enabled, onCheckedChange = onToggle, modifier = Modifier.size(40.dp))
                Spacer(Modifier.width(4.dp))
                Box {
                    IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.MoreVert, null, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("Test key") }, onClick = { menuOpen = false; onTest() },
                            leadingIcon = { Icon(Icons.Filled.CheckCircle, null) })
                        if (!isActive && k.enabled) {
                            DropdownMenuItem(text = { Text("Set active") }, onClick = { menuOpen = false; onSetActive() },
                                leadingIcon = { Icon(Icons.Filled.Bolt, null) })
                        }
                        DropdownMenuItem(text = { Text("Delete key") }, onClick = { menuOpen = false; onDelete() },
                            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) })
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Row {
                Text("${k.requestsToday} req · ${k.failuresToday} fail today", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                Text(if (k.lastTestOk) "last test ✓" else "untested", fontSize = 10.sp, color = if (k.lastTestOk) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Priority", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                Slider(value = k.priority.toFloat(), onValueChange = { onPriority(it.toInt()) }, valueRange = 0f..100f, steps = 0, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(6.dp))
                Text("${k.priority}", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(22.dp))
            }
            if (k.cooldownUntilEpoch > System.currentTimeMillis() / 1000) {
                Text("cooldown active", fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary)
            }
            if (k.notes.isNotBlank()) {
                Text(k.notes, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun StatPill(label: String, icon: ImageVector, modifier: Modifier) {
    Row(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp)
    }
}

@Composable
private fun ModelRow(m: AIModel, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(8.dp).clip(CircleShape).background(if (m.enabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline),
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(m.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Row {
                Text(m.capability.toLabel(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(" · ${m.contextWindow / 1000}k ctx", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (m.costPer1kTokens == 0.0) Text(" · free", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary)
                else Text(" · \$${m.costPer1kTokens}/1k", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(
            checked = m.enabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(),
        )
    }
}

@Composable
private fun CreateProviderDialog(
    onConfirm: (String, ProviderType, String, String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ProviderType.OPENAI_COMPATIBLE) }
    var baseUrl by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("Global") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New AI provider") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("e.g. OpenRouter, Internal-Sovereign") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                Text("Type", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row {
                    listOf(
                        ProviderType.OPENAI_COMPATIBLE to "OpenAI",
                        ProviderType.ANTHROPIC_COMPATIBLE to "Anthropic",
                        ProviderType.GOOGLE_COMPATIBLE to "Google",
                        ProviderType.LOCAL_OLLAMA to "Ollama",
                        ProviderType.CUSTOM_HTTP to "Custom",
                    ).forEach { (t, label) ->
                        val sel = t == type
                        Box(
                            Modifier.padding(end = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (sel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clickable { type = t }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(label, fontSize = 10.sp, color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, placeholder = { Text("Base URL") }, singleLine = true)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(value = region, onValueChange = { region = it }, placeholder = { Text("Region") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, type, baseUrl.ifBlank { "https://" }, region) }) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun AddKeyDialog(
    onConfirm: (String, String, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var label by remember { mutableStateOf("") }
    var raw by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(50f) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add API key") },
        text = {
            Column {
                Text("Label", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(value = label, onValueChange = { label = it }, placeholder = { Text("e.g. Production · prod-1") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                Text("Token (stored encrypted · masked in UI)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(value = raw, onValueChange = { raw = it }, placeholder = { Text("paste token here") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                Text("Priority: ${priority.toInt()}", fontSize = 11.sp)
                Slider(value = priority, onValueChange = { priority = it }, valueRange = 0f..100f, steps = 0)
                Text("Higher priority keys are tried first. Router rotates on cooldown / failure.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = { TextButton(onClick = { if (label.isNotBlank() && raw.isNotBlank()) onConfirm(label, raw, priority.toInt()) }) { Text("Add key") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun ProviderType.toLabel(): String = when (this) {
    ProviderType.OPENAI_COMPATIBLE -> "OpenAI-compatible"
    ProviderType.ANTHROPIC_COMPATIBLE -> "Anthropic-compatible"
    ProviderType.GOOGLE_COMPATIBLE -> "Google-compatible"
    ProviderType.LOCAL_OLLAMA -> "Local Ollama"
    ProviderType.CUSTOM_HTTP -> "Custom HTTP"
}

@Composable
private fun ModelCapability.toLabel(): String = when (this) {
    ModelCapability.CHAT -> "chat"
    ModelCapability.CODE -> "code"
    ModelCapability.REASONING -> "reasoning"
    ModelCapability.VISION -> "vision"
    ModelCapability.EMBEDDING -> "embedding"
    ModelCapability.LONG_CONTEXT -> "long-ctx"
    ModelCapability.AGENT -> "agent"
}
