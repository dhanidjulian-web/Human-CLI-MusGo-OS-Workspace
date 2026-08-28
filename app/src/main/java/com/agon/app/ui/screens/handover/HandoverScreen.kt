package com.agon.app.ui.screens.handover

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Code
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
import com.agon.app.data.Handover
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.TagChip
import com.agon.app.viewmodel.MusGoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HandoverScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()

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
                    Text("Agent Handover", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Persistent task state · session termination does not destroy progress", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            SovereignCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row {
                        Metric("Active handovers", "${state.handovers.size}", Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Metric("Resumed", "${state.handovers.count { it.resumedByAgentId != null }}", Modifier.weight(1f), MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(8.dp))
                        Metric("Blocked", "${state.handovers.count { it.blockers.isNotEmpty() }}", Modifier.weight(1f), MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
        items(state.handovers, key = { it.id }) { h -> HandoverCard(h, vm) }
    }
}

@Composable
private fun Metric(label: String, value: String, modifier: Modifier, color: Color = MaterialTheme.colorScheme.onSurface) {
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
private fun HandoverCard(h: Handover, vm: MusGoViewModel) {
    val from = vm.agentById(h.fromAgentId)
    val to = vm.agentById(h.toAgentId)
    val project = vm.projectById(h.projectId)
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.SwapHoriz, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("${from?.name ?: "?"} → ${to?.name ?: "?"}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(project?.name ?: h.projectId, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (h.resumedByAgentId != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CheckCircle, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Resumed", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text("Task", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(h.task, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Text("Current state", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(h.currentState, fontSize = 12.sp)
            Spacer(Modifier.height(10.dp))

            if (h.completedWork.isNotEmpty()) {
                SectionTitle("Completed")
                h.completedWork.forEach { Bullet(it, MaterialTheme.colorScheme.secondary) }
            }
            if (h.remainingWork.isNotEmpty()) {
                SectionTitle("Remaining")
                h.remainingWork.forEach { Bullet(it, MaterialTheme.colorScheme.primary) }
            }
            if (h.blockers.isNotEmpty()) {
                SectionTitle("Blockers")
                h.blockers.forEach { Bullet(it, MaterialTheme.colorScheme.error) }
            }
            if (h.decisions.isNotEmpty()) {
                SectionTitle("Decisions")
                h.decisions.forEach { Bullet(it, MaterialTheme.colorScheme.tertiary) }
            }

            Spacer(Modifier.height(8.dp))
            Text("Next action", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(8.dp),
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(h.nextAction, fontSize = 12.sp)
                }
            }

            if (h.filesInvolved.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row {
                    Icon(Icons.Outlined.Code, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(h.filesInvolved.joinToString(" · "), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                }
            }
            if (h.skillsActive.isNotEmpty() || h.blueprintsActive.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Row {
                    h.skillsActive.forEach { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
                    h.blueprintsActive.forEach { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Created ${SimpleDateFormat("MMM d HH:mm", Locale.US).format(Date(h.createdAtEpoch * 1000))}",
                fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(Modifier.height(8.dp))
    Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun Bullet(text: String, color: Color) {
    Row(Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
        Box(
            Modifier.size(6.dp).clip(RoundedCornerShape(50)).background(color).padding(top = 6.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(text, fontSize = 12.sp)
    }
}
