package com.ghzawi.proxystoreagent

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghzawi.proxystoreagent.ui.ProxyViewModel
import com.ghzawi.proxystoreagent.ui.components.*
import com.ghzawi.proxystoreagent.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProxyStoreAgentTheme {
                val viewModel: ProxyViewModel = viewModel()
                PermissionRequest()
                ProxyAgentScreen(viewModel)
            }
        }
    }
}

@Composable
fun PermissionRequest() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun ProxyAgentScreen(viewModel: ProxyViewModel) {
    var token by remember { mutableStateOf(viewModel.pairingToken ?: "") }
    val status = viewModel.connectionStatus
    val isConnecting = viewModel.isConnecting
    val credentials = viewModel.deviceCredentials
    val context = LocalContext.current
    val isOnline = status == "ONLINE"

    // Show toast when status changes
    LaunchedEffect(status) {
        if (status == "ONLINE") {
            Toast.makeText(context, "Connected successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    // Show toast when credentials received
    LaunchedEffect(credentials) {
        if (credentials != null) {
            Toast.makeText(context, "Device registered: ${credentials.deviceName}", Toast.LENGTH_LONG).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundVoid  // Deep black background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Brutal Screen Header with ASCII decoration
            BrutalScreenHeader(
                title = "PROXYSTORE_AGENT",
                trailingContent = if (isOnline) {
                    { BrutalStatusIndicator(status = "ONLINE", color = TerminalGreen) }
                } else null
            )

            Column(
                modifier = Modifier.padding(ProxySpacing.lg),
                verticalArrangement = Arrangement.spacedBy(ProxySpacing.lg)
            ) {
                // ASCII Logo Box
                AsciiLogoBox()

                Spacer(modifier = Modifier.height(ProxySpacing.md))

                if (!isOnline) {
                    // Token Input Section (when not connected)
                    TokenInputSection(
                        token = token,
                        onTokenChange = { newToken ->
                            if (newToken.length <= 6) {
                                token = newToken.uppercase()
                            }
                        },
                        enabled = !isOnline
                    )

                    Spacer(modifier = Modifier.height(ProxySpacing.sm))

                    // Helper comment
                    BrutalComment("Get pairing token from dashboard")

                    Spacer(modifier = Modifier.height(ProxySpacing.lg))

                    // Connect Button
                    BrutalButton(
                        text = if (isConnecting) "CONNECTING..." else "CONNECT",
                        onClick = {
                            if (!isConnecting && token.length == 6) {
                                viewModel.connect(token)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = token.length == 6 && !isConnecting,
                        borderColor = TerminalAmber,
                        icon = if (isConnecting) null else Icons.Default.Power
                    )

                    if (isConnecting) {
                        Spacer(modifier = Modifier.height(ProxySpacing.md))
                        ConnectionInfoCard()
                    }
                } else {
                    // Dashboard when connected
                    credentials?.let { creds ->
                        // Device Credentials Card
                        CredentialsCard(
                            context = context,
                            credentials = creds
                        )

                        Spacer(modifier = Modifier.height(ProxySpacing.md))

                        // Activity Stats Card
                        ProxyActivityCard(
                            activeConnections = viewModel.activeConnections,
                            totalBytes = viewModel.totalBytesTransferred
                        )

                        Spacer(modifier = Modifier.height(ProxySpacing.lg))

                        // Disconnect Button
                        BrutalButton(
                            text = "DISCONNECT",
                            onClick = { viewModel.disconnect() },
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = TerminalRed,
                            icon = Icons.Default.Close
                        )

                        Spacer(modifier = Modifier.height(ProxySpacing.sm))

                        // Offboard Button
                        BrutalOutlinedButton(
                            text = "OFFBOARD_DEVICE",
                            onClick = {
                                viewModel.offboard()
                                token = ""
                            },
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = TerminalRed
                        )
                    }
                }
            }
        }
    }
}

/**
 * ASCII Logo Box - Terminal-style branded header
 */
@Composable
fun AsciiLogoBox() {
    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = TerminalAmber
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ProxySpacing.sm)
        ) {
            // ASCII logo/icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(TerminalAmber.copy(alpha = 0.2f), RectangleShape)
                    .border(2.dp, TerminalAmber, RectangleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "ProxyStore",
                    tint = TerminalAmber,
                    modifier = Modifier.size(28.dp)
                )
            }

            Text(
                text = "█ PROXYSTORE █",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                ),
                color = TerminalAmber
            )

            BrutalComment("Android Proxy Agent")
        }
    }
}

