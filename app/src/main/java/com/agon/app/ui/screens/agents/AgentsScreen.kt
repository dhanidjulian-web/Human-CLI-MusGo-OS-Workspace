package com.agon.app.ui.screens.agents

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.Agent
import com.agon.app.data.AgentRole
import com.agon.app.data.AgentState
import com.agon.app.data.AgentTask
import com.agon.app.ui.components.ProgressLine
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.StatusBadge
import com.agon.app.ui.components.TagChip
import com.agon.app.ui.components.toColor
import com.agon.app.ui.components.toLabel
import com.agon.app.viewmodel.MusGoViewModel

@Composable
fun AgentsScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val runningTasks = state.tasks.filter { it.state == com.agon.app.data.TaskState.RUNNING }

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
                    Text("Agent Orchestrator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Planner · Developer · Tester · Builder · Deployer · Security · Docs · Reviewer", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row {
                        MetricPill("Agents", "${state.agents.size}", Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        MetricPill("Running", "${state.agents.count { it.state == AgentState.RUNNING }}", Modifier.weight(1f), MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(8.dp))
                        MetricPill("Awaiting human", "${state.agents.count { it.state == AgentState.AWAITING_HUMAN }}", Modifier.weight(1f), MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
        items(state.agents, key = { it.id }) { a ->
            AgentCard(a, runningTasks.firstOrNull { it.agentId == a.id })
        }
    }
}

@Composable
private fun MetricPill(label: String, value: String, modifier: Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    Column(
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AgentCard(a: Agent, task: AgentTask?) {
    val color = a.state.toColor()
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(a.role.color().copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(a.role.icon(), null, tint = a.role.color(), modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(a.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.width(6.dp))
                        TagChip(a.role.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                    Text(a.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(a.state.toLabel(), color, pulse = a.state == AgentState.RUNNING)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoMini("success", "${(a.successRate * 100).toInt()}%", Modifier.weight(1f))
                InfoMini("tasks", "${a.tasksCompleted}", Modifier.weight(1f))
                InfoMini("avg", "${a.avgLatencyMs}ms", Modifier.weight(1f))
                InfoMini("model", a.modelPreference, Modifier.weight(1.5f))
            }
            Spacer(Modifier.height(10.dp))
            Text("Capabilities", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                a.capabilities.forEach { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                a.skillIds.forEach { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
            }
            if (task != null) {
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(10.dp),
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Bolt, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Active task", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.tertiary)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(task.title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        ProgressLine(task.progressPct, color = task.state.toColor())
                        Spacer(Modifier.height(4.dp))
                        Text("${task.progressPct}% · ${task.state.toLabel()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoMini(label: String, value: String, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(value, fontWeight = FontWeight.Medium, fontSize = 12.sp)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AgentRole.color(): Color = when (this) {
    AgentRole.PLANNER -> Color(0xFFA855F7)
    AgentRole.DEVELOPER -> Color(0xFF3B82F6)
    AgentRole.TESTER -> Color(0xFF14B886)
    AgentRole.BUILDER -> Color(0xFFF59E0B)
    AgentRole.DEPLOYER -> Color(0xFFEF4444)
    AgentRole.REVIEWER -> Color(0xFF8B5CF6)
    AgentRole.SECURITY -> Color(0xFF06B6D4)
    AgentRole.DOCS -> Color(0xFF94A3B8)
}

@Composable
private fun AgentRole.icon() = when (this) {
    AgentRole.PLANNER -> Icons.Outlined.Hub
    AgentRole.DEVELOPER -> Icons.Outlined.Code
    AgentRole.TESTER -> Icons.Outlined.FactCheck
    AgentRole.BUILDER -> Icons.Outlined.Engineering
    AgentRole.DEPLOYER -> Icons.Outlined.RocketLaunch
    AgentRole.REVIEWER -> Icons.Outlined.FactCheck
    AgentRole.SECURITY -> Icons.Outlined.PrivacyTip
    AgentRole.DOCS -> Icons.Outlined.SmartToy
}
