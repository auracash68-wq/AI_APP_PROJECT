package com.taskflow.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),      // 8dp Standard Corners (Stitch)
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),     // 16dp Large Panels & Task Cards
    extraLarge = RoundedCornerShape(24.dp)
)

val PillShape = RoundedCornerShape(9999.dp)
val CardShape = RoundedCornerShape(16.dp)
val SmallButtonShape = RoundedCornerShape(8.dp)
val ChipShape = RoundedCornerShape(9999.dp)
