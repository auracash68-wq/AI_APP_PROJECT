package com.taskflow.app.model

enum class TaskStatus(val displayName: String) {
    TODO("To Do"),
    IN_PROGRESS("In Progress"),
    REVIEW("Review"),
    COMPLETED("Completed");

    val isCompleted: Boolean
        get() = this == COMPLETED

    fun next(): TaskStatus {
        return when (this) {
            TODO -> IN_PROGRESS
            IN_PROGRESS -> REVIEW
            REVIEW -> COMPLETED
            COMPLETED -> TODO
        }
    }

    companion object {
        fun fromString(value: String): TaskStatus {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: TODO
        }
    }
}
