package com.agon.app.ui.screens.github

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.MergeType
import androidx.compose.material.icons.outlined.Token
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.GitHubPat
import com.agon.app.data.GitHubRepo
import com.agon.app.data.PrState
import com.agon.app.data.PullRequest
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.StatusBadge
import com.agon.app.ui.components.toColor
import com.agon.app.ui.components.toLabel
import com.agon.app.viewmodel.MusGoViewModel

private enum class GithubTab { PAT, REPOS, PRS }

@Composable
fun GithubScreen(nav: NavHostController, vm: MusGoViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(GithubTab.PAT) }
    var showAddPat by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.clip(RoundedCornerShape(10.dp)).clickable { nav.popBackStack() }.padding(8.dp)) {
                Icon(Icons.Filled.ArrowBack, null)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text("GitHub Connector", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Authenticate · clone · commit · push · PR — without GitHub App", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Tabs
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).horizontalScroll(rememberScrollState())) {
            GithubTab.values().forEachIndexed { idx, t ->
                val selected = tab == t
                val label = when (t) {
                    GithubTab.PAT -> "PATs · ${state.pats.size}"
                    GithubTab.REPOS -> "Repos · ${state.repos.size}"
                    GithubTab.PRS -> "Pull requests · ${state.pullRequests.size}"
                }
                Box(
                    Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { tab = t }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
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
                GithubTab.PAT -> patItems(state.pats, vm) { showAddPat = true }
                GithubTab.REPOS -> repoItems(state.repos, state.pullRequests, vm)
                GithubTab.PRS -> prItems(state.pullRequests, vm)
            }
        }
    }

    if (showAddPat) {
        var label by remember { mutableStateOf("") }
        var token by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddPat = false },
            title = { Text("Add GitHub PAT") },
            text = {
                Column {
                    Text("Tokens are stored encrypted · never displayed in full. PAT auth does not require a GitHub App.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(value = label, onValueChange = { label = it }, placeholder = { Text("e.g. Dhani (Personal)") }, singleLine = true)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(value = token, onValueChange = { token = it }, placeholder = { Text("ghp_…") }, singleLine = true)
                }
            },
            confirmButton = { TextButton(onClick = { if (label.isNotBlank() && token.isNotBlank()) { vm.addGitHubPat(label, token); showAddPat = false } }) { Text("Add") } },
            dismissButton = { TextButton(onClick = { showAddPat = false }) { Text("Cancel") } },
        )
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.patItems(pats: List<GitHubPat>, vm: MusGoViewModel, onAdd: () -> Unit) {
    item {
        SovereignCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row {
                    Column(Modifier.weight(1f)) {
                        Text("Personal access tokens", fontWeight = FontWeight.SemiBold)
                        Text("Tokens are stored encrypted · never displayed in full · OAuth/App auth can be added later", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Box(
                        Modifier.clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary)
                            .clickable(onClick = onAdd)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add PAT", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
    items(pats, key = { it.id }) { pat ->
        PatCard(pat) { vm.revokePat(pat.id) }
    }
}

@Composable
private fun PatCard(pat: GitHubPat, onRevoke: () -> Unit) {
    val color = pat.status.toColor()
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Token, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(pat.label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(pat.maskedToken, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row {
                    pat.scopes.forEach {
                        Box(
                            Modifier.padding(end = 4.dp).clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) { Text(it, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace) }
                    }
                }
            }
            StatusBadge(pat.status.toLabel(), color)
            if (pat.status.toLabel() != "Revoked") {
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.18f))
                        .clickable(onClick = onRevoke)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Block, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Revoke", fontSize = 11.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.repoItems(repos: List<GitHubRepo>, prs: List<PullRequest>, vm: MusGoViewModel) {
    items(repos, key = { it.id }) { r ->
        RepoCard(r, prs.count { it.repoFullName == r.fullName }, vm)
    }
}

@Composable
private fun RepoCard(r: GitHubRepo, openPrs: Int, vm: MusGoViewModel) {
    val project = vm.projectById(r.connectedProjectId)
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                        .background(if (r.visibility == com.agon.app.data.ProjectVisibility.PUBLIC) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.AccountTree, null, modifier = Modifier.size(18.dp), tint = if (r.visibility == com.agon.app.data.ProjectVisibility.PUBLIC) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(r.fullName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("branch · ${r.branch} · default ${r.defaultBranch}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (r.visibility == com.agon.app.data.ProjectVisibility.PUBLIC) {
                    Icon(Icons.Outlined.Visibility, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Code, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(4.dp))
                Text(r.lastCommitSha, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(4.dp))
            Text(r.lastCommitMessage, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Row {
                Tag2("PRs · $openPrs")
                Spacer(Modifier.width(6.dp))
                Tag2("Issues · ${r.openIssues}")
                Spacer(Modifier.width(6.dp))
                Tag2("${r.sizeKb / 1000} MB")
                Spacer(Modifier.weight(1f))
                project?.let { Text("→ ${it.name}", fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Medium) }
            }
        }
    }
}

@Composable
private fun Tag2(text: String) {
    Box(
        Modifier.clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(text, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.prItems(prs: List<PullRequest>, vm: MusGoViewModel) {
    items(prs, key = { it.id }) { pr -> PrCard(pr, vm) }
}

@Composable
private fun PrCard(pr: PullRequest, vm: MusGoViewModel) {
    val color = pr.state.toColor()
    val project = vm.projectById(pr.projectId)
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.MergeType, null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text("#${pr.number} · ${pr.title}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("${pr.sourceBranch} → ${pr.targetBranch}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
                }
                StatusBadge(pr.state.toLabel(), color)
            }
            Spacer(Modifier.height(8.dp))
            Row {
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp))
                        .background(if (pr.checksPassing) MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f) else MaterialTheme.colorScheme.error.copy(alpha = 0.18f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) { Text(if (pr.checksPassing) "checks ✓" else "checks ✗", fontSize = 10.sp, color = if (pr.checksPassing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error) }
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.18f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) { Text("+${pr.additions}", fontSize = 10.sp, color = MaterialTheme.colorScheme.secondary, fontFamily = FontFamily.Monospace) }
                Spacer(Modifier.width(4.dp))
                Box(
                    Modifier.clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.18f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) { Text("-${pr.deletions}", fontSize = 10.sp, color = MaterialTheme.colorScheme.error, fontFamily = FontFamily.Monospace) }
                Spacer(Modifier.weight(1f))
                Text(pr.author, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            project?.let {
                Spacer(Modifier.height(6.dp))
                Text("→ ${it.name}", fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Medium)
            }
        }
    }
}
