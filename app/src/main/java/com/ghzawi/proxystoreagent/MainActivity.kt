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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // App Header with Icon
            AppHeader()

            Spacer(modifier = Modifier.height(48.dp))

            // Token Input Section
            TokenInputSection(
                token = token,
                onTokenChange = { newToken ->
                    if (newToken.length <= 6) {
                        token = newToken.uppercase()
                    }
                },
                enabled = !isOnline
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status Indicator
            StatusIndicator(
                isOnline = isOnline,
                isConnecting = isConnecting
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Connect/Disconnect Button
            ConnectButton(
                isOnline = isOnline,
                isConnecting = isConnecting,
                enabled = (token.length == 6 && !isConnecting) || isOnline,
                onClick = {
                    if (isOnline) {
                        viewModel.disconnect()
                    } else {
                        viewModel.connect(token)
                    }
                }
            )

            // Connection Info (while connecting)
            if (isConnecting) {
                Spacer(modifier = Modifier.height(24.dp))
                ConnectionInfoCard()
            }

            // Device Credentials (when online)
            if (isOnline && credentials != null) {
                Spacer(modifier = Modifier.height(32.dp))

                CredentialsCard(
                    context = context,
                    credentials = credentials
                )

                // Activity Stats
                Spacer(modifier = Modifier.height(16.dp))
                ProxyActivityCard(
                    activeConnections = viewModel.activeConnections,
                    totalBytes = viewModel.totalBytesTransferred
                )

                // Offboard Button
                Spacer(modifier = Modifier.height(16.dp))
                OffboardButton(
                    onClick = {
                        viewModel.offboard()
                        token = ""
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AppHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Icon with glassmorphism background
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = Primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "ProxyStore Agent",
                tint = Primary,
                modifier = Modifier.size(36.dp)
            )
        }

        // App Title
        Text(
            text = "ProxyStore Agent",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary
        )
    }
}

@Composable
fun TokenInputSection(
    token: String,
    onTokenChange: (String) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Label
        Text(
            text = "ENTER PAIRING TOKEN",
            style = MaterialTheme.typography.labelMedium,
            color = TextTertiary,
            modifier = Modifier.padding(start = 4.dp)
        )

        // Input Field
        OutlinedTextField(
            value = token,
            onValueChange = onTokenChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.1.sp
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Ascii
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = SurfaceDarker,
                focusedContainerColor = SurfaceDarker,
                disabledContainerColor = SurfaceDarker,
                unfocusedBorderColor = BorderDark,
                focusedBorderColor = Primary,
                disabledBorderColor = BorderDark,
                unfocusedTextColor = TextPrimary,
                focusedTextColor = TextPrimary,
                disabledTextColor = TextSecondary,
                cursorColor = Primary
            ),
            shape = RoundedCornerShape(12.dp),
            placeholder = {
                Text(
                    text = "ABC123",
                    color = TextTertiary,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            singleLine = true
        )
    }
}

@Composable
fun StatusIndicator(
    isOnline: Boolean,
    isConnecting: Boolean
) {
    val statusColor = when {
        isOnline -> Success
        isConnecting -> Warning
        else -> Error
    }
    val statusText = when {
        isOnline -> "ONLINE"
        isConnecting -> "CONNECTING..."
        else -> "OFFLINE"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Status indicator dot or spinner
        if (isConnecting) {
            CircularProgressIndicator(
                modifier = Modifier.size(10.dp),
                strokeWidth = 2.dp,
                color = statusColor
            )
        } else {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = statusColor,
                        shape = CircleShape
                    )
            )
        }

        // Status text
        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.05.sp
            ),
            color = statusColor
        )
    }
}

@Composable
fun ConnectButton(
    isOnline: Boolean,
    isConnecting: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            disabledContainerColor = Color(0xFF334155),
            contentColor = TextPrimary,
            disabledContentColor = TextTertiary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isConnecting) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = TextPrimary
                )
                Text(
                    text = "Connecting",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.Close else Icons.Default.Power,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isOnline) "Disconnect" else "Connect",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun ConnectionInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = Primary
            )
            Text(
                text = "Waiting for server response...\nThis may take a few seconds.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun CredentialsCard(
    context: Context,
    credentials: com.ghzawi.proxystoreagent.service.WelcomePayload
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Device Credentials",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )

            HorizontalDivider(color = BorderDark, thickness = 1.dp)

            CredentialRow(
                context = context,
                label = "Device Name",
                value = credentials.deviceName
            )

            CredentialRow(
                context = context,
                label = "Username",
                value = credentials.username
            )
        }
    }
}

@Composable
fun ProxyActivityCard(
    activeConnections: Int,
    totalBytes: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        border = BorderStroke(1.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Proxy Activity",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )

            HorizontalDivider(color = BorderDark, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Active Connections
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "ACTIVE CONNECTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                    Text(
                        text = activeConnections.toString(),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Primary
                    )
                }

                // Data Transferred
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "DATA TRANSFERRED",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = formatBytes(totalBytes),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Primary,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun OffboardButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Error
        ),
        border = BorderStroke(1.dp, Error),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "Offboard Device",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }

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
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
        else -> "${bytes / (1024 * 1024 * 1024)} GB"
    }
}
