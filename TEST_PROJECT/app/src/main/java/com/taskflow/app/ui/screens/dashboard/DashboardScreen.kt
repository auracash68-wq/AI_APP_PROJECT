package com.taskflow.app.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taskflow.app.model.Task
import com.taskflow.app.ui.components.*
import com.taskflow.app.ui.theme.CardShape
import com.taskflow.app.ui.theme.PillShape
import com.taskflow.app.ui.theme.PrimaryCobalt
import com.taskflow.app.ui.viewmodel.TaskUiState
import com.taskflow.app.ui.viewmodel.TaskViewModel

@Composable
fun DashboardScreen(
    viewModel: TaskViewModel,
    uiState: TaskUiState,
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaskFlowTopBar(
                title = "TaskFlow",
                subtitle = "Welcome back, Alex",
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                showSearchToggle = true
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = PrimaryCobalt,
                contentColor = Color.White,
                shape = CardShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Progress Card
            item {
                ProductivityHeroCard(stats = uiState.stats)
            }

            // Interactive Filter Chips Row
            item {
                FilterChipsRow(
                    selectedStatus = uiState.selectedStatusFilter,
                    onStatusSelected = { viewModel.onStatusFilterSelected(it) },
                    selectedPriority = uiState.selectedPriorityFilter,
                    onPrioritySelected = { viewModel.onPriorityFilterSelected(it) },
                    modifier = Modifier.padding(horizontal = 0.dp)
                )
            }

            // Section Header: "Today's Focus" / "Active Tasks"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Today's Focus",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(PillShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${uiState.filteredTasks.size}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Task List or Empty State
            if (uiState.filteredTasks.isEmpty()) {
                item {
                    EmptyStateView(
                        title = if (uiState.searchQuery.isNotEmpty()) "No matching tasks" else "All tasks completed!",
                        description = if (uiState.searchQuery.isNotEmpty())
                            "Try searching for something else or clear the search filter."
                        else
                            "Great job! You have cleared your current task queue. Create a new task below.",
                        onActionClick = onNavigateToAddTask,
                        actionText = "Create Task"
                    )
                }
            } else {
                items(
                    items = uiState.filteredTasks,
                    key = { it.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onTaskClick = { onNavigateToTaskDetail(task.id) },
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onAdvanceStatus = { viewModel.advanceTaskStatus(task) },
                        onDeleteClick = { taskToDelete = task }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    taskToDelete?.let { task ->
        DeleteConfirmationDialog(
            taskTitle = task.title,
            onConfirm = {
                viewModel.deleteTask(task)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }
}
