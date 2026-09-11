package com.taskflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.taskflow.app.ui.navigation.TaskFlowApp
import com.taskflow.app.ui.theme.TaskFlowTheme
import com.taskflow.app.ui.viewmodel.TaskViewModel
import com.taskflow.app.ui.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels {
        val app = application as TaskFlowApplication
        TaskViewModelFactory(app.repository, app.themePreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            TaskFlowTheme(themeMode = uiState.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TaskFlowApp(viewModel = viewModel)
                }
            }
        }
    }
}
