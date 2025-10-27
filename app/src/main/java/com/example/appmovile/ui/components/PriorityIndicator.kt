package com.example.appmovile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.example.appmovile.ui.theme.PriorityHigh
import com.example.appmovile.ui.theme.PriorityMedium
import com.example.appmovile.ui.theme.PriorityLow

@Composable
fun PriorityIndicator(priority: String) {
    val color = when (priority.uppercase()) {
        "ALTA" -> PriorityHigh
        "MEDIA" -> PriorityMedium
        "BAJA" -> PriorityLow
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(10.dp) // Tamaño pequeño para un indicador discreto
            .background(color, CircleShape)
    )
}