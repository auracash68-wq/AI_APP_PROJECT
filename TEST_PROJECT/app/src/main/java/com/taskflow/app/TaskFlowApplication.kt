package com.taskflow.app

import android.app.Application
import com.taskflow.app.data.local.TaskFlowDatabase
import com.taskflow.app.data.repository.TaskRepository
import com.taskflow.app.data.repository.TaskRepositoryImpl
import com.taskflow.app.data.repository.ThemePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TaskFlowApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: TaskFlowDatabase by lazy { TaskFlowDatabase.getDatabase(this) }
    val repository: TaskRepository by lazy { TaskRepositoryImpl(database.taskDao()) }
    val themePreferences: ThemePreferences by lazy { ThemePreferences(this) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.populateInitialDataIfEmpty()
        }
    }
}
