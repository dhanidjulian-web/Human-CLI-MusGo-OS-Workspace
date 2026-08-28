package com.agon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.AgentState
import com.agon.app.data.ArtifactStatus
import com.agon.app.data.AuditSeverity
import com.agon.app.data.DeployState
import com.agon.app.data.LogStream
import com.agon.app.data.PatStatus
import com.agon.app.data.PrState
import com.agon.app.data.ProjectStatus
import com.agon.app.data.ProviderStatus
import com.agon.app.data.SandboxState
import com.agon.app.data.StepState
import com.agon.app.data.TaskState
import com.agon.app.data.WorkflowState

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (trailing != null) trailing()
    }
}

@Composable
fun SovereignCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun StatChip(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
) {
    SovereignCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StatusBadge(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    pulse: Boolean = false,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (pulse) color else color.copy(alpha = 0.9f))
        )
        Spacer(Modifier.width(6.dp))
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProviderStatus.toColor(): Color = when (this) {
    ProviderStatus.ONLINE -> Color(0xFF14B886)
    ProviderStatus.DEGRADED -> Color(0xFFF59E0B)
    ProviderStatus.OFFLINE -> Color(0xFFEF4444)
    ProviderStatus.MAINTENANCE -> Color(0xFF94A3B8)
    ProviderStatus.RATE_LIMITED -> Color(0xFFF59E0B)
    ProviderStatus.COOLDOWN -> Color(0xFFF97316)
    ProviderStatus.UNKNOWN -> Color(0xFF94A3B8)
}

@Composable
fun ProviderStatus.toLabel(): String = when (this) {
    ProviderStatus.ONLINE -> "Online"
    ProviderStatus.DEGRADED -> "Degraded"
    ProviderStatus.OFFLINE -> "Offline"
    ProviderStatus.MAINTENANCE -> "Maintenance"
    ProviderStatus.RATE_LIMITED -> "Rate limited"
    ProviderStatus.COOLDOWN -> "Cooldown"
    ProviderStatus.UNKNOWN -> "Unknown"
}

@Composable
fun ProjectStatus.toColor(): Color = when (this) {
    ProjectStatus.DRAFT -> Color(0xFF94A3B8)
    ProjectStatus.ACTIVE -> Color(0xFF14B886)
    ProjectStatus.BUILDING -> Color(0xFF3B82F6)
    ProjectStatus.TESTING -> Color(0xFFA855F7)
    ProjectStatus.AWAITING_APPROVAL -> Color(0xFFF59E0B)
    ProjectStatus.DEPLOYED -> Color(0xFF14B886)
    ProjectStatus.FAILED -> Color(0xFFEF4444)
    ProjectStatus.PAUSED -> Color(0xFF64748B)
}

@Composable
fun ProjectStatus.toLabel(): String = when (this) {
    ProjectStatus.DRAFT -> "Draft"
    ProjectStatus.ACTIVE -> "Active"
    ProjectStatus.BUILDING -> "Building"
    ProjectStatus.TESTING -> "Testing"
    ProjectStatus.AWAITING_APPROVAL -> "Awaiting approval"
    ProjectStatus.DEPLOYED -> "Deployed"
    ProjectStatus.FAILED -> "Failed"
    ProjectStatus.PAUSED -> "Paused"
}

@Composable
fun TaskState.toColor(): Color = when (this) {
    TaskState.QUEUED -> Color(0xFF94A3B8)
    TaskState.RUNNING -> Color(0xFF3B82F6)
    TaskState.WAITING -> Color(0xFFF59E0B)
    TaskState.REVIEW -> Color(0xFFA855F7)
    TaskState.DONE -> Color(0xFF14B886)
    TaskState.FAILED -> Color(0xFFEF4444)
    TaskState.BLOCKED -> Color(0xFFF97316)
    TaskState.CANCELLED -> Color(0xFF64748B)
}

@Composable
fun StepState.toColor(): Color = when (this) {
    StepState.PENDING -> Color(0xFF94A3B8)
    StepState.RUNNING -> Color(0xFF3B82F6)
    StepState.DONE -> Color(0xFF14B886)
    StepState.FAILED -> Color(0xFFEF4444)
    StepState.SKIPPED -> Color(0xFF64748B)
    StepState.BLOCKED -> Color(0xFFF97316)
    StepState.WAITING_APPROVAL -> Color(0xFFF59E0B)
}

