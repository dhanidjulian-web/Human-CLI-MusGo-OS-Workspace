package com.agon.app.ui.screens.memory

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.AccountTree
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.MemoryEntry
import com.agon.app.data.MemoryScope
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.TagChip
import com.agon.app.viewmodel.MusGoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemoryScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var scope by remember { mutableStateOf(MemoryScope.USER) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.clip(RoundedCornerShape(10.dp)).clickable { nav.popBackStack() }.padding(8.dp)) {
                Icon(Icons.Filled.ArrowBack, null)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Memory Layers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Conversation history is NOT the authoritative project state", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Scope tabs
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState())) {
            MemoryScope.values().forEach { sc ->
                val selected = sc == scope
                val count = state.memoryEntries.count { it.scope == sc }
                Box(
                    Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { scope = sc }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(sc.icon(), null, modifier = Modifier.size(14.dp), tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Text("${sc.name.lowercase().replaceFirstChar { it.uppercase() }} · $count",
                            fontSize = 12.sp, fontWeight = FontWeight.Medium,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
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
            val entries = state.memoryEntries.filter { it.scope == scope }
            if (entries.isEmpty()) {
                item {
                    Text("No entries in this scope.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(entries, key = { it.id }) { e -> MemoryCard(e) }
        }
    }
}

@Composable
private fun MemoryCard(e: MemoryEntry) {
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(28.dp).clip(CircleShape).background(e.scope.color().copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) { Icon(e.scope.icon(), null, tint = e.scope.color(), modifier = Modifier.size(14.dp)) }
                Spacer(Modifier.width(8.dp))
                Text(e.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                if (e.pinned) {
                    Icon(Icons.Filled.PushPin, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(e.body, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row {
                e.tags.take(4).forEach { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
                Spacer(Modifier.weight(1f))
                Text("${e.retentionDays}d retention · ${SimpleDateFormat("MMM d", Locale.US).format(Date(e.createdAtEpoch * 1000))}",
                    fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun MemoryScope.color(): Color = when (this) {
    MemoryScope.USER -> Color(0xFFD4AF37)
    MemoryScope.PROJECT -> Color(0xFF3B82F6)
    MemoryScope.TASK -> Color(0xFF14B886)
    MemoryScope.AGENT -> Color(0xFFA855F7)
    MemoryScope.WORKFLOW -> Color(0xFFF59E0B)
    MemoryScope.HANDOVER -> Color(0xFFEF4444)
}

private fun MemoryScope.icon(): ImageVector = when (this) {
    MemoryScope.USER -> Icons.Outlined.Memory
    MemoryScope.PROJECT -> Icons.Outlined.Hub
    MemoryScope.TASK -> Icons.Outlined.Engineering
    MemoryScope.AGENT -> Icons.Outlined.SmartToy
    MemoryScope.WORKFLOW -> Icons.Outlined.AccountTree
    MemoryScope.HANDOVER -> Icons.Outlined.SwapHoriz
}
