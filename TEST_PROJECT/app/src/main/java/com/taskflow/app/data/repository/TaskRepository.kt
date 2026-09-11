package com.taskflow.app.data.repository

import com.taskflow.app.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(id: Long): Flow<Task?>
    suspend fun getTaskByIdOnce(id: Long): Task?
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun deleteTaskById(id: Long)
    suspend fun toggleTaskComplete(task: Task)
    suspend fun advanceTaskStatus(task: Task)
    suspend fun resetToDemoData()
    suspend fun clearCompletedTasks()
    suspend fun deleteAllTasks()
    suspend fun populateInitialDataIfEmpty()
}
