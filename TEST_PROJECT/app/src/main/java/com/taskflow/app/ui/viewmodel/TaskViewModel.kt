package com.taskflow.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.app.data.repository.TaskRepository
import com.taskflow.app.data.repository.ThemePreferences
import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStats
import com.taskflow.app.model.TaskStatus
import com.taskflow.app.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private data class FilterState(
    val query: String,
    val status: TaskStatus?,
    val priority: TaskPriority?,
    val category: String?,
    val sort: TaskSortOption
)

class TaskViewModel(
    private val repository: TaskRepository,
    private val themePreferences: ThemePreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<TaskStatus?>(null)
    private val _selectedPriority = MutableStateFlow<TaskPriority?>(null)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _sortOption = MutableStateFlow(TaskSortOption.DEFAULT)
    private val _userMessage = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(TaskUiState(isLoading = true))
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        val filterFlow = combine(
            _searchQuery,
            _selectedStatus,
            _selectedPriority,
            _selectedCategory,
            _sortOption
        ) { query, status, priority, category, sort ->
            FilterState(query, status, priority, category, sort)
        }

        viewModelScope.launch {
            combine(
                repository.getTasks(),
                filterFlow,
                themePreferences.themeMode,
                _userMessage
            ) { tasks, filter, theme, message ->
                val filtered = applyFilterAndSort(
                    tasks = tasks,
                    query = filter.query,
                    status = filter.status,
                    priority = filter.priority,
                    category = filter.category,
                    sort = filter.sort
                )
                val stats = computeStats(tasks)
                TaskUiState(
                    tasks = tasks,
                    filteredTasks = filtered,
                    stats = stats,
                    searchQuery = filter.query,
                    selectedStatusFilter = filter.status,
                    selectedPriorityFilter = filter.priority,
                    selectedCategoryFilter = filter.category,
                    sortOption = filter.sort,
                    themeMode = theme,
                    isLoading = false,
                    userMessage = message
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun applyFilterAndSort(
        tasks: List<Task>,
        query: String,
        status: TaskStatus?,
        priority: TaskPriority?,
        category: String?,
        sort: TaskSortOption
    ): List<Task> {
        val trimmedQuery = query.trim().lowercase()

        val filtered = tasks.filter { task ->
            val matchesQuery = if (trimmedQuery.isEmpty()) true else {
                task.title.lowercase().contains(trimmedQuery) ||
                        task.description.lowercase().contains(trimmedQuery) ||
                        task.category.lowercase().contains(trimmedQuery)
            }
            val matchesStatus = status == null || task.status == status
            val matchesPriority = priority == null || task.priority == priority
            val matchesCategory = category == null || task.category.equals(category, ignoreCase = true)

            matchesQuery && matchesStatus && matchesPriority && matchesCategory
        }

        return when (sort) {
            TaskSortOption.DEFAULT -> filtered.sortedByDescending { it.createdAt }
            TaskSortOption.DUE_DATE_ASC -> filtered.sortedBy { it.dueDate }
            TaskSortOption.PRIORITY_DESC -> filtered.sortedByDescending { it.priority.weight }
            TaskSortOption.TITLE_ASC -> filtered.sortedBy { it.title.lowercase() }
        }
    }

    private fun computeStats(tasks: List<Task>): TaskStats {
        val now = System.currentTimeMillis()
        val total = tasks.size
        val completed = tasks.count { it.status == TaskStatus.COMPLETED }
        val inProgress = tasks.count { it.status == TaskStatus.IN_PROGRESS }
        val todo = tasks.count { it.status == TaskStatus.TODO }
        val critical = tasks.count { it.priority == TaskPriority.CRITICAL && !it.status.isCompleted }
        val overdue = tasks.count { !it.status.isCompleted && it.dueDate < now }

        return TaskStats(
            totalTasks = total,
            completedTasks = completed,
            inProgressTasks = inProgress,
            todoTasks = todo,
            criticalTasks = critical,
            overdueTasks = overdue,
            streakDays = if (completed > 0) 5 else 1
        )
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterSelected(status: TaskStatus?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }

    fun onPriorityFilterSelected(priority: TaskPriority?) {
        _selectedPriority.value = if (_selectedPriority.value == priority) null else priority
    }

    fun onCategoryFilterSelected(category: String?) {
        _selectedCategory.value = if (_selectedCategory.value == category) null else category
    }

    fun onSortOptionSelected(sortOption: TaskSortOption) {
        _sortOption.value = sortOption
    }

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskComplete(task)
            _userMessage.value = if (!task.status.isCompleted) {
                "Task completed! 🎉"
            } else {
                "Task reopened"
            }
        }
    }

    fun advanceTaskStatus(task: Task) {
        viewModelScope.launch {
            repository.advanceTaskStatus(task)
            _userMessage.value = "Status updated"
        }
    }

    fun saveTask(
        id: Long = 0,
        title: String,
        description: String,
        priority: TaskPriority,
        status: TaskStatus,
        category: String,
        dueDate: Long
    ): Boolean {
        if (title.isBlank()) {
            _userMessage.value = "Task title cannot be empty"
            return false
        }

        viewModelScope.launch {
            val task = Task(
                id = id,
                title = title.trim(),
                description = description.trim(),
                priority = priority,
                status = status,
                category = if (category.isBlank()) "General" else category.trim(),
                dueDate = dueDate,
                createdAt = if (id == 0L) System.currentTimeMillis() else (repository.getTaskByIdOnce(id)?.createdAt ?: System.currentTimeMillis()),
                completedAt = if (status == TaskStatus.COMPLETED) System.currentTimeMillis() else null
            )

            if (id == 0L) {
                repository.insertTask(task)
                _userMessage.value = "Task created successfully"
            } else {
                repository.updateTask(task)
                _userMessage.value = "Task updated successfully"
            }
        }
        return true
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _userMessage.value = "Task deleted"
        }
    }

    fun deleteTaskById(id: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
            _userMessage.value = "Task deleted"
        }
    }

    fun resetToDemoData() {
        viewModelScope.launch {
            repository.resetToDemoData()
            _userMessage.value = "Demo data restored"
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            repository.clearCompletedTasks()
            _userMessage.value = "Completed tasks cleared"
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        themePreferences.setThemeMode(mode)
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    suspend fun getTaskById(id: Long): Task? {
        return repository.getTaskByIdOnce(id)
    }
}
