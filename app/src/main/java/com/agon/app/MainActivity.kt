package com.agon.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.agon.app.ui.screens.agents.AgentsScreen
import com.agon.app.ui.screens.build.BuildScreen
import com.agon.app.ui.screens.chat.ChatScreen
import com.agon.app.ui.screens.chat.ProvidersScreen
import com.agon.app.ui.screens.dashboard.DashboardScreen
import com.agon.app.ui.screens.deployment.DeploymentScreen
import com.agon.app.ui.screens.github.GithubScreen
import com.agon.app.ui.screens.handover.HandoverScreen
import com.agon.app.ui.screens.library.LibraryScreen
import com.agon.app.ui.screens.memory.MemoryScreen
import com.agon.app.ui.screens.projects.ProjectDetailScreen
import com.agon.app.ui.screens.projects.ProjectsScreen
import com.agon.app.ui.screens.sandbox.SandboxScreen
import com.agon.app.ui.screens.security.SecurityScreen
import com.agon.app.ui.screens.settings.SettingsScreen
import com.agon.app.ui.screens.workflow.WorkflowScreen
import com.agon.app.ui.theme.AgonAppTheme
import com.agon.app.viewmodel.MusGoViewModel
import com.agon.app.viewmodel.ToastEvent
import com.agon.app.viewmodel.ToastTone
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AgonAppTheme {
                MainApp()
            }
        }
    }
}

data class TabItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val vm: MusGoViewModel = viewModel()

    val tabs = listOf(
        TabItem("dashboard", "Sovereign", Icons.Outlined.Dashboard),
        TabItem("projects", "Projects", Icons.Outlined.AccountTree),
        TabItem("chat", "Chat", Icons.Outlined.Chat),
        TabItem("library", "Library", Icons.Outlined.LibraryBooks),
        TabItem("settings", "Settings", Icons.Outlined.Settings),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNav(navController, tabs) },
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.fillMaxSize(),
            ) {
                composable("dashboard") { DashboardScreen(navController, vm) }
                composable("projects") { ProjectsScreen(navController, vm) }
                composable("chat") { ChatScreen(navController, vm) }
                composable("library") { LibraryScreen(navController, vm) }
                composable("settings") { SettingsScreen(navController, vm) }

                composable("agents") { AgentsScreen(navController, vm) }
                composable("workflow") { WorkflowScreen(navController, vm) }
                composable("sandbox") { SandboxScreen(navController, vm) }
                composable("build") { BuildScreen(navController, vm) }
                composable("deployment") { DeploymentScreen(navController, vm) }
                composable("memory") { MemoryScreen(navController, vm) }
                composable("handover") { HandoverScreen(navController, vm) }
                composable("security") { SecurityScreen(navController, vm) }
                composable("github") { GithubScreen(navController, vm) }
                composable("providers") { ProvidersScreen(navController, vm) }

                composable("project/{id}") { entry ->
                    val id = entry.arguments?.getString("id")
                    ProjectDetailScreen(navController, vm, id)
                }
            }

            // Toast host — bottom of screen above navigation
            val state by vm.state.collectAsStateWithLifecycle()
            ToastHost(
                toasts = state.toasts,
                onDismiss = { vm.dismissToast(it) },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            )
        }
    }
}

@Composable
private fun ToastHost(toasts: List<ToastEvent>, onDismiss: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        toasts.takeLast(3).forEach { t ->
            LaunchedEffect(t.id) {
                delay(3500)
                onDismiss(t.id)
            }
            val color = when (t.tone) {
                ToastTone.SUCCESS -> MaterialTheme.colorScheme.secondary
                ToastTone.WARNING -> MaterialTheme.colorScheme.tertiary
                ToastTone.ERROR -> MaterialTheme.colorScheme.error
                ToastTone.INFO -> MaterialTheme.colorScheme.primary
            }
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.16f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, null, tint = color, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(t.message, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = color)
                }
            }
        }
    }
}

@Composable
fun BottomNav(navController: NavHostController, tabs: List<TabItem>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        tabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(tab.route) {
                            popUpTo("dashboard") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                ),
            )
        }
    }
}

// (No additional helper needed — Modifier.background is imported from androidx.compose.foundation)
