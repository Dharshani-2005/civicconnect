package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDocScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Project Architecture & Specs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mughavari Civic Connect Platform",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Next-Generation AI & Spatial Civic Infrastructure Platform modeled after Tamil Nadu CM Helpline (1100 Mudhalvarin Mugavari).",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            item {
                DocSectionCard(
                    title = "1. Core Architectural Modules",
                    icon = Icons.Default.Layers
                ) {
                    Text("• Local Database Engine: Android Room DB with KSP code generation and Flow real-time stream subscriptions.", style = MaterialTheme.typography.bodySmall)
                    Text("• SLA Escalation Engine: Automated background queue scanner raising unanswered grievances across 3 escalation tiers (Ward Officer -> Department Head -> Commissioner).", style = MaterialTheme.typography.bodySmall)
                    Text("• Duplicate Detection Engine: Geohash spatial proximity search + Jaccard string similarity analysis preventing duplicate complaints.", style = MaterialTheme.typography.bodySmall)
                    Text("• Gemini AI Communication Assistant: Integration with Gemini 3.5 Flash for formal petition refining, multi-lingual query answering, and translation.", style = MaterialTheme.typography.bodySmall)
                    Text("• Statutory RTI Application Engine: Automatic generation of legal RTI applications with PDF export conforming to RTI Act 2005.", style = MaterialTheme.typography.bodySmall)
                }
            }

            item {
                DocSectionCard(
                    title = "2. Security & Compliance Architecture",
                    icon = Icons.Default.Security
                ) {
                    Text("• Password Security: SHA-256 cryptographic salt and hash algorithm.", style = MaterialTheme.typography.bodySmall)
                    Text("• Role-Based Access Control (RBAC): Strict segregation between Citizen, Field Officer, Department Head, and Commissioner.", style = MaterialTheme.typography.bodySmall)
                    Text("• Security Audit Logging: Persisted audit log tracking all login events, status modifications, anonymous petitions, and escalation triggers.", style = MaterialTheme.typography.bodySmall)
                    Text("• Citizen Privacy Safeguards: Geohash spatial anonymization & optional anonymous petition filing.", style = MaterialTheme.typography.bodySmall)
                }
            }

            item {
                DocSectionCard(
                    title = "3. Statutory SLA Matrix & Priorities",
                    icon = Icons.Default.AccessTime
                ) {
                    Text("• Water Supply & Sanitation: 48 Hours SLA (Base Priority: 80/100)", style = MaterialTheme.typography.bodySmall)
                    Text("• Street Lighting & Electrical Hazards: 24 Hours SLA (Base Priority: 85/100)", style = MaterialTheme.typography.bodySmall)
                    Text("• Potholes & Road Damage: 7 Days SLA (Base Priority: 70/100)", style = MaterialTheme.typography.bodySmall)
                    Text("• Drainage & Municipal Public Works: 14 Days SLA (Base Priority: 60/100)", style = MaterialTheme.typography.bodySmall)
                }
            }

            item {
                DocSectionCard(
                    title = "4. Database Schema Specification",
                    icon = Icons.Default.Storage
                ) {
                    Text(
                        text = """
                        Entities:
                        - users (id, name, email, passwordHash, role, wardCode, phone)
                        - wards (code, name, zone, totalGrievances, resolvedGrievances)
                        - grievances (id, trackingId, title, description, category, latitude, longitude, geohash, status, priorityScore, anonymous, photoPath, submittedAt, slaDeadline, citizenId, wardCode, officerNote)
                        - escalations (id, grievanceId, escalatedTo, reason, escalatedAt)
                        - rti_requests (id, citizenId, citizenName, subject, department, description, submittedAt, deadline, status, pdfPath)
                        - public_projects (id, title, wardCode, department, budgetInLakhs, spentInLakhs, progressPercent, contractor, status, startDate)
                        - audit_logs (id, actionType, userId, details, ipHash, timestamp)
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DocSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
