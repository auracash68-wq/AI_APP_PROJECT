package com.taskflow.app.data.repository

import com.taskflow.app.data.DemoData
import com.taskflow.app.data.local.TaskDao
import com.taskflow.app.data.local.TaskEntity
import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTaskById(id: Long): Flow<Task?> {
        return taskDao.getTaskById(id).map { it?.toDomain() }
    }

    override suspend fun getTaskByIdOnce(id: Long): Task? {
        return taskDao.getTaskByIdOnce(id)?.toDomain()
    }

    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(TaskEntity.fromDomain(task))
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(TaskEntity.fromDomain(task))
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(TaskEntity.fromDomain(task))
    }

    override suspend fun deleteTaskById(id: Long) {
        taskDao.deleteTaskById(id)
    }

    override suspend fun toggleTaskComplete(task: Task) {
        val updated = if (task.status.isCompleted) {
            task.copy(
                status = TaskStatus.TODO,
                completedAt = null
            )
        } else {
            task.copy(
                status = TaskStatus.COMPLETED,
                completedAt = System.currentTimeMillis()
            )
        }
        updateTask(updated)
    }

    override suspend fun advanceTaskStatus(task: Task) {
        val nextStatus = task.status.next()
        val completedAt = if (nextStatus == TaskStatus.COMPLETED) System.currentTimeMillis() else null
        val updated = task.copy(status = nextStatus, completedAt = completedAt)
        updateTask(updated)
    }

    override suspend fun resetToDemoData() {
        taskDao.deleteAllTasks()
        val sampleEntities = DemoData.getSampleTasks().map { TaskEntity.fromDomain(it) }
        taskDao.insertTasks(sampleEntities)
    }

    override suspend fun clearCompletedTasks() {
        taskDao.deleteCompletedTasks()
    }

    override suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    override suspend fun populateInitialDataIfEmpty() {
        val existing = taskDao.getAllTasks().first()
        if (existing.isEmpty()) {
            val sampleEntities = DemoData.getSampleTasks().map { TaskEntity.fromDomain(it) }
            taskDao.insertTasks(sampleEntities)
        }
    }
}
