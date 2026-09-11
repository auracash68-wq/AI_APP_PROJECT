package com.taskflow.app

import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStats
import com.taskflow.app.model.TaskStatus
import org.junit.Assert.*
import org.junit.Test

class TaskModelTest {

    @Test
    fun testPriorityWeights() {
        assertTrue(TaskPriority.CRITICAL.weight > TaskPriority.HIGH.weight)
        assertTrue(TaskPriority.HIGH.weight > TaskPriority.MEDIUM.weight)
        assertTrue(TaskPriority.MEDIUM.weight > TaskPriority.LOW.weight)
    }

    @Test
    fun testStatusWorkflowTransitions() {
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.TODO.next())
        assertEquals(TaskStatus.REVIEW, TaskStatus.IN_PROGRESS.next())
        assertEquals(TaskStatus.COMPLETED, TaskStatus.REVIEW.next())
        assertEquals(TaskStatus.TODO, TaskStatus.COMPLETED.next())
    }

    @Test
    fun testStatsCompletionPercentage() {
        val emptyStats = TaskStats(totalTasks = 0, completedTasks = 0)
        assertEquals(0, emptyStats.completionPercentage)

        val halfStats = TaskStats(totalTasks = 10, completedTasks = 5)
        assertEquals(50, halfStats.completionPercentage)

        val fullStats = TaskStats(totalTasks = 12, completedTasks = 12)
        assertEquals(100, fullStats.completionPercentage)
    }

    @Test
    fun testTaskOverdueCalculation() {
        val pastDueTask = Task(
            title = "Overdue task",
            status = TaskStatus.TODO,
            dueDate = System.currentTimeMillis() - 10_000L
        )
        assertTrue(pastDueTask.isOverdue)

        val completedPastDueTask = Task(
            title = "Completed past due task",
            status = TaskStatus.COMPLETED,
            dueDate = System.currentTimeMillis() - 10_000L
        )
        assertFalse(completedPastDueTask.isOverdue)

        val futureTask = Task(
            title = "Future task",
            status = TaskStatus.TODO,
            dueDate = System.currentTimeMillis() + 100_000L
        )
        assertFalse(futureTask.isOverdue)
    }
}
