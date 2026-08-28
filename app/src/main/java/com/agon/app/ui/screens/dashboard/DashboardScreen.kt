package com.agon.app.ui.screens.dashboard

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.Agent
import com.agon.app.data.AgentState
import com.agon.app.data.Deployment
import com.agon.app.data.Project
import com.agon.app.data.Workflow
import com.agon.app.data.WorkflowState
import com.agon.app.ui.components.ProgressLine
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.StatChip
import com.agon.app.ui.components.StatusBadge
import com.agon.app.ui.components.TagChip
import com.agon.app.ui.components.toColor
import com.agon.app.ui.components.toLabel
import com.agon.app.viewmodel.MusGoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val u = state.user

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { SovereignHero(u.displayName, u.handle, u.org, u.role) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChip(
                    label = "Providers Online",
                    value = "${vm.onlineProviderCount()}/${state.providers.size}",
                    icon = Icons.Outlined.Hub,
                    accent = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                )
                StatChip(
                    label = "Running Tasks",
                    value = "${vm.runningTasksCount()}",
                    icon = Icons.Outlined.AutoAwesome,
                    accent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChip(
                    label = "Active Workflows",
                    value = "${vm.runningWorkflowsCount()}",
                    icon = Icons.Outlined.Timeline,
                    accent = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                )
                StatChip(
                    label = "Live Deploys",
                    value = "${vm.liveDeploys()}",
                    icon = Icons.Outlined.RocketLaunch,
                    accent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            QuickActions(onAgents = { nav.navigate("agents") }, onWorkflow = { nav.navigate("workflow") },
                onSandbox = { nav.navigate("sandbox") }, onSecurity = { nav.navigate("security") })
        }

        item {
            SectionHeader(title = "Active Workflows", subtitle = "Persistent across sessions") {
                Text("See all", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { nav.navigate("workflow") })
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(state.workflows) { wf ->
                    WorkflowCard(wf, onClick = { nav.navigate("workflow") })
                }
            }
        }

        item { SectionHeader(title = "Projects", subtitle = "Tap any project to inspect its workflow") }
        items(state.projects, key = { it.id }) { p ->
            ProjectCard(p, onClick = { nav.navigate("project/${p.id}") })
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SovereignCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Outlined.Memory, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp)) }
                            Spacer(Modifier.width(10.dp))
                            Text("Memory Layers", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("${state.memoryEntries.size} entries", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "${state.memoryEntries.count { it.pinned }} pinned · 6 scopes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(10.dp))
                        Box(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clickable { nav.navigate("memory") }
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Open memory", fontWeight = FontWeight.Medium)
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.ChevronRight, null)
                            }
                        }
                    }
                }
                SovereignCard(modifier = Modifier.weight(1f)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center,
                            ) { Icon(Icons.Outlined.Cloud, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp)) }
                            Spacer(Modifier.width(10.dp))
                            Text("Handover", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text("${state.handovers.size} open", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Agents can resume work",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(10.dp))
                        Box(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clickable { nav.navigate("handover") }
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Open handover", fontWeight = FontWeight.Medium)
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.ChevronRight, null)
                            }
                        }
                    }
                }
            }
        }

        item { SectionHeader(title = "Recent Deployments", subtitle = "Cross-environment state") }
        items(state.deployments) { d ->
            DeploymentRow(d)
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun SovereignHero(name: String, handle: String, org: String, role: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                        MaterialTheme.colorScheme.surfaceVariant,
                    )
                )
            )
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(18.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Default.Verified, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    Text("$handle · $role", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(org, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Hub, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Sovereign AI Operating Civilization", fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
            Spacer(Modifier.height(2.dp))
            Text("MusGo · 2in1 Musyawarah & Gotong-Royong · v1.0", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickActions(onAgents: () -> Unit, onWorkflow: () -> Unit, onSandbox: () -> Unit, onSecurity: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ActionPill("Agents", Icons.Outlined.AutoAwesome, Modifier.weight(1f), onAgents)
        ActionPill("Workflow", Icons.Outlined.Timeline, Modifier.weight(1f), onWorkflow)
        ActionPill("Sandbox", Icons.Filled.Storage, Modifier.weight(1f), onSandbox)
        ActionPill("Security", Icons.Filled.Security, Modifier.weight(1f), onSecurity)
    }
}

@Composable
private fun ActionPill(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(6.dp))
            Text(label, fontWeight = FontWeight.Medium, fontSize = 12.sp)
        }
    }
}

@Composable
private fun WorkflowCard(wf: Workflow, onClick: () -> Unit) {
    val color = wf.state.toColor()
    Box(
        Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(label = wf.state.toLabel(), color = color, pulse = wf.state == WorkflowState.RUNNING || wf.state == WorkflowState.AWAITING_APPROVAL)
                Spacer(Modifier.weight(1f))
                Text(wf.id, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(10.dp))
            Text(wf.name, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Text("${wf.steps.size} steps · step ${wf.currentStepIndex + 1}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            val pct = (wf.currentStepIndex + 1) * 100 / wf.steps.size
            ProgressLine(pct, color = color)
        }
    }
}

@Composable
private fun ProjectCard(p: Project, onClick: () -> Unit) {
    val color = p.status.toColor()
    SovereignCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(p.status.toLabel(), color, pulse = p.status == com.agon.app.data.ProjectStatus.BUILDING || p.status == com.agon.app.data.ProjectStatus.TESTING)
                Spacer(Modifier.width(8.dp))
                Text(p.visibility.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                p.repoFullName?.let {
                    Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(p.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(p.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))
            Row {
                p.tags.take(3).forEach {
                    TagChip(it, modifier = Modifier.padding(end = 6.dp))
                }
                Spacer(Modifier.weight(1f))
                Text("branch · ${p.branch}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Row {
                MetaText("${p.agentCount} agents")
                Spacer(Modifier.width(10.dp))
                MetaText("${p.skillCount} skills")
                Spacer(Modifier.width(10.dp))
                MetaText("${p.blueprintCount} blueprints")
                Spacer(Modifier.weight(1f))
                p.lastBuildAtEpoch?.let {
                    Text("built ${timeAgo(it)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun MetaText(text: String) {
    Text(text, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun DeploymentRow(d: Deployment) {
    val color = d.state.toColor()
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.RocketLaunch, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(d.environment.name.lowercase().replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    StatusBadge(d.state.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }, color)
                }
                Text("v${d.version} · ${d.url ?: "no-url"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(timeAgo(d.startedAtEpoch), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String? = null, trailing: @Composable (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing?.invoke()
    }
}

private fun timeAgo(epoch: Long): String {
    val nowSec = System.currentTimeMillis() / 1000
    val diff = nowSec - epoch
    return when {
        diff < 60 -> "just now"
        diff < 3600 -> "${diff / 60}m ago"
        diff < 86_400 -> "${diff / 3600}h ago"
        diff < 604_800 -> "${diff / 86_400}d ago"
        else -> SimpleDateFormat("MMM d", Locale.US).format(Date(epoch * 1000))
    }
}
