package com.agon.app.ui.screens.library

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.Blueprint
import com.agon.app.data.Skill
import com.agon.app.data.SkillSource
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.TagChip
import com.agon.app.viewmodel.MusGoViewModel

private enum class LibTab { BLUEPRINTS, SKILLS }

@Composable
fun LibraryScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(LibTab.BLUEPRINTS) }
    var query by remember { mutableStateOf("") }
    var showIngestBp by remember { mutableStateOf(false) }
    var showIngestSkill by remember { mutableStateOf(false) }

    val blueprints = state.blueprints.filter {
        query.isBlank() || it.title.contains(query, ignoreCase = true) || it.tags.any { t -> t.contains(query, ignoreCase = true) }
    }
    val skills = state.skills.filter {
        query.isBlank() || it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
    }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Library", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Index-first retrieval — agents only load what they query.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { if (tab == LibTab.BLUEPRINTS) showIngestBp = true else showIngestSkill = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Link, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ingest", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    }
                }
            }
        }

        // Search bar
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("Search the index…") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )

        Spacer(Modifier.height(10.dp))

        // Tabs
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            TabButton("Blueprints · ${state.blueprints.size}", tab == LibTab.BLUEPRINTS) { tab = LibTab.BLUEPRINTS }
            Spacer(Modifier.width(8.dp))
            TabButton("Skills · ${state.skills.size}", tab == LibTab.SKILLS) { tab = LibTab.SKILLS }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            when (tab) {
                LibTab.BLUEPRINTS -> {
                    item { LibrarySummary(state.blueprints.size, state.skills.size) }
                    items(blueprints, key = { it.id }) { b -> BlueprintCard(b) }
                }
                LibTab.SKILLS -> {
                    item { LibrarySummary(state.blueprints.size, state.skills.size) }
                    items(skills, key = { it.id }) { s -> SkillCard(s, onToggle = { vm.toggleSkill(s.id) }) }
                }
            }
        }
    }

    if (showIngestBp) {
        IngestDialog(
            title = "Ingest blueprint",
            placeholder = "https://github.com/owner/blueprint-repo",
            onConfirm = { title, url ->
                vm.ingestBlueprintFromUrl(title, url)
                showIngestBp = false
            },
            onDismiss = { showIngestBp = false },
        )
    }
    if (showIngestSkill) {
        IngestDialog(
            title = "Ingest skill",
            placeholder = "https://github.com/owner/skill-repo · or paste skill.md URL",
            onConfirm = { name, url ->
                vm.ingestSkillFromUrl(name, url)
                showIngestSkill = false
            },
            onDismiss = { showIngestSkill = false },
        )
    }
}

@Composable
private fun IngestDialog(title: String, placeholder: String, onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text("Metadata (title, tags, chunks, stars) is generated from the source automatically.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, placeholder = { Text("Display name") }, singleLine = true)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(value = url, onValueChange = { url = it }, placeholder = { Text(placeholder) }, singleLine = true)
            }
        },
        confirmButton = { TextButton(onClick = { if (name.isNotBlank() && url.isNotBlank()) onConfirm(name, url) }) { Text("Ingest") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun TabButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LibrarySummary(blueprintCount: Int, skillCount: Int) {
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Row {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.LibraryBooks, null, tint = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Index-first retrieval", fontWeight = FontWeight.SemiBold)
                Text(
                    "$blueprintCount blueprints · $skillCount skills · agent context only loads the matched chunks.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun BlueprintCard(b: Blueprint) {
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.MenuBook, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp)) }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(b.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text("${b.framework} · ${b.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (b.trusted) {
                    Box(
                        Modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)).padding(horizontal = 6.dp, vertical = 3.dp),
                    ) { Text("Trusted", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary) }
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(b.summary, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(2.dp))
                    Text("${b.stars}", fontSize = 11.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text("· ${b.uses} uses", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(10.dp))
                Text("· ${b.indexedChunks} indexed chunks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(6.dp))
            Row {
                b.tags.take(4).forEach { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
            }
        }
    }
}

@Composable
private fun SkillCard(s: Skill, onToggle: () -> Unit) {
    val color = if (s.enabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Filled.AutoAwesome, null, tint = color, modifier = Modifier.size(18.dp)) }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(s.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.width(6.dp))
                        Text("v${s.version}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("${s.category} · ${s.source.toLabel()}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onToggle)
                        .background(if (s.enabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(if (s.enabled) "Enabled" else "Disabled", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.background)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(s.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row {
                if (s.trusted) {
                    Box(
                        Modifier.padding(end = 6.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f)).padding(horizontal = 6.dp, vertical = 3.dp),
                    ) { Text("Trusted", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary) }
                }
                Text("${s.sizeKb} KB · ${s.usageCount} invocations", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(6.dp))
            Row {
                s.tags.take(4).forEach { TagChip(it, modifier = Modifier.padding(end = 4.dp)) }
            }
        }
    }
}

@Composable
private fun SkillSource.toLabel(): String = when (this) {
    SkillSource.UPLOAD -> "upload"
    SkillSource.PASTED -> "pasted"
    SkillSource.GITHUB -> "github"
    SkillSource.URL -> "url"
    SkillSource.AGENT_CREATED -> "agent-created"
    SkillSource.ZIP -> "zip"
}
