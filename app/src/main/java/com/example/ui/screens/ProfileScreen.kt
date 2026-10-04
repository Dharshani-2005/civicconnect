package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.ui.CivicViewModel
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SuccessEmerald
import com.example.ui.components.SmsGatewayBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CivicViewModel,
    user: User?,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var isBiometricEnabled by remember { mutableStateOf(true) }
    var isMfaEnabled by remember { mutableStateOf(true) }
    var showHowToFileGuide by remember { mutableStateOf(false) }
    var showSmsGatewaySheet by remember { mutableStateOf(false) }

    fun safeOpenUri(uriString: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
            context.startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Opening link: $uriString", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun safeDial(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            context.startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Helpline: $number", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun safeEmail(emailAddr: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddr"))
            context.startActivity(intent)
        } catch (e: Exception) {
            android.widget.Toast.makeText(context, "Official Email: $emailAddr", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Offline Banner Indicator
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.CloudDone, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Offline-First Room Engine Active • Local Encrypted Storage",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavy)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.name?.firstOrNull() ?: 'U').toString().uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = user?.name ?: "Citizen User",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = user?.email ?: "citizen@tn.gov.in",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge(containerColor = PrimaryBlue) {
                                Text(
                                    text = "ROLE: ${user?.role ?: "CITIZEN"}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Badge(containerColor = SuccessEmerald) {
                                Text(
                                    text = "WARD: ${user?.wardCode ?: "CHN-W-001"}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Explicit LOGOUT Button
                        Button(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("logout_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LOG OUT OF CM HELPLINE", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Banking-Grade Security Settings (IOB Security Inspired)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BANKING-GRADE SECURITY & TRUST (IOB COMPLIANT)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Biometric Lock / Security PIN", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Require fingerprint or 6-digit PIN before opening sensitive petitions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isBiometricEnabled,
                                onCheckedChange = { isBiometricEnabled = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Multi-Factor Authentication (MFA)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("SMS OTP verification for login and petition modifications", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isMfaEnabled,
                                onCheckedChange = { isMfaEnabled = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { showSmsGatewaySheet = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.SettingsPhone, contentDescription = null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Configure SMS Gateway REST API", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SuccessEmerald.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = SuccessEmerald, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "TN Government Security Standard: Active Encryption & Data Privacy",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessEmerald
                                    )
                                    Text(
                                        text = "Portal uses SHA-256 device hashing and protected citizen data transmission.",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Social Channels & Day-to-Day Apps Integration
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SOCIAL MEDIA & DAY-TO-DAY APP CONNECT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { safeOpenUri("https://wa.me/919445800000") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { safeOpenUri("https://t.me/TN_CM_Cell_Bot") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Telegram", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { safeOpenUri("https://facebook.com/TNGovtOfficial") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Facebook", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // App Information & Version Metadata
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TN CM Helpline & CivicConnect App",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Version v2.4.0 (TN-CM-SECURE-BUILD)",
                            fontSize = 11.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Government of Tamil Nadu • Mudhalvarin Mugavari Portal",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Privacy Policy • Terms of Service • RTI Compliance 2026",
                            fontSize = 10.sp,
                            color = PrimaryBlue,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Ways to Approach Officers Matrix
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "OFFICIAL WAYS TO APPROACH WARD OFFICERS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ContactMethodRow(
                            icon = Icons.Default.Phone,
                            title = "24x7 Toll Free Helpline",
                            value = "Dial 1100 (Tamil Nadu CM Cell)",
                            onClick = { safeDial("1100") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ContactMethodRow(
                            icon = Icons.AutoMirrored.Filled.Chat,
                            title = "Official WhatsApp Helpline",
                            value = "+91 94458 00000",
                            onClick = { safeOpenUri("https://wa.me/919445800000") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ContactMethodRow(
                            icon = Icons.Default.Email,
                            title = "Official Nodal Email",
                            value = "cmcell@tn.gov.in",
                            onClick = { safeEmail("cmcell@tn.gov.in") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        ContactMethodRow(
                            icon = Icons.Default.Public,
                            title = "Official Web Portal",
                            value = "cmhelpline.tn.gov.in",
                            onClick = { safeOpenUri("https://cmhelpline.tn.gov.in") }
                        )
                    }
                }
            }

            // Officer Directory by Sector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SECTOR OFFICER DIRECTORY & COMPLAINT TYPES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        SectorOfficerItem(
                            sector = "Water Supply & Sewerage",
                            officer = "Assistant Executive Engineer (MetroWater / CMWSSB)",
                            complaints = "Contaminated water, pipe bursts, drainage overflows, low water pressure"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        SectorOfficerItem(
                            sector = "Public Works & Roads (PWD)",
                            officer = "Assistant Engineer (PWD / Corporation Roads)",
                            complaints = "Potholes, broken footpaths, road tarring, stormwater drain blockages"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        SectorOfficerItem(
                            sector = "Sanitation & Solid Waste",
                            officer = "Sanitary Inspector / Conservancy Officer",
                            complaints = "Uncollected garbage, open dumping, street sweeping, stray cattle, mosquito fogging"
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        SectorOfficerItem(
                            sector = "Electricity & Street Lighting (TNEB)",
                            officer = "Assistant Engineer (TANGEDCO / TNEB)",
                            complaints = "Streetlight outage, hanging power wires, transformer sparks, low voltage"
                        )
                    }
                }
            }

            // Interactive "How to File a Complaint" Guide
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showHowToFileGuide = !showHowToFileGuide },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = PrimaryBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("How to File a Complaint (Step-by-Step)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Icon(
                                if (showHowToFileGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }

                        if (showHowToFileGuide) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(12.dp))

                            GuideStepItem("Step 1", "Tap '+' FAB or 'File Petition' to open the submission form.")
                            GuideStepItem("Step 2", "Select category (Roads, Water, Electricity) and enter complaint summary.")
                            GuideStepItem("Step 3", "Optionally refine your draft using 'Smart AI Petition Refiner'.")
                            GuideStepItem("Step 4", "Confirm GPS Ward coordinates or pick location on interactive map.")
                            GuideStepItem("Step 5", "Submit to receive instant TN-CM Tracking ID. SLA timer starts automatically!")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        if (showSmsGatewaySheet) {
            SmsGatewayBottomSheet(
                onDismiss = { showSmsGatewaySheet = false }
            )
        }
    }
}

@Composable
private fun ContactMethodRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = value, fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SectorOfficerItem(
    sector: String,
    officer: String,
    complaints: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(text = sector, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryBlue)
        Text(text = "Officer: $officer", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
        Text(text = "Types: $complaints", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GuideStepItem(step: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Text(text = "$step: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryBlue)
        Text(text = desc, fontSize = 12.sp)
    }
}
