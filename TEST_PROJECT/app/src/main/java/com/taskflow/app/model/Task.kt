package com.taskflow.app.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val category: String = "General",
    val dueDate: Long = System.currentTimeMillis() + 86_400_000L, // Default tomorrow
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    val isOverdue: Boolean
        get() = !status.isCompleted && dueDate < System.currentTimeMillis()
}
