package com.taskflow.app

import com.taskflow.app.data.local.TaskDao
import com.taskflow.app.data.local.TaskEntity
import com.taskflow.app.data.repository.TaskRepositoryImpl
import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeTaskDao : TaskDao {
    val tasksMap = mutableMapOf<Long, TaskEntity>()
    val tasksFlow = MutableStateFlow<List<TaskEntity>>(emptyList())
    private var nextId = 1L

    private fun updateFlow() {
        tasksFlow.value = tasksMap.values.toList().sortedByDescending { it.createdAt }
    }

    override fun getAllTasks(): Flow<List<TaskEntity>> = tasksFlow

    override fun getTaskById(id: Long): Flow<TaskEntity?> = tasksFlow.map { list -> list.find { it.id == id } }

    override suspend fun getTaskByIdOnce(id: Long): TaskEntity? = tasksMap[id]

    override suspend fun insertTask(task: TaskEntity): Long {
        val assignedId = if (task.id == 0L) nextId++ else task.id
        val entity = task.copy(id = assignedId)
        tasksMap[assignedId] = entity
        updateFlow()
        return assignedId
    }

    override suspend fun insertTasks(tasks: List<TaskEntity>) {
        tasks.forEach { insertTask(it) }
    }

    override suspend fun updateTask(task: TaskEntity) {
        tasksMap[task.id] = task
        updateFlow()
    }

    override suspend fun deleteTask(task: TaskEntity) {
        tasksMap.remove(task.id)
        updateFlow()
    }

    override suspend fun deleteTaskById(id: Long) {
        tasksMap.remove(id)
        updateFlow()
    }

    override suspend fun deleteAllTasks() {
        tasksMap.clear()
        updateFlow()
    }

    override suspend fun deleteCompletedTasks() {
        val keysToRemove = tasksMap.filter { it.value.status == TaskStatus.COMPLETED.name }.keys
        keysToRemove.forEach { tasksMap.remove(it) }
        updateFlow()
    }
}

class TaskRepositoryTest {

    private lateinit var fakeDao: FakeTaskDao
    private lateinit var repository: TaskRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakeTaskDao()
        repository = TaskRepositoryImpl(fakeDao)
    }

    @Test
    fun testInsertAndGetTasks() = runBlocking {
        val task = Task(
            title = "Test Architecture",
            description = "Test description",
            priority = TaskPriority.HIGH,
            status = TaskStatus.TODO
        )

        val id = repository.insertTask(task)
        assertTrue(id > 0)

        val tasks = repository.getTasks().first()
        assertEquals(1, tasks.size)
        assertEquals("Test Architecture", tasks[0].title)
    }

    @Test
    fun testToggleComplete() = runBlocking {
        val task = Task(
            title = "Toggle me",
            status = TaskStatus.TODO
        )
        val id = repository.insertTask(task)
        val inserted = repository.getTasks().first().first()

        repository.toggleTaskComplete(inserted)
        val completedTask = repository.getTaskByIdOnce(id)
        assertNotNull(completedTask)
        assertEquals(TaskStatus.COMPLETED, completedTask?.status)
        assertNotNull(completedTask?.completedAt)

        repository.toggleTaskComplete(completedTask!!)
        val reopenedTask = repository.getTaskByIdOnce(id)
        assertEquals(TaskStatus.TODO, reopenedTask?.status)
        assertNull(reopenedTask?.completedAt)
    }

    @Test
    fun testAdvanceTaskStatus() = runBlocking {
        val task = Task(
            title = "Advance me",
            status = TaskStatus.TODO
        )
        val id = repository.insertTask(task)
        val inserted = repository.getTaskByIdOnce(id)!!

        repository.advanceTaskStatus(inserted)
        val step1 = repository.getTaskByIdOnce(id)!!
        assertEquals(TaskStatus.IN_PROGRESS, step1.status)

        repository.advanceTaskStatus(step1)
        val step2 = repository.getTaskByIdOnce(id)!!
        assertEquals(TaskStatus.REVIEW, step2.status)

        repository.advanceTaskStatus(step2)
        val step3 = repository.getTaskByIdOnce(id)!!
        assertEquals(TaskStatus.COMPLETED, step3.status)
        assertNotNull(step3.completedAt)
    }

    @Test
    fun testResetToDemoData() = runBlocking {
        repository.resetToDemoData()
        val tasks = repository.getTasks().first()
        assertTrue(tasks.isNotEmpty())
        assertEquals(6, tasks.size)
    }

    @Test
    fun testClearCompletedTasks() = runBlocking {
        repository.resetToDemoData()
        repository.clearCompletedTasks()
        val remaining = repository.getTasks().first()
        assertTrue(remaining.none { it.status == TaskStatus.COMPLETED })
    }
}
