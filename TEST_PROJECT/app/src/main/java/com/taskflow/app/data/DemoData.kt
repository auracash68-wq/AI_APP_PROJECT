package com.taskflow.app.data

import com.taskflow.app.model.Task
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStatus

object DemoData {
    fun getSampleTasks(): List<Task> {
        val now = System.currentTimeMillis()
        val oneHour = 3_600_000L
        val oneDay = 86_400_000L

        return listOf(
            Task(
                id = 1L,
                title = "Revise Design System Architecture",
                description = "Align spacing scale with Android 8pt grid, integrate Geist & Inter typography hierarchy, and standardize component tokens.",
                priority = TaskPriority.CRITICAL,
                status = TaskStatus.IN_PROGRESS,
                category = "Engineering",
                dueDate = now + (4 * oneHour),
                createdAt = now - (2 * oneDay)
            ),
            Task(
                id = 2L,
                title = "Mobile Flow Wireframes Review",
                description = "Walk through the high-fidelity onboarding and task creation screens with the UX research team.",
                priority = TaskPriority.HIGH,
                status = TaskStatus.TODO,
                category = "Design",
                dueDate = now + (6 * oneHour),
                createdAt = now - (1 * oneDay)
            ),
            Task(
                id = 3L,
                title = "Finalize Q3 OKR Metrics",
                description = "Summarize core team deliverables, code coverage targets, and sprint velocity measurements.",
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.REVIEW,
                category = "Personal",
                dueDate = now + oneDay,
                createdAt = now - (3 * oneDay)
            ),
            Task(
                id = 4L,
                title = "Sync with Client Lead",
                description = "Review release roadmap, discuss Android 15/16 compatibility updates, and establish launch timeline.",
                priority = TaskPriority.LOW,
                status = TaskStatus.COMPLETED,
                category = "Personal",
                dueDate = now - (5 * oneHour),
                createdAt = now - (4 * oneDay),
                completedAt = now - (2 * oneHour)
            ),
            Task(
                id = 5L,
                title = "Implement Room Database Migrations",
                description = "Ensure schema indices, foreign keys, and clean test fixtures for offline-first synchronization.",
                priority = TaskPriority.HIGH,
                status = TaskStatus.IN_PROGRESS,
                category = "Engineering",
                dueDate = now + (2 * oneDay),
                createdAt = now - (12 * oneHour)
            ),
            Task(
                id = 6L,
                title = "Prepare Product Launch Deck",
                description = "Create 10-slide keynote presentation highlighting TaskFlow's clean 2026 UI, micro-interactions, and local privacy.",
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.TODO,
                category = "Marketing",
                dueDate = now + (4 * oneDay),
                createdAt = now - (6 * oneHour)
            )
        )
    }
}
