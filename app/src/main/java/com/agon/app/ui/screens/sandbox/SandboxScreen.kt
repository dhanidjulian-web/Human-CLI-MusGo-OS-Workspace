package com.agon.app.ui.screens.sandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.Sandbox
import com.agon.app.data.SandboxLog
import com.agon.app.data.SandboxRuntime
import com.agon.app.data.TerminalKind
import com.agon.app.data.TerminalSession
import com.agon.app.data.TerminalState
import com.agon.app.ui.components.ProgressLine
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.StatusBadge
import com.agon.app.ui.components.toColor
import com.agon.app.ui.components.toLabel
import com.agon.app.viewmodel.MusGoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SandboxScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var selectedId by remember { mutableStateOf(state.sandboxes.firstOrNull()?.id) }
    val selected = state.sandboxes.firstOrNull { it.id == selectedId } ?: state.sandboxes.first()
    val logs = state.sandboxLogs.filter { it.sandboxId == selected.id }
    val terminals = state.terminals.filter { it.sandboxId == selected.id }
    var showCreate by remember { mutableStateOf(false) }
    var showCommand by remember { mutableStateOf(false) }
    var destroyFor by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.clip(RoundedCornerShape(10.dp)).clickable { nav.popBackStack() }.padding(8.dp)) {
                Icon(Icons.Filled.ArrowBack, null)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text("Sandbox Runtime", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Isolated, ephemeral · controlled filesystem, network, and resource limits", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    Text("Create", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        // Sandbox selector
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState())) {
            state.sandboxes.forEach { sb ->
                val isSel = sb.id == selectedId
                Box(
                    Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { selectedId = sb.id }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Storage, null, modifier = Modifier.size(14.dp), tint = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Text(sb.workdirPath.substringAfterLast('/'), fontSize = 12.sp, fontWeight = FontWeight.Medium,
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                SandboxInfoCard(
                    selected,
                    onExecute = { showCommand = true },
                    onDestroy = { destroyFor = selected.id },
                )
            }
            item {
                TerminalCard(
                    terminals = terminals,
                    onConnect = { id -> vm.connectTerminal(id) },
                    onDisconnect = { id -> vm.disconnectTerminal(id) },
                )
            }
            item { Text("Log stream", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
            items(logs) { log -> LogRow(log) }
        }
    }

    if (showCreate) {
        CreateSandboxDialog(
            onConfirm = { projectId, runtime ->
                val id = vm.createSandbox(projectId, runtime)
                selectedId = id
                showCreate = false
            },
            onDismiss = { showCreate = false },
            projects = state.projects.map { it.id to it.name },
        )
    }

    if (showCommand) {
        RunCommandDialog(
            onConfirm = { cmd ->
                vm.executeSandboxCommand(selected.id, cmd)
                showCommand = false
            },
            onDismiss = { showCommand = false },
        )
    }

    destroyFor?.let { id ->
        AlertDialog(
            onDismissRequest = { destroyFor = null },
            title = { Text("Destroy sandbox?") },
            text = { Text("The sandbox filesystem will be torn down. Persistent project state, workflows, artifacts, and memory are NOT destroyed.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.destroySandbox(id)
                    destroyFor = null
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Destroy", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = { TextButton(onClick = { destroyFor = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun SandboxInfoCard(s: Sandbox, onExecute: () -> Unit, onDestroy: () -> Unit) {
    val color = s.state.toColor()
    var menuOpen by remember { mutableStateOf(false) }
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(s.workdirPath, fontWeight = FontWeight.Medium, fontSize = 12.sp, modifier = Modifier.weight(1f))
                StatusBadge(s.state.toLabel(), color)
                Spacer(Modifier.width(4.dp))
                Box {
                    IconButton(onClick = { menuOpen = true }) { Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp)) }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("Execute command") }, onClick = { menuOpen = false; onExecute() },
                            leadingIcon = { Icon(Icons.Filled.PlayArrow, null) })
                        DropdownMenuItem(text = { Text("Destroy") }, onClick = { menuOpen = false; onDestroy() },
                            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) })
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoBox("CPU", s.cpuLimit, Modifier.weight(1f))
                InfoBox("Memory", "${s.resourceLimitMb} MB", Modifier.weight(1f))
                InfoBox("Net", if (s.networkAccess) "Open" else "Closed", Modifier.weight(1f), if (s.networkAccess) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary)
            }
            Spacer(Modifier.height(8.dp))
            Row {
                InfoBox("Commands", "${s.commandCount}", Modifier.weight(1f))
                InfoBox("Logs", "${s.logCount}", Modifier.weight(1f))
                InfoBox("SHA", s.clonedSha ?: "—", Modifier.weight(1.4f))
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Runtime", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                Text(s.runtime.toLabel(), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                if (s.remoteEndpoint != null) {
                    Spacer(Modifier.width(6.dp))
                    Text(s.remoteEndpoint, fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary, fontFamily = FontFamily.Monospace)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row {
                Text("Lifetime", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                val total = s.expiresAtEpoch - s.createdAtEpoch
                val used = System.currentTimeMillis() / 1000 - s.createdAtEpoch
                ProgressLine(((used.toFloat() / total) * 100).toInt().coerceIn(0, 100), color = color, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Text("expires ${formatTime(s.expiresAtEpoch)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun TerminalCard(terminals: List<TerminalSession>, onConnect: (String) -> Unit, onDisconnect: (String) -> Unit) {
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Terminal, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Terminals · CLI / SSH / MCP", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            if (terminals.isEmpty()) {
                Text("No terminals attached.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                terminals.forEach { t ->
                    TerminalRow(t, onConnect = { onConnect(t.id) }, onDisconnect = { onDisconnect(t.id) })
                    Spacer(Modifier.height(6.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Note: remote-backed terminals (SSH, remote Docker, MCP) require a connected execution backend; local shells run on-device.", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TerminalRow(t: TerminalSession, onConnect: () -> Unit, onDisconnect: () -> Unit) {
    val color = when (t.state) {
        TerminalState.CONNECTED -> MaterialTheme.colorScheme.secondary
        TerminalState.DISCONNECTED -> MaterialTheme.colorScheme.outline
        TerminalState.ERROR -> MaterialTheme.colorScheme.error
        TerminalState.IDLE -> MaterialTheme.colorScheme.tertiary
    }
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            when (t.kind) {
                TerminalKind.LOCAL_SHELL -> Icons.Outlined.Terminal
                TerminalKind.SSH -> Icons.Outlined.Hub
                TerminalKind.MCP -> Icons.Outlined.SmartToy
                TerminalKind.DOCKER_EXEC -> Icons.Filled.Storage
                TerminalKind.K8S_EXEC -> Icons.Outlined.Cloud
            },
            null,
            tint = color,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(t.kind.toLabel(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(6.dp))
                StatusBadge(t.state.toLabel(), color, pulse = t.state == TerminalState.CONNECTED)
                if (t.requiresRemote) {
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier.clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                    ) { Text("remote", fontSize = 9.sp, color = MaterialTheme.colorScheme.tertiary) }
                }
            }
            Text(t.endpoint, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${t.commandCount} commands · last ${formatTime(t.lastCommandAtEpoch)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (t.state == TerminalState.CONNECTED || t.state == TerminalState.IDLE) {
            Box(
                Modifier.clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.18f))
                    .clickable(onClick = onDisconnect)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) { Text("Disconnect", fontSize = 10.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium) }
        } else {
            Box(
                Modifier.clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onConnect)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) { Text("Connect", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium) }
        }
    }
}

@Composable
private fun TerminalKind.toLabel(): String = when (this) {
    TerminalKind.LOCAL_SHELL -> "Local shell"
    TerminalKind.SSH -> "SSH"
    TerminalKind.MCP -> "MCP"
    TerminalKind.DOCKER_EXEC -> "Docker exec"
    TerminalKind.K8S_EXEC -> "K8s exec"
}

@Composable
private fun TerminalState.toLabel(): String = when (this) {
    TerminalState.CONNECTED -> "Connected"
    TerminalState.DISCONNECTED -> "Disconnected"
    TerminalState.ERROR -> "Error"
    TerminalState.IDLE -> "Idle"
}

@Composable
private fun SandboxRuntime.toLabel(): String = when (this) {
    SandboxRuntime.LOCAL_SHELL -> "Local shell"
    SandboxRuntime.REMOTE_DOCKER -> "Remote Docker"
    SandboxRuntime.REMOTE_K8S -> "Remote K8s"
    SandboxRuntime.REMOTE_SSH -> "Remote SSH"
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(value, fontWeight = FontWeight.Medium, fontSize = 12.sp, color = valueColor)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LogRow(log: SandboxLog) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(log.stream.toLabel(), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = log.stream.toColor(), modifier = Modifier.width(60.dp))
        Text(formatTime(log.timestampEpoch), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(70.dp))
        Column(Modifier.weight(1f)) {
            Text("$ ${log.command}", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(log.line, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
        log.exitCode?.let {
            Box(
                Modifier.clip(RoundedCornerShape(4.dp))
                    .background(if (it == 0) MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.error.copy(alpha = 0.18f))
                    .padding(horizontal = 4.dp, vertical = 1.dp),
            ) { Text("exit $it", fontSize = 9.sp, color = if (it == 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun CreateSandboxDialog(
    onConfirm: (String, SandboxRuntime) -> Unit,
    onDismiss: () -> Unit,
    projects: List<Pair<String, String>>,
) {
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()?.first ?: "") }
    var runtime by remember { mutableStateOf(SandboxRuntime.LOCAL_SHELL) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create sandbox") },
        text = {
            Column {
                Text("Project", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                projects.forEach { (id, pname) ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(if (selectedProject == id) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
                            .clickable { selectedProject = id }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(pname, fontSize = 12.sp, modifier = Modifier.weight(1f), color = if (selectedProject == id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text("Runtime", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row {
                    listOf(
                        SandboxRuntime.LOCAL_SHELL to "Local shell",
                        SandboxRuntime.REMOTE_DOCKER to "Docker",
                        SandboxRuntime.REMOTE_K8S to "K8s",
                        SandboxRuntime.REMOTE_SSH to "SSH",
                    ).forEach { (r, label) ->
                        val sel = r == runtime
                        Box(
                            Modifier.padding(end = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (sel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clickable { runtime = r }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(label, fontSize = 10.sp, color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (runtime != SandboxRuntime.LOCAL_SHELL) {
                    Text("Remote runtime requires an authenticated execution backend (SSH/Docker/K8s) — wired in via the Terminals panel.", fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary)
                }
            }
        },
        confirmButton = { TextButton(onClick = { if (selectedProject.isNotBlank()) onConfirm(selectedProject, runtime) }) { Text("Provision") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun RunCommandDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var cmd by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Execute command") },
        text = {
            Column {
                Text("This command runs in the sandbox. Network access follows the sandbox policy.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(value = cmd, onValueChange = { cmd = it }, placeholder = { Text("e.g. ./gradlew :app:test") }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { if (cmd.isNotBlank()) onConfirm(cmd) }) { Text("Run") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

private fun formatTime(epoch: Long): String {
    if (epoch == 0L) return "—"
    return SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(epoch * 1000))
}
