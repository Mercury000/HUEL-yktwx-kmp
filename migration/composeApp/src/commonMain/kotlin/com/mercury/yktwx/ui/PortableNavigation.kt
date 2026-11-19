package com.mercury.yktwx.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

// The probe has no expanding tablet rail. Keep the original top bar's shell inputs explicit.
internal val LocalMainNavigationTitleAlpha = staticCompositionLocalOf<() -> Float> { { 1f } }
internal val LocalMainNavigationGlassRail = staticCompositionLocalOf { false }

@Composable
internal fun isMainNavigationWideScreen(): Boolean {
    val windowSize = LocalWindowInfo.current.containerSize
    val (width, height) = with(LocalDensity.current) { windowSize.width.toDp() to windowSize.height.toDp() }
    return width >= 840.dp || (width >= 600.dp && height / width < 1.2f)
}
