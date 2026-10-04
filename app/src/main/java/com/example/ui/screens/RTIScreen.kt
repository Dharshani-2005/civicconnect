package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Subject
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.RTIRequest
import com.example.ui.CivicViewModel
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessEmerald
import java.io.File

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.OutlinedButton
import android.widget.Toast
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.service.PdfGeneratorService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RTIScreen(
    viewModel: CivicViewModel,
    rtiRequests: List<RTIRequest>
) {
    val context = LocalContext.current

    var subject by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("Public Works Department (PWD)") }
    var description by remember { mutableStateOf("") }

    var deptDropdownExpanded by remember { mutableStateOf(false) }
    var selectedRtiForDialog by remember { mutableStateOf<RTIRequest?>(null) }
    
    val departments = listOf(
        "Public Works Department (PWD)",
        "Water Supply & Sewerage Board",
        "Electricity Infrastructure Board",
        "Health & Sanitation Department",
        "Town Planning & Revenue Dept"
    )

    fun ensureAndGetPdf(rti: RTIRequest): File {
        val existingPath = rti.pdfPath
        if (!existingPath.isNullOrEmpty()) {
            val file = File(existingPath)
            if (file.exists()) return file
        }
        // Generate on the fly
        val newPdf = PdfGeneratorService.generateRTIPdf(context, rti)
        viewModel.submitRTIPathUpdate(rti.id, newPdf.absolutePath)
        return newPdf
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "RTI Application Generator",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Draft legal RTI requests under Right to Information Act 2005 with statutory 30-day deadline.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "FORM 'A' - SECTION 6(1) DRAFT", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Department Dropdown
                    ExposedDropdownMenuBox(
                        expanded = deptDropdownExpanded,
                        onExpandedChange = { deptDropdownExpanded = !deptDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = department,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Target Public Authority / Dept") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        )
                        ExposedDropdownMenu(
                            expanded = deptDropdownExpanded,
                            onDismissRequest = { deptDropdownExpanded = false }
                        ) {
                            departments.forEach { dept ->
                                DropdownMenuItem(
                                    text = { Text(dept) },
                                    onClick = {
                                        department = dept
                                        deptDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Information Subject Line") },
                        placeholder = { Text("e.g. Status of road repair budget in T Nagar") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Subject, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Specific Particulars of Information Sought") },
                        placeholder = { Text("State exact questions, contractor names, sanction dates, or fund allocation queries...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (subject.isBlank() || description.isBlank()) return@Button
                            viewModel.submitRTI(subject, department, description)
                            Toast.makeText(context, "RTI Generated & Saved to Downloads folder!", Toast.LENGTH_LONG).show()
                            subject = ""
                            description = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GENERATE & DOWNLOAD RTI PDF", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Past RTI History Feed
            Text(
                text = "YOUR FILED RTI APPLICATIONS (${rtiRequests.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (rtiRequests.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No RTI applications generated yet. Draft your first RTI above.",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                rtiRequests.forEach { rti ->
                    RtiItemCard(
                        rti = rti,
                        onDownloadPdf = {
                            val file = ensureAndGetPdf(rti)
                            Toast.makeText(context, "Downloaded to ${file.name} in Downloads folder!", Toast.LENGTH_LONG).show()
                        },
                        onOpenPdf = {
                            val file = ensureAndGetPdf(rti)
                            try {
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    file
                                )
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "Open RTI Application PDF"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Opening PDF preview in app...", Toast.LENGTH_SHORT).show()
                                selectedRtiForDialog = rti
                            }
                        },
                        onViewDraft = {
                            selectedRtiForDialog = rti
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Full RTI View Dialog
        selectedRtiForDialog?.let { rti ->
            AlertDialog(
                onDismissRequest = { selectedRtiForDialog = null },
                title = { Text("RTI Application #${rti.id} Details", fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("FORM 'A' - RIGHT TO INFORMATION ACT 2005", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Public Authority: ${rti.department}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text("Applicant: ${rti.citizenName}", fontSize = 12.sp)
                        Text("Submitted At: ${rti.submittedAt}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("30-Day Response SLA: ${rti.deadline}", fontSize = 11.sp, color = SuccessEmerald, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Subject: ${rti.subject}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(rti.description, modifier = Modifier.padding(10.dp), fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val file = ensureAndGetPdf(rti)
                            Toast.makeText(context, "PDF saved to Downloads as ${file.name}", Toast.LENGTH_LONG).show()
                            selectedRtiForDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download PDF")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedRtiForDialog = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()

@Composable
fun RtiItemCard(
    rti: RTIRequest,
    onDownloadPdf: () -> Unit,
    onOpenPdf: () -> Unit,
    onViewDraft: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("RTI REF: #${rti.id}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryBlue)
                Box(
                    modifier = Modifier
                        .background(SuccessEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("30-Day Deadline: ${rti.deadline}", fontSize = 10.sp, color = SuccessEmerald, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(rti.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Department: ${rti.department}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDownloadPdf,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DOWNLOAD PDF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onOpenPdf,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("OPEN / VIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onViewDraft,
                    modifier = Modifier
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
