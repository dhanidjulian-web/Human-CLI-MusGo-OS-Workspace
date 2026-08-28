package com.agon.app.ui.screens.deployment

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.agon.app.data.DeployEnvironment
import com.agon.app.data.Deployment
import com.agon.app.ui.components.SovereignCard
import com.agon.app.ui.components.StatusBadge
import com.agon.app.ui.components.toColor
import com.agon.app.ui.components.toLabel
import com.agon.app.viewmodel.MusGoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DeploymentScreen(nav: NavHostController, vm: MusGoViewModel) {
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
                    Text("Deployment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Promotes verified artifacts through environments with health gates", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Row {
                listOf(
                    DeployEnvironment.STAGING to Icons.Outlined.Science,
                    DeployEnvironment.CANARY to Icons.Outlined.Visibility,
                    DeployEnvironment.PRODUCTION to Icons.Outlined.Public,
                    DeployEnvironment.EDGE to Icons.Outlined.Cloud,
                ).forEach { (env, icon) ->
                    val count = state.deployments.count { it.environment == env }
                    EnvSummary(env, icon, count, Modifier.weight(1f))
                    Spacer(Modifier.width(6.dp))
                }
            }
        }
        items(state.deployments, key = { it.id }) { d ->
            DeploymentCard(d) { vm.approveDeployment(d.id) }
        }
    }
}

@Composable
private fun EnvSummary(env: DeployEnvironment, icon: ImageVector, count: Int, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(env.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
        Text("$count deploys", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DeploymentCard(d: Deployment, onApprove: () -> Unit) {
    val color = d.state.toColor()
    SovereignCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.RocketLaunch, null, tint = color, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(d.environment.name.lowercase().replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(6.dp))
                        Text("v${d.version}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    d.url?.let { Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.tertiary) }
                }
                StatusBadge(d.state.toLabel(), color)
            }
            Spacer(Modifier.height(8.dp))
            Text(d.notes, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row {
                InfoBox("Started", SimpleDateFormat("MMM d HH:mm", Locale.US).format(Date(d.startedAtEpoch * 1000)), Modifier.weight(1f))
                Spacer(Modifier.width(6.dp))
                InfoBox("Health", if (d.healthCheckPass) "Passing" else "Failing", Modifier.weight(1f),
                    color = if (d.healthCheckPass) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error)
                Spacer(Modifier.width(6.dp))
                InfoBox("Rollback", if (d.rollbackAvailable) "Available" else "Locked", Modifier.weight(1f))
            }
            if (d.state.toLabel() == "Queued") {
                Spacer(Modifier.height(10.dp))
                Row {
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(onClick = onApprove)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.PlayArrow, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Approve & promote", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier, color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface) {
    Column(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(value, fontWeight = FontWeight.Medium, fontSize = 11.sp, color = color)
        Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
