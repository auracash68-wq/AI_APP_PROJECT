package com.taskflow.app.model

data class TaskStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val todoTasks: Int = 0,
    val criticalTasks: Int = 0,
    val overdueTasks: Int = 0,
    val streakDays: Int = 5
) {
    val completionPercentage: Int
        get() = if (totalTasks == 0) 0 else ((completedTasks.toFloat() / totalTasks) * 100).toInt()
}
