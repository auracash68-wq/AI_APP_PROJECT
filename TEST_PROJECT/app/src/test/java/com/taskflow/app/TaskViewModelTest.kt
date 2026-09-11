package com.taskflow.app

import android.content.Context
import com.taskflow.app.data.DemoData
import com.taskflow.app.data.repository.TaskRepository
import com.taskflow.app.data.repository.ThemePreferences
import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStatus
import com.taskflow.app.model.ThemeMode
import com.taskflow.app.ui.viewmodel.TaskSortOption
import com.taskflow.app.ui.viewmodel.TaskViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDao: FakeTaskDao
    private lateinit var repository: TaskRepository
    private lateinit var themePreferences: ThemePreferences
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDao = FakeTaskDao()
        repository = com.taskflow.app.data.repository.TaskRepositoryImpl(fakeDao)

        // Mock context for ThemePreferences
        val mockContext = mockk<Context>(relaxed = true)
        val mockPrefs = mockk<android.content.SharedPreferences>(relaxed = true)
        val mockEditor = mockk<android.content.SharedPreferences.Editor>(relaxed = true)
        every { mockContext.getSharedPreferences(any(), any()) } returns mockPrefs
        every { mockPrefs.getString(any(), any()) } returns ThemeMode.SYSTEM.name
        every { mockPrefs.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor

        themePreferences = ThemePreferences(mockContext)
        viewModel = TaskViewModel(repository, themePreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialTasksLoadAndStats() = runTest(testDispatcher) {
        repository.resetToDemoData()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(6, state.tasks.size)
        assertEquals(6, state.stats.totalTasks)
        assertTrue(state.stats.completedTasks >= 1)
        assertTrue(state.stats.inProgressTasks >= 1)
    }

    @Test
    fun testSearchFiltering() = runTest(testDispatcher) {
        repository.resetToDemoData()
        advanceUntilIdle()

        viewModel.onSearchQueryChanged("wireframes")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredTasks.size)
        assertEquals("Mobile Flow Wireframes Review", state.filteredTasks[0].title)
    }

    @Test
    fun testStatusFiltering() = runTest(testDispatcher) {
        repository.resetToDemoData()
        advanceUntilIdle()

        viewModel.onStatusFilterSelected(TaskStatus.COMPLETED)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.filteredTasks.isNotEmpty())
        assertTrue(state.filteredTasks.all { it.status == TaskStatus.COMPLETED })
    }

    @Test
    fun testPriorityFiltering() = runTest(testDispatcher) {
        repository.resetToDemoData()
        advanceUntilIdle()

        viewModel.onPriorityFilterSelected(TaskPriority.CRITICAL)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.filteredTasks.isNotEmpty())
        assertTrue(state.filteredTasks.all { it.priority == TaskPriority.CRITICAL })
    }

    @Test
    fun testFormValidationForEmptyTitle() = runTest(testDispatcher) {
        val result = viewModel.saveTask(
            title = "   ",
            description = "Some description",
            priority = TaskPriority.LOW,
            status = TaskStatus.TODO,
            category = "General",
            dueDate = System.currentTimeMillis()
        )
        assertFalse("Should fail validation when title is blank", result)
    }

    @Test
    fun testTaskCreationAndDeletion() = runTest(testDispatcher) {
        val title = "Brand New Task 2026"
        val saved = viewModel.saveTask(
            title = title,
            description = "High priority unit testing",
            priority = TaskPriority.HIGH,
            status = TaskStatus.TODO,
            category = "Engineering",
            dueDate = System.currentTimeMillis() + 100_000L
        )
        assertTrue(saved)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val created = state.tasks.find { it.title == title }
        assertNotNull(created)

        viewModel.deleteTask(created!!)
        advanceUntilIdle()

        val stateAfterDelete = viewModel.uiState.value
        assertNull(stateAfterDelete.tasks.find { it.title == title })
    }

    @Test
    fun testThemeModeSelection() = runTest(testDispatcher) {
        viewModel.setThemeMode(ThemeMode.DARK)
        advanceUntilIdle()

        assertEquals(ThemeMode.DARK, viewModel.uiState.value.themeMode)
    }
}
