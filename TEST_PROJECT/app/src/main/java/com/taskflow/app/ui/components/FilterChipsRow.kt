package com.taskflow.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStatus
import com.taskflow.app.ui.theme.ChipShape
import com.taskflow.app.ui.theme.PrimaryCobalt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipsRow(
    selectedStatus: TaskStatus?,
    onStatusSelected: (TaskStatus?) -> Unit,
    selectedPriority: TaskPriority?,
    onPrioritySelected: (TaskPriority?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All" chip
        FilterChip(
            selected = selectedStatus == null && selectedPriority == null,
            onClick = {
                onStatusSelected(null)
                onPrioritySelected(null)
            },
            label = { Text("All Tasks") },
            shape = ChipShape,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PrimaryCobalt,
                selectedLabelColor = Color.White
            )
        )

        // Status chips
        TaskStatus.entries.forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(if (selectedStatus == status) null else status) },
                label = { Text(status.displayName) },
                shape = ChipShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryCobalt,
                    selectedLabelColor = Color.White
                )
            )
        }

        // Urgent/Critical Priority chip
        FilterChip(
            selected = selectedPriority == TaskPriority.CRITICAL,
            onClick = { onPrioritySelected(if (selectedPriority == TaskPriority.CRITICAL) null else TaskPriority.CRITICAL) },
            label = { Text("⚡ Critical") },
            shape = ChipShape,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.error,
                selectedLabelColor = Color.White
            )
        )
    }
}
