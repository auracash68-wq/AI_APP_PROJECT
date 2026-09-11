package com.taskflow.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.taskflow.app.data.local.TaskDao
import com.taskflow.app.data.local.TaskEntity
import com.taskflow.app.data.local.TaskFlowDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class TaskDaoTest {

    private lateinit var db: TaskFlowDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TaskFlowDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetTask() = runBlocking {
        val entity = TaskEntity(
            title = "Room Database Test Task",
            description = "Verifying DAO persistence",
            priority = "CRITICAL",
            status = "TODO",
            category = "Engineering",
            dueDate = 1700000000000L,
            createdAt = 1690000000000L
        )
        val id = taskDao.insertTask(entity)
        val retrieved = taskDao.getTaskByIdOnce(id)
        assertNotNull(retrieved)
        assertEquals("Room Database Test Task", retrieved?.title)
        assertEquals("CRITICAL", retrieved?.priority)

        val all = taskDao.getAllTasks().first()
        assertEquals(1, all.size)
    }

    @Test
    fun updateTask() = runBlocking {
        val entity = TaskEntity(
            title = "Original Title",
            description = "Desc",
            priority = "LOW",
            status = "TODO",
            category = "General",
            dueDate = 1700000000000L,
            createdAt = 1690000000000L
        )
        val id = taskDao.insertTask(entity)
        val inserted = taskDao.getTaskByIdOnce(id)!!

        val updated = inserted.copy(title = "Updated Title", status = "COMPLETED")
        taskDao.updateTask(updated)

        val retrieved = taskDao.getTaskByIdOnce(id)!!
        assertEquals("Updated Title", retrieved.title)
        assertEquals("COMPLETED", retrieved.status)
    }

    @Test
    fun deleteTask() = runBlocking {
        val entity = TaskEntity(
            title = "Task to be deleted",
            description = "Desc",
            priority = "MEDIUM",
            status = "TODO",
            category = "Personal",
            dueDate = 1700000000000L,
            createdAt = 1690000000000L
        )
        val id = taskDao.insertTask(entity)
        val inserted = taskDao.getTaskByIdOnce(id)!!

        taskDao.deleteTask(inserted)
        val retrieved = taskDao.getTaskByIdOnce(id)
        assertNull(retrieved)
    }
}
