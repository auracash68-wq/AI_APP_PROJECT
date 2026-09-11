package com.taskflow.app.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.automirrored.outlined.Sort
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
import com.taskflow.app.ui.viewmodel.TaskSortOption
import com.taskflow.app.ui.viewmodel.TaskUiState
import com.taskflow.app.ui.viewmodel.TaskViewModel

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    uiState: TaskUiState,
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaskFlowTopBar(
                title = "All Tasks",
                subtitle = "${uiState.filteredTasks.size} tasks listed",
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
                    contentDescription = "Create Task",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Filter Chips
            item {
                FilterChipsRow(
                    selectedStatus = uiState.selectedStatusFilter,
                    onStatusSelected = { viewModel.onStatusFilterSelected(it) },
                    selectedPriority = uiState.selectedPriorityFilter,
                    onPrioritySelected = { viewModel.onPriorityFilterSelected(it) }
                )
            }

            // Controls row: Sort button & Clear Filters
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box {
                        OutlinedButton(
                            onClick = { showSortMenu = true },
                            shape = PillShape,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Sort,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.sortOption.title,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            TaskSortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.title) },
                                    onClick = {
                                        viewModel.onSortOptionSelected(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }

                    if (uiState.selectedStatusFilter != null || uiState.selectedPriorityFilter != null || uiState.searchQuery.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.onStatusFilterSelected(null)
                                viewModel.onPriorityFilterSelected(null)
                                viewModel.onSearchQueryChanged("")
                            }
                        ) {
                            Text("Reset filters", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // List of Task Cards
            if (uiState.filteredTasks.isEmpty()) {
                item {
                    EmptyStateView(
                        title = "No tasks found",
                        description = "Try adjusting your filters or search query, or create a brand new task.",
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