@Composable
fun WorkflowState.toColor(): Color = when (this) {
    WorkflowState.DRAFT -> Color(0xFF94A3B8)
    WorkflowState.RUNNING -> Color(0xFF3B82F6)
    WorkflowState.PAUSED -> Color(0xFFF59E0B)
    WorkflowState.AWAITING_APPROVAL -> Color(0xFFF59E0B)
    WorkflowState.COMPLETED -> Color(0xFF14B886)
    WorkflowState.FAILED -> Color(0xFFEF4444)
    WorkflowState.ROLLED_BACK -> Color(0xFFF97316)
}

@Composable
fun WorkflowState.toLabel(): String = when (this) {
    WorkflowState.DRAFT -> "Draft"
    WorkflowState.RUNNING -> "Running"
    WorkflowState.PAUSED -> "Paused"
    WorkflowState.AWAITING_APPROVAL -> "Awaiting approval"
    WorkflowState.COMPLETED -> "Completed"
    WorkflowState.FAILED -> "Failed"
    WorkflowState.ROLLED_BACK -> "Rolled back"
}

@Composable
fun TaskState.toLabel(): String = when (this) {
    TaskState.QUEUED -> "Queued"
    TaskState.RUNNING -> "Running"
    TaskState.WAITING -> "Waiting"
    TaskState.REVIEW -> "Review"
    TaskState.DONE -> "Done"
    TaskState.FAILED -> "Failed"
    TaskState.BLOCKED -> "Blocked"
    TaskState.CANCELLED -> "Cancelled"
}

@Composable
fun StepState.toLabel(): String = when (this) {
    StepState.PENDING -> "Pending"
    StepState.RUNNING -> "Running"
    StepState.DONE -> "Done"
    StepState.FAILED -> "Failed"
    StepState.SKIPPED -> "Skipped"
    StepState.BLOCKED -> "Blocked"
    StepState.WAITING_APPROVAL -> "Awaiting approval"
}

@Composable
fun DeployState.toLabel(): String = when (this) {
    DeployState.QUEUED -> "Queued"
    DeployState.IN_PROGRESS -> "In progress"
    DeployState.LIVE -> "Live"
    DeployState.DEGRADED -> "Degraded"
    DeployState.FAILED -> "Failed"
    DeployState.ROLLED_BACK -> "Rolled back"
}

@Composable
fun AgentState.toLabel(): String = when (this) {
    AgentState.IDLE -> "Idle"
    AgentState.RUNNING -> "Running"
    AgentState.WAITING_APPROVAL -> "Waiting approval"
    AgentState.AWAITING_HUMAN -> "Awaiting human"
    AgentState.RECOVERING -> "Recovering"
    AgentState.OFFLINE -> "Offline"
}

@Composable
fun SandboxState.toLabel(): String = when (this) {
    SandboxState.PROVISIONING -> "Provisioning"
    SandboxState.READY -> "Ready"
    SandboxState.EXECUTING -> "Executing"
    SandboxState.AWAITING_APPROVAL -> "Awaiting approval"
    SandboxState.DESTROYED -> "Destroyed"
    SandboxState.FAILED -> "Failed"
}

@Composable
fun ArtifactStatus.toLabel(): String = when (this) {
    ArtifactStatus.PENDING -> "Pending"
    ArtifactStatus.VERIFIED -> "Verified"
    ArtifactStatus.DEPLOYED -> "Deployed"
    ArtifactStatus.SUPERSEDED -> "Superseded"
    ArtifactStatus.FAILED -> "Failed"
}

@Composable
fun PrState.toLabel(): String = when (this) {
    PrState.OPEN -> "Open"
    PrState.MERGED -> "Merged"
    PrState.CLOSED -> "Closed"
    PrState.DRAFT -> "Draft"
}

@Composable
fun PatStatus.toLabel(): String = when (this) {
    PatStatus.ACTIVE -> "Active"
    PatStatus.EXPIRING -> "Expiring"
    PatStatus.REVOKED -> "Revoked"
}

