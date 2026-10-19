package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightCyan
import com.example.ui.theme.LightTextPrimary

@Composable
fun CyberControlsBar(
    availablePairs: Int,
    isDarkMode: Boolean = true,
    onHint: () -> Unit,
    onShuffle: () -> Unit,
    onUndo: () -> Unit,
    onResetView: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val barBackground = if (isDarkMode) {
        Brush.verticalGradient(
            listOf(
                CyberSurface.copy(alpha = 0.94f),
                Color(0xFF070B14).copy(alpha = 0.96f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.97f),
                Color(0xFFF1F5F9).copy(alpha = 0.97f)
            )
        )
    }
    val barBorder = if (isDarkMode) CyberBorder else LightBorder
    val cyanAccent = if (isDarkMode) CyberCyan else LightCyan
    val goldAccent = if (isDarkMode) CyberGold else Color(0xFFD97706)
    val greenAccent = if (isDarkMode) CyberGreen else Color(0xFF16A34A)
    val magentaAccent = if (isDarkMode) CyberMagenta else Color(0xFFC026D3)
    val neutralAccent = if (isDarkMode) Color.White.copy(alpha = 0.85f) else Color(0xFF334155)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(barBackground)
                .border(1.2.dp, barBorder, RoundedCornerShape(22.dp))
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Hint Button
            CyberControlButton(
                icon = Icons.Default.Lightbulb,
                label = "Pista",
                accentColor = goldAccent,
                badge = if (availablePairs > 0) "$availablePairs" else null,
                isDarkMode = isDarkMode,
                testTag = "btn_hint",
                onClick = onHint
            )

            // 2. Shuffle Button
            CyberControlButton(
                icon = Icons.Default.Refresh,
                label = "Mezclar",
                accentColor = cyanAccent,
                isDarkMode = isDarkMode,
                testTag = "btn_shuffle",
                onClick = onShuffle
            )

            // 3. Undo Button
            CyberControlButton(
                icon = Icons.Default.Undo,
                label = "Deshacer",
                accentColor = greenAccent,
                isDarkMode = isDarkMode,
                testTag = "btn_undo",
                onClick = onUndo
            )

            // 4. Center View Button
            CyberControlButton(
                icon = Icons.Default.CenterFocusStrong,
                label = "Centrar",
                accentColor = neutralAccent,
                isDarkMode = isDarkMode,
                testTag = "btn_reset_view",
                onClick = onResetView
            )

            // 5. Zoom In
            CyberControlButton(
                icon = Icons.Default.ZoomIn,
                label = "Acercar",
                accentColor = cyanAccent,
                isDarkMode = isDarkMode,
                testTag = "btn_zoom_in",
                onClick = onZoomIn
            )

            // 6. Zoom Out
            CyberControlButton(
                icon = Icons.Default.ZoomOut,
                label = "Alejar",
                accentColor = magentaAccent,
                isDarkMode = isDarkMode,
                testTag = "btn_zoom_out",
                onClick = onZoomOut
            )
        }
    }
}

@Composable
private fun CyberControlButton(
    icon: ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    isDarkMode: Boolean = true,
    badge: String? = null,
    isActive: Boolean = false
) {
    val labelColor = if (isDarkMode) Color.White.copy(alpha = 0.88f) else LightTextPrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive) accentColor.copy(alpha = 0.28f)
                        else accentColor.copy(alpha = if (isDarkMode) 0.12f else 0.16f)
                    )
                    .border(
                        1.dp,
                        if (isActive) accentColor else accentColor.copy(alpha = 0.45f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            badge?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (isDarkMode) CyberMagenta else Color(0xFFC026D3)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = label,
            color = labelColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
