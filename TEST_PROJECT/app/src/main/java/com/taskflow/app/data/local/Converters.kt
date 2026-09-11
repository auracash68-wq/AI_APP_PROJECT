package com.taskflow.app.data.local

import androidx.room.TypeConverter
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStatus

class Converters {
    @TypeConverter
    fun fromPriority(priority: TaskPriority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): TaskPriority = TaskPriority.fromString(value)

    @TypeConverter
    fun fromStatus(status: TaskStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): TaskStatus = TaskStatus.fromString(value)
}
