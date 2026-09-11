package com.taskflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.taskflow.app.model.TaskPriority
import com.taskflow.app.model.TaskStatus
import com.taskflow.app.ui.theme.*

@Composable
fun PriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val (bgColor, borderColor, textColor) = when (priority) {
        TaskPriority.CRITICAL -> if (isDark) {
            Triple(CriticalRedDarkSurface, CriticalRedDarkBorder, CriticalRed)
        } else {
            Triple(CriticalRedSurface, CriticalRedBorder, CriticalRed)
        }
        TaskPriority.HIGH -> if (isDark) {
            Triple(HighAmberDarkSurface, HighAmberDarkBorder, HighAmber)
        } else {
            Triple(HighAmberSurface, HighAmberBorder, HighAmber)
        }
        TaskPriority.MEDIUM -> if (isDark) {
            Triple(MediumBlueDarkSurface, MediumBlueDarkBorder, PrimaryCobaltLight)
        } else {
            Triple(MediumBlueSurface, MediumBlueBorder, PrimaryCobalt)
        }
        TaskPriority.LOW -> if (isDark) {
            Triple(LowSlateDarkSurface, LowSlateDarkBorder, LightSlate)
        } else {
            Triple(LowSlateSurface, LowSlateBorder, SlateGray)
        }
    }

    Box(
        modifier = modifier
            .clip(PillShape)
            .background(bgColor)
            .border(1.dp, borderColor, PillShape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = priority.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun StatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val (bgColor, borderColor, textColor) = when (status) {
        TaskStatus.TODO -> if (isDark) {
            Triple(LowSlateDarkSurface, LowSlateDarkBorder, DarkTextSecondary)
        } else {
            Triple(LowSlateSurface, LowSlateBorder, SlateGray)
        }
        TaskStatus.IN_PROGRESS -> if (isDark) {
            Triple(MediumBlueDarkSurface, MediumBlueDarkBorder, PrimaryCobaltLight)
        } else {
            Triple(MediumBlueSurface, MediumBlueBorder, PrimaryCobalt)
        }
        TaskStatus.REVIEW -> if (isDark) {
            Triple(Color(0xFF2C1E4A), Color(0xFF6D28D9), Color(0xFFC4B5FD))
        } else {
            Triple(Color(0xFFF5F3FF), Color(0xFFDDD6FE), Color(0xFF7C3AED))
        }
        TaskStatus.COMPLETED -> if (isDark) {
            Triple(SuccessGreenDarkSurface, SuccessGreenDarkBorder, SuccessGreen)
        } else {
            Triple(SuccessGreenSurface, SuccessGreenBorder, SuccessGreen)
        }
    }

    Box(
        modifier = modifier
            .clip(PillShape)
            .background(bgColor)
            .border(1.dp, borderColor, PillShape)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(PillShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = "• $category",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
