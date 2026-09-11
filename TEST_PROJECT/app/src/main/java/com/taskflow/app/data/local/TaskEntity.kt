package com.taskflow.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val category: String,
    val dueDate: Long,
    val createdAt: Long,
    val completedAt: Long? = null
) {
    fun toDomain(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            priority = TaskPriority.fromString(priority),
            status = TaskStatus.fromString(status),
            category = category,
            dueDate = dueDate,
            createdAt = createdAt,
            completedAt = completedAt
        )
    }

    companion object {
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                priority = task.priority.name,
                status = task.status.name,
                category = task.category,
                dueDate = task.dueDate,
                createdAt = task.createdAt,
                completedAt = task.completedAt
            )
        }
    }
}
