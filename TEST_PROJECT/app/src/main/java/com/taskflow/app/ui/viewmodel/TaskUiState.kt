package com.taskflow.app.ui.viewmodel

import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStats
import com.taskflow.app.model.TaskStatus
import com.taskflow.app.model.ThemeMode

enum class TaskSortOption(val title: String) {
    DEFAULT("Newest First"),
    DUE_DATE_ASC("Due Date (Earliest)"),
    PRIORITY_DESC("Priority (Highest)"),
    TITLE_ASC("Title (A-Z)")
}

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val stats: TaskStats = TaskStats(),
    val searchQuery: String = "",
    val selectedStatusFilter: TaskStatus? = null,
    val selectedPriorityFilter: TaskPriority? = null,
    val selectedCategoryFilter: String? = null,
    val sortOption: TaskSortOption = TaskSortOption.DEFAULT,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)
