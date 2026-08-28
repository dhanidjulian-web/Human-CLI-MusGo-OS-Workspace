package com.agon.app.ui.screens.projects

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
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.Agent
import com.agon.app.data.AgentTask
import com.agon.app.data.BuildArtifact
import com.agon.app.data.Deployment
import com.agon.app.data.Project
import com.agon.app.data.Sandbox
import com.agon.app.data.SandboxLog
import com.agon.app.data.Workflow
import com.agon.app.data.WorkflowStep
import com.agon.app.data.AgentState
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.StatusBadge
import com.agon.app.ui.components.TagChip
import com.agon.app.ui.components.toColor
import com.agon.app.ui.components.toLabel
import com.agon.app.viewmodel.MusGoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class ProjectTab(val label: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Outlined.AccountTree),
    WORKFLOW("Workflow", Icons.Outlined.Hub),
    AGENTS("Agents", Icons.Outlined.Code),
    SANDBOX("Sandbox", Icons.Outlined.Storage),
    BUILD("Build", Icons.Outlined.Build),
    DEPLOY("Deploy", Icons.Outlined.RocketLaunch),
}

@Composable
fun ProjectDetailScreen(nav: NavHostController, vm: MusGoViewModel, projectId: String?) {
    val state by vm.state.collectAsStateWithLifecycle()
    val project = vm.projectById(projectId)
    if (project == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Project not found") }
        return
    }
    var tab by remember { mutableStateOf(ProjectTab.OVERVIEW) }
    val workflow = state.workflows.firstOrNull { it.projectId == project.id }
    val tasks = state.tasks.filter { it.projectId == project.id }
    val agents = state.agents
    val sandbox = state.sandboxes.firstOrNull { it.projectId == project.id }
    val logs = state.sandboxLogs.filter { sandbox != null && it.sandboxId == sandbox.id }
    val artifacts = state.artifacts.filter { it.projectId == project.id }
    val deployments = state.deployments.filter { it.projectId == project.id }
    val color = project.status.toColor()

    Column(Modifier.fillMaxSize()) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.clip(RoundedCornerShape(10.dp)).clickable { nav.popBackStack() }.padding(8.dp),
            ) { Icon(Icons.Filled.ArrowBack, null) }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(project.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    StatusBadge(project.status.toLabel(), color)
                }
                project.repoFullName?.let {
                    Text("$it · ${project.branch}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Tabs
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState()),
        ) {
            ProjectTab.values().forEach { t ->
                val selected = t == tab
                Box(
                    Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { tab = t }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(t.icon, null, modifier = Modifier.size(14.dp), tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Text(t.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (tab) {
                ProjectTab.OVERVIEW -> overviewItems(project)
                ProjectTab.WORKFLOW -> workflowItems(workflow, tasks)
                ProjectTab.AGENTS -> agentItems(agents)
                ProjectTab.SANDBOX -> sandboxItems(sandbox, logs)
                ProjectTab.BUILD -> buildItems(artifacts)
                ProjectTab.DEPLOY -> deployItems(deployments)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.overviewItems(p: Project) {
    item {
        SovereignCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(p.description, fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
                Row {
                    p.tags.forEach { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
                }
                Spacer(Modifier.height(10.dp))
                InfoLine("Slug", p.slug)
                InfoLine("Branch", p.branch)
                InfoLine("Visibility", p.visibility.name.lowercase().replaceFirstChar { it.uppercase() })
                InfoLine("Created", SimpleDateFormat("MMM d, yyyy", Locale.US).format(Date(p.createdAtEpoch * 1000)))
                p.lastBuildAtEpoch?.let { InfoLine("Last build", SimpleDateFormat("MMM d, HH:mm", Locale.US).format(Date(it * 1000))) }
                p.lastDeployAtEpoch?.let { InfoLine("Last deploy", SimpleDateFormat("MMM d, HH:mm", Locale.US).format(Date(it * 1000))) }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.workflowItems(workflow: Workflow?, tasks: List<AgentTask>) {
    if (workflow == null) {
        item { Text("No active workflow.") }
        return
    }
    item {
        SovereignCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(workflow.name, fontWeight = FontWeight.SemiBold)
                Text("${workflow.steps.size} steps · step ${workflow.currentStepIndex + 1}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    items(items = workflow.steps, key = { it.id }) { step ->
        val color = step.state.toColor()
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) { Text("${step.order + 1}", fontWeight = FontWeight.Bold, color = color, fontSize = 13.sp) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(step.name, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                Text("${step.kind.name.lowercase().replaceFirstChar { it.uppercase() }} · ${step.state.toLabel()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(step.state.toLabel(), color)
        }
    }
    item {
        Text("Tasks", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp))
    }
    items(items = tasks, key = { it.id }) { t ->
        SovereignCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(t.title, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    StatusBadge(t.state.toLabel(), t.state.toColor())
                }
                if (t.dependencies.isNotEmpty()) {
                    Text("deps · ${t.dependencies.joinToString(", ")}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.agentItems(agents: List<Agent>) {
    items(items = agents, key = { it.id }) { a ->
        val color = a.state.toColor()
        SovereignCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(a.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    TagChip(a.role.name.lowercase().replaceFirstChar { it.uppercase() })
                    Spacer(Modifier.weight(1f))
                    StatusBadge(a.state.toLabel(), color, pulse = a.state.toLabel() == "Running")
                }
                Text(a.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                Row {
                    Text("success · ${(a.successRate * 100).toInt()}%", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(10.dp))
                    Text("tasks · ${a.tasksCompleted}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(10.dp))
                    Text("avg · ${a.avgLatencyMs}ms", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.sandboxItems(sandbox: Sandbox?, logs: List<SandboxLog>) {
    if (sandbox == null) {
        item { Text("No active sandbox.") }
        return
    }
    item {
        SovereignCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(sandbox.workdirPath, fontWeight = FontWeight.Medium, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    StatusBadge(sandbox.state.toLabel(), sandbox.state.toColor())
                }
                Spacer(Modifier.height(8.dp))
                InfoLine("Cloned SHA", sandbox.clonedSha ?: "—")
                InfoLine("CPU limit", sandbox.cpuLimit)
                InfoLine("Memory", "${sandbox.resourceLimitMb} MB")
                InfoLine("Network", if (sandbox.networkAccess) "allowed" else "blocked")
                InfoLine("Commands", "${sandbox.commandCount}")
                InfoLine("Logs", "${sandbox.logCount}")
            }
        }
    }
    item { Text("Log stream", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp)) }
    items(items = logs, key = { it.id }) { l ->
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(l.stream.toLabel(), fontSize = 10.sp, fontWeight = FontWeight.Medium, color = l.stream.toColor(), modifier = Modifier.width(60.dp))
            Text("${l.command} →", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(120.dp))
            Text(l.line, fontSize = 11.sp, modifier = Modifier.weight(1f))
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.buildItems(artifacts: List<BuildArtifact>) {
    if (artifacts.isEmpty()) {
        item { Text("No artifacts yet.") }
        return
    }
    items(items = artifacts, key = { it.id }) { a ->
        val color = a.status.toColor()
        SovereignCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("v${a.version}", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    TagChip(a.type.name)
                    Spacer(Modifier.weight(1f))
                    StatusBadge(a.status.toLabel(), color)
                }
                Text("commit ${a.commitSha}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${a.sizeMb} MB · ${a.checksum}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.deployItems(deployments: List<Deployment>) {
    if (deployments.isEmpty()) {
        item { Text("No deployments yet.") }
        return
    }
    items(items = deployments, key = { it.id }) { d ->
        val color = d.state.toColor()
        SovereignCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(d.environment.name.lowercase().replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(8.dp))
                    StatusBadge(d.state.toLabel(), color)
                    Spacer(Modifier.weight(1f))
                    Text("v${d.version}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                d.url?.let { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary) }
                Text(d.notes, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
