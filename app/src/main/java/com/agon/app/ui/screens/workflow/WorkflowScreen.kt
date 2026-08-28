package com.agon.app.ui.screens.workflow

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PlayCircle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.StepKind
import com.agon.app.data.StepState
import com.agon.app.data.Workflow
import com.agon.app.data.WorkflowStep
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
fun WorkflowScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var showCreate by remember { mutableStateOf(false) }
    var deleteFor by remember { mutableStateOf<String?>(null) }
    var addStepFor by remember { mutableStateOf<String?>(null) }

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
                Column(Modifier.weight(1f)) {
                    Text("Workflow Engine", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Sequential · parallel · dependencies · retries · approvals · rollback", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Text("New workflow", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
        items(state.workflows, key = { it.id }) { wf ->
            WorkflowCard(
                wf,
                onRun = { vm.runWorkflow(wf.id) },
                onDelete = { deleteFor = wf.id },
                onAdvance = { vm.advanceWorkflow(wf.id) },
                onAddStep = { addStepFor = wf.id },
                onRemoveStep = { stepId -> vm.removeWorkflowStep(wf.id, stepId) },
            )
        }
    }

    if (showCreate) {
        CreateWorkflowDialog(
            onConfirm = { name, projectId, requiresHuman ->
                vm.createWorkflow(name, projectId, requiresHuman)
                showCreate = false
            },
            onDismiss = { showCreate = false },
            projects = state.projects.map { it.id to it.name },
        )
    }

    deleteFor?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteFor = null },
            title = { Text("Delete workflow?") },
            text = { Text("This action cannot be undone. Active tasks linked to this workflow will be cancelled.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteWorkflow(id)
                    deleteFor = null
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = { TextButton(onClick = { deleteFor = null }) { Text("Cancel") } },
        )
    }

    addStepFor?.let { id ->
        AddStepDialog(
            onConfirm = { name, kind ->
                vm.addWorkflowStep(id, name, kind)
                addStepFor = null
            },
            onDismiss = { addStepFor = null },
        )
    }
}

