package com.taskflow.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.taskflow.app.ui.theme.CardShape
import com.taskflow.app.ui.theme.CriticalRed
import com.taskflow.app.ui.theme.PillShape

@Composable
fun DeleteConfirmationDialog(
    taskTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = CardShape,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = CriticalRed
            )
        },
        title = {
            Text(text = "Delete Task?")
        },
        text = {
            Text(text = "Are you sure you want to delete \"$taskTitle\"? This action cannot be undone.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = CriticalRed)
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = PillShape
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ResetDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = CardShape,
        icon = {
            Icon(
                imageVector = Icons.Outlined.RestartAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(text = "Reset to Demo Data?")
        },
        text = {
            Text(text = "This will replace your current tasks with the default sample tasks. Any custom tasks will be deleted.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Reset Data", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = PillShape
            ) {
                Text("Cancel")
            }
        }
    )
}
