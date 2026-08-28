package com.agon.app.ui.screens.security

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Token
import androidx.compose.material.icons.outlined.WarningAmber
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.AuditEvent
import com.agon.app.data.SecurityPosture
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
fun SecurityScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var redaction by remember { mutableStateOf(state.security.redactionEnabled) }
    var dangerousConfirm by remember { mutableStateOf(state.security.dangerousActionConfirm) }
    var sandboxEgress by remember { mutableStateOf(state.security.sandboxEgressAllowed) }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.clip(RoundedCornerShape(10.dp)).clickable { nav.popBackStack() }.padding(8.dp)) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Security & Audit", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Mandatory: secrets, authz, redaction, audit, dangerous-action confirmation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item { PostureCard(state.security) }

        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Policies", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    PolicyToggle("Redact secrets in logs, chat, agent context, handover, build output", Icons.Outlined.Lock, redaction) { redaction = it }
                    PolicyToggle("Confirm dangerous actions (deploy, force-push, secret exposure)", Icons.Outlined.WarningAmber, dangerousConfirm) { dangerousConfirm = it }
                    PolicyToggle("Allow sandbox network egress", Icons.Outlined.Hub, sandboxEgress) { sandboxEgress = it }
                }
            }
        }

        if (state.security.threats.isNotEmpty()) {
            item {
                SovereignCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Active threats", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        state.security.threats.forEach { t ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.WarningAmber, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(t, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
        }

        item {
            Row {
                Text("Audit events", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(vertical = 6.dp))
                Spacer(Modifier.weight(1f))
                Text("${state.auditEvents.size} total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        items(state.auditEvents, key = { it.id }) { e -> AuditRow(e) }
    }
}

@Composable
private fun PostureCard(s: SecurityPosture) {
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Shield, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Security posture", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                StatusBadge(if (s.threats.isEmpty()) "Clean" else "${s.threats.size} alerts",
                    if (s.threats.isEmpty()) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(10.dp))
            Row {
                StatBox("Secrets", "${s.secretCount}", Modifier.weight(1f), Icons.Outlined.Lock)
                Spacer(Modifier.width(6.dp))
                StatBox("PATs", "${s.patCount}", Modifier.weight(1f), Icons.Outlined.Token)
                Spacer(Modifier.width(6.dp))
                StatBox("OAuth", "${s.oauthCount}", Modifier.weight(1f), Icons.Outlined.Token)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Last secret scan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(6.dp))
                Text(s.lastScanEpoch?.let { SimpleDateFormat("MMM d HH:mm", Locale.US).format(Date(it * 1000)) } ?: "never", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(4.dp))
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun PolicyToggle(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Switch(checked = value, onCheckedChange = onChange)
    }
}

@Composable
private fun AuditRow(e: AuditEvent) {
    val color = e.severity.toColor()
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier.size(28.dp).clip(CircleShape).background(color.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.CheckCircle, null, tint = color, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row {
                StatusBadge(e.severity.toLabel(), color)
                Spacer(Modifier.width(6.dp))
                Text(e.action, fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
            }
            Text("actor · ${e.actor}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("target · ${e.target}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("outcome · ${e.outcome}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(SimpleDateFormat("MMM d HH:mm", Locale.US).format(Date(e.timestampEpoch * 1000)),
            fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
