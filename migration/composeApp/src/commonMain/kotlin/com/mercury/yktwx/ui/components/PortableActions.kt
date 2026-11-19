package com.mercury.yktwx.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Hide
import top.yukonga.miuix.kmp.icon.extended.Show
import top.yukonga.miuix.kmp.theme.MiuixTheme

// The original number component stays unchanged. Its Android app press-feedback
// wrapper is the only action boundary replaced in this narrow UI probe.
@Composable
internal fun VisibilityToggle(visible: Boolean, subject: String, onClick: () -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true) {
    IconButton(onClick = onClick, enabled = enabled, modifier = modifier.size(24.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Icon(if (visible) MiuixIcons.Hide else MiuixIcons.Show,
                contentDescription = if (visible) "隐藏$subject" else "显示$subject",
                modifier = Modifier.size(16.dp), tint = MiuixTheme.colorScheme.onSurfaceVariantSummary)
        }
    }
}
