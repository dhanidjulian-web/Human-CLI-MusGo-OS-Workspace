package com.agon.app.ui.screens.settings

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Brightness4
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.SyncAlt
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.RoutingMode
import com.agon.app.ui.components.SovereignCard
import com.agon.app.viewmodel.MusGoViewModel

@Composable
fun SettingsScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var dark by remember { mutableStateOf(true) }
    var redaction by remember { mutableStateOf(true) }
    var dangerousConfirm by remember { mutableStateOf(true) }
    var sandboxEgress by remember { mutableStateOf(false) }
    val user = state.user

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        }

        // Profile card
        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(56.dp).clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(user.displayName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(user.displayName, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Filled.Verified, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                        }
                        Text(user.handle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user.email, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item { SectionLabel("Modules") }
        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    NavRow("AI Providers", "${state.providers.size} configured", Icons.Outlined.Hub) { nav.navigate("providers") }
                    NavRow("GitHub Connector", "${state.repos.size} repos · ${state.pats.size} PATs", Icons.Outlined.Code) { nav.navigate("github") }
                    NavRow("Agent Orchestrator", "${state.agents.size} agents", Icons.Outlined.SwapHoriz) { nav.navigate("agents") }
                    NavRow("Workflow Engine", "${state.workflows.size} workflows", Icons.Outlined.AccountTree) { nav.navigate("workflow") }
                    NavRow("Sandbox Runtime", "${state.sandboxes.size} sandboxes", Icons.Outlined.Memory) { nav.navigate("sandbox") }
                    NavRow("Build & Artifacts", "${state.artifacts.size} artifacts", Icons.Outlined.Tune) { nav.navigate("build") }
                    NavRow("Deployment", "${state.deployments.size} deploys", Icons.Outlined.SyncAlt) { nav.navigate("deployment") }
                    NavRow("Memory Layers", "${state.memoryEntries.size} entries", Icons.Outlined.Memory) { nav.navigate("memory") }
                    NavRow("Agent Handover", "${state.handovers.size} active", Icons.Outlined.SwapHoriz) { nav.navigate("handover") }
                    NavRow("Security & Audit", "${state.auditEvents.size} events", Icons.Outlined.Security) { nav.navigate("security") }
                }
            }
        }

        item { SectionLabel("Defaults") }
        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    ToggleRow("Dark theme (system follows)", Icons.Outlined.Brightness4, dark) { dark = it }
                    ToggleRow("Redact secrets in logs/agent context", Icons.Outlined.Security, redaction) { redaction = it }
                    ToggleRow("Confirm dangerous actions", Icons.Outlined.Security, dangerousConfirm) { dangerousConfirm = it }
                    ToggleRow("Allow sandbox network egress", Icons.Outlined.Hub, sandboxEgress) { sandboxEgress = it }
                }
            }
        }

        item { SectionLabel("Default routing mode") }
        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(Modifier.fillMaxWidth()) {
                        listOf(
                            RoutingMode.AUTO_FREE to "Free",
                            RoutingMode.AUTO_BALANCED to "Balanced",
                            RoutingMode.AUTO_QUICK to "Quick",
                            RoutingMode.MANUAL to "Manual",
                        ).forEach { (mode, label) ->
                            val selected = state.activeRoutingMode == mode
                            Box(
                                Modifier
                                    .weight(1f)
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
                                    .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .clickable { vm.setRoutingMode(mode) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        item { SectionLabel("Constitution") }
        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("MusGo-OS · 2in1 Musyawarah & Gotong-Royong", fontWeight = FontWeight.SemiBold)
                    Text("Sovereign AI Operating Civilization", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("© 2026 — Dhani Yuliawan · All Rights Reserved", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("22 specification modules · 10 build phases", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
}

@Composable
private fun NavRow(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun ToggleRow(title: String, icon: ImageVector, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(title, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Switch(checked = value, onCheckedChange = onChange)
    }
}
