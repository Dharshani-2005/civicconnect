package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Grievance
import com.example.ui.CivicViewModel
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessEmerald

@Composable
fun GrievanceListScreen(
    viewModel: CivicViewModel,
    grievances: List<Grievance>,
    onGrievanceClick: (Grievance) -> Unit
) {
    val liveGrievances by viewModel.allGrievances.collectAsState()
    val effectiveGrievances = if (liveGrievances.isNotEmpty()) liveGrievances else grievances
    val lastSubmitted by viewModel.lastSubmittedGrievance.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    // Default to Newest First (false) so newly filed complaints appear immediately at the top
    var sortByPriority by remember { mutableStateOf(false) }

    val userWard = viewModel.session.getWardCode()

    val filteredGrievances = effectiveGrievances.filter { g ->
        val matchesSearch = g.title.contains(searchQuery, ignoreCase = true) ||
                g.trackingId.contains(searchQuery, ignoreCase = true) ||
                g.description.contains(searchQuery, ignoreCase = true) ||
                g.wardCode.contains(searchQuery, ignoreCase = true)

        val matchesCategory = when (selectedCategory) {
            "ALL" -> true
            "MY WARD" -> g.wardCode.equals(userWard, ignoreCase = true)
            "MY PETITIONS" -> g.citizenId == viewModel.session.getUserId()
            else -> g.category.equals(selectedCategory, ignoreCase = true) ||
                    g.category.contains(selectedCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }.let { list ->
        if (sortByPriority) {
            list.sortedWith(compareByDescending<Grievance> { it.priorityScore }.thenByDescending { it.id })
        } else {
            list.sortedByDescending { it.id }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Civic Petitions & Grievances",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Live database feed • Newly filed petitions appear first",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(PrimaryBlue.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Total: ${effectiveGrievances.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Highlight Banner for Recently Submitted Petition
        if (lastSubmitted != null) {
            val latest = effectiveGrievances.find { it.id == lastSubmitted!!.id } ?: lastSubmitted!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onGrievanceClick(latest) },
                colors = CardDefaults.cardColors(containerColor = SuccessEmerald.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Newly Filed",
                        tint = SuccessEmerald,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NEWLY FILED PETITION • ${latest.trackingId}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessEmerald
                        )
                        Text(
                            text = latest.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = "View →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by title, ward, or tracking ID (e.g. CC-2024)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Category Chips
        val categories = listOf("ALL", "MY PETITIONS", "MY WARD", "ROAD", "WATER", "SANITATION", "ELECTRICITY", "OTHER")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredGrievances.size} Petitions Showing",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FilterChip(
                selected = sortByPriority,
                onClick = { sortByPriority = !sortByPriority },
                label = { Text(if (sortByPriority) "Sorted: Priority Score" else "Sorted: Newest First", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = "Sort", modifier = Modifier.height(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredGrievances.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No petitions found for your filter.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGrievances, key = { it.id }) { grievance ->
                    GrievanceCard(
                        grievance = grievance,
                        onClick = { onGrievanceClick(grievance) },
                        onUpvote = { viewModel.upvoteGrievance(grievance.id) }
                    )
                }
            }
        }
    }
}