/**
 * Token Input Section - Brutal styled input for pairing token
 */
@Composable
fun TokenInputSection(
    token: String,
    onTokenChange: (String) -> Unit,
    enabled: Boolean
) {
    BrutalCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "PAIRING_TOKEN",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Black
            ),
            color = TextTertiary
        )

        Spacer(modifier = Modifier.height(ProxySpacing.sm))

        BrutalTextField(
            value = token,
            onValueChange = { newValue ->
                onTokenChange(newValue.filter { it.isLetterOrDigit() })
            },
            placeholder = "ABC123",
            enabled = enabled,
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontSize = 24.sp
            )
        )
    }
}

/**
 * Connection Info Card - Shown while connecting
 */
@Composable
fun ConnectionInfoCard() {
    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = TerminalAmber
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ProxySpacing.md)
        ) {
            // Terminal-style loading indicator (no circular spinner)
            Text(
                text = "▶",
                style = MaterialTheme.typography.titleLarge,
                color = TerminalAmber
            )

            Column {
                Text(
                    text = "CONNECTING_TO_SERVER",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )
                BrutalComment("Waiting for server response...")
            }
        }
    }
}

/**
 * Credentials Card - Shows device credentials when online
 */
@Composable
fun CredentialsCard(
    context: Context,
    credentials: com.ghzawi.proxystoreagent.service.WelcomePayload
) {
    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = TerminalGreen
    ) {
        Text(
            text = "┌─ DEVICE_CREDENTIALS ─┐",
            style = MaterialTheme.typography.headlineSmall,
            color = TerminalGreen
        )

        Spacer(modifier = Modifier.height(ProxySpacing.md))

        BrutalDivider(color = BorderBrutal)

        Spacer(modifier = Modifier.height(ProxySpacing.md))

        // Device Name
        CredentialRow(
            context = context,
            label = "DEVICE_NAME",
            value = credentials.deviceName
        )

        Spacer(modifier = Modifier.height(ProxySpacing.md))

        // Username
        CredentialRow(
            context = context,
            label = "USERNAME",
            value = credentials.username
        )

        Spacer(modifier = Modifier.height(ProxySpacing.sm))

        BrutalComment("Credentials copied to clipboard on tap")
    }
}

/**
 * Proxy Activity Card - Shows connection stats
 */
@Composable
fun ProxyActivityCard(
    activeConnections: Int,
    totalBytes: Long
) {
    BrutalCard(
        modifier = Modifier.fillMaxWidth(),
        accentColor = TerminalCyan
    ) {
        Text(
            text = "> PROXY_ACTIVITY",
            style = MaterialTheme.typography.headlineSmall,
            color = TerminalCyan
        )

        Spacer(modifier = Modifier.height(ProxySpacing.md))

        BrutalDivider()

        Spacer(modifier = Modifier.height(ProxySpacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Active Connections
            Column(
                verticalArrangement = Arrangement.spacedBy(ProxySpacing.xs)
            ) {
                Text(
                    text = "CONNECTIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
                Text(
                    text = activeConnections.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = TerminalCyan
                )
            }

            // Data Transferred
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(ProxySpacing.xs)
            ) {
                Text(
                    text = "DATA_TRANSFERRED",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    textAlign = TextAlign.End
                )
                Text(
                    text = formatBytes(totalBytes),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = TerminalAmber,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(ProxySpacing.sm))

        BrutalComment("Real-time proxy statistics")
    }
}

/**
 * Credential Row - Single credential with copy button
 */
@Composable
fun CredentialRow(
    context: Context,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ProxySpacing.xs)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
        }

        // Copy button
        IconButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
                Toast.makeText(context, "$label copied", Toast.LENGTH_SHORT).show()
            }
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy $label",
                tint = TerminalCyan,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Format bytes to human-readable format
 */
fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