@Composable
fun AuditSeverity.toLabel(): String = when (this) {
    AuditSeverity.INFO -> "Info"
    AuditSeverity.NOTICE -> "Notice"
    AuditSeverity.WARNING -> "Warning"
    AuditSeverity.CRITICAL -> "Critical"
}

@Composable
fun LogStream.toLabel(): String = when (this) {
    LogStream.STDOUT -> "stdout"
    LogStream.STDERR -> "stderr"
    LogStream.SYSTEM -> "system"
    LogStream.APPROVAL -> "approval"
    LogStream.NETWORK -> "network"
}

@Composable
fun DeployState.toColor(): Color = when (this) {
    DeployState.QUEUED -> Color(0xFF94A3B8)
    DeployState.IN_PROGRESS -> Color(0xFF3B82F6)
    DeployState.LIVE -> Color(0xFF14B886)
    DeployState.DEGRADED -> Color(0xFFF59E0B)
    DeployState.FAILED -> Color(0xFFEF4444)
    DeployState.ROLLED_BACK -> Color(0xFFF97316)
}

@Composable
fun AgentState.toColor(): Color = when (this) {
    AgentState.IDLE -> Color(0xFF94A3B8)
    AgentState.RUNNING -> Color(0xFF3B82F6)
    AgentState.WAITING_APPROVAL -> Color(0xFFF59E0B)
    AgentState.AWAITING_HUMAN -> Color(0xFFA855F7)
    AgentState.RECOVERING -> Color(0xFFF97316)
    AgentState.OFFLINE -> Color(0xFF64748B)
}

@Composable
fun SandboxState.toColor(): Color = when (this) {
    SandboxState.PROVISIONING -> Color(0xFF94A3B8)
    SandboxState.READY -> Color(0xFF3B82F6)
    SandboxState.EXECUTING -> Color(0xFF3B82F6)
    SandboxState.AWAITING_APPROVAL -> Color(0xFFF59E0B)
    SandboxState.DESTROYED -> Color(0xFF64748B)
    SandboxState.FAILED -> Color(0xFFEF4444)
}

@Composable
fun ArtifactStatus.toColor(): Color = when (this) {
    ArtifactStatus.PENDING -> Color(0xFF94A3B8)
    ArtifactStatus.VERIFIED -> Color(0xFF14B886)
    ArtifactStatus.DEPLOYED -> Color(0xFF14B886)
    ArtifactStatus.SUPERSEDED -> Color(0xFF64748B)
    ArtifactStatus.FAILED -> Color(0xFFEF4444)
}

@Composable
fun PrState.toColor(): Color = when (this) {
    PrState.OPEN -> Color(0xFF14B886)
    PrState.MERGED -> Color(0xFFA855F7)
    PrState.CLOSED -> Color(0xFFEF4444)
    PrState.DRAFT -> Color(0xFF94A3B8)
}

@Composable
fun PatStatus.toColor(): Color = when (this) {
    PatStatus.ACTIVE -> Color(0xFF14B886)
    PatStatus.EXPIRING -> Color(0xFFF59E0B)
    PatStatus.REVOKED -> Color(0xFFEF4444)
}

@Composable
fun AuditSeverity.toColor(): Color = when (this) {
    AuditSeverity.INFO -> Color(0xFF3B82F6)
    AuditSeverity.NOTICE -> Color(0xFF14B886)
    AuditSeverity.WARNING -> Color(0xFFF59E0B)
    AuditSeverity.CRITICAL -> Color(0xFFEF4444)
}

@Composable
fun LogStream.toColor(): Color = when (this) {
    LogStream.STDOUT -> Color(0xFF14B886)
    LogStream.STDERR -> Color(0xFFF59E0B)
    LogStream.SYSTEM -> Color(0xFF3B82F6)
    LogStream.APPROVAL -> Color(0xFFA855F7)
    LogStream.NETWORK -> Color(0xFF94A3B8)
}

@Composable
fun GradientHeader(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f),
                    )
                )
            )
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(20.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun KeyValueRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProgressLine(pct: Int, color: Color = MaterialTheme.colorScheme.primary, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(
            Modifier
                .fillMaxWidth(pct / 100f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
    }
}

@Composable
fun TagChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EmptyState(title: String, body: String, icon: ImageVector = Icons.Default.Bolt, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
