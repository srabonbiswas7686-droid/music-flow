package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: UserProfile?,
    allUsers: List<UserProfile>,
    favoritesCount: Int,
    playlistsCount: Int,
    onSwitchUser: (String) -> Unit,
    onCreateUser: (username: String, name: String, email: String, isAdmin: Boolean) -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSwitchUserDialog by remember { mutableStateOf(false) }
    var showCreateUserDialog by remember { mutableStateOf(false) }
    var audioQuality by remember { mutableStateOf("Lossless FLAC (320kbps)") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(FlowCyan, FlowViolet))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.displayName?.firstOrNull() ?: 'U').toString().uppercase(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user?.displayName ?: "Music Flow User",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Text(
                        text = user?.email ?: "user@musicflow.app",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = FlowCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FlowCyan)
                        ) {
                            Text(
                                text = "MUSIC FLOW PRO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = FlowCyan,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        if (user?.isAdmin == true) {
                            Surface(
                                color = FlowViolet.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(20.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FlowVioletLight)
                            ) {
                                Text(
                                    text = "ADMINISTRATOR",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FlowVioletLight,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Liked Songs",
                    value = "$favoritesCount",
                    icon = Icons.Default.Favorite,
                    tint = AccentHeart,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Playlists",
                    value = "$playlistsCount",
                    icon = Icons.Default.QueueMusic,
                    tint = FlowCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Listened",
                    value = "${(user?.minutesListened ?: 1420) / 60}h",
                    icon = Icons.Default.Headphones,
                    tint = FlowVioletLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Admin Dashboard Entry
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToAdmin),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FlowViolet.copy(alpha = 0.25f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlowVioletLight.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = FlowCyan,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Admin Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Add/edit songs, artists, albums & manage users",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextTertiary
                    )
                }
            }
        }

        // Settings & Account Section
        item {
            Text(
                text = "Account & Settings",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = FlowCyan,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingItem(
                        icon = Icons.Default.SwitchAccount,
                        title = "Switch Demo Account",
                        subtitle = "Currently: ${user?.displayName ?: "Active User"}",
                        onClick = { showSwitchUserDialog = true }
                    )
                    Divider(color = DarkCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingItem(
                        icon = Icons.Default.PersonAdd,
                        title = "Create New Account",
                        subtitle = "Add a listener or admin profile",
                        onClick = { showCreateUserDialog = true }
                    )
                    Divider(color = DarkCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingItem(
                        icon = Icons.Default.Audiotrack,
                        title = "Streaming Audio Quality",
                        subtitle = audioQuality,
                        onClick = {
                            audioQuality = when (audioQuality) {
                                "Normal (128kbps)" -> "High (256kbps)"
                                "High (256kbps)" -> "Lossless FLAC (320kbps)"
                                else -> "Normal (128kbps)"
                            }
                        }
                    )
                    Divider(color = DarkCardBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingItem(
                        icon = Icons.Default.Info,
                        title = "About MUSIC FLOW",
                        subtitle = "Version 1.0.0 • Royalty-Free Demo Engine",
                        onClick = {}
                    )
                }
            }
        }
    }

    // Switch User Dialog
    if (showSwitchUserDialog) {
        AlertDialog(
            onDismissRequest = { showSwitchUserDialog = false },
            title = { Text("Switch User Account", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    allUsers.forEach { u ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (u.id == user?.id) FlowCyan.copy(alpha = 0.2f) else DarkSurface)
                                .clickable {
                                    onSwitchUser(u.id)
                                    showSwitchUserDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (u.isAdmin) Icons.Default.Security else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (u.isAdmin) FlowVioletLight else FlowCyan
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(u.displayName, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("${u.username} • ${if (u.isAdmin) "Admin" else "User"}", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                            }
                            if (u.id == user?.id) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = FlowCyan)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSwitchUserDialog = false }) {
                    Text("Close", color = FlowCyan)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }

    // Create User Dialog
    if (showCreateUserDialog) {
        var username by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var makeAdmin by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCreateUserDialog = false },
            title = { Text("Create Profile", color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = FlowCyan
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = FlowCyan
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = FlowCyan
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = makeAdmin,
                            onCheckedChange = { makeAdmin = it },
                            colors = CheckboxDefaults.colors(checkedColor = FlowCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Grant Administrator Privileges", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && username.isNotBlank()) {
                            onCreateUser(username.trim(), name.trim(), email.trim(), makeAdmin)
                            showCreateUserDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlowCyan, contentColor = Color.Black)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateUserDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceVariant
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = FlowCyan, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(20.dp))
    }
}