@Composable
private fun WorkflowCard(
    wf: Workflow,
    onRun: () -> Unit,
    onDelete: () -> Unit,
    onAdvance: () -> Unit,
    onAddStep: () -> Unit,
    onRemoveStep: (String) -> Unit,
) {
    val color = wf.state.toColor()
    var menuOpen by remember { mutableStateOf(false) }
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(wf.state.toLabel(), color, pulse = wf.state.toString() == "RUNNING" || wf.state.toString() == "AWAITING_APPROVAL")
                Spacer(Modifier.width(8.dp))
                Text(wf.id, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                if (wf.requiresHumanApproval) {
                    Box(
                        Modifier.clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) { Text("Requires human approval", fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary) }
                }
                Box {
                    IconButton(onClick = { menuOpen = true }) { Icon(Icons.Filled.Add, null) }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        DropdownMenuItem(text = { Text("Run workflow") }, onClick = { menuOpen = false; onRun() },
                            leadingIcon = { Icon(Icons.Filled.PlayArrow, null) })
                        DropdownMenuItem(text = { Text("Add step") }, onClick = { menuOpen = false; onAddStep() },
                            leadingIcon = { Icon(Icons.Filled.Add, null) })
                        DropdownMenuItem(text = { Text("Advance (manual)") }, onClick = { menuOpen = false; onAdvance() },
                            leadingIcon = { Icon(Icons.Outlined.PlayCircle, null) })
                        DropdownMenuItem(text = { Text("Delete workflow") }, onClick = { menuOpen = false; onDelete() },
                            leadingIcon = { Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error) })
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(wf.name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text("Step ${wf.currentStepIndex + 1} of ${wf.steps.size} · updated ${timeAgo(wf.updatedAtEpoch)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            val pct = if (wf.steps.isEmpty()) 0 else (wf.currentStepIndex + 1) * 100 / wf.steps.size
            ProgressLine(pct, color = color)
            Spacer(Modifier.height(12.dp))
            wf.steps.forEachIndexed { idx, step ->
                StepRow(step, idx == wf.currentStepIndex, isLast = idx == wf.steps.size - 1, onRemove = { onRemoveStep(step.id) })
            }
            if (wf.state.toString() == "DRAFT" || wf.state.toString() == "PAUSED") {
                Spacer(Modifier.height(10.dp))
                Row {
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier.clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(onClick = onRun)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.PlayArrow, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Run", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepRow(step: WorkflowStep, isCurrent: Boolean, isLast: Boolean, onRemove: () -> Unit) {
    val color = step.state.toColor()
    Row(verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(color.copy(alpha = 0.18f))
                    .border(2.dp, color, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    when (step.state) {
                        StepState.DONE -> Icons.Outlined.CheckCircle
                        StepState.RUNNING -> Icons.Outlined.PlayCircle
                        StepState.FAILED -> Icons.Outlined.ErrorOutline
                        StepState.WAITING_APPROVAL -> Icons.Outlined.Lock
                        StepState.BLOCKED -> Icons.Outlined.HourglassEmpty
                        else -> Icons.Outlined.AccessTime
                    },
                    null,
                    tint = color,
                    modifier = Modifier.size(16.dp),
                )
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(color.copy(alpha = 0.4f))
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(step.name, fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                Spacer(Modifier.width(6.dp))
                if (isCurrent) {
                    Box(
                        Modifier.clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) { Text("current", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium) }
                }
                Spacer(Modifier.weight(1f))
                Text(step.kind.toLabel(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(12.dp))
                }
            }
            Row {
                Text(step.state.toLabel(), fontSize = 11.sp, color = color, fontWeight = FontWeight.Medium)
                if (step.dependsOn.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Text("· deps ${step.dependsOn.joinToString(", ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (step.parallel) {
                    Spacer(Modifier.width(8.dp))
                    Text("· parallel", fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun CreateWorkflowDialog(
    onConfirm: (String, String, Boolean) -> Unit,
    onDismiss: () -> Unit,
    projects: List<Pair<String, String>>,
) {
    var name by remember { mutableStateOf("") }
    var selectedProject by remember { mutableStateOf(projects.firstOrNull()?.first ?: "") }
    var requiresHuman by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New workflow") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("e.g. RBAC v2 — full lifecycle") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                Text("Project", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Column {
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
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = requiresHuman, onCheckedChange = { requiresHuman = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Requires human approval before deploy", fontSize = 12.sp)
                }
            }
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank() && selectedProject.isNotBlank()) onConfirm(name, selectedProject, requiresHuman) }) { Text("Create") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun AddStepDialog(
    onConfirm: (String, StepKind) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf(StepKind.CODE) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add workflow step") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("e.g. Run integration tests") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                Text("Step kind", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row {
                    StepKind.values().forEach { k ->
                        val sel = k == kind
                        Box(
                            Modifier.padding(end = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (sel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh)
                                .clickable { kind = k }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Text(k.toLabel(), fontSize = 10.sp, color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank()) onConfirm(name, kind) }) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun StepKind.toLabel(): String = when (this) {
    StepKind.PLAN -> "Plan"
    StepKind.CODE -> "Code"
    StepKind.TEST -> "Test"
    StepKind.BUILD -> "Build"
    StepKind.COMMIT -> "Commit"
    StepKind.PUSH -> "Push"
    StepKind.PR -> "PR"
    StepKind.REVIEW -> "Review"
    StepKind.APPROVAL -> "Approval"
    StepKind.DEPLOY -> "Deploy"
    StepKind.HEALTHCHECK -> "Health"
}

private fun timeAgo(epoch: Long): String {
    val diff = System.currentTimeMillis() / 1000 - epoch
    return when {
        diff < 60 -> "just now"
        diff < 3600 -> "${diff / 60}m ago"
        diff < 86_400 -> "${diff / 3600}h ago"
        diff < 604_800 -> "${diff / 86_400}d ago"
        else -> SimpleDateFormat("MMM d", Locale.US).format(Date(epoch * 1000))
    }
}
