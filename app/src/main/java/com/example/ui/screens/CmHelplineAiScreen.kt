package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import com.example.ui.CivicViewModel
import com.example.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CmHelplineAiScreen(
    viewModel: CivicViewModel,
    onNavigateToSubmit: () -> Unit
) {
    val context = LocalContext.current
    val aiResponse by viewModel.aiResponse.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: 1100 Helpline, 1: AI Petition Refiner, 2: AI Assistant Chat
    var rawDraft by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Roads & Potholes") }
    var userQuery by remember { mutableStateOf("") }

    val categories = listOf("Roads & Potholes", "Water Supply", "Sanitation", "Street Lighting", "CM Public Relief Fund", "Kalaignar Scheme")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Banner Header: Mudhalvarin Mugavari TN CM Cell
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "CM Cell Emblem",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mudhalvarin Mugavari (TN CM Cell)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Helpline 1100 • AI Communication Assistant • Direct Redressal Portal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ElevatedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1100"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                android.widget.Toast.makeText(context, "CM Helpline Toll-Free: 1100", android.widget.Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("dial_1100_button")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Dial 1100 Toll Free", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onNavigateToSubmit,
                        modifier = Modifier.testTag("file_cm_petition_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("File Petition")
                    }
                }
            }
        }

        // Segmented Tab Selector
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            SegmentedButton(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
            ) {
                Text("CM Helpline", fontSize = 12.sp)
            }
            SegmentedButton(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
            ) {
                Text("AI Refiner", fontSize = 12.sp)
            }
            SegmentedButton(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
            ) {
                Text("AI Chat", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedTab == 0) {
                // CM Helpline Info & Services
                item {
                    Text(
                        text = "CM Special Cell Services & Guidelines",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    HelplineInfoCard(
                        title = "1100 Integrated Grievance Redressal System (IGRS)",
                        description = "A unified platform connecting all departments of Tamil Nadu State Government. Complaints are assigned a unique tracking number (TN-CM-YYYY-XXXXX) and monitored directly by the Special Officer.",
                        icon = Icons.Default.SupportAgent
                    )
                }

                item {
                    HelplineInfoCard(
                        title = "Statutory SLA Timeline Matrix",
                        description = "• Water & Sanitation: 48 Hours\n• Streetlights: 24 Hours\n• Road Repairs & Potholes: 7 Days\n• Revenue & Land Records: 14 Days",
                        icon = Icons.Default.Timer
                    )
                }

                item {
                    HelplineInfoCard(
                        title = "Chief Minister's Public Relief Fund (CMPRF)",
                        description = "Financial assistance for medical emergency treatments, natural disaster relief, and specialized healthcare for citizens below poverty line.",
                        icon = Icons.Default.VolunteerActivism
                    )
                }
            } else if (selectedTab == 1) {
                // AI Petition Refiner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Smart AI Petition Refiner (Gemini 3.5 Flash)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter an informal complaint in plain language (English or Tamil). Gemini AI will refine it into a structured, legal petition with proper statutory references.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = rawDraft,
                                onValueChange = { rawDraft = it },
                                label = { Text("Describe complaint in plain language...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .testTag("ai_raw_draft_input"),
                                maxLines = 4
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (rawDraft.isNotBlank()) {
                                        viewModel.refinePetitionWithAi(rawDraft, selectedCategory, "Ward 12")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("generate_refined_petition_button"),
                                enabled = !isAiLoading && rawDraft.isNotBlank()
                            ) {
                                if (isAiLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Refining with Gemini AI...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate Formal CM Petition")
                                }
                            }
                        }
                    }
                }

                if (aiResponse.isNotBlank()) {
                    item {
                        var editableAiText by remember(aiResponse) { mutableStateOf(aiResponse) }
                        val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                        val context = LocalContext.current

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Refined Formal Petition Result (Editable)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Row {
                                        IconButton(onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(editableAiText))
                                            android.widget.Toast.makeText(context, "Copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy AI Text", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = editableAiText,
                                    onValueChange = { editableAiText = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 120.dp, max = 240.dp),
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(editableAiText))
                                            viewModel.setPrefilledGrievance(
                                                title = "CM Helpline Petition: ${rawDraft.take(36).ifBlank { selectedCategory }}",
                                                desc = editableAiText,
                                                category = selectedCategory,
                                                photoPath = null
                                            )
                                            onNavigateToSubmit()
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Edit & File", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.performSubmission(
                                                title = "CM Helpline Petition: ${rawDraft.take(36).ifBlank { selectedCategory }}",
                                                description = editableAiText,
                                                category = selectedCategory,
                                                lat = 13.0418,
                                                lng = 80.2341,
                                                anonymous = false,
                                                wardCode = viewModel.session.getWardCode().ifBlank { "CHN-W-001" },
                                                photoPath = null
                                            )
                                            android.widget.Toast.makeText(context, "Petition submitted to Petitions page!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Submit Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // AI Chatbot Assistant
                item {
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                    val context = LocalContext.current

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mughavari AI Helpline Assistant",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ask questions regarding Tamil Nadu municipal services, petition escalation, RTI filing, or government welfare schemes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("QUICK TOPICS (1-TAP):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        userQuery = "What is the statutory SLA timeline for 1100 complaints?"
                                        viewModel.chatWithAiAssistant(userQuery)
                                    },
                                    label = { Text("SLA Timelines", fontSize = 10.sp) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        userQuery = "How to file RTI request under RTI Act 2005?"
                                        viewModel.chatWithAiAssistant(userQuery)
                                    },
                                    label = { Text("RTI Filing Rules", fontSize = 10.sp) }
                                )
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        userQuery = "How to approach Water Supply officer in Ward?"
                                        viewModel.chatWithAiAssistant(userQuery)
                                    },
                                    label = { Text("Water Officer Contact", fontSize = 10.sp) }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = userQuery,
                                onValueChange = { userQuery = it },
                                label = { Text("Ask Mughavari AI Assistant...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ai_chat_query_input"),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (userQuery.isNotBlank()) {
                                                viewModel.chatWithAiAssistant(userQuery)
                                                userQuery = ""
                                            }
                                        },
                                        enabled = !isAiLoading && userQuery.isNotBlank(),
                                        modifier = Modifier.testTag("send_ai_query_button")
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Query")
                                    }
                                }
                            )
                        }
                    }
                }

                if (isAiLoading) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Mughavari AI is typing response...",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = PrimaryBlue
                                    )
                                    Text(
                                        text = "Status: Sent • Delivered • AI Processing",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                if (aiResponse.isNotBlank() && !isAiLoading) {
                    item {
                        var editableAnswer by remember(aiResponse) { mutableStateOf(aiResponse) }
                        var isPlayingAudio by remember { mutableStateOf(false) }
                        var isPlayingVideo by remember { mutableStateOf(false) }
                        val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                        val context = LocalContext.current

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "AI Assistant Answer (Copyable & Editable)",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                            text = "Status: Sent • Delivered & Read • Clicked",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = com.example.ui.theme.SuccessEmerald
                                        )
                                    }
                                    IconButton(onClick = {
                                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(editableAnswer))
                                        android.widget.Toast.makeText(context, "Answer copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Answer", tint = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = editableAnswer,
                                    onValueChange = { editableAnswer = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 100.dp, max = 220.dp),
                                    textStyle = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Real Text-To-Speech (TTS) Voice Narration Engine
                                val voiceSpeechManager = remember { com.example.util.VoiceSpeechManager(context) }
                                var isSpeaking by remember { mutableStateOf(false) }

                                DisposableEffect(Unit) {
                                    onDispose {
                                        voiceSpeechManager.shutdown()
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (isSpeaking) {
                                                voiceSpeechManager.stop()
                                                isSpeaking = false
                                            } else {
                                                voiceSpeechManager.speak(editableAnswer)
                                                isSpeaking = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSpeaking) MaterialTheme.colorScheme.error else PrimaryBlue
                                        )
                                    ) {
                                        Icon(
                                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isSpeaking) "Stop Narration" else "Read Response Aloud (TTS)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(editableAnswer))
                                            android.widget.Toast.makeText(context, "Copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(0.8f)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy Text", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun HelplineInfoCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
