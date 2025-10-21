package com.heklast.smartspender.responsive

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WidthClass { Compact, Medium, Expanded }
enum class HeightClass { Compact, Medium, Expanded }

@Stable
data class WindowSize(
    val width: WidthClass,
    val height: HeightClass,
    val maxWidth: Dp,
    val maxHeight: Dp
)

/** Works on Android + iOS (no platform APIs). */
@Composable
fun rememberWindowSize(): WindowSize {
    var win by remember {
        mutableStateOf(
            WindowSize(WidthClass.Compact, HeightClass.Compact, 0.dp, 0.dp)
        )
    }
    BoxWithConstraints {
        val w = maxWidth
        val h = maxHeight
        val widthClass = when {
            w < 730.dp -> WidthClass.Compact       // phones portrait, smallPhone in android
            w < 840.dp -> WidthClass.Medium        // big phones / small tablets
            else       -> WidthClass.Expanded      // tablets / wide, medPhone in android
        }
        val heightClass = when {
            h < 480.dp -> HeightClass.Compact
            h < 900.dp -> HeightClass.Medium
            else       -> HeightClass.Expanded
        }
        val computed = WindowSize(widthClass, heightClass, w, h)
        if (win != computed) win = computed
    }
    return win
}

/** Optional spacing tokens you can reuse everywhere. */
@Stable
data class Dimens(val padding: Dp, val gap: Dp, val corner: Dp)

@Composable
fun rememberDimens(win: WindowSize = rememberWindowSize()): Dimens = when (win.width) {
    WidthClass.Compact  -> Dimens(16.dp, 8.dp, 16.dp)
    WidthClass.Medium   -> Dimens(24.dp, 12.dp, 20.dp)
    WidthClass.Expanded -> Dimens(32.dp, 16.dp, 24.dp)
}