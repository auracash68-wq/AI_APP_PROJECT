package com.taskflow.app.ui.screens.addedit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStatus
import com.taskflow.app.ui.components.PriorityBadge
import com.taskflow.app.ui.theme.CardShape
import com.taskflow.app.ui.theme.CriticalRed
import com.taskflow.app.ui.theme.PillShape
import com.taskflow.app.ui.theme.PrimaryCobalt
import com.taskflow.app.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    taskId: Long = 0L,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var status by remember { mutableStateOf(TaskStatus.TODO) }
    var category by remember { mutableStateOf("Engineering") }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis() + 86_400_000L) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    // Preset categories
    val categoryPresets = listOf("Engineering", "Design", "Personal", "Marketing", "Urgent")

    // Load existing task data if in edit mode
    LaunchedEffect(taskId) {
        if (taskId != 0L && !isInitialized) {
            val task = viewModel.getTaskById(taskId)
            task?.let {
                title = it.title
                description = it.description
                priority = it.priority
                status = it.status
                category = it.category
                dueDate = it.dueDate
            }
            isInitialized = true
        }
    }

    val isEditMode = taskId != 0L
    val scrollState = rememberScrollState()

    fun performSave() {
        if (title.isBlank()) {
            titleError = "Task title is required"
            return
        }
        titleError = null
        val success = viewModel.saveTask(
            id = taskId,
            title = title,
            description = description,
            priority = priority,
            status = status,
            category = category,
            dueDate = dueDate
        )
        if (success) {
            onNavigateBack()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Task" else "Create Task",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { performSave() },
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Save", color = Color.White, style = MaterialTheme.typography.labelMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Task Title Field
            Column {
                Text(
                    text = "Task Title *",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Design System Architecture Review") },
                    isError = titleError != null,
                    supportingText = {
                        if (titleError != null) {
                            Text(text = titleError!!, color = CriticalRed)
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCobalt,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // Description Field
            Column {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    placeholder = { Text("Add key objectives, notes, acceptance criteria...") },
                    maxLines = 5,
                    shape = MaterialTheme.shapes.small,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCobalt,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // Priority Selection
            Column {
                Text(
                    text = "Priority",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.entries.forEach { p ->
                        val isSelected = priority == p
                        OutlinedButton(
                            onClick = { priority = p },
                            modifier = Modifier.weight(1f),
                            shape = PillShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) PrimaryCobalt.copy(alpha = 0.12f) else Color.Transparent
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PrimaryCobalt else MaterialTheme.colorScheme.outline
                            ),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = p.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PrimaryCobalt else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Status Selection
            Column {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskStatus.entries.forEach { s ->
                        val isSelected = status == s
                        FilterChip(
                            selected = isSelected,
                            onClick = { status = s },
                            label = { Text(s.displayName) },
                            shape = PillShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCobalt,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Category Selection
            Column {
                Text(
                    text = "Category / Tag",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryPresets.forEach { preset ->
                        val isSelected = category.equals(preset, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { category = preset },
                            label = { Text(preset) },
                            shape = PillShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Due Date & Time Presets
            Column {
                val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy · h:mm a", Locale.getDefault())
                Text(
                    text = "Due Date",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    border = CardDefaults.outlinedCardBorder(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PrimaryCobalt,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = dateFormat.format(Date(dueDate)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date Presets row
                val now = System.currentTimeMillis()
                val oneHour = 3_600_000L
                val oneDay = 86_400_000L

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = { dueDate = now + (4 * oneHour) },
                        label = { Text("Today (+4h)") },
                        shape = PillShape
                    )
                    AssistChip(
                        onClick = { dueDate = now + oneDay },
                        label = { Text("Tomorrow") },
                        shape = PillShape
                    )
                    AssistChip(
                        onClick = { dueDate = now + (3 * oneDay) },
                        label = { Text("In 3 Days") },
                        shape = PillShape
                    )
                    AssistChip(
                        onClick = { dueDate = now + (7 * oneDay) },
                        label = { Text("In 1 Week") },
                        shape = PillShape
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Bottom Action Button
            Button(
                onClick = { performSave() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditMode) "Save Changes" else "Create Task",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }
    }
}
